package com.xiaoliu.aiCodeMother.controller;

import cn.hutool.json.JSONUtil;
import com.xiaoliu.aiCodeMother.common.BaseResponse;
import com.xiaoliu.aiCodeMother.common.ErrorCode;
import com.xiaoliu.aiCodeMother.common.ResultUtils;
import com.xiaoliu.aiCodeMother.exception.BusinessException;
import com.xiaoliu.aiCodeMother.model.dto.chat.ChatRequest;
import com.xiaoliu.aiCodeMother.model.vo.ChatVO;
import com.xiaoliu.aiCodeMother.service.StreamAiChatService;
import com.xiaoliu.aiCodeMother.service.SyncAiChatService;
import dev.langchain4j.memory.chat.ChatMemoryProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

/**
 * AI 聊天 Controller
 */
@RestController
@RequestMapping("/chat")
@Slf4j
@Tag(name = "AI 聊天接口",description = "AI 聊天相关接口")
public class ChatController {
    @Resource
    private SyncAiChatService syncAiChatService;

    @Resource
    private StreamAiChatService streamAiChatService;

    @Resource
    private ChatMemoryProvider chatMemoryProvider;

    /**
     * AI 对话
     *
     * @param chatRequest 聊天请求
     * @return AI 回复
     */
    @PostMapping("/send")
    @Operation(summary = "同步对话(无记忆存储)", description = "用户输入消息，获取 AI 的回复")
    public BaseResponse<ChatVO> sendMessage(@RequestBody ChatRequest chatRequest){
        String message = chatRequest.getMessage();
        if (message == null || message.isBlank()){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"消息不能为空");
        }

        // 2. 【新增】参数校验：消息长度不能超过 2000 个字符
        // 为什么要限制长度？防止恶意用户发送超长文本，导致大模型 Token 消耗过大，或者引发 OOM（内存溢出）
        if (message.length() > 2000) {
            return ResultUtils.error(ErrorCode.PARAMS_ERROR, "消息长度不能超过 2000 个字符");
        }

        // 2. 调用ai服务
        long startTime = System.currentTimeMillis();
        String reply = syncAiChatService.chat(message);
        long duration = System.currentTimeMillis() - startTime;
        log.info("AI 对话耗时：{}ms，请求消息长度：{}，回复消息长度：{}", duration, message.length(), reply.length());
        // 3. 返回结果
        ChatVO chatVO = new ChatVO();
        chatVO.setReply(reply);
        return ResultUtils.success(chatVO);
    }

    /**
     * AI 对话（流式 SSE）
     *
     * 前端可以通过 EventSource 或 fetch + ReadableStream 接收
     */
    @PostMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @Operation(summary = "流式对话(无记忆存储)", description = "用户输入消息，获取 AI 的流式回复")
    public Flux<ServerSentEvent<String>> chatStream(@RequestBody String message){
        // 参数校验
        if (message==null || message.isBlank()){
            return  Flux.just(ServerSentEvent.<String>builder()
                    .event("error")
                    .data("消息不能为空")
                    .build());
        }
        log.info("流式对话开始，消息：{}", message);


        // 调用 AI 流式服务，将 Flux<String> 转换成 Flux<ServerSentEvent>
        Flux<String> contentFlux= streamAiChatService.chatStream(message);

        return contentFlux
                // 1. 将每个 chunk 包装成一个包含“文本”和“当前累计长度”的对象
                // 这里我们用一个简单的 Map 来充当临时载体
                .scan(
                        // 初始状态：0个字符，空字符串
                        Map.<String,Object>of("reply", "", "totalChars", 0),
                        // 累积逻辑：拿到上一个状态(prev)和当前的文本块(chunk)
                        (prev,chunk)->{
                            // 2. 安全地取出上一次的字符数，并强制转换为 int
                            int prevTotal = (int) prev.get("totalChars");
                            int newTotal = prevTotal + chunk.length();

                            // 3. 返回新的 Map，同样明确泛型
                            return Map.<String,Object>of(
                                    "reply", chunk,
                                    "totalChars", newTotal
                            );
                        }
                        )

                // 4. 跳过初始状态
                .skip(1)


                .map(wrapper ->{
                    // 每个数据块包装成 SSE 事件
                    String jsonData= JSONUtil.toJsonStr(wrapper);
                    return ServerSentEvent.<String>builder()
                            .event("message")
                            .data(jsonData)
                            .build();
                })
                .concatWith(Mono.just(
                        // 发送结束事件
                        ServerSentEvent.<String>builder()
                                .event("done")
                                .data("")
                                .build()
                ))
                .doOnTerminate(() -> log.info("流式对话结束"));
    }

    // ===========  有记忆的对话接口   ================
    @PostMapping(value = "/stream/memory", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @Operation(summary = "流式对话（有记忆存储）", description = "用户输入消息，获取 AI 的流式回复")
    public Flux<ServerSentEvent<String>> chatStreamWithMemory(
            @RequestBody String message,
            HttpServletRequest request){
        if (message==null || message.isBlank()){
            return Flux.just(ServerSentEvent.<String>builder().event("error").data("消息不能为空").build());
        }

        // 用 Session ID 作为记忆 ID（未登录用户也有 Session）
        String memoryId=request.getSession().getId();

        // 注意：这里获取的是“上一轮”结束后的消息数量，包含当前用户刚发的这条消息
        int currentMessageCount = chatMemoryProvider.get(memoryId).messages().size();

        log.info("流式对话开始，消息：{}", message);

        Flux<String> contentFlux= streamAiChatService.chatStreamWithMemory(memoryId,message);

        return contentFlux
                .map(chunk->{
                    Map<String,Object> wrapper=Map.of(
                            "reply",chunk,
                            "messageCount", currentMessageCount);// 动态传入当前的消息数量
                    return ServerSentEvent.<String>builder()
                            .event("message")
                            .data(JSONUtil.toJsonStr(wrapper))
                            .build();
                })
                .concatWith(Mono.just(
                        ServerSentEvent.<String>builder().event("done").data("").build()
                ))
                .doOnTerminate(() -> log.info("流式对话结束"));
    }

    /*  2. 清除记忆接口 */
    @PostMapping("/memory/clear")
    @Operation(summary = "清除记忆", description = "清除当前会话的记忆信息")
    public BaseResponse<Boolean> clearMemory(HttpServletRequest request) {
        // 用 Session ID 作为记忆 ID（未登录用户也有 Session）
        String memoryId = request.getSession().getId();
        chatMemoryProvider.get(memoryId).clear();
        log.info("已清除 Session [{}] 的对话记忆", memoryId);
        return ResultUtils.success(true);
    }
}

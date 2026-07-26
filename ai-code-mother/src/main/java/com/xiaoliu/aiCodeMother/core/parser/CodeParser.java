package com.xiaoliu.aiCodeMother.core.parser;

/**
 * 代码解析器接口
 *
 * @param <T> 解析结果类型
 */
public interface CodeParser<T> {

    /**
     * 从 AI 回复中解析代码
     *
     * @param aiResponse AI 的完整回复内容
     * @return 解析后的结构化结果
     */
    T parseCode(String aiResponse);
}

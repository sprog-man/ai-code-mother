<template>
  <div>
    <a-layout style="min-height: 100vh">
      <a-layout-header style="background: #fff; padding: 0 50px">
        <h1>AI 代码生成平台</h1>
      </a-layout-header>

      <a-layout-content style="padding: 50px">
        <a-card title="测试后端接口">
          <a-space direction="vertical" style="width: 100%">
            <!--测试成功响应-->
            <a-button type="primary" @click="handleTestSuccess" :loading="loading">测试成功响应</a-button>
            <!--测试失败响应-->
            <a-button danger @click="handleTestError" :loading="loading">测试失败响应</a-button>
            <!--获取项目信息-->
            <a-button  @click="handleGetInfo" :loading="loading">获取项目信息</a-button>
            <!--显示结果-->
            <a-card v-if="result" title="响应结果" size="small">
              <pre>{{ result }}</pre>
            </a-card>
            <input v-model="name" type="text" placeholder="请输入姓名">
            </input>
            <a-button type="primary" @click="handleHello" :loading="loading">提交</a-button>
            <div style="margin-top:10px">{{ result }}</div>

          </a-space>
        </a-card>
      </a-layout-content>
    </a-layout>
  </div>
</template>

<script setup lang="ts">
import {ref} from 'vue'
import {message} from "ant-design-vue";
import {testApi} from "@/api/test.ts";

const loading=ref(false)
const result=ref<any>(null)
const name=ref<string>()

//测试成功响应
const handleTestSuccess=async()=>{
  loading.value=true
  try{
    const data=await testApi.testSuccess()
    message.success('测试成功')
    result.value=data
  }catch (e){
    message.error('测试失败')
  }finally {
    loading.value=false
  }
}

//测试错误响应
const handleTestError=async()=>{
  loading.value=true
  try{
    await testApi.testError()
  }catch (e){
    // 错误已在 axios 拦截器中处理c
  }finally {
    loading.value=false
  }
}

//获取项目信息
const handleGetInfo=async()=>{
  loading.value=true
  try{
    const data=await testApi.getInfo()
    message.success('获取项目信息成功')
    result.value=data
  }catch (error){
    console.error(error)
  }finally {
    loading.value=false
  }
}

//hello接口测试
const handleHello=async()=>{
  if (!name.value){
    result.value="请输入姓名"
    return
  }
  try {
    const data=await testApi.testHello(name.value)
    message.success("hello请求测试成功")
    result.value=data
  }catch (error){
    message.error("hello请求测试失败")
  }finally {
    loading.value=false
  }
}

</script>

<style scoped>
h1 {
  margin: 0;
  color: #1890ff;
}

pre {
  background: #f5f5f5;
  padding: 10px;
  border-radius: 4px;
}
</style>


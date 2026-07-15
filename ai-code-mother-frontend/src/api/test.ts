import request from '@/request.ts'

/**
 * 测试接口
 *
 */
export const testApi= {
    //测试成功响应
    testSuccess(){
        return request.get('/test/success')
    },

    //测试失败响应
    testError(){
        return request.get('/test/error')
    },

    //获取项目信息
    getInfo(){
        return request.get<any,{ author: string; version: string; name: string}> ('/test/info')
    },

    //新增测试hello接口
    testHello(name:string){
        return request.get(`/test/hello?name=${encodeURIComponent(name)}`)
    }
}
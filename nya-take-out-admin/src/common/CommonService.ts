import axios from "axios";
import storage from "./storage";
import {message} from "ant-design-vue";

const service = axios.create({
    baseURL: 'http://localhost:8080', // 所有请求的公共地址部分
    timeout: 3000 // 请求超时时间 这里的意思是当请求时间超过5秒还未取得结果时 提示用户请求超时
})

// 请求拦截器
service.interceptors.request.use(config => {
        // // 在这个位置需要统一的去注入token
        // if (store.getters.token) {
        //     if (isCheckTimeout()) {
        //         // 登出操作
        //         store.dispatch('user/logout')
        //         return Promise.reject(new Error('token 失效'))
        //     }
        //     // 如果token存在 注入token
        //     config.headers.Authorization = `Bearer ${store.getters.token}`
        // }
        const token = storage.get("authorization", '').token;
        if (token) {
            // @ts-ignore
            config.headers.Authorization = token
        }
        /**
         * 对get方法的请求进行参数拼接
         */
        if (config.method === 'GET' && config.params) {
            let url = config.url + '?';
            for (const propName of Object.keys(config.params)) {
                const value = config.params[propName];
                const part = encodeURIComponent(propName) + "=";
                if (value !== null && typeof (value) !== "undefined") {
                    if (typeof value === 'object') {
                        for (const key of Object.keys(value)) {
                            let params = propName + '[' + key + ']';
                            const subPart = encodeURIComponent(params) + "=";
                            url += subPart + encodeURIComponent(value[key]) + "&";
                        }
                    } else {
                        url += part + encodeURIComponent(value) + "&";
                    }
                }
            }
            url = url.slice(0, -1);
            config.params = {};
            config.url = url;
        }
        return config // 必须返回配置
    }, error => {
        return Promise.reject(error)
    }
)

//响应拦截器
service.interceptors.response.use(response => {
        console.log(response)
        return response;
    }, error => {
        if (error.response) {
            // 请求成功发出且服务器也响应了状态码，但状态代码超出了 2xx 的范围
            if (error.response.status === 401) {
                logout()
            }
        }
        return Promise.reject(error)
    }
)
const logout = () => {
    storage.set("authorization", null);
    // @ts-ignore
    window.location = "/sign-in"
}
export default service;
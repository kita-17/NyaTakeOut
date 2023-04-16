// @ts-nocheck
import UserStorage from "@/storage/UserStorage";

const BASE_URL = 'http://127.0.0.1:8080';

export const service = (options: { url: any; method: any; data: any; }) => {
    return new Promise<any>((resolve, reject) => {
            // 封装主体：网络请求
            uni.request({
                url: BASE_URL + options.url,
                data: options.data || {},
                method: options.method || 'GET',// 默认值GET，如果有需要改动，在options中设定其他的method值
                header: {'Authorization': UserStorage.getToken()},
                success: (res) => {// 控制台显示数据信息
                    resolve(res)
                    // 登陆超时移除用户信息
                    if (res.data.code == 1001) {
                        UserStorage.logout();
                        uni.showModal({
                            content: res.data.msg,
                            showCancel: false
                        })
                    }
                },
                fail: (err) => {
                    // 页面中弹框显示失败
                    uni.showToast({
                        title: '请求接口失败',
                        icon: 'error'
                    })
                    // 返回错误消息
                    reject(err)
                }
            })
        }
    )
}

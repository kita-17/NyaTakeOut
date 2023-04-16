<template>
    <view>
        <form @submit="login">
            <view class="main-box">
                <input v-model="phone" class="phone-input" maxlength="11" name="phone" placeholder="请输入手机号码"
                       type="number"/>
                <view class="code-box">
                    <input class="code-input" maxlength="6" name="code" placeholder="请输入验证码" type="number"/>
                    <button :disabled="codeDisable" class="code-button" @click="getCode">{{ codeText }}</button>
                </view>
                <button class="login-button" form-type="submit">登入</button>
            </view>
        </form>
    </view>
</template>

<script lang="ts" setup>
import {service} from '@/api/api';
import UserStorage from '@/storage/UserStorage';
import RegxUtil from '@/utils/RegxUtil';
import {ref} from 'vue';

const phone = ref();

const codeText = ref('获取验证码')
const codeDisable = ref(false)
const codeStatus = ref(true)
const counter = ref(10)

const login = (e: any) => {
    const data = e.detail.value;
    if (data.phone == '' || !RegxUtil.validatorPhone(data.phone)) {
        uni.showModal({
            content: '请输入正确的手机号',
            showCancel: false
        })
        return;
    } else if (data.code == '') {
        uni.showModal({
            content: '请输入验证码',
            showCancel: false
        })
        return;
    }
    service({url: '/user/login', method: 'post', data: data})
        .then(res => {
            if (res.data.code == 400) {
                uni.showModal({
                    content: res.data.msg,
                    showCancel: false
                })
                return;
            }
            if (res.data.code == 200) {
                UserStorage.login(res.data.data);
                uni.showModal({
                    content: '登录成功',
                    showCancel: false
                })
                uni.navigateBack({
                    delta: 1
                })
            }
        })
}

const getCode = () => {
    if (!codeStatus.value) {
        return;
    }
    codeStatus.value = false;
    codeDisable.value = true;
    service({url: '/user/code', method: 'get', data: {phone: phone.value}})
        .then(res => {
            if (res.data.code == 400) {
                uni.showModal({
                    content: res.data.msg,
                    showCancel: false
                })
                return;
            }
        })
    const timer = setInterval(() => {
        if (counter.value === 0) {
            //清除定时器
            codeStatus.value = true;
            codeDisable.value = false;
            codeText.value = '获取验证码'
            counter.value = 10
            clearInterval(timer)
        } else {
            counter.value--
            codeText.value = counter.value + "秒后重新获取"
        }
    }, 1000, {immediate: false});
}
</script>

<style scoped>
.main-box {
    display: flex;
    flex-direction: column;
}

.phone-input {
    margin: 16rpx;
    height: 120rpx;
    font-size: 32rpx;
    font-weight: 550;
}

.code-input {
    margin: 16rpx;
    height: 120rpx;
    width: 125%;
}

.code-box {
    display: flex;
    justify-content: space-between;
    align-items: center;
}

.code-button {
    width: 100%;
    height: 100%;
    background-color: #ff6e7f;
    color: #ffffff;
    margin: 0 10rpx 0 100rpx;
}

.login-button {
    background-color: #3e88ff;
    color: #ffffff;
    width: 75%;
    margin-top: 64rpx;
}
</style>
<template>
    <view v-if="!authorization" :style="'height:' + notLoginHeight + 'rpx'" class="go-to-box">
        <button class="go-to-login-button" @click="toLogin">去登入</button>
    </view>
    <view v-else>
        <view class="profile-head">
            <image :lazy-load="true" class="profile-avatar" src="../../static/home_select.png"/>
            <view class="profile-name">
                <text>
                    {{ profile.username || '用户名字' }}
                </text>
                <text style="font-size: 24rpx;margin: 10rpx 0 10rpx 0;">
                    其他信息
                </text>
            </view>
        </view>
        <view class="profile-content">
            <uni-card>
                <view class="box-1">
                    <view class="profile-content-item" @click="toAddress">
                        <uni-icons size="32" style="margin: 16rpx 0 0 0;" type="location"/>
                        <text>我的地址</text>
                    </view>
                    <view class="profile-content-item">
                        <uni-icons size="32" style="margin: 16rpx 0 0 0;" type="phone"/>
                        <text>我的客服</text>
                    </view>
                    <view class="profile-content-item" @click="toFavorite">
                        <uni-icons size="32" style="margin: 16rpx 0 0 0;" type="star"/>
                        <text>收藏店铺</text>
                    </view>
                    <view class="profile-content-item" @click="toComment">
                        <uni-icons size="32" style="margin: 16rpx 0 0 0;" type="chatboxes"/>
                        <text>我的评价</text>
                    </view>
                    <view class="profile-content-item" @click="toCouponList">
                        <uni-icons size="32" style="margin: 16rpx 0 0 0;" type="gift"/>
                        <text>优惠券</text>
                    </view>
                </view>
            </uni-card>
        </view>
    </view>
</template>

<script lang="ts" setup>
import UserStorage from '@/storage/UserStorage';
import userStorage from '@/storage/UserStorage';
import {onLaunch, onShow} from '@dcloudio/uni-app';
import {ref} from 'vue';

const notLoginHeight = ref(1145)
const authorization = ref(false);
const profile = ref()
/**
 * 获取屏幕高度
 */
const calcHeight = () => {
    uni.getSystemInfo({
        success(res) {
            notLoginHeight.value = res.screenHeight;
        }
    })
}
/**
 * 导航到地址簿
 */
const toAddress = () => {
    uni.navigateTo({
        url: '/pages/address/list'
    })
}
/**
 * 未登录时，导航到登陆界面
 */
const toLogin = () => {
    if (!authorization.value) {
        uni.navigateTo({
            url: '/pages/user/login'
        });
    }
}
//导航到店铺收藏
const toFavorite = () => {
    if (userStorage.checkAuthorization()) {
        uni.navigateTo({
            url: '/pages/favorite/index'
        });
    }
}
//导航到店铺评论
const toComment = () => {
    if (userStorage.checkAuthorization()) {
        uni.navigateTo({
            url: '/pages/comment/index'
        });
    }
}

const toCouponList = () => {
    if (userStorage.checkAuthorization()) {
        uni.navigateTo({
            url: '/pages/user/CouponList'
        });
    }
}

onLaunch(() => {
    calcHeight();
})

onShow(() => {
    authorization.value = UserStorage.checkAuthorization();
    profile.value = uni.getStorageSync('UserProfile');
})

</script>
<style scoped>
.go-to-box {
    display: flex;
    flex-flow: column;
}

.go-to-login-button {
    width: 50%;
    height: 80rpx;
    color: aliceblue;
    background-color: rgb(43, 153, 255);
    margin: auto;
}

.profile-head {
    display: flex;
    height: 200rpx;
}

.profile-content {
    height: 100%;
}

.profile-avatar {
    height: 150rpx;
    width: 150rpx;
    display: flex;
    border-radius: 16px;
    justify-items: center;
    align-items: center;
    margin: 32rpx;
    position: relative;
}

.profile-name {
    display: flex;
    flex-direction: column;
    color: rgb(42, 42, 42);
    font-size: 40rpx;
    margin: 32rpx;
    padding: auto;
    align-items: flex-start;
    justify-content: left;
}

.profile-content-item {
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    margin: 5rpx 0 5rpx 0;
    flex: 0 0 25%;
}

/* 去除每行尾的多余边距 */
.profile-content-item:nth-child(4n) {
    margin-right: 0;
}

/* 使最后一个元素的边距填满剩余空间 */
.profile-content-item:last-child {
    margin-right: auto;
}

.box-1 {
    display: flex;
    justify-content: space-between;
    flex-wrap: wrap;
}
</style>
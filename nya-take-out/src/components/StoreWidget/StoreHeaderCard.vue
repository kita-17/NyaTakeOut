<template>
    <view class="container">
        <image src="../../static/banner.png" style="width: 100%; height: 150px;">
            <view class="fav-button" @click="setFavorite">
                <text>{{ isFav ? '已关注' : '关注店铺' }}</text>
                <uni-icons
                        :color="isFav ? '#FFAA33' : '#FFFFFF'" :type="isFav ? 'star-filled' : 'star'"
                        size="45"
                />
            </view>
        </image>
        <uni-card class="info-card">
            <view class="business-header" style="height: 240rpx;width: 100%;">
                <view class="business-header-content">
                    <text class="business-header-title">{{ title }}</text>
                    <view>
                        <text class="business-header-rate">{{ rate }}分</text>
                        <text class="business-header-info"> {{ deliveryType }} 约60分钟 - 月售{{ sales }}</text>
                    </view>
                    <view>
                        <text class="business-header-description">{{ description }}</text>
                    </view>
                </view>
                <view>
                    <image class="business-header-image" src="../../static/logo.png"/>
                </view>
            </view>
        </uni-card>
    </view>
</template>

<script lang="ts" setup>
import {onShow} from "@dcloudio/uni-app";
import {ref} from "vue";
import {service} from "@/api/api";
import UserStorage from "@/storage/UserStorage";

const props = defineProps({
    //商店名称
    title: {
        type: String,
        default: ''
    },
    //店铺评分
    rate: {
        type: String,
        default: '5.0'
    },
    //店铺简介
    description: {
        type: String,
        default: '下北泽会员制餐厅, 非常地新鲜, 非常地美味;本店响应光盘行动,吃完了有奖励, 吃不完将有惩罚'
    },
    //派送方式
    deliveryType: {
        type: String,
        default: '猫咪速达'
    },
    //每月销量
    sales: {
        type: String,
        default: '0'
    },
    isFavorite: {
        type: Boolean,
        default: false
    },
    storeId: {
        type: String || Number,
    }
})
let uid = UserStorage.getUserId()
const setFavorite = () => {
    if (!isFav.value) {
        service({url: '/store/favorite/add', method: 'POST', data: {userId: uid, storeId: props.storeId}}).then(
            res => {
                if (res.data.code == 200) {
                    isFav.value = true;
                }
            }
        )
    } else {
        service({url: '/store/favorite/remove', method: 'POST', data: {userId: uid, storeId: props.storeId}}).then(
            res => {
                if (res.data.code == 200) {
                    isFav.value = false;
                }
            }
        )
    }
}
const isFav = ref(false)
onShow(() => {
    service({url: '/store/favorite/is', method: 'GET', data: {userId: uid, storeId: props.storeId}}).then(
        res => {
            if (res.data.code == 200) {
                isFav.value = true;
            }
        }
    )
})
</script>

<style scoped>
.container {
    position: relative;
}

.info-card {
    width: 100%;
    height: 100%;
    margin: auto;
    position: absolute;
    top: 150rpx;
    left: 0;
    bottom: 0;
    right: 0;
}

.business-header-image {
    position: absolute;
    top: 10px;
    right: 10px;
    border-radius: 5px;
    width: 128rpx;
    height: 128rpx;
}

.business-header-content {
    display: flex;
    flex-direction: column;
    justify-content: space-between;
}

.business-header-title {
    position: absolute;
    top: 24rpx;
    left: 20rpx;
    font-size: 64rpx;
    font-weight: bold;
    margin: 7px;
}

.business-header-rate {
    color: #fd1b1b;
    font-size: 40rpx;
    font-weight: bold;
    position: absolute;
    top: 130rpx;
    left: 24rpx;
    margin: 7rpx;
}

.business-header-info {
    font-size: 32rpx;
    font-weight: bold;
    position: absolute;
    top: 120rpx;
    left: 130rpx;
    margin: 7px;
}

.business-header-description {
    position: absolute;
    top: 170rpx;
    left: 0;
    display: -webkit-box;
    -webkit-box-orient: vertical;
    -webkit-line-clamp: 2;
    overflow: hidden;
    padding-left: 5px;
    padding-right: 5px;
    margin: 14rpx;
    font-size: 32rpx;
}

.fav-button {
    display: flex;
    justify-content: center;
    align-items: center;
    position: absolute;
    z-index: 1000;
    right: 0;
    top: 0;
    margin: 10rpx;
    padding: 10rpx;
    color: #FFFFFF;
}
</style>
<template>
    <scroll-view :enable-back-to-top="true" :scroll-with-animation="true" :scroll-y="true" :show-scrollbar="false" enhanced
                 style="height: 100%;">
        <view v-for="(order, index) in dataSource" :key="index">
            <uni-card margin="10">
                <view style="display: flex; flex-direction: column">
                    <view @click="lookOrderDetail(order)">
                        <!--店家名字-->
                        <view class="order-store-header">
                            <view class="order-store-name">
                                <image src="../../static/logo.png" style="height: 50rpx;width: 50rpx;"/>
                                <text style="margin-left: 20rpx;font-weight: bold;color: #000000">{{ order.storeName }}
                                </text>
                            </view>
                            <view class="order-status">
                                {{ getStatus(order.status) }}
                            </view>
                        </view>
                        <!--菜品内容-->
                        <view class="order-item-content">
                            <view class="order-item-image">
                                <!--订单物品的图片-->
                                <image
                                        v-for="(item, eIndex) in order.detail.slice(0, 3)" :key="eIndex"
                                        :src="getImageUrl(item.image)"
                                        style="height: 120rpx;width: 120rpx;border-radius: 15rpx;margin-left: 15rpx;margin-right: 15rpx"
                                />
                            </view>
                            <!--菜品名-->
                            <view v-if="order.detail.length < 2"
                                  style="display: flex;justify-content: center;align-items: center">
                                <text style="font-size: 32rpx; font-weight: bold">
                                    {{ order.detail[0].name }}
                                </text>
                            </view>
                        </view>
                    </view>
                    <view style="display: flex;justify-content: space-between;">
                        <view class="order-item-price-amount">
                            <view class="order-total-amount">
                                <text>共{{ order.detail.length }}项</text>
                            </view>
                            <view class="order-total-price">
                                <text>¥{{ order.price }}</text>
                            </view>
                        </view>
                        <view v-if="!order.commented" class="comment-btn-box"
                              @click="toComment(order.storeId, order.id)">
                            <text class="comment-btn">评价</text>
                        </view>
                        <view v-else class="comment-btn-box">
                            <text class="comment-btn-ed">已评价</text>
                        </view>
                    </view>
                </view>
            </uni-card>
        </view>
    </scroll-view>
</template>

<script lang="ts" setup>
import {ref} from "vue";
import {service} from "@/api/api";
import {onShow} from "@dcloudio/uni-app";
import UserStorage from "@/storage/UserStorage";

const dataSource = ref()
//初始化订单信息
const init = () => {
    let id = UserStorage.getUserId()
    service({url: '/orders', method: 'get', data: {id: id},}).then(res => {
        if (res.data.code == 200) {
            dataSource.value = res.data.data
        }
    })
}
const getImageUrl = (url: string) => {
    return `http://127.0.0.1:8080/image/download?name=${url}`
}

//订单详情页面
const lookOrderDetail = (item: any) => {
    uni.navigateTo({
        url: '/pages/orders/detail?orders=' + JSON.stringify(item)
    })
}
/**
 * 订单状态 1待付款，2待派送，3已派送，4已完成，5已取消
 */
const getStatus = (state: number) => {
    switch (state) {
        case 1:
            return '待付款';
        case 2:
            return '待派送';
        case 3:
            return '已派送';
        case 4:
            return '已完成';
        case 5:
            return '已取消';
    }
}

const toComment = (storeId: string | number, orderId: string | number) => {
    uni.navigateTo({
        url: '/pages/comment/edit?storeId=' + storeId + "&orderId=" + orderId
    })
}
onShow(() => {
    init()
})
</script>

<style scoped>
.order-store-header {
    display: flex;
    width: 100%;
    height: 50rpx;
    justify-content: space-between;
}

.order-store-name {
    display: flex;
    align-items: center;
}

.order-item-content {
    width: 100%;
    height: 150rpx;
    display: flex;
}

.order-item-image {
    margin: 15rpx 15rpx 15rpx 0;
}

.order-item-price-amount {
    display: flex;
    justify-content: flex-end;
    flex-direction: column;
}

.order-total-price {
    font-weight: bold;
    font-size: 32rpx;
}

.order-status {
    display: flex;
}

.comment-btn {
    display: flex;
    justify-content: center;
    align-content: center;
    border-radius: 32px;
    margin: 0 16rpx 0 16rpx;
    padding: 8rpx 16rpx 8rpx 16rpx;
    width: 128rpx;
    font-size: 32rpx;
    color: #FFFFFF;
    background-color: #3e88ff;
}

.comment-btn-ed {
    display: flex;
    justify-content: center;
    align-content: center;
    border-radius: 32px;
    margin: 0 16rpx 0 16rpx;
    padding: 8rpx 16rpx 8rpx 16rpx;
    width: 128rpx;
    font-size: 32rpx;
    color: #FFFFFF;
    background-color: #ff9154;
}
</style>
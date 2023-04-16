<template>
    <scroll-view
            :enable-back-to-top="true"
            :scroll-with-animation="true"
            :scroll-y="true"
            :show-scrollbar="false"
            enhanced
            style="height: 100%;"
    >
        <view class="detail-container">
            <!--订单状态-->
            <view class="detail-header">
                <text style="margin: 0 0 0 25rpx">
                    订单已完成
                </text>
            </view>
            <uni-card margin="10" padding="0">
                <!--订单物品信息-->
                <view class="detail-item-info">
                    <!--店家名称-->
                    <view class="detail-store-name">
                        下北泽餐厅
                    </view>
                    <!--商品列表-->
                    <view class="detail-item-list">
                        <view v-for="(item, index) in dataSource.detail" :key="index">
                            <view class="detail-item-box">
                                <image
                                        :src="getImageUrl(item.image)"
                                        style="height: 128rpx;width: 128rpx;border-radius: 13rpx;margin: 0 12rpx 12rpx 12rpx"
                                />
                                <view style="display: flex; flex-direction: column;">
                                    <text>{{ item.name }}</text>
                                    <text>x{{ item.amount }}</text>
                                </view>
                                <view style="display: flex;position: absolute;right: 20rpx">
                                    ¥{{ item.amount * item.price }}
                                </view>
                            </view>
                        </view>
                    </view>
                    <!--实付款-->
                    <view class="detail-total-price">
                        <text style="margin: 25rpx">
                            实付款¥{{ dataSource.price }}
                        </text>
                    </view>
                </view>
                <!--订单详细信息-->
                <view class="detail-info">
                    <!--收货地址信息-->
                    <view class="detail-address">
                        <text>收货信息:</text>
                        <view style="display: flex;flex-direction: column;">
                            <text>{{ getAddress() }}</text>
                            <text style="display: flex;justify-content: flex-end">{{ getConsignee() }}</text>
                        </view>
                    </view>
                    <!--备注-->
                    <view class="detail-remark">
                        <text>备注:</text>
                        <text class="detail-box">{{ dataSource.remark }}</text>
                    </view>
                    <!--订单号-->
                    <view class="detail-orders-id">
                        <text>订单号:</text>
                        <text>{{ dataSource.number }}</text>
                    </view>
                    <!--送达时间-->
                    <view class="orders-finish-date">
                        <text>送达时间:</text>
                        <text>尽快送达</text>
                    </view>
                    <!--配送方式-->
                    <view class="orders-finish-date">
                        <text>配送方式:</text>
                        <text>猫咪速达</text>
                    </view>
                    <!--下单时间-->
                    <view class="order-time">
                        <text>下单时间:</text>
                        <text>{{ dataSource.checkoutTime }}</text>
                    </view>
                </view>
            </uni-card>
        </view>
    </scroll-view>
</template>

<script lang="ts" setup>
import {ref} from "vue";

const props = defineProps(['orders'])
const dataSource = ref()
dataSource.value = JSON.parse(props.orders)

const getImageUrl = (url: string) => {
    return `http://127.0.0.1:8080/image/download?name=${url}`
}

const getAddress = () => {
    let address = dataSource.value.address
    return address.provinceName + address.cityName + address.districtName + address.detail
}

const getConsignee = () => {
    let address = dataSource.value.address
    return address.consignee + address.phone
}
</script>

<style scoped>
.detail-header {
    width: 100%;
    height: 120rpx;
    /*background-color: #9fcaff;*/
    color: #606060;
    font-size: 50rpx;
    font-weight: bold;
    display: flex;
    align-items: center;
}

.detail-item-info {
    display: flex;
    flex-direction: column;
}

.detail-info {
    height: 400rpx;
}

.detail-store-name {
    font-weight: bold;
    margin: 14rpx 0 14rpx 0;
}

.detail-total-price {
    display: flex;
    width: 100%;
    height: 150rpx;
    font-weight: bold;
    font-size: 32rpx;
    justify-content: flex-end;
}

.detail-address {
    display: flex;
    justify-content: space-between;
}

.detail-remark {
    margin: 12rpx 0 12rpx 0;
    display: flex;
    justify-content: space-between;
}

.detail-orders-id {
    margin: 12rpx 0 12rpx 0;
    display: flex;
    justify-content: space-between;
}

.orders-finish-date {
    margin: 12rpx 0 12rpx 0;
    display: flex;
    justify-content: space-between;
}

.order-time {
    margin: 12rpx 0 12rpx 0;
    display: flex;
    justify-content: space-between;
}

.detail-item-box {
    display: flex;
    margin: 20rpx 20rpx 20rpx 0;
}
</style>
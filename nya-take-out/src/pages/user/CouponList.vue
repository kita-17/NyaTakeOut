<template>
    <view class="main-con">
        <view v-for="(coupon,index) in dataSource" :key="index">
            <!-- 优惠券 -->
            <view :class="coupon.type == 0 ? 'coupon-content-nor' : 'coupon-content-sk'">
                <view style="display: flex; flex-direction: column;">
                    <view class="use-store-box">
                        <text class="use-text">{{ coupon.storeName || '店铺名字' }}</text>
                    </view>
                    <view :class="coupon.type == 0 ? 'dive-line-1' : 'dive-line-2'"/>
                    <view class="coupon-content" style="display: flex;">
                        <view class="coupon-box">
                            <text class="coupon-price"> ¥{{ coupon.discount }}</text>
                            <text class="coupon-rule"> 满¥{{ coupon.couponCondition }}元可用</text>
                        </view>
                        <view class="coupon-box-2">
                            <text class="coupon-name"> {{ coupon.name }}</text>
                            <text class="coupon-date"> {{ coupon.date || '2000-01-01' }}到期</text>
                        </view>
                        <view v-if="props.chooseMode" class="coupon-box-2">
                            <view v-if="canUse(coupon)">
                                <button
                                        class="use-button"
                                        style="background-color: #4c64fd;color:#fff"
                                        @click="selectCoupon(coupon)"
                                >
                                    <text>使用</text>
                                </button>
                            </view>
                            <view v-else>
                                <button
                                        :disabled="true"
                                        class="use-button"
                                        style="background-color: #525252;color:#fff"
                                        @click="selectCoupon(coupon)"
                                >
                                    <text>无法使用</text>
                                </button>
                            </view>
                        </view>
                    </view>
                </view>
            </view>
        </view>
    </view>
</template>

<script lang="ts" setup>
import {ref} from "vue";
import {service} from "@/api/api";
import UserStorage from "@/storage/UserStorage";

const dataSource = ref()
const props = defineProps(['amount', 'storeId', 'chooseMode'])
let uid = UserStorage.getUserId();

service({url: '/coupon/user', method: "GET", data: {userId: uid}}).then(res => {
    if (res.data.code == 200) {
        dataSource.value = res.data.data
    }
})

const selectCoupon = (data: any) => {
    uni.$emit('handlerSelectCoupon', data);
    uni.navigateBack({
        delta: 1
    })
}

const canUse = (value: any) => {
    return (props.amount >= value.couponCondition) && (props.storeId == value.storeId)
}
</script>

<style scoped>
.main-con {
    padding: 8px;
}

.coupon-box {
    margin: 16rpx;
    padding: 12rpx;
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;

}

.coupon-price {
    color: #ff5555;
    font-size: 50rpx;
    font-weight: 700;
    margin-top: 12rpx;
}

.coupon-rule {
    color: #434343;
    font-size: 20rpx;
}

.coupon-name {
    margin-top: 12rpx;
    margin-bottom: 12rpx;
    font-size: 40rpx;
}

.coupon-date {
    font-size: 20rpx;
    color: #3e88ff;
    font-weight: bold;
}

.coupon-box-2 {
    margin: 16rpx;
    padding: 12rpx;
    display: flex;
    flex-direction: column;
    justify-content: center;
}

.coupon-btn-box {
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
}

.coupon-content-nor {
    margin: 12px;
    padding-left: 10rpx;
    padding-right: 10rpx;
    background-color: #ffffff;
    border-radius: 16px;
    box-shadow: 0px 0px 3px 1px rgba(0, 0, 0, 0.08);
}

.coupon-content-sk {
    margin: 12px;
    padding-left: 10rpx;
    padding-right: 10rpx;
    background-color: #ff9253;
    border-radius: 16px;
    color: #ffffff;
    box-shadow: 0px 0px 3px 1px rgba(0, 0, 0, 0.08);
}

.use-store-box {
    margin: 16rpx;
}

.use-text {
    font-size: 37rpx;
}

.dive-line-1 {
    border-bottom: #7a7a7a 2px solid;
}

.dive-line-2 {
    border-bottom: #d75a1e 2px solid;
}

.use-button {
    border-radius: 64rpx;
    width: 130rpx;
    font-size: 20rpx;
    color: #FFFFFF;
    margin: 12rpx;

}
</style>
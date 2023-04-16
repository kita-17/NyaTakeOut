<template>
    <view class="coupon-main-view" @click="showCoupon">
        <text class="coupon-main-text">查看优惠券</text>
    </view>
	<!--优惠券弹窗-->
    <uni-popup ref="popup" background-color="#fff" type="bottom">
        <view class="fill-box"/>
        <scroll-view
                :scroll-with-animation="true"
                :scroll-y="true"
                :show-scrollbar="false"
                :style="'height:' + popH + 'rpx'"
                enhanced
        >
            <view v-for="(coupon,index) in dataSource" :key="index">
                <!-- 优惠券 -->
                <view :class="coupon.type == 0 ? 'coupon-content-nor' : 'coupon-content-sk' ">
                    <view style="display: flex; justify-content: space-between;align-items: center;">
                        <view class="coupon-content" style="display: flex;">
                            <view class="coupon-box">
                                <text class="coupon-price"> ¥{{ coupon.discount }}</text>
                                <text class="coupon-rule"> 满¥{{ coupon.couponCondition }}元可用</text>
                            </view>
                            <view class="coupon-box-2">
                                <text class="coupon-name"> {{ coupon.name }}</text>
                                <text class="coupon-date"> {{ coupon.validDays }}天有效</text>
                            </view>
                        </view>
                        <view class="coupon-btn-box">
                            <view v-if="coupon.type == 0">
                                <button class="coupon-order-button" @click="bugCoupon(coupon)">3元购买</button>
                            </view>
                            <view v-else>
                                <button class="coupon-order-button" @click="bugCoupon(coupon)">3元抢购</button>
                            </view>
                            <text>
                                剩余 {{ coupon.count }} 个
                            </text>
                        </view>
                    </view>
                </view>
            </view>
        </scroll-view>
        <view class="fill-box"/>
        <nya-message ref="msg"/>
    </uni-popup>
</template>

<script lang="ts" setup>

import {ref} from "vue";
import {service} from "@/api/api";
import NyaMessage from "@/components/NyaMessage.vue";
import UserStorage from "@/storage/UserStorage";
import {onShow} from "@dcloudio/uni-app";

const props = defineProps({
    storeId: {
        type: String || Number
    }
})
let uid = UserStorage.getUserId();
const dataSource = ref()
const popup = ref();
const msg = ref()
const popH = ref(120)


const getStoreCoupon = () => {
    service({url: '/coupon/store', method: "GET", data: {storeId: props.storeId}}).then(res => {
        if (res.data.code == 200) {
            dataSource.value = res.data.data
            popH.value += dataSource.value.length * 100;
        }
    })
}

const showCoupon = () => {
    popup.value.open()
}

const bugCoupon = (data: any) => {
    data.userId = uid
    service({
        url: '/coupon/add/user',
        method: 'POST',
        data: data
    }).then(res => {
        if (res.data.code == 200) {
            msg.value.info(res.data.msg)
            data.count -= 1;
        } else msg.value.error(res.data.msg)
    })
}
onShow(() => {
    getStoreCoupon()
})

</script>

<style scoped>
.coupon-box {
    margin: 26rpx;
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
    font-size: 27rpx;
    color: #3e88ff;
    font-weight: bold;
}

.coupon-order-button {
    border-radius: 32rpx;
    width: 140rpx;
    height: auto;
    background-color: #ff645c;
    color: #ffffff;
    font-weight: 700;
    font-size: small;
    text-align: center;
    margin: 10rpx;
}

.coupon-box-2 {
    margin: 26rpx;
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

.coupon-main-view {
    margin: 32rpx;
    padding: 12rpx;
    background-color: #fd664e;
    border-radius: 16px;
    color: #FFFFFF;
}

.coupon-main-text {
    margin-left: 16rpx;
}

.fill-box {
    height: 75rpx;
}

.coupon-content-nor {
    margin: 6rpx 12px 6px 12px;
    padding-left: 10rpx;
    padding-right: 10rpx;
    background-color: #ffffff;
    border-radius: 16px;
    box-shadow: 0px 0px 3px 1px rgba(0, 0, 0, 0.08);
}

.coupon-content-sk {
    margin: 6rpx 12px 6px 12px;
    padding-left: 10rpx;
    padding-right: 10rpx;
    background-color: #ff9253;
    border-radius: 16px;
    color: #ffffff;
    box-shadow: 0px 0px 3px 1px rgba(0, 0, 0, 0.08);
}
</style>
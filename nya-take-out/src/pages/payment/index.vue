<template>
    <view class="orders-container">
        <!--头部信息-->
        <!--<view class="orders-header">-->
        <!--	<text class="orders-header-title">订单信息</text>-->
        <!--</view>-->
        <!--选中的商品-->
        <view class="orders-content">
            <view class="orders-address">
                <view class="orders-address-text">
                    <view v-if="addressData != null"
                          style="display: flex;flex-direction: column">
                        <text>
                            {{
								addressData.provinceName + addressData.cityName + addressData.districtName + addressData.detail
                            }}
                        </text>
                        <text style="font-size: 24rpx;margin-top: 16rpx;color: #6e6e6e">
                            {{ addressData.consignee + addressData.phone }}
                        </text>
                    </view>
                    <view v-else>
                        选择收货地址
                    </view>
                </view>
                <uni-icons size="24" type="compose" @click="handlerChangeAddress"/>
            </view>
            <uni-card>
                <scroll-view
                        :enable-back-to-top="true"
                        :scroll-with-animation="true"
                        :scroll-y="true"
                        :show-scrollbar="false"
                        enhanced
                        style="height: 400rpx;"
                >
                    <view class="orders-item-list">
                        <view v-for="(item, index) in dataSource" :key="index">
                            <view class="orders-item">
                                <image :src="getImageUrl(item.image)"
                                       style="height: 100rpx;width: 100rpx;border-radius: 16px"/>
                                <view style="display: flex;flex-direction: column">
                                    <text class="orders-item-name">{{ item.name }}</text>
                                    <view class="orders-item-flavor">
                                        <view v-for="(f, fIndex) in item.flavor" :key="fIndex">
                                            <text style="margin: 0 3px 0 3px">{{ f.selected }}</text>
                                        </view>
                                    </view>
                                    <text class="orders-item-amount">x{{ item.amount }}</text>
                                    <text class="orders-item-price">¥{{ item.amount * item.price }}</text>
                                </view>
                            </view>
                        </view>
                    </view>
                </scroll-view>
            </uni-card>
            <view class="coupon-select-box">
                <text style="margin: 16rpx;padding: 16rpx;">
                    猫咪优惠券
                </text>
                <text v-if="coupon!=null" class="select-coupon" @click="getCoupon">
                    已优惠
                    <text>
                        {{ coupon.discount }}
                    </text>
                    元
                </text>
                <text v-else class="not-select-coupon" @click="getCoupon">
                    还未选择优惠券
                </text>
            </view>
            <view class="remark-box">
                <uni-easyinput
                        v-model="remark"
                        placeholder="请输入您的备注"
                        type="textarea"
                />
            </view>
        </view>
        <!--确认支付按钮-->
        <view class="orders-footer">
            <view class="orders-total-price">
                总共: ¥{{ finalAmount }}
            </view>
            <view class="orders-submit-button">
                <button
                        style="height: 90rpx;width: 256rpx;background-color: #007aff;color: #FFFFFF;border-radius: 64rpx"
                        @click="handlerOrderNow"
                >
                    <text>提交订单</text>
                </button>
            </view>
        </view>
    </view>
    <nya-message ref="msg"/>
</template>

<script lang="ts" setup>

import {onUnmounted, ref} from "vue";
import {CartEntity} from "@/entitys/nEntity";
import {service} from "@/api/api";
import {onLoad, onShow} from "@dcloudio/uni-app";
import UserStorage from "@/storage/UserStorage";
import NyaMessage from "@/components/NyaMessage.vue";

const props = defineProps(['cartData', 'totalPrice', 'storeId'])
const dataSource = ref()
const addressData = ref()
const finalAmount = ref(0);
const remark = ref()
const coupon = ref()
const msg = ref()

//更改地址 -> 跳转到修改地址页面 返回时会把新地址返回回来
const handlerChangeAddress = () => {
    uni.navigateTo({
        url: '/pages/address/list?choose=1'
    })
}
//下单
const handlerOrderNow = () => {
    if (addressData.value == null) {
        msg.value.error("请选择收货地址")
        return;
    }
    let orders = {
        //订单状态 1待付款，2待派送，3已派送，4已完成，5已取消
        status: 1,
        userId: '',
        addressBookId: addressData.value.id,
        payMethod: 1,
        price: finalAmount.value,
        remark: remark.value,
        detail: [],
        coupon: coupon.value,
        storeId: props.storeId,
        couponId: null
    }
    if (coupon.value) {
        orders.couponId = coupon.value.id
    }
    //订单转成Orders表的结构
    orders.detail = dataSource.value.map((item: CartEntity) => {
        return {
            name: item.name,
            image: item.image,
            dishId: item.dishId,
            setmealId: item.setmealId,
            dishFlavor: item.flavor.map(i => i.selected).join(","),
            amount: item.amount,
            price: item.price,
            type: item.type,
        }
    })
    orders.userId = UserStorage.getUserId()
    service({url: '/orders', method: 'post', data: orders}).then(res => {
        if (res.data.code == 200) {
            uni.navigateBack({
                delta: 2
            })
        }
    })
}
//返回商品图片链接
const getImageUrl = (url: string) => {
    return `http://127.0.0.1:8080/image/download?name=${url}`
}
uni.$on('chooseAddressOnOrder', result => {
    addressData.value = result
    console.log(addressData.value)
})

//获取优惠券
const getCoupon = () => {
    uni.navigateTo({
        url: '/pages/user/CouponList?amount=' + props.totalPrice
            + "&storeId=" + props.storeId + '&chooseMode=1'
    })
}
uni.$on('handlerSelectCoupon', result => {
    coupon.value = result;
})
let uid = UserStorage.getUserId();
onUnmounted(() => {
    uni.$off('chooseAddressOnOrder')
    uni.$off('handlerSelectCoupon')
})
// todo 地址检查，禁止空地址提交
onLoad(() => {
    //初始化购物车数据以及收货地址
    if (props.cartData) {
        dataSource.value = JSON.parse(props.cartData)
    }
    //获取默认地址 1 默认 0 非默认
    if (addressData.value == null) {
        service({url: '/address', method: 'get', data: {id: uid, isDefault: 1}}).then(res => {
            if (res.data.data && res.data.data.length > 0) {
                addressData.value = res.data.data[0]
            }
        })
    }
})
onShow(() => {
    //最终价格
    finalAmount.value = props.totalPrice;
    //若没有使用优惠券
    if (coupon.value != null && coupon.value.count >= 0 && coupon.value.couponCondition <= props.totalPrice) {
        finalAmount.value = props.totalPrice - coupon.value.discount;
    }
    if (finalAmount.value < 0) { //优惠后价格小于0  将不使用优惠券，防止白嫖以及操作失误而导致异常优惠券
        finalAmount.value = props.totalPrice
        coupon.value = null;
        uni.showToast({
            title: '非法的优惠券',
            icon: 'error'
        })
    }
})
</script>

<style lang="scss" scoped>
.orders-container {
}

.orders-header {
  position: fixed;
  top: 0;
  z-index: 999;
  width: 100%;
  height: 100rpx;

  &-title {
    display: flex;
    align-items: center;
    justify-content: center;
  }
}

.orders-content {
  width: 100%;
  position: absolute;
  //top: 100rpx;
  height: calc(100% - 150rpx);
}

.orders-footer {
  position: fixed;
  bottom: 0;
  z-index: 999;
  width: 100%;
  height: 150rpx;
  display: flex;
  box-shadow: 0 -5px 10px -5px rgba(161, 161, 161, 0.53);
}

.orders-address {
  display: flex;
  height: 100rpx;
  padding: 15rpx;
  background-color: #fff1de;
  box-shadow: 0 5px 10px -5px rgba(161, 161, 161, 0.53);
  justify-content: space-between;
}

.orders-address-text {
  font-size: 30rpx;
  display: flex;
  flex-direction: column;
}

.orders-item-list {
  padding: 15rpx;
  height: calc(100% - 350rpx);
}

.orders-item {
  display: flex;
  height: 128rpx;
  margin-top: 32rpx;
  margin-bottom: 32rpx;

  &-name {
    margin-left: 15rpx;
    font-size: 32rpx;
  }

  &-flavor {
    display: flex;
    margin-left: 15rpx;
    font-size: 20rpx;
    flex-wrap: wrap;
  }

  &-amount {
    margin-left: 15rpx;
    font-size: 27rpx;
    position: relative;
    left: 10rpx;
  }

  &-price {
    font-size: 30rpx;
    position: relative;
    left: 370rpx;
    top: -25rpx;
    color: #e43d33;
    font-weight: bold;
  }
}

.orders-total-price {
  width: 50%;
  justify-content: flex-start;
  padding: 32rpx;
}

.orders-submit-button {
  padding: 32rpx;
}

.remark-box {
  background-color: #ffffff;
  margin: 15rpx;
  position: absolute;
  bottom: 32rpx;
  left: 0;
  right: 0;
}

.coupon-select-box {
  height: 100rpx;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.select-coupon {
  margin: 16rpx;
  padding: 16rpx;
  border: #ffb264 solid 1px;
  border-radius: 64rpx;
  background-color: #ffb264;
  color: #FFFFFF;
  font-size: 20rpx;
}

.not-select-coupon {
  margin: 16rpx;
  padding: 16rpx;
  border: #ff7373 solid 1px;
  border-radius: 64rpx;
  font-size: 16rpx;
  background-color: #ff7373;
  color: #FFFFFF
}
</style>
<template>
    <view class="container">
        <view class="address-list">
            <scroll-view
                    :enable-back-to-top="true"
                    :scroll-with-animation="true"
                    :scroll-y="true"
                    :show-scrollbar="false"
                    enhanced
                    style="height: 1250rpx"
            >
                <view v-for="(address, index) in dataSource" :key="index">
                    <uni-card :border="false" :is-shadow="false" is-full>
                        <view style="display: flex;">
                            <view class="address-box">
                                <view @click="handlerSelectAddress(address)">
                                    <view style="font-weight: bold;color: #000000">
                                        {{
											address.provinceName + address.cityName + address.districtName + address.detail
                                        }}
                                    </view>
                                    <view style="display: flex">
                                        <view style="margin-right:32rpx">
                                            {{ address.consignee }}
                                        </view>
                                        <view>
                                            {{ address.phone }}
                                        </view>
                                    </view>
                                </view>
                                <uni-icons size="32" type="compose" @click="edit(address)"/>
                            </view>
                        </view>
                    </uni-card>
                </view>
            </scroll-view>
        </view>
        <view class="address-new-button">
            <button style="width: 70%;background-color: #4c64fd; color: #FFFFFF" @click="add">新增收货地址</button>
        </view>
    </view>
</template>

<script lang="ts" setup>
import UniCard from "@/uni_modules/uni-card/components/uni-card/uni-card.vue";
import {onUnmounted, ref} from "vue";
import {service} from "@/api/api";
import {onShow} from "@dcloudio/uni-app";
import {AddressEntity} from "@/entitys/nEntity";
import UserStorage from "@/storage/UserStorage";

const props = defineProps({
    //用户id 后台拿数据用
    userId: String,
    //是否点击选择一个地址并返回, 用户下单时使用
    choose: String,
})
const dataSource = ref<AddressEntity[]>([]);
//初始化地址
//获取默认地址
const init = async () => {
    let id = UserStorage.getUserId()
    await service({url: '/address', method: 'GET', data: {id: id}}).then(res => {
        dataSource.value = res.data.data
    })
}
onShow(() => {
    init()
})
//选择地址 - 下单时的地址选择 -返回最新选择的地址信息`
const handlerSelectAddress = (address: AddressEntity) => {
    if (props.choose == '1') {
        uni.$emit('chooseAddressOnOrder', address)
        uni.navigateBack({
            delta: 1
        });
    }
}
//地址更新
uni.$on('eAddressUpdate', result => {
    Object.assign(dataSource.value.filter(i => i.id == result.id)[0], result)
})
//地址新增
uni.$on('eAddressSave', result => {
    dataSource.value.push(result)
})
//地址删除
uni.$on('eAddressRemove', result => {
    dataSource.value = dataSource.value.filter(i => i.id != result)
})

//修改地址
const edit = (value: any) => {
    uni.navigateTo({
        url: '/pages/address/new-address?address=' + JSON.stringify(value)
    })
}
//新增地址
const add = () => {
    uni.navigateTo({
        url: '/pages/address/new-address'
    })
}
onUnmounted(() => {
    uni.$off('eAddressUpdate')
    uni.$off('eAddressSave')
    uni.$off('eAddressRemove')
})
</script>

<style scoped>
.address-new-button {
    position: fixed;
    bottom: 50rpx;
    z-index: 999;
    width: 100%;
    margin-top: 20rpx;
    margin-bottom: 20rpx;
}

.address-box {
    display: flex;
    justify-content: space-between;
    width: 100%;
}

.address-default-and-edit {
    display: flex;
    flex-direction: row;
    justify-content: space-between;
}
</style>
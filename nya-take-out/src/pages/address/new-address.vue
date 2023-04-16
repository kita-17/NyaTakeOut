<template>
    <view class="address-container">
        <!--地图-->
        <map style="width: 100%; height: 256rpx;"/>
        <uni-card margin="10">
            <!--省市县地址的选择与显示-->
            <view class="address-content">
                <text class="address-detail-text">地址</text>
                <view @click="handlerShowAddressPicker">
                    <text v-if="showAddress == null" class="select-address">
                        选择收货地址
                    </text>
                    <text v-else class="select-address">
                        {{ showAddress }}
                    </text>
                </view>
            </view>
            <uni-forms :modelValue="currentAddress">
                <uni-forms-item label="门牌号" name="hobby">
                    <uni-easyinput v-model="currentAddress.detail" :auto-height="true" :inputBorder="false"
                                   placeholder="填写详细地址, 门牌号等"/>
                </uni-forms-item>
                <uni-forms-item label="收货人" name="name">
                    <uni-easyinput v-model="currentAddress.consignee" :inputBorder="false"
                                   placeholder="请填写收货人姓名"/>
                </uni-forms-item>
                <uni-forms-item label="手机号" name="name">
                    <uni-easyinput v-model="currentAddress.phone" :inputBorder="false"
                                   placeholder="请填写正确的收货人手机号码"/>
                </uni-forms-item>
            </uni-forms>
        </uni-card>
        <view class="save-button">
            <button class="del-button-box" @click="handlerRemove">删除</button>
            <button class="save-button-box" @click="handlerSubmit">保存</button>
        </view>
        <!--地址选择器 > 省市县-->
        <pick-address ref="AddressPickerInstance"></pick-address>
        <nya-message ref="msg"/>
    </view>
</template>

<script lang="ts" setup>
import PickAddress from "@/uni_modules/john-pickAddress/components/john-pickAddress/pickAddress.vue";
import {onUnmounted, reactive, ref} from "vue";
import UniCard from "@/uni_modules/uni-card/components/uni-card/uni-card.vue";
import {service} from "@/api/api";
import {AddressEntity} from "@/entitys/nEntity";
import UserStorage from "@/storage/UserStorage";
import {onShow} from "@dcloudio/uni-app";
import NyaMessage from "@/components/NyaMessage.vue";

const props = defineProps({
    address: {
        type: String,
        default: null
    }
})
let currentAddress = reactive<AddressEntity>({
    userId: "",
    cityCode: "",
    cityName: "",
    consignee: "",
    default: false,
    detail: "",
    districtCode: "",
    districtName: "",
    id: "",
    phone: "",
    provinceCode: "",
    provinceName: ""
})
const showAddress = ref()
const AddressPickerInstance = ref()
const msg = ref();

const handlerShowAddressPicker = () => {
    AddressPickerInstance.value.onOpen()
}
//提交保存
const handlerSubmit = () => {
    currentAddress.userId = UserStorage.getUserId();
    if (currentAddress.provinceName.length > 0 && currentAddress.cityName.length > 0 && currentAddress.districtName.length > 0 &&
        currentAddress.consignee.length > 0 && currentAddress.detail.length > 0 && currentAddress.phone.length > 0) {
        let state = false;
        if (currentAddress.id != '' && props.address != null) { //修改地址的操作
            //新数据返回给前页面 - 保存
            service({url: '/address', method: 'put', data: currentAddress}).then(
                res => {
                    if (res.data.code == 200) {
                        uni.$emit('eAddressUpdate', currentAddress)
                        state = true
                        uni.navigateBack({
                            delta: 1
                        })
                    } else {
                        msg.value.error(res.data.msg)
                    }
                }
            )
        } else { //新增地址
            service({url: '/address', method: 'post', data: currentAddress}).then(
                res => {
                    if (res.data.code == 200) {
                        currentAddress.id = res.data.data
                        uni.$emit('eAddressSave', currentAddress)
                        state = true
                        uni.navigateBack({
                            delta: 1
                        })
                    } else {
                        msg.value.error(res.data.msg)
                    }
                }
            )
        }
        //根据后台反应再决定跳转
        if (state) {
            uni.navigateBack({
                delta: 1
            })
        }
    } else {
        msg.value.error('请输入完整的地址信息');
    }
}
//提交删除
const handlerRemove = () => {
    service({url: '/address', method: 'delete', data: currentAddress}).then(
        res => {
            if (res.data.code == 200) {
                uni.$emit('eAddressRemove', currentAddress.id)
                //根据后台反应再决定跳转
                uni.navigateBack({
                    delta: 1
                })
            } else {
                msg.value.error(res.data.msg)
            }
        }
    )
}
//是否默认
const switch2Change = (e: any) => {
    currentAddress.default = e.detail.value
}
//地址选择器更新触发
uni.$on('addressUpdate', result => {
    currentAddress.provinceName = result.province.name
    currentAddress.cityName = result.city.name
    currentAddress.districtName = result.district.name
    currentAddress.provinceCode = result.province.code
    currentAddress.cityCode = result.city.code
    currentAddress.districtCode = result.district.code
    showAddress.value = currentAddress.provinceName + currentAddress.cityName + currentAddress.districtName
})
onShow(() => {
    if (props.address != null) {
        currentAddress = JSON.parse(props.address);
        // currentAddress.id = data.id
        // currentAddress.provinceName = data.provinceName
        // currentAddress.cityName = data.cityName
        // currentAddress.districtName = data.districtName
        // currentAddress.provinceCode = data.provinceCode
        // currentAddress.cityCode = data.cityCode
        // currentAddress.districtCode = data.districtCode
        // currentAddress.detail = data.detail
        // currentAddress.consignee = data.consignee
        // currentAddress.phone = data.phone
        // currentAddress.default = data.default
        showAddress.value = currentAddress.provinceName + currentAddress.cityName + currentAddress.districtName
    }
})
onUnmounted(() => {
    uni.$off('addressUpdate')
})
</script>

<style scoped>
.address-detail-input {
    margin: 15rpx;
    display: flex;
    padding-top: 13rpx;
    padding-bottom: 13rpx;
    width: 100%;
}

.address-detail-text {
    display: flex;
    padding-right: 15rpx;
    align-items: center;
    justify-content: center;
    font-weight: bold;
}

.address-content {
    margin: 20rpx;
    padding: 12rpx;
    height: 57rpx;
    display: flex;
    align-items: center;
}

.address-detail-under-line {
    width: 450rpx;
    border-bottom: 1px solid #c9c9c9;
}

.select-address {
    padding-left: 32rpx;
    color: #4bacff;
    font-weight: bold;
    align-items: center;
    display: flex;
}

.save-button {
    display: flex;
    margin-top: 64rpx;
    margin-bottom: 64rpx;
}

.save-button-box {
    width: 40%;
    color: #FFFFFF;
    background-color: #4bacff;
}

.del-button-box {
    width: 40%;
    color: #FFFFFF;
    background-color: #ff3551;
}
</style>
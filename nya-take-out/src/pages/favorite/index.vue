<template>
    <view v-for="(store, index) in dataSource" :key="index" class="store-list-main">
        <uni-card :is-full="true" :is-shadow="false">
            <view class="store-content">
                <view style="display: flex" @click="toStore(store)">
                    <image class="store-icon" src="../../static/banner.png"/>
                    <view class="store-name-desc">
                        <text class="store-name">{{ store.title }}</text>
                        <text class="store-desc">{{ store.description }}</text>
                    </view>
                </view>
                <view class="un-fav-box">
                    <button class="un-fav-btn" @click="unFav(store.id)">取消收藏</button>
                </view>
            </view>
        </uni-card>
    </view>
    <view class="fav-fill"/>
    <view class="fav-footer"/>
</template>

<script lang="ts" setup>
import {ref} from "vue";
import UniCard from "@/uni_modules/uni-card/components/uni-card/uni-card.vue";
import {onShow} from "@dcloudio/uni-app";
import {service} from "@/api/api";
import UserStorage from "@/storage/UserStorage";

const dataSource = ref<{ id: any, title: any, description: any, iconUrl: any }[]>([]);
let uid = UserStorage.getUserId()
const toStore = (store: any) => {
    uni.navigateTo({
        url: '/pages/store/index?id=' + store.id + '&title=' + store.name
    })
}

const unFav = (id: any) => {
    service({url: '/store/favorite/remove', method: 'POST', data: {userId: uid, storeId: id}}).then(
        res => {
            if (res.data.code == 200) {
                dataSource.value = dataSource.value.filter(i => i.id != id)
            }
        }
    )
}
onShow(() => {
    service({url: '/store/favorite/list', method: 'GET', data: {userId: uid}}).then(res => {
        if (res.data.code == 200) {
            dataSource.value = res.data.data
        }
    })
})
</script>
<style scoped>
.store-icon {
    height: 128rpx;
    width: 128rpx;
    border-radius: 8px;
    margin: 16rpx;
}

.store-content {
    display: flex;
    flex-direction: row;
}

.store-name-desc {
    display: flex;
    flex-direction: column;
}

.store-name {
    margin: 12rpx;
    padding: 10rpx;
    font-size: 37rpx;
    font-weight: bold;
}

.store-desc {
    margin: 0 10rpx 0 10rpx;
    padding: 0 10rpx 0 10rpx;
    overflow: hidden;
    white-space: nowrap;
    text-overflow: ellipsis;
    width: 270rpx;
}

.fav-footer {
    position: fixed;
    right: 0;
    left: 0;
    bottom: 0;
    z-index: 1000;
    background-color: #ff743c;
    height: 32px;
}

.fav-fill {
    height: 48rpx;
}

.un-fav-box {
    display: flex;
    align-items: center;
    justify-content: center;
    position: relative;
    left: 100rpx;
}

.un-fav-btn {
    border-radius: 32px;
    font-size: 20rpx;
    color: #FFFFFF;
    background-color: #e43d33;
}
</style>
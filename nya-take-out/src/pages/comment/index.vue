<template>
    <view v-for="(item, index) in dataSource" :key="index">
        <uni-card :border="false">
            <view style="display: flex;flex-direction: column">
                <view style="display: flex;margin: 16rpx 0 16rpx 0; justify-content: space-between">
                    <view style="display: flex;justify-content: center;align-content: center">
                        <image :lazy-load="true" :src="getImageUrl(item.storeIcon)" class="comment-user-avatar"/>
                        <text class="comment-user-name"> {{ item.storeName }}</text>
                    </view>
                    <text class="comment-date"> {{ item.createDate }}</text>
                </view>
                <view class="comment-image-box">
                    <view v-for="(image, eI) in item.imageList" :key="eI">
                        <image
                                :src="getImageUrl(image.fileName)"
                                class="comment-image"
                        />
                    </view>
                </view>
            </view>
            <view class="comment-content-base">
                {{ item.content }}
            </view>
            <view class="comment-content-footer">
                <uni-icons size="30" type="hand-up-filled"/>
                <text> {{ item.likes }}</text>
            </view>
        </uni-card>
    </view>
    <view class="fav-fill"/>
    <view class="fav-footer"/>
</template>

<script lang="ts" setup>
import UniCard from "@/uni_modules/uni-card/components/uni-card/uni-card.vue";
import {ref} from "vue";
import {service} from "@/api/api";
import UserStorage from "@/storage/UserStorage";

const dataSource = ref([]);
let uid = UserStorage.getUserId();
service({
    url: '/comment/user',
    method: 'GET',
    data: {userId: uid}
}).then(res => {
    if (res.data.code == 200) {
        dataSource.value = res.data.data
    }
})
const getImageUrl = (url: string) => {
    return 'http://127.0.0.1:8080/image/download?name=' + url
}
</script>

<style scoped>
.fav-footer {
    position: fixed;
    right: 0;
    left: 0;
    bottom: 0;
    z-index: 1000;
    background-color: #7fff99;
    height: 32px;
}

.fav-fill {
    height: 48rpx;
}

.comment-user-avatar {
    height: 75rpx;
    width: 75rpx;
    border-radius: 64rpx;
    margin: 0 16rpx 0 16rpx;
}

.comment-user-name {
    margin: 0 16rpx 0 16rpx;
    font-weight: 700;
    color: #000000;
}

.comment-date {
    margin: 0 16rpx 0 16rpx;
}

.comment-content-base {
    height: auto;
    margin: 16rpx;
    color: #000000;
}

.comment-content-footer {
    height: 32rpx;
    display: flex;
    justify-content: flex-end;
    margin: 16rpx;
}

.comment-image {
    height: 128rpx;
    width: 128rpx;
    margin: 3px;
    border-radius: 16px;
}

.comment-image-box {
    display: flex;
    flex-direction: row;
}
</style>
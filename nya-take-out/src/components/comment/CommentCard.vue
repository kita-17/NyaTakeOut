<template>
    <view v-for="(item,index) in dataSource" :key="index">
        <uni-card :border="false">
            <view class="comment-head">
                <view style="display: flex;flex-direction: column">
                    <view style="display: flex;margin: 16rpx 0 16rpx 0; justify-content: space-between">
                        <view style="display: flex;justify-content: center;align-content: center">
                            <image :lazy-load="true" class="comment-user-avatar" src="../../static/home_select.png"/>
                            <text class="comment-user-name"> {{ item.userName }}</text>
                        </view>
                        <text class="comment-date"> {{ item.createDate }}</text>
                    </view>
                    <view class="comment-image-box">
                        <view v-for="(image, eI) in item.imageList" :key="eI">
                            <image
                                    :src="getImageUrl(image.fileName)"
                                    class="comment-image"
                                    @click="previewImage(getImageUrl(image.fileName))"
                            />
                        </view>
                    </view>
                </view>
            </view>
            <view class="comment-content-base">
                {{ item.content }}
            </view>
            <view class="comment-content-footer" @click="userLike(item)">
                <uni-icons :type="item.liked ? 'hand-up-filled' : 'hand-up'" size="30"/>
                <text> {{ item.likes }}</text>
            </view>
        </uni-card>
    </view>
    <view style="height: 64rpx;"/>
</template>
<script lang="ts" setup>
import UserStorage from "@/storage/UserStorage";
import {service} from "@/api/api";

const props = defineProps({
    dataSource: {
        type: Array,
        default: []
    }
});
let uid = UserStorage.getUserId();

const userLike = (comment: any) => {
    service({url: '/comment/like', method: 'POST', data: {userId: uid, id: comment.id}}).then(res => {
        if (res.data.code == 200) {
            if (res.data.data) {
                comment.liked = true
                comment.likes += 1
            } else {
                comment.liked = false
                comment.likes -= 1
            }
        }
    })
}
const getImageUrl = (url: string) => {
    return 'http://127.0.0.1:8080/image/download?name=' + url
}

const previewImage = (file: string) => {
    uni.previewImage({
        urls: [file],
        longPressActions: {
            itemList: ['发送给朋友', '保存图片', '收藏'],
            success: function (data) {
                console.log('选中了第' + (data.tapIndex + 1) + '个按钮,第' + (data.index + 1) + '张图片');
            }
        }
    });
}


</script>
<style>
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
<template>
    <view>
        <uni-card :is-full="true" @click="test">
            <uni-row>
                <uni-col :span="8">
                    <image
                            :src="bannerUrl"
                            style="width: 100px; height: 100px; background-color: #eeeeee;margin-right: 15px"
                    />
                </uni-col>
                <uni-col :span="16">
                    <uni-row>
                        <uni-col :span="16">
                            <text style="font-size: 20px">{{ storeName }}</text>
                        </uni-col>
                        <uni-col>
                            <text class="desc-box">{{ description }}</text>
                        </uni-col>
                    </uni-row>
                </uni-col>
            </uni-row>
        </uni-card>
    </view>
</template>

<script lang="ts" setup>
const props = defineProps({
    storeName: {
        type: String
    },
    description: {
        type: String
    },
    bannerUrl: {
        type: String
    },
    storeId: {
        type: String || Number,
    }
});
const test = () => {
    uni.navigateTo({
        url: '/pages/store/index?storeTitle=' + props.storeName + "&storeId=" + props.storeId + "&storeDescription=" + props.description
    });
}
</script>

<style scoped>
.desc-box {
    width: 320rpx;
    color: #333333;
    display: -webkit-box;
    -webkit-line-clamp: 3;
    -webkit-box-orient: vertical;
    overflow: hidden;
}
</style>
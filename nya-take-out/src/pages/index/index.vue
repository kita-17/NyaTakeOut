<template>
    <view v-for="(store, index) in dataSource" :key="index" class="content">
        <!--		<adaption-nav-bar/>-->
        <store-card
                :banner-url="'../../static/logo.png'"
                :description="store.description"
                :store-id="store.id"
                :store-name="store.title"
        />
    </view>
</template>

<script lang="ts" setup>
import StoreCard from "@/components/StoreCard/index.vue";
import {ref} from "vue";
import {service} from "@/api/api";
import {onShow} from "@dcloudio/uni-app";
import UserStorage from "@/storage/UserStorage";

const dataSource = ref()

onShow(() => {
    let uid = UserStorage.getUserId()
    service({url: '/store/list', method: 'GET', data: {userId: uid}}).then(res => {
        if (res.data.code == 200) {
            dataSource.value = res.data.data
        }
    })
})
</script>

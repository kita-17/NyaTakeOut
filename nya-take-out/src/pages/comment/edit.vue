<template>
    <view class="comment-content">
        <uni-easyinput
                v-model="comment"
                :autoHeight="true"
                :inputBorder="false"
                maxlength="280"
                placeholder="请输入评论"
                type="textarea"
        />
    </view>
    <uni-card>
        <uni-file-picker
                ref="ImageUpload"
                v-model="imageFiles"
                :auto-upload="false"
                file-extname="png,jpg"
                fileMediatype="image"
                limit="9"
                mode="grid"
                @select="selectImage"
        />
    </uni-card>
    <button class="submit-btn" @click="submitUpload">发布</button>
</template>

<script lang="ts" setup>
import {ref} from "vue";
import {service} from "@/api/api";
import UserStorage from "@/storage/UserStorage";

const props = defineProps(['storeId', 'orderId'])
const comment = ref(null)
const imageFiles = ref<any[]>([])
const ImageUpload = ref();

const selectImage = (image: any) => {
    let file = image.tempFiles[0];
    uni.uploadFile({
        url: 'http://127.0.0.1:8080/file/upload', //上传图片的后端接口
        filePath: file.path,
        name: 'file',
        success: res => {
            let data = JSON.parse(res.data);
            imageFiles.value.push({fileName: data.data})
        }
    })
}
const uid = UserStorage.getUserId()
const submitUpload = () => {
    if (comment.value == null) {
        uni.showToast({
            title: '请编写评论',
            icon: 'error'
        })
        return;
    }
    service({
        url: '/comment',
        method: 'POST',
        data: {
            userId: uid,
            storeId: props.storeId,
            orderId: props.orderId,
            content: comment.value,
            imageList: imageFiles.value
        }
    }).then(res => {
        if (res.data.code == 200) {
            uni.navigateBack({
                delta: 1
            })
        } else {
            uni.showToast({
                title: '发生错误',
                icon: 'error'
            })
        }
    })
}
</script>

<style scoped>
.comment-content {
    margin: 16px;
    height: auto;
}

.submit-btn {
    width: 75%;
    background-color: #3e88ff;
    color: #FFFFFF;
    border-radius: 16px;
}
</style>
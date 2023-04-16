<template>
	<!--提示框-->
    <uni-popup ref="message" type="message">
        <uni-popup-message :duration="2000" :message="messageText" :type="msgType"/>
    </uni-popup>
</template>

<script lang="ts">
import {defineComponent, ref} from "vue";

export default defineComponent({
    setup() {
        const message = ref()
        const msgType = ref()
        const messageText = ref()
        const info = (msg: string) => {
            msgType.value = 'info'
            messageText.value = msg
            message.value.open()
        }
        const error = (msg: string) => {
            msgType.value = 'error'
            messageText.value = msg
            message.value.open()
        }
        return {
            message, messageText, msgType, info, error
        }
    }
})
</script>
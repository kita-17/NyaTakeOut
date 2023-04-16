<template>
    <view class="header-content">
        <view :style="{height: stateBarHeight + 'px'}" class="header-position"></view>
        <view :style="{height: tabBarHeight + 'px'}" class="header-tabBar">
            <slot/>
        </view>
    </view>
</template>

<script lang="ts">
import {defineComponent} from "vue";

export default defineComponent({
    data() {
        return {
            windowWidth: 0,
            stateBarHeight: 0,
            tabBarHeight: 50,
        };
    },
    created() {
        // 获取设备信息
        this.getStateBarHeight();
        // 获取小程序右侧悬浮按钮
        // #ifdef MP-WEIXIN
        this.getMenuButtonInfo();
        // #endif
    },
    methods: {
        getStateBarHeight: function () {
            let info = uni.getSystemInfoSync();
            this.stateBarHeight = info.statusBarHeight;
            this.windowWidth = info.windowWidth;
        },
        getMenuButtonInfo() {
            let menuButtonInfo = uni.getMenuButtonBoundingClientRect();
            let paddingSize = menuButtonInfo.top - this.stateBarHeight;
            this.tabBarHeight = (menuButtonInfo.bottom - this.stateBarHeight) + paddingSize;
            this.windowWidth = menuButtonInfo.left;
        }
    }
});
</script>

<style lang="scss">
.header-content {
  background: $uni-color-error;

  .header-tabBar {
    width: 100%;
    display: flex;
    align-items: center;
  }
}

.header-position {
  position: fixed;
  left: 0;
  top: 0;
  border: 1px solid red;
  width: 100%;
}
</style>
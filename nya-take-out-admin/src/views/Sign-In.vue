<template>
    <div class="sign-in">
        <a-row :gutter="[24,24]" align="middle" justify="space-around" type="flex">
            <!-- Sign In Form Column -->
            <a-col :lg="{span: 12, offset: 0}" :md="12" :span="24" :xl="{span: 6, offset: 2}" class="col-form">
                <h1 class="mb-15">登录</h1>
                <h5 class="font-regular text-muted">输入您的账号与密码登录</h5>

                <!-- Sign In Form -->
                <a-form
                        id="components-form-demo-normal-login"
                        :model="formState"
                        autocomplete="off"
                        class="login-form"
                        @finish="handleSubmit"
                        @finishFailed="handleSubmitFailed"
                >
                    <a-form-item :colon="false" class="mb-10">
                        <a-input v-model:value="formState.username" placeholder="Email"/>
                    </a-form-item>

                    <a-form-item :colon="false" class="mb-5">
                        <a-input v-model:value="formState.password" placeholder="Password" type="password"/>
                    </a-form-item>

                    <a-form-item class="mb-10">
                        <a-switch v-model:checked="formState.remember"/>
                        记住账号
                    </a-form-item>

                    <a-form-item>
                        <a-button block class="login-form-button" html-type="submit" type="primary">
                            登录
                        </a-button>
                    </a-form-item>
                </a-form>
                <!-- / Sign In Form -->

                <p class="font-semibold text-muted">Don't have an account?
                    <router-link class="font-bold text-dark" to="/sign-up">Sign Up</router-link>
                </p>
            </a-col>
            <!-- / Sign In Form Column -->

            <!-- Sign In Image Column -->
            <a-col :lg="12" :md="12" :span="24" :xl="12" class="col-img">
                <img alt="" src="/images/img-signin.jpg">
            </a-col>
            <!-- Sign In Image Column -->

        </a-row>

    </div>
</template>

<script lang="ts">
import {defineComponent, reactive} from "vue";
import {SignFormState} from "../entity/SignFormState";
import employeeAPI from "../api/employeeAPI";
import storage from "../common/storage";
import {message} from "ant-design-vue";

export default defineComponent({
    setup() {
        const formState = reactive<SignFormState>({
            username: '',
            password: '',
            remember: true,
        });

        const handleSubmit = (values: any) => {
            employeeAPI.login({
                username: formState.username,
                password: formState.password,
            }).then(res => {
                if (res.data.code == 200) {
                    storage.set("authorization", res.data.data)
                    storage.setStoreId(res.data.data.storeId || '')
                    window.location.href = '/'
                } else if (res.data.code == 400) {
                    message.info(res.data.msg)
                }
            })
        }

        const handleSubmitFailed = (values: any) => {
            console.log("Failed: ", formState);
        }
        return {
            formState,
            handleSubmit,
            handleSubmitFailed
        }
    },
})

</script>

<style lang="scss">
body {
  background-color: #ffffff;
}
</style>
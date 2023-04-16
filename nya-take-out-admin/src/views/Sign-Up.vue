<template>
	<div class="sign-in">
		<a-row :gutter="[24,24]" align="middle" justify="space-around" type="flex">
			<!-- Sign In Image Column -->
			<a-col :lg="6" :md="6" :span="24" :xl="6" class="col-img">
				<img alt="" src="/images/img-signup.jpg">
			</a-col>
			<!-- Sign In Image Column -->
			<!-- Sign In Form Column -->
			<a-col :lg="{span: 12, offset: 0}" :md="12" :span="24" :xl="{span: 6, pull: 3}" class="col-form">
				<h1 class="mb-15">注册</h1>
				<h5 class="font-regular text-muted">输入您的用户名, 邮箱, 密码注册</h5>

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
						<a-input v-model:value="formState.username" placeholder="UserName"/>
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
							注册
						</a-button>
					</a-form-item>
				</a-form>
				<!-- / Sign In Form -->

				<p class="font-semibold text-muted">已经有帐号了?
					<router-link class="font-bold text-dark" to="/sign-in">点击登录</router-link>
				</p>
			</a-col>
			<!-- / Sign In Form Column -->
		</a-row>

	</div>
</template>

<script lang="ts">
import {defineComponent, reactive} from "vue";
import {SignFormState} from "../entity/SignFormState";

export default defineComponent({
	setup() {
		const formState = reactive<SignFormState>({
			username: '',
			password: '',
			remember: true,
		});

		const handleSubmit = (values: any) => {
			console.log("success: ", formState.username + " - " + formState.password + " - " + formState.remember);
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
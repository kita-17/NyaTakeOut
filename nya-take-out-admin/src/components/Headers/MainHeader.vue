<template>

	<!-- Main Sidebar -->
	<!-- Layout Header -->
	<a-layout-header>
		<a-row type="flex">

			<!-- Header Breadcrumbs & Title Column -->
			<a-col :md="6" :span="24">

				<!-- Header Breadcrumbs -->
				<a-breadcrumb>
					<a-breadcrumb-item>
						<router-link to="/"> Pages</router-link>
					</a-breadcrumb-item>
					<a-breadcrumb-item>{{ this.$route.name }}</a-breadcrumb-item>
				</a-breadcrumb>
				<!-- / Header Breadcrumbs -->

				<!-- Header Page Title -->
				<div class="ant-page-header-heading">
					<span class="ant-page-header-heading-title">{{ this.$route.name }}</span>
				</div>
				<!-- / Header Page Title -->

			</a-col>
			<!-- / Header Breadcrumbs & Title Column -->

			<!-- Header Control Column -->
			<a-col :md="18" :span="24" class="header-control">
				<router-link v-if="!authorization_verification" class="btn-sign-in" to="/sign-in"
				             @click="e => e.preventDefault()">
					<svg fill="none" height="32" viewBox="0 0 20 20" width="32" xmlns="http://www.w3.org/2000/svg">
						<path clip-rule="evenodd" d="M18 10C18 14.4183 14.4183 18 10 18C5.58172 18 2 14.4183 2 10C2 5.58172 5.58172 2 10 2C14.4183 2 18 5.58172 18 10ZM12 7C12 8.10457 11.1046 9 10 9C8.89543 9 8 8.10457 8 7C8 5.89543 8.89543 5 10 5C11.1046 5 12 5.89543 12 7ZM9.99993 11C7.98239 11 6.24394 12.195 5.45374 13.9157C6.55403 15.192 8.18265 16 9.99998 16C11.8173 16 13.4459 15.1921 14.5462 13.9158C13.756 12.195 12.0175 11 9.99993 11Z"
						      fill="#111827"
						      fill-rule="evenodd"/>
					</svg>
					<span>登录</span>
				</router-link>
				<p style="margin: 0;word-spacing: 10px;">
					当前账号: {{ profile.username }}
					<a @click="logout">退出登录</a>
				</p>
				<!-- / Header Control Buttons -->
			</a-col>
			<!-- / Header Control Column -->
		</a-row>
	</a-layout-header>
	<!--  /Layout Header -->
	<!-- / Main Sidebar -->
</template>

<script>

import {defineComponent} from "vue";
import storage from "@/common/storage";

export default defineComponent({
	setup() {
		const authorization_verification = () => {
			const auth = storage.get("authorization", "")
			return (auth || auth !== '' || auth !== null);
		}
		const profile = storage.get("authorization", "");

		const logout = () => {
			storage.set("authorization", null);
			window.location = "/sign-in"
		}

		return {
			authorization_verification,
			profile, logout
		}
	}
})
</script>

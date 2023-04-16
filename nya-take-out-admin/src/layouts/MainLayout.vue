<template>
	<div>
		<!-- Dashboard Layout -->
		<a-layout id="layout-dashboard" class="layout-dashboard">
			<!-- Main Sidebar -->
			<DashboardSidebar/>
			<a-layout>
				<DashboardHeader/>
				<a-layout-content>
					<router-view/>
				</a-layout-content>
				<DashboardFooter/>
			</a-layout>
		</a-layout>
	</div>
</template>

<script lang="ts">

import DashboardSidebar from '../components/Sidebars/DashboardSidebar.vue';
import DashboardHeader from '../components/Headers/MainHeader.vue';
import DashboardFooter from '../components/Footers/MainFooter.vue';
import {defineComponent} from "vue";

export default defineComponent({
	components: {
		DashboardSidebar,
		DashboardHeader,
		DashboardFooter,
	},
	computed: {
		// Sets layout's element's class based on route's meta data.
		layoutClass() {
			return this.$route.meta.layoutClass;
		}
	},
})

</script>

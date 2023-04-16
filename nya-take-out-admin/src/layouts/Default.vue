<template>
	<div>
		<!-- Default Layout -->
		<a-layout id="layout-default" :class="[layoutClass]" class="layout-default">

			<!-- Layout Header ( Navbar ) -->
			<DefaultHeader></DefaultHeader>
			<!-- / Layout Header ( Navbar ) -->
			<!-- Page Content -->
			<a-layout-content>
				<router-view/>
			</a-layout-content>
			<!-- / Page Content -->
			<!-- Layout Footer -->
			<DefaultFooter></DefaultFooter>
			<!-- / Layout Footer -->
		</a-layout>
		<!-- / Default Layout -->
	</div>
</template>

<script>

import DefaultHeader from '../components/Headers/DefaultHeader.vue' ;
import DefaultFooter from '../components/Footers/DefaultFooter.vue' ;
import {defineComponent} from "vue";

export default defineComponent({
	components: {
		DefaultHeader,
		DefaultFooter,
	},
	data() {
		return {}
	},
	computed: {
		// Sets layout's element's class based on route's meta data.
		layoutClass() {
			return this.$route.meta.layoutClass;
		}
	},
})

</script>
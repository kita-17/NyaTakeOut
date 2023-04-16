<template>
	<a-card style="margin: 5px 0px 5px 0px">
		<h5 class="category_message">
			{{ text }}
		</h5>
	</a-card>
</template>

<script lang="ts">
import {defineComponent} from "vue";

export default defineComponent({
	props: {
		text: null
	}
})
</script>

<style scoped>
.category_message {
	word-spacing: 10px;
	display: flex;
	line-height: 100%;
	/*this centers the text vertically*/
	align-items: center;
	font-size: 1.2rem;
	margin: 0 0 0 0;
}
</style>
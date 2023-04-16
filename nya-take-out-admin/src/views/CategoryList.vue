<template>
	<CardMessage :text="drag_sort_text"/>
	<CardCategoryList/>
</template>

<script lang="ts">
import {defineComponent} from "vue";
import CardCategoryList from "../components/Cards/list/CardCategoryList.vue";
import CardMessage from "../components/Cards/CardMessage.vue";

export default defineComponent({
	components: {CardMessage, CardCategoryList},
	setup() {
		const drag_sort_text = "此列表的顺序将决定移动端的展示顺序"
		return {
			drag_sort_text
		}
	}
})
</script>
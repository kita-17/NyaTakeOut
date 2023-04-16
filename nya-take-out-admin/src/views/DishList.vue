<template>
	<CardDishList/>
</template>

<script lang="ts">
import {defineComponent} from "vue";
import CardDishList from "../components/Cards/list/CardDishList.vue";

export default defineComponent({
	name: "DishList",
	components: {CardDishList}
})
</script>

<style scoped>

</style>
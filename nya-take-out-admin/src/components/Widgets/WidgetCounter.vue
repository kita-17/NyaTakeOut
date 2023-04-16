<template>

	<a-card :bordered="false" class="widget-1">
		<a-statistic
			:class="'text-' + status"
			:precision="0"
			:prefix="prefix"
			:suffix="suffix"
			:title="title"
			:value="value"
			class="text-success"
		>
		</a-statistic>
		<div class="icon" v-html="icon"></div>
	</a-card>

</template>

<script>

export default ({
	props: {
		title: {
			type: String,
			default: "",
		},
		value: {
			type: Number,
			default: 0,
		},
		prefix: {
			type: String,
			default: "",
		},
		suffix: {
			type: String,
			default: "",
		},
		icon: {
			type: String,
			default: "",
		},
		status: {
			type: String,
			default: "success",
		},
	},
	data() {
		return {}
	},
})

</script>
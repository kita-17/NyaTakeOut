<template>
	<a-result status="404" sub-title="Sorry, the page you visited does not exist." title="404">
		<template #extra>
			<router-link class="ant-btn ant-btn-primary" to="/">Back Home</router-link>
		</template>
	</a-result>
</template>
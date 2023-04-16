<template>
	<a-form
		:model="dishFormState"
		autocomplete="off"
		name="newDishForm"
	>
		<a-form-item
			label="套餐名称"
			name="setmealName"
		>
			<a-input v-model:value="dishFormState.name" placeholder="请输入套餐名称"/>
		</a-form-item>

		<a-form-item
			label="分类"
			name="category"
		>
			<a-select
				ref="select"
				v-model:value="dishFormState.category"
				:options="categoryOptions"
				style="width: 120px"
			/>
		</a-form-item>

		<a-form-item
			label="价格"
			name="price"
		>
			<a-input v-model:value="dishFormState.price" type="number"/>
		</a-form-item>

		<a-form-item
			label="图片"
			name="ImgIcon"
		>
			<a-upload
				action="https://www.mocky.io/v2/5cc8019d300000980a055e76"
				name="file"
			>
				<a-button>
					<upload-outlined/>
					点击上传
				</a-button>
			</a-upload>
		</a-form-item>

		<a-form-item
			label="介绍"
			name="description"
		>
			<a-input v-model:value="dishFormState.description" placeholder="请输入菜品介绍"/>
		</a-form-item>
		<a-space :size="32" style="margin: 10px 0 10px 0">
			是否售卖:
			<a-switch v-model:checked="dishFormState.status"/>
		</a-space>

		<a-form-item v-show="this.formType === 1" name="new_dish">
			<a-button block style="background-color: #52C41A;border-color: #d9d9d9;" type="primary" @click="add">
				新增套餐
			</a-button>
		</a-form-item>
		<a-form-item v-show="this.formType === 0" name="save_dish">
			<a-button block type="primary" @click="save">保存套餐</a-button>
		</a-form-item>
	</a-form>
</template>

<script lang="ts">
import {defineComponent, reactive} from "vue";
import {UploadOutlined} from '@ant-design/icons-vue';

const flavor_options = reactive([
	{
		label: `Long Label 1`,
		value: 1
	},
	{
		label: `Long Label 2`,
		value: 2
	},
	{
		label: `Long Label 3`,
		value: 3
	},
	{
		label: `Long Label 4`,
		value: 4
	},
]);

export default defineComponent({
	props: {
		dishId: null,
		formType: null,
		dishFormState: null,
		categoryOptions: null,
		targetDataSource: null
	},
	components: {
		UploadOutlined,
	},
	setup() {
		return {
			flavor_options
		}
	},
	methods: {
		save() {
			this.$emit("save", this.dishFormState)
		},
		add() {
			this.$emit("add", this.dishFormState)
		}
	}
})
</script>
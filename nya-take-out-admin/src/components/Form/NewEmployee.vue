<template>
	<a-form
		ref="formRef"
		:hide-required-mark="true"
		:model="formState"
		:rules="rules"
		name="newEmployeeForm"
		@finish="handleFinish"
	>
		<!--账号密码-->
		<a-row :gutter="16">
			<a-col :span="12">
				<a-form-item
					label="账号"
					name="username"
				>
					<a-input v-model:value="formState.username"/>
				</a-form-item>
			</a-col>

			<a-col :span="12">
				<a-form-item
					label="密码"
					name="password"
				>
					<a-input v-model:value="formState.password"/>
				</a-form-item>
			</a-col>
		</a-row>
		<!--员工信息-->
		<a-row :gutter="16">
			<a-col :span="12">
				<a-form-item
					label="名字"
					name="name"
				>
					<a-input v-model:value="formState.name"/>
				</a-form-item>
			</a-col>

			<a-col :span="12">
				<a-form-item
					label="手机"
					name="phone"
				>
					<a-input v-model:value="formState.phone"/>
				</a-form-item>
			</a-col>
		</a-row>

		<!-- 权限与身份证ID -->
		<a-row :gutter="16">
			<a-col :span="12">
				<a-form-item
					label="身份证"
					name="eid"
				>
					<a-input v-model:value="formState.idNumber"/>
				</a-form-item>
			</a-col>

			<a-col :span="12">

			</a-col>
		</a-row>

		<!-- 其他 -->
		<a-row :gutter="16">
			<a-col>
				<a-form-item
					label="性别"
					name="sex"
				>
					<a-select
						ref="select"
						v-model:value="formState.sex"
						:options="sex_opt"
						style="width: 120px"
					/>
				</a-form-item>
			</a-col>
			<a-col>
				<a-form-item
					label="权限"
					name="permissions"
				>
					<a-select
						ref="select"
						v-model:value="formState.permissions"
						:options="per_opt"
						style="width: 120px"
					/>
				</a-form-item>
			</a-col>
			<a-col :offset="3">
				<a-button html-type="submit" style="width: 120px;" type="primary">提交</a-button>
			</a-col>
		</a-row>

	</a-form>
</template>

<script lang="ts">
import {defineComponent, reactive, ref} from "vue";
import {FormInstance, message, SelectProps} from "ant-design-vue";
import {Rule} from "ant-design-vue/es/form";
import Validate from "../../common/validate";
import {EmployeeEntity} from "../../entity/EmployeeEntity";
import EmployeeAPI from "../../api/employeeAPI";
import storage from "../../common/storage";

export default defineComponent({
	setup(props, ctx) {
		const per_opt = ref<SelectProps['options']>([
			{
				value: 3000,
				label: "管理员"
			},
			{
				value: 1,
				label: "员工"
			},
		])

		const sex_opt = ref<SelectProps['options']>([
			{
				value: 1,
				label: "男"
			},
			{
				value: 2,
				label: "女"
			},
		])
		const formRef = ref<FormInstance>();
		
		const formState = reactive<EmployeeEntity>({
            idNumber: null,
			storeId: storage.getStoreId(),
			id: "",
			key: "",
			name: "",
			password: "",
			permissions: 1,
			phone: "",
			sex: 1,
			status: true,
			username: ""
		})

		//账号输入检测
		let checkUserName = async (_rule: Rule, value: string) => {
			if (value === '') {
				return Promise.reject('请输入员工账号')
			} else if (value.toString().length > 20 || value.toString().length < 3) {
				return Promise.reject('员工账号长度应大于3 小于20')
			} else {
				return Promise.resolve()
			}
		}
		let checkPassword = async (_rule: Rule, value: string) => {
			if (value === '') {
				return Promise.reject('请输入员工密码')
			} else if (value.toString().length > 20 || value.toString().length < 3) {
				return Promise.reject('员工密码长度应大于3 小于20')
			} else {
				return Promise.resolve()
			}
		}
		let checkName = async (_rule: Rule, value: string) => {
			if (value === '') {
				return Promise.reject('请输入员工姓名')
			} else if (value.toString().length > 12) {
				return Promise.reject('员工名字过长, 请重新输入')
			} else {
				return Promise.resolve()
			}
		}
		//身份证检测
		let checkEID = async (_rule: Rule, value: number) => {
			if (!value) {
				return Promise.reject('请输入身份证号码')
			} else {
				if (Validate.validID(value) == false) {
					return Promise.reject('身份证号码错误, 请重新输入')
				} else {
					return Promise.resolve();
				}
			}
		}
		//手机号码检测
		let checkPhone = async (_rule: Rule, value: number) => {
			if (!value) {
				return Promise.reject('请输入手机号码')
			} else {
				if (Validate.checkPhone(value) == false) {
					return Promise.reject('手机号码错误, 请重新输入')
				} else {
					return Promise.resolve()
				}
			}
		}

		const rules: Record<string, Rule[]> = {
			username: [{required: true, validator: checkUserName, trigger: 'change'}],
			password: [{required: true, validator: checkPassword, trigger: 'change'}],
			name: [{required: true, validator: checkName, trigger: 'change'}],
            idNumber: [{required: true, validator: checkEID, trigger: 'change'}],
			phone: [{required: true, validator: checkPhone, trigger: 'change'}],
		};

		const handleFinish = async () => {
			EmployeeAPI.add(formState).then(res => {
				if (res.data.code == 200) {
					message.info(res.data.msg)
					ctx.emit("saveData", formState)
					formRef.value.resetFields();
				} else {
					message.error(res.data.msg)
				}
			}).catch(reason => {
				console.log(reason)
				message.warn("网络错误")
			})
		}
		return {
			per_opt, sex_opt, rules, formState, handleFinish, formRef
		}
	}
})
</script>
<style>
.ant-form-item-label {
	display: inline-block;
	width: 4em;
	text-align: justify;
	text-align-last: justify;
}

.ant-form-item-label :after {
	content: '';
	display: inline-block;
	width: 100%;
}
</style>
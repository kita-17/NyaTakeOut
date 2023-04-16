<template>
	<a-card>
		<template #extra>
			<a-button style="width: 150px" type="primary" @click="showEmployee">新增员工</a-button>
		</template>
		<a-table
			:columns="employeeListColumns"
			:data-source="dataSource"
			:pagination="{
				current: pageCurrent,
				defaultPageSize: 10,
				position: ['bottomCenter'],
				onChange: (pageNumber) => pageCurrent = pageNumber
			}"
		>
			<template #bodyCell="{column, text, record}">
				
				<!--支持修改的数据-->
				<template v-if="['name','phone', 'password'].includes(column.dataIndex)">
					<a-input
						v-if="editableData[record.key]"
						v-model:value="editableData[record.key][column.dataIndex]"
					/>
				</template>
				
				<!--显示账号状态-->
				<template v-if="column.dataIndex === 'status'">
					<a-switch
						v-if="editableData[record.key]"
						v-model:checked="switchData[record.key]"
						checked-children="启用"
						un-checked-children="禁用"
					/>
					<template v-else>
						{{ record.status === true ? '启用' : '禁用' }}
					</template>
				</template>
				
				<template v-if="column.dataIndex === 'sex'">
					{{ record.sex == 1 ? '男' : '女' }}
				</template>
				
				<template v-if="column.dataIndex === 'password'">
					<div v-if="!editableData[record.key]">
						点击右边的编辑按钮设置新密码
					</div>
				</template>
				
				<!--修改按钮-->
				<template v-if="column.dataIndex === 'operation'">
					<div class="editable-row-operations">
						<span v-if="editableData[record.key]">
							<a-typography-link @click="save(record.key)">保存</a-typography-link> |
							<a-popconfirm title="确定取消?" @confirm="cancel(record.key)"><a>取消</a></a-popconfirm>
						</span>
						<span v-else><a @click="edit(record.key)">编辑</a></span>
					</div>
				</template>
			
			</template>
		</a-table>
	</a-card>
	
	<a-modal
		v-model:visible="addEmployeeVisible"
		:mask="false"
		:width="750"
		footer=""
		title="新员工信息录入"
	>
		<new-employee @saveData="employeeFinish"/>
	</a-modal>

</template>

<script lang="ts">
import {defineComponent, reactive, ref, UnwrapRef} from 'vue';

import {SearchOutlined} from '@ant-design/icons-vue';

import {EmployeeEntity} from "../../../entity/EmployeeEntity";
import {cloneDeep} from "lodash-es";
import NewEmployee from "../../Form/NewEmployee.vue";

import {message} from 'ant-design-vue';
import EmployeeAPI from "../../../api/employeeAPI";
import storage from "../../../common/storage";

//员工列表数据
const dataSource = ref<EmployeeEntity[]>([]);
//新增员工表单是否可视
const addEmployeeVisible = ref(false);
//新增员工表单模型
const formState = reactive<EmployeeEntity>({
	id_number: null, id: "", key: "", name: "", password: "", permissions: 1, phone: "", sex: 1, status: true, username: "",storeId: storage.getStoreId()
});
//新增员工状态
const newEmployeeRequest = ref<boolean>(false);

const employeeListColumns = [
	{
		key: 'name',
		title: '员工名字',
		dataIndex: 'name',
		width: '7%',
	},
	{
		key: 'sex',
		title: '性别',
		dataIndex: 'sex',
		width: '7%',
	},
	{
		key: 'username',
		title: '账号',
		dataIndex: 'username',
		width: '10%',
	},
	{
		key: 'password',
		title: '密码',
		dataIndex: 'password',
		width: '15%',
	},
	{
		key: 'id_number',
		title: '身份证',
		dataIndex: 'id_number',
	},
	{
		key: 'phone',
		title: '手机号',
		dataIndex: 'phone',
	},
	{
		key: 'status',
		title: '状态',
		dataIndex: 'status',
	},
	{
		key: 'operation',
		title: '操作',
		dataIndex: 'operation',
	},
];

export default defineComponent({
	setup() {
		const loadEmployeeData = () => {
			EmployeeAPI.get({storeId: storage.getStoreId()}).then(res => {
				if (res.data.code == 200) {
					dataSource.value = res.data.data
					dataSource.value.forEach(i => {
						i.key = i.id;
						i.status = i.status == 1;
					})
				}
			})
		}
		
		loadEmployeeData()
		
		const pageCurrent = ref(1)
		
		const editableData: UnwrapRef<Record<string, EmployeeEntity>> = reactive({});
		
		const switchData: UnwrapRef<Record<string, boolean>> = reactive({});
		
		const edit = (key: string) => {
			editableData[key] = cloneDeep(dataSource.value.filter(item => key === item.key)[0]);
			switchData[key] = dataSource.value.filter(item => key === item.key)[0]['status'];
		};
		
		const save = (key: string) => {
			editableData[key]['status'] = switchData[key];
			EmployeeAPI.set(editableData[key]).then(res => {
				if (res.data.code == 200) {
					Object.assign(dataSource.value.filter(item => key === item.key)[0], editableData[key]);
					delete editableData[key];
					delete switchData[key];
					message.info(res.data.msg)
				} else message.warn(res.data.msg)
			})
		};
		
		const cancel = (key: string) => {
			delete editableData[key];
			delete switchData[key];
		};
		
		const showEmployee = () => {
			addEmployeeVisible.value = true;
		}
		
		const employeeFinish = v => {
			dataSource.value.push(v)
			addEmployeeVisible.value = false
			if (dataSource.value.length % 10 == 0) {
				pageCurrent.value += 1
			}
		}
		
		return {
			dataSource,
			pageCurrent,
			editableData,
			switchData,
			edit,
			save,
			cancel,
			addEmployeeVisible,
			showEmployee,
			employeeFinish,
			formState,
			newEmployeeRequest,
			employeeListColumns
		};
	},
	components: {
		NewEmployee,
		SearchOutlined
	},
});
</script>
<style lang="scss">
.ant-input-search-button {
	height: 40px;
}

.ant-modal-content {
	border-radius: 7px;
}

.ant-modal-header {
	background-color: #f6f5f5;
	border-top-left-radius: 7px;
	border-top-right-radius: 7px;
}
</style>
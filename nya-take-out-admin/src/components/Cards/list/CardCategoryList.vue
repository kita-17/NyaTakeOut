<template>
    <a-card>
        <template #title>
            <a-input v-model:value="searchData" placeholder="Input search text" style="width: 200px"/>
        </template>
        <template #extra>
            <a-space>
                <a-button style="width: 150px" type="primary" @click="addCategory">新增分类</a-button>
                <a-popconfirm :visible="visible" title="确定删除?" @confirm="rowDelete"
                              @visibleChange="handleVisibleChange">
                    <a-button danger style="width: 150px" type="primary">删除分类</a-button>
                </a-popconfirm>
            </a-space>
        </template>
        <a-table id="category_table" :columns="categoryListColumns" :data-source="dataSource"
                 :pagination="{
				current: pageCurrent,
				defaultPageSize: 10,
				position: ['bottomCenter'],
				total: 100,
				onChange: (pageNumber, pageSize) => {
					pageCurrent = pageNumber
					loadData(searchData)
				}
			}" :row-selection="rowSelection">
            <template #bodyCell="{ column, text, record, index }">

                <template v-if="['name', 'sort'].includes(column.dataIndex)">
                    <a-input v-if="editableData[record.key]" v-model:value="editableData[record.key][column.dataIndex]"
                             style="margin: -5px 0; width: 128px"/>
                </template>

                <template v-if="column.dataIndex === 'type'">
                    <a-select v-if="editableData[record.key]" v-model:value="editableData[record.key][column.dataIndex]"
                              :options="typeData" style="margin: -5px 0"/>
                    <template v-else>
                        {{ record.type === 1 ? '菜品分类' : '套餐分类' }}
                    </template>
                </template>

                <template v-if="column.dataIndex === 'operation'">
                    <div class="editable-row-operations">
						<span v-if="editableData[record.key]">
							<a-space :size="16">
								<a-typography-link @click="save(record.key)">保存</a-typography-link>
								<a-typography-link @click="cancel(record.key)">取消</a-typography-link>
							</a-space>
						</span>
                        <span v-else>
							<a @click="edit(record.id)">编辑</a>
						</span>
                    </div>
                </template>

            </template>
        </a-table>
    </a-card>
</template>

<script lang="ts">

import {cloneDeep} from "lodash-es";
import {defineComponent, reactive, ref, UnwrapRef, watch} from "vue";
import {message, SelectProps} from "ant-design-vue";
import categoryAPI from "../../../api/categoryAPI";
import CategoryAPI from "../../../api/categoryAPI";
import utils from "../../../common/utils";
import storage from "../../../common/storage";

const categoryListColumns = [
    {
        key: 'category',
        title: '分类名称',
        dataIndex: 'name',
        width: 256
    },
    {
        key: 'editTime',
        title: '最后一次操作时间',
        dataIndex: 'editTime'
    },
    {
        key: 'type',
        title: '类型',
        dataIndex: 'type'
    },
    {
        key: 'sort',
        title: '排序',
        dataIndex: 'sort'
    },
    {
        key: 'operation',
        title: '操作',
        dataIndex: 'operation'
    }
];

const dataSource = ref([])

const typeData = ref<SelectProps['options']>([{
    value: 1,
    label: '菜品分类'
}, {
    value: 2,
    label: '套餐分类'
}])

const rowSelectData = ref([])
const searchData = ref<string>()

export default defineComponent({
    computed: {
        storage() {
            return storage
        }
    },
    setup() {
        const pageCurrent = ref(1);
        const loadData = (n: string) => {
            CategoryAPI.get({
                storeId: storage.getStoreId(),
                name: n
            }).then(res => {
                if (res.data.code == 200) {
                    if(res.data.data != null) {
                        dataSource.value = res.data.data
                    }
                }
            })
        }

        loadData(null)

        watch(searchData, () => {
            loadData(searchData.value)
        })

        //删除按钮的二次确认
        const visible = ref<boolean>(false);
        const handleVisibleChange = (bool: boolean) => {
            if (!bool) {
                visible.value = false;
                return;
            }
            if (rowSelectData.value.length > 0) {
                visible.value = true;
            } else {
                message.error("请至少选择一个分类")
            }
        };
        //选择行数据后触发, 对选择的数据进行复制
        const rowSelection = ref({
            checkStrictly: false,
            onChange: (selectedRowKeys: (string | number)[], selectedRows: []) => {
                rowSelectData.value = selectedRows;
            }
        });
        //选择行数据后删除操作
        const rowDelete = () => {
            const ids = []
            rowSelectData.value.forEach(i => {
                ids.push(i.id)
            })
            categoryAPI.del({ids: ids.join(","), storeId: storage.getStoreId()}).then(res => {
                if (res.data.code == 3000) {
                    editableData[key].id = res.data.data
                    message.info(res.data.msg)
                } else {
                    message.info(res.data.msg)
                    return;
                }
            })
            dataSource.value = dataSource.value.filter(item => !rowSelectData.value.includes(item))
        }

        const addCategory = () => {
            if (dataSource.value.length % 10 == 0) {
                pageCurrent.value += 1
            }
            let ke = dataSource.value.length + 1
            dataSource.value.push({
                id: -1,
                key: ke,
                name: '测试分类_1',
                editTime: '2022-01-01',
                sort: 10000000,
                type: 1,
                storeId: storage.getStoreId()
            })
            edit(String(ke))
        }

        const editableData: UnwrapRef<Record<string, {}>> = reactive({});

        const edit = (key: string) => {
            editableData[key] = cloneDeep(dataSource.value.filter(item => key == item.key)[0]);
        };

        const save = (key: string) => {
            if (editableData[key].id > 0) {
                //更新操作
                categoryAPI.set(editableData[key]).then(res => {
                    if (res.data.code == 3004) {
                        message.info(res.data.msg)
                    } else {
                        message.info(res.data.msg)
                        return;
                    }
                })
            } else {
                //新增操作
                categoryAPI.add(editableData[key]).then(res => {
                    if (res.data.code == 3000) {
                        editableData[key].id = res.data
                        message.info(res.data.msg)
                    } else {
                        message.info(res.data.msg)
                        return;
                    }
                })
            }
            Object.assign(dataSource.value.filter(item => key == item.key)[0], editableData[key]);
            delete editableData[key];
            dataSource.value.sort(utils.handleSort('sort'))
        };
        const cancel = (key: string) => {
            delete editableData[key];
        };

        return {
            searchData, rowSelectData, dataSource, typeData, pageCurrent,
            handleVisibleChange, visible, loadData,
            categoryListColumns, rowSelection, rowDelete,
            addCategory, editableData, edit, save, cancel,
        }
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
<template>
    <a-card>
        <template #title>
            <a-input v-model:value="searchData" placeholder="Input search text" style="width: 200px"/>
        </template>
        <template #extra>
            <a-space>
                <a-button style="width: 150px" type="primary" @click="onCreateSetmeal">新增套餐</a-button>
                <a-popconfirm :visible="popconfirm_visible" title="确定删除?" @confirm="onRowDelete"
                              @visibleChange="handleVisibleChange">
                    <a-button danger style="width: 150px" type="primary">删除套餐</a-button>
                </a-popconfirm>
            </a-space>
        </template>
        <a-table :columns="dishListColumns" :data-source="dataSource" :pagination="{
			current: pageCurrent,
			defaultPageSize: 10,
			total: totalData,
			position: ['bottomCenter'],
			onChange: (pageNumber) => pageCurrent = pageNumber
		}" :row-selection="rowSelection" :row-key="record => record.id">
            <template #bodyCell="{ column, text, record }">
                <template v-if="'categoryName' === column.dataIndex">
                    {{ record.categoryName }}
                </template>

                <template v-if="'image' === column.dataIndex">
                    <a-image :height="64" :src="getImage(record.image)" :width="64"
                             fallback="data:image/png;base64,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"/>
                </template>

                <!--显示状态-->
                <template v-if="column.dataIndex === 'status'">
                    {{ record.status === true ? '启用' : '禁用' }}
                </template>

                <!--修改按钮-->
                <template v-if="column.dataIndex === 'operation'">
                    <div class="editable-row-operations">
                        <span><a @click="onEditSetmeal(record.id)">编辑</a></span>
                    </div>
                </template>
            </template>
        </a-table>
        <DrawerForm
                ref="DrawerForm"
                @add="handlerAdd"
                @update="handlerUpdate"
        />
    </a-card>
</template>

<script lang="ts">
import {defineComponent, ref, watch} from "vue";
import {DishEntity} from "../../../entity/DishEntity";
import {cloneDeep} from "lodash-es";
import {UploadOutlined} from "@ant-design/icons-vue";
import {message, SelectProps} from "ant-design-vue";
import NewDishForm from "../../Form/DrawerForm.vue";
import {FormStateEntity} from "../../../entity/FormStateEntity";
import CategoryAPI from "../../../api/categoryAPI";
import SetmealAPI from "../../../api/SetmealAPI";
import DrawerForm from "../../Form/DrawerForm.vue";
import DishAPI from "../../../api/dishAPI";
import storage from "../../../common/storage";

const dishListColumns = [
    {
        key: 'setmeal',
        title: '套餐名称',
        dataIndex: 'name',
    },
    {
        key: 'image',
        title: '图片',
        dataIndex: 'image'
    },
    {
        key: 'category',
        title: '套餐分类',
        dataIndex: 'categoryName',
    },
    {
        key: 'price',
        title: '单价',
        dataIndex: 'price',
    },
    {
        key: 'description',
        title: '描述',
        dataIndex: 'description',
    },
    {
        key: 'status',
        title: '售卖状态',
        dataIndex: 'status',
    },
    {
        key: 'operation',
        title: '操作',
        dataIndex: 'operation',
    },
];
const searchData = ref<string>()
const dataSource = ref<FormStateEntity[]>([])
const categoryData = ref<SelectProps['options']>();
const rowSelectData = ref([])

export default defineComponent({
    components: {
        DrawerForm,
        UploadOutlined,
    },
    setup() {
        //抽屉表单实例
        const DrawerForm = ref<DrawerForm>()
        //当前表格页数
        const pageCurrent = ref(1)
        //表格总数据条数
        const totalData = ref(0);

        const loadData = () => {
            SetmealAPI.get({name: searchData.value, storeId: storage.getStoreId(), showHide: true}).then(res => {
                if (res.data.code == 200) {
                    if(res.data.data) {
                        dataSource.value = res.data.data
                    }
                }
            })
        }

        const loadCategory = () => {
            CategoryAPI.get({storeId: storage.getStoreId()}).then(res => {
                if (res.data.code == 200) {
                    let d = []
                    res.data.data.forEach(item => {
                        d.push({
                            type: item.type,
                            value: item.id,
                            label: item.name
                        })
                    })
                    categoryData.value = d;
                }
            })
        }

        loadData()
        loadCategory()

        watch(searchData, () => {
            loadData()
        })

        watch(dataSource.value, () => {
            totalData.value = dataSource.value.length
        })

        //删除按钮的二次确认
        const popconfirm_visible = ref<boolean>(false);
        const handleVisibleChange = (bool: boolean) => {
            if (!bool) {
                popconfirm_visible.value = false;
                return;
            }
            if (rowSelectData.value.length > 0) {
                popconfirm_visible.value = true;
            } else {
                message.error("请至少选择一个菜品")
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
        const onRowDelete = () => {
            let data = []
            rowSelectData.value.forEach(i => {
                data.push(i.id)
            })

            SetmealAPI.del({ids: data.join(","), storeId: storage.getStoreId()}).then(res => {
                if (res.data.code == 200) {
                    dataSource.value = dataSource.value.filter(item => !rowSelectData.value.includes(item))
                    message.info(res.data.msg)
                } else {
                    message.warn(res.data.msg)
                }

            })
        }
        const onEditSetmeal = (key: string) => {
            const data = cloneDeep(dataSource.value.filter(item => key == item.id)[0])
            DrawerForm.value.parseData(data, "套餐", "edit", categoryData.value.filter(item => 2 == item.type))
        };

        const onCreateSetmeal = () => {
            if (dataSource.value.length % 10 == 0) {
                pageCurrent.value += 1
            }
            DrawerForm.value.parseData(null, "套餐", "add", categoryData.value.filter(item => 2 == item.type))
        };

        const handlerAdd = (value) => {
            SetmealAPI.add(value).then(res => {
                if (res.data.code == 200) {
                    const setmeal = res.data.data
                    setmeal.key = setmeal.id
                    setmeal.categoryName = categoryData.value.filter(item => setmeal.category == item.value)[0].label
                    dataSource.value.push(setmeal)
                    message.info(res.data.msg)
                } else {
                    message.error(res.data.msg)
                }
            })
        }

        const handlerUpdate = (value) => {
            SetmealAPI.set(value).then(res => {
                if (res.data.code == 200) {
                    Object.assign(dataSource.value.filter(item => value.key == item.key)[0], value)
                    message.info(res.data.msg)
                } else {
                    message.error(res.data.msg)
                }
            })
        }

        const getImage = (img: string) => {
            return `http://127.0.0.1:8080/image/download?name=${img}`
        }

        return {
            dataSource, dishListColumns, searchData, pageCurrent, totalData,
            onEditSetmeal, onCreateSetmeal, categoryData,
            getImage, rowSelection, onRowDelete, popconfirm_visible, handleVisibleChange,
            handlerAdd, handlerUpdate, DrawerForm
        }
    }
})
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
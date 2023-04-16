<template>
    <a-card>
        <template #extra>
            <a-space>
                <a-button style="width: 150px" type="primary" @click="addCoupon">新增优惠券</a-button>
                <a-popconfirm :visible="popconfirm_visible" title="确定删除?" @confirm="removeCoupon"
                              @visibleChange="handleVisibleChange">
                    <a-button danger style="width: 150px" type="primary">删除优惠券</a-button>
                </a-popconfirm>
            </a-space>
        </template>
        <a-table
                :columns="couponColumns"
                :data-source="dataSource"
                :pagination="{position: ['bottomCenter']}"
                :row-selection="rowSelection"
                :row-key="(record) => record.id"
        >
            <template #bodyCell="{ column, text, record }">
                <template v-if="column.dataIndex === 'name'">
                    <a-input v-if="editData[record.id]" v-model:value="editData[record.id][column.dataIndex]"/>
                    <text v-else>
                        {{ text }}
                    </text>
                </template>
                <template v-if="column.dataIndex === 'type'">
                    <a-select v-if="editData[record.id]" v-model:value="editData[record.id][column.dataIndex]">
                        <a-select-option value="0">普通券</a-select-option>
                        <a-select-option value="1">限购券</a-select-option>
                    </a-select>
                    <text v-else>
                        {{ record.type == '0' ? '普通券' : '限购券' }}
                    </text>
                </template>
                <template v-if="column.dataIndex === 'validDays'">
                    <a-input v-if="editData[record.id]" v-model:value="editData[record.id][column.dataIndex]"/>
                    <text v-else>
                        {{ record.validDays }} 天
                    </text>
                </template>
                <template v-if="column.dataIndex === 'count'">
                    <a-input v-if="editData[record.id]" v-model:value="editData[record.id][column.dataIndex]"/>
                    <text v-else>
                        {{ record.count }}
                    </text>
                </template>
                <template v-if="column.dataIndex === 'discount'">
                    <a-input v-if="editData[record.id]" v-model:value="editData[record.id][column.dataIndex]"/>
                    <text v-else>
                        {{ record.discount }} 元
                    </text>
                </template>
                <template v-if="column.dataIndex === 'couponCondition'">
                    <a-input v-if="editData[record.id]" v-model:value="editData[record.id][column.dataIndex]"
                             class="edit-input"/>
                    <text v-else>
                        满 {{ record.couponCondition }} 元可用
                    </text>
                </template>
                <template v-if="column.dataIndex === 'showDate'">
                    <a-range-picker
                            v-if="editData[record.id]" :locale="locale"
                            v-model:value="editData[record.id]['date']"
                            :show-time="true"
                            @ok="date2String(record.id, $event)"
                    />
                    <text v-else>
                        {{ text }}
                    </text>
                </template>
                <template v-if="column.dataIndex === 'startDate'">
                    <div v-if="editData[record.id]"/>
                    <div v-else> {{ text }}</div>
                </template>
                <template v-if="column.dataIndex === 'endDate'">
                    <div v-if="editData[record.id]"/>
                    <div v-else> {{ text }}</div>
                </template>
                <!--修改按钮-->
                <template v-if="column.dataIndex === 'operation'">
                    <div class="editable-row-operations">
						<span v-if="editData[record.id]">
							<a-typography-link @click="save(record.id)">保存</a-typography-link> |
							<a-popconfirm title="确定取消?" @confirm="cancel(record.id)"><a>取消</a></a-popconfirm>
						</span>
                        <span v-else><a @click="edit(record.id)">编辑</a></span>
                    </div>
                </template>
            </template>
        </a-table>
    </a-card>
</template>

<script setup lang="ts">
import {reactive, ref, UnwrapRef} from "vue";
import {message} from "ant-design-vue";
import {EmployeeEntity} from "../entity/EmployeeEntity";
import {cloneDeep} from "lodash-es";
import dayjs from 'dayjs';
import 'dayjs/locale/zh-cn';
import locale from 'ant-design-vue/es/date-picker/locale/zh_CN';
import type {TableColumnType} from 'ant-design-vue';
import CouponAPI from "../api/couponAPI";
import storage from "../common/storage";

dayjs.locale('zh-cn');

const couponColumns: TableColumnType[] = [
    {
        key: 'coupon_name',
        title: '优惠券名称',
        dataIndex: 'name',
        width: 170,
    },
    {
        key: 'coupon_type',
        title: '优惠券类型',
        dataIndex: 'type',
        width: 100,
    },
    {
        key: 'coupon_count',
        title: '库存',
        dataIndex: 'count',
        width: 100,
    },
    {
        key: 'coupon_valid_days',
        title: '有效时间',
        dataIndex: 'validDays',
        width: 170,
    },
    {
        key: 'coupon_discount',
        title: '优惠金额',
        dataIndex: 'discount',
        width: 170,
    },
    {
        key: 'coupon_coupon_condition',
        title: '使用条件',
        dataIndex: 'couponCondition',
        width: 170,
    },
    {
        key: 'coupon_date',
        title: '优惠券可用时间范围',
        dataIndex: 'showDate',
        colSpan: 3,
    },
    {
        key: 'coupon_start_date',
        title: '优惠券开放时间',
        dataIndex: 'startDate',
        colSpan: 0,
    },
    {
        key: 'coupon_start_date',
        title: '优惠券结束时间',
        dataIndex: 'endDate',
        colSpan: 0,
    },
    {
        key: 'operation',
        title: '操作',
        dataIndex: 'operation',
        colSpan: 1,
        width: 170,
    }
];
//  TODO 查询接口
const dataSource = ref([
    {
        id: '1' || 1,
        storeId: 0 || '0',
        name: '先辈特价券',
        type: 0,
        count: 10,
        validDays: 3,
        discount: 5,
        couponCondition: 20,
        startDate: '',
        endDate: '',
        date: [dayjs(), dayjs().add(7, 'day')]
    }
])
//获取后台优惠券数据
CouponAPI.getCouponFromStore({storeId: storage.getStoreId()}).then(res => {
    if (res.data.code == 200) {
        if (res.data.data.length > 0) {
            dataSource.value = res.data.data
            dataSource.value.forEach(i => {
                i.date = [dayjs(i.startDate, "YYYY-MM-DD HH:mm:ss"), dayjs(i.endDate, "YYYY-MM-DD HH:mm:ss")]
            })
        }
    }
})

// 行数据选择
const selectData = ref([])
const rowSelection = ref({
    checkStrictly: false,
    onChange: (selectedRowKeys: (string | number)[], selectedRows: []) => {
        selectData.value = selectedRows;
    }
});
//编辑行
const editData: UnwrapRef<Record<string, EmployeeEntity>> = reactive({});
const edit = (id: any) => {
    editData[id] = cloneDeep(dataSource.value.filter(item => id === item.id)[0]);
}
const save = (id: any) => {
    //  TODO 接口
    const datum = editData[id];
    datum.date = null;
    CouponAPI.addCouponToStore(datum).then(res => {
        if (res.data.code == 200) {
            Object.assign(dataSource.value.filter(item => id === item.id)[0], editData[id]);
            delete editData[id];
        }
    })
}
const cancel = (id: any) => {
    delete editData[id];
}
//新增优惠券
const addCoupon = () => {
    const nextId = dataSource.value.length + 1;
    dataSource.value.push({
        storeId: storage.getStoreId(),
        count: 0,
        couponCondition: 0,
        date: [dayjs(), dayjs().add(7, 'day')],
        discount: 0,
        endDate: "",
        id: nextId,
        name: "",
        startDate: "",
        type: 0,
        validDays: 0
    })
    edit(nextId);
}
//删除优惠券
const removeCoupon = () => {
    //  TODO 接口
    const map = selectData.value.map(i => i.id);
    CouponAPI.removeCouponFromStore({ids: map.join(","), storeId: storage.getStoreId()}).then(res => {
        if (res.data.code == 200) {
            dataSource.value = dataSource.value.filter(item => !selectData.value.includes(item))
        }
    })
}
//删除按钮的二次确认
const popconfirm_visible = ref<boolean>(false);
const handleVisibleChange = (bool: boolean) => {
    if (!bool) {
        popconfirm_visible.value = false;
        return;
    }
    if (selectData.value.length > 0) {
        popconfirm_visible.value = true;
    } else {
        message.error("请至少选择一个优惠券")
    }
};
//时间转字符串
const date2String = (id: any, e: any) => {
    const start = dayjs(e[0]).format("YYYY-MM-DD HH:mm:ss").toString();
    const end = dayjs(e[1]).format("YYYY-MM-DD HH:mm:ss").toString();
    editData[id].startDate = start || '';
    editData[id].endDate = end || '';
}
</script>

<style scoped>
.edit-input {
    width: auto;
}
</style>
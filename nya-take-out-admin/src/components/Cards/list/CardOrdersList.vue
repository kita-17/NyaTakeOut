<template>
    <a-card>
        <template #title>
            <a-input
                    v-model:value="searchData"
                    class="ant-input-search-button"
                    placeholder="输入日期搜索"
                    style="width: 200px"
            />
        </template>
        <a-table
                :columns="ordersListColumns"
                :data-source="dataSource"
                :pagination="{
				current: pageCurrent,
				defaultPageSize: 10,
				total: totalData,
				position: ['bottomCenter'],
				onChange: (pageNumber) => pageCurrent = pageNumber}"
        >
            <template #bodyCell="{column, text, record}">
                <template v-if="column.dataIndex === 'status'">
                    {{ getStatus(text) }}
                </template>
            </template>

            <template #expandedRowRender="{ record }">
                <div class="detail-container">
                    <div class="detail-number">
                        订单编号: {{ record.number }}
                    </div>
                    <div class="detail-status">
                        订单状态: {{ getStatus(record.status) }}
                    </div>
                    <div class="detail-price">
                        实收金额: {{ record.price }} 元
                    </div>
                    <div class="detail-order-time">
                        下单时间: {{ record.orderTime }}
                    </div>
                    <div class="detail-order-time">
                        配送地址:
                        {{
							record.address.provinceName + record.address.cityName + record.address.districtName + record.address.detail
                        }}
                    </div>
                    <div class="detail-order-time">
                        收货人: {{ record.address.consignee }}
                    </div>
                    <div class="detail-order-time">
                        联系电话: {{ record.address.phone }}
                    </div>
                    <div class="detail-order-time">
                        备注: {{ record.remark }}
                    </div>
                </div>
            </template>
        </a-table>
    </a-card>
</template>

<script setup lang="ts">
import {ref, watch} from "vue";
import orderAPI from "../../../api/orderAPI";
import storage from "../../../common/storage";

const ordersListColumns = [
    {
        key: 'number',
        title: '订单号',
        dataIndex: 'number',
    },
    {
        key: 'status',
        title: '订单状态',
        dataIndex: 'status'
    },
    {
        key: 'price',
        title: '实收金额',
        dataIndex: 'price',
    },
    {
        key: 'orderTime',
        title: '下单时间',
        dataIndex: 'orderTime',
    },
];
const dataSource = ref<[]>([]);
const searchData = ref<string>()
//当前表格页数
const pageCurrent = ref(1)
//表格总数据条数
const totalData = ref(0);

const load = () => {
    const sid = storage.getStoreId()
    orderAPI.list({storeId: sid, value: searchData.value}).then(res => {
        dataSource.value = res.data.data;
    })
}
load()

watch(searchData, () => {
    load()
})
/**
 * 订单状态 1待付款，2待派送，3已派送，4已完成，5已取消
 */
const getStatus = (state: number) => {
    switch (state) {
        case 1:
            return '待付款';
        case 2:
            return '待派送';
        case 3:
            return '已派送';
        case 4:
            return '已完成';
        case 5:
            return '已取消';
    }
}
</script>

<style>
.detail-container {
    display: flex;

    flex-direction: column;
}

.detail-number {
    margin: 10px;
    font-weight: bold;
}

.detail-status {
    margin: 10px;
}

.detail-price {
    margin: 10px;
}

.detail-order-time {
    margin: 10px;
}
</style>
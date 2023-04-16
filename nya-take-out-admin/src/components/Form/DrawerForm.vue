<template>
    <a-drawer v-model:visible="drawer_visible" :autofocus="true" :get-container="false" :mask="false" :width="500"
              placement="right" title="编辑" @close="onDrawerClose">
        <a-form
                ref="formRef"
                :model="formState"
                autocomplete="off"
                name="newDishForm"
                :rules="rules"
                @finish="handleFinish"
                :hide-required-mark="true"
        >
            <a-form-item
                    :label="formType"
                    name="name"
            >
                <a-input v-model:value="formState.name" :placeholder="'请输入' + formType + '名称'"/>
            </a-form-item>

            <a-form-item
                    label="分类"
                    name="category"
            >
                <a-select
                        ref="select"
                        v-model:value="formState.category"
                        :options="categoryOptions"
                        style="width: 120px"
                />
            </a-form-item>

            <a-form-item
                    label="价格"
                    name="price"
            >
                <a-input v-model:value="formState.price" type="number"/>
            </a-form-item>

            <a-form-item
                    label="图片"
                    name="ImgIcon"
            >
                <a-upload
                        :before-upload="beforeUpload"
                        :show-upload-list="false"
                        action="http://localhost:8080/file/upload"
                        name="file"
                        @change="fileUpload"
                >
                    <a-button>
                        <div v-if="!loading">
                            <upload-outlined/>
                            点击上传
                        </div>
                        <div v-else>
                            <loading-outlined/>
                            上传中
                        </div>
                    </a-button>
                </a-upload>
            </a-form-item>

            <a-form-item
                    label="介绍"
                    name="description"
            >
                <a-input v-model:value="formState.description" :placeholder="'请输入' + formType + '介绍'"/>
            </a-form-item>
            <div v-if="formType==='菜品'">
                <div v-for="(item, index) in flavorSelectData[formState.id]" :key="index">
                    <div class="flavor-picker">
                        <div class="flavor-select-content">
                            <!--菜品口味选择-->
                            <a-form-item name="flavorCategory" style="width: 100px">
                                <a-input
                                        :bordered="false"
                                        placeholder="口味分类"
                                        style="height: 40px"
                                        v-model:value="item.name"
                                />
                            </a-form-item>
                            <a-form-item name="flavorSelect" style="width: 100%;">
                                <a-select
                                        mode="tags"
                                        :bordered="false"
                                        placeholder="请输入口味"
                                        size="large"
                                        :options="item.options"
                                        v-model:value='item.value'
                                />
                            </a-form-item>
                            <a-button @click="removeFlavorSlot(item.key)">
                                <template #icon>
                                    <minus-outlined/>
                                </template>
                            </a-button>
                        </div>
                    </div>
                </div>
                <div style="display: flex;justify-content: center;margin: 0 5px 15px 15px">
                    <a-button @click="addFlavorSlot">
                        <template #icon>
                            <plus-outlined/>
                        </template>
                    </a-button>
                </div>
            </div>
            <!--套餐菜品内容选择-->
            <a-form-item label="菜类" name="dishSelect" v-if="formType==='套餐'">
                <a-select
                        v-model:value="formState.flavor"
                        :options="flavor_options"
                        :placeholder="formType==='菜品' ? '请选择或输入菜品口味':'请选择或输入套餐菜类'"
                        :mode="formType === '菜品' ? 'tags' : 'multiple'" name="flavor"
                        style="width: 100%"
                />
            </a-form-item>

            <a-space :size="32" style="margin: 10px 0 10px 0">
                是否售卖:
                <a-switch v-model:checked="formState.status"/>
            </a-space>

            <a-form-item v-show="submitType === 'add'" name="new">
                <a-button block style="background-color: #52C41A;border-color: #d9d9d9;" type="primary"
                          html-type="submit">
                    新增
                </a-button>
            </a-form-item>
            <a-form-item v-show="submitType === 'edit'" name="save">
                <a-button block type="primary" html-type="submit">保存</a-button>
            </a-form-item>
        </a-form>
    </a-drawer>
</template>

<script lang="ts">
import {defineComponent, reactive, ref, UnwrapRef} from "vue";
import {LoadingOutlined, MinusOutlined, PlusOutlined, UploadOutlined} from '@ant-design/icons-vue';
import {FormInstance, message, SelectProps, UploadChangeParam, UploadProps} from "ant-design-vue";
import DishAPI from "../../api/dishAPI";
import {FormStateEntity} from "../../entity/FormStateEntity";
import {Rule} from "ant-design-vue/es/form";
import {cloneDeep} from "lodash-es";
import storage from "../../common/storage";

const formState = ref<FormStateEntity>({
    category: 0,
    categoryName: "",
    description: "",
    flavor: [],
    id: 1145141919810,
    image: "",
    key: 1145141919810,
    name: "",
    price: null,
    storeId: storage.getStoreId(),
    status: true
})

const formType = ref<string>("")
const submitType = ref<string>("")
const categoryOptions = ref<SelectProps['options']>()
const flavor_options = ref([]);
const image = ref<string>();

interface FlavorSelectData {
    key: string
    //所属菜品ID
    dishId: string
    //口味名字 -> 辣度, 忌口等
    name: string
    //口味选项 -> 微辣 中辣 不辣 不吃蒜等
    options: []
    //口味数据 -> 微辣 中辣 不辣 不吃蒜等
    value: []
}

//口味选择器数据源
const flavorSelectData: UnwrapRef<Record<string, FlavorSelectData[]>> = reactive({});

export default defineComponent({
    components: {
        UploadOutlined,
        LoadingOutlined,
        PlusOutlined,
        MinusOutlined
    },
    setup(props, ctx) {
        //表单dom元素
        const formRef = ref<FormInstance>()

        const loading = ref<boolean>(false);

        //添加一行口味选择
        const addFlavorSlot = () => {
            let v = formState.value
            if (!flavorSelectData[v.id]) {
                flavorSelectData[v.id] = []
            }
            flavorSelectData[v.id].push({
                key: flavorSelectData[v.id].length,
                dishId: v.id, name: '', options: [], value: []
            } as FlavorSelectData)
        }
        //删除一行口味选择
        const removeFlavorSlot = (eKey: any) => {
            let v = formState.value
            flavorSelectData[v.id] = flavorSelectData[v.id].filter(i => i.key != eKey)
        }
        /**
         * 文件上传
         * @param info
         */
        const fileUpload = (info: UploadChangeParam) => {
            if (info.file.status === 'uploading') {
                loading.value = true;
                return;
            }
            if (info.file.status === 'done') {
                loading.value = false;
                formState.value.image = info.file.response.data
                message.info('上传成功');
            }
            if (info.file.status === 'error') {
                loading.value = false;
                message.error('upload error');
            }
        };
        /**
         * 文件上传前触发, 进行文件大小检测等
         * @param file
         */
        const beforeUpload = (file: UploadProps['fileList'][number]) => {
            const isJpgOrPng = file.type === 'image/jpeg' || file.type === 'image/png';
            if (!isJpgOrPng) {
                message.error('只允许JPEG或PNG格式的图片!');
            }
            const isLt2M = file.size / 1024 / 1024 < 2;
            if (!isLt2M) {
                message.error('图片大小不能超过2M!');
            }
            return isJpgOrPng && isLt2M;
        };

        //加载菜品列表供套餐使用
        const loadDishList = () => {
            flavor_options.value = []
            DishAPI.get({storeId: storage.getStoreId()}).then(res => {
                if (res.data.code == 200) {
                    res.data.data.forEach(i => {
                        flavor_options.value.push({
                            value: i.id,
                            dishId: i.id,
                            setmealId: formState.value.id,
                            label: i.name
                        })
                    })
                }
            })
        }
        /**
         * =============================表单相关========================================
         */
        const DATA_NAME = formType.value == '菜品' ? '菜品' : '套餐';
        //名称输入检测
        let checkName = async (_rule: Rule, value: string) => {
            if (value === '') {
                return Promise.reject('请输入' + DATA_NAME + '名称')
            } else {
                return Promise.resolve()
            }
        }
        //价格输入检测
        let checkPrice = async (_rule: Rule, value: Number) => {
            if (!value) {
                return Promise.reject('请输入价格')
            } else {
                if (value < 0) {
                    return Promise.reject('价格不能小于0')
                }
                return Promise.resolve()
            }
        }
        const rules: Record<string, Rule[]> = {
            name: [{required: true, validator: checkName, trigger: 'change'}],
            price: [{required: true, validator: checkPrice, trigger: 'change'}],
        }
        //表单提交时触发
        const handleFinish = async () => {
            const data = cloneDeep(formState.value)
            //###########################对菜品口味进行转换####################################
            if (formType.value == '菜品') {
                let flavorData = flavorSelectData[formState.value.id]
                console.log("口味数据： ", flavorData)
                if (flavorData instanceof Array) {
                    console.log("口味数据： ", flavorData)
                    data.flavor = []
                    if (flavorData.length > 0) {
                        let fo = []
                        //这里的i.value 必定会是一个存放select数据的数组
                        for (let i of flavorData) { // 口味分类的循环
                            let fl = []
                            if (i.value.length > 0) { // 必须要有有效数据
                                for (let f of i.value) { // 每个口味分类下的 每条口味循环
                                    if (f.value) {
                                        fl.push(f.value)
                                    } else {
                                        fl.push(f)
                                    }
                                }
                            }
                            fo.push({
                                dishId: i.dishId,
                                name: i.name,
                                value: fl
                            } as FormStateEntity)
                        }
                        data.flavor = convertFlavor(fo)
                        console.log("最终口味数据： ", convertFlavor(fo))
                    }
                }
            } //##############################################################################
            else if (formType.value == '套餐') {
                //如果改变菜品的话, 组件 a-select 的value属性会变成['id', 'id'...] 格式
                //所以需要判定第一个元素的 'dishId' 在决定要不要转换
                if(data.flavor.length > 0) {
                    if (!data.flavor[0]['dishId']) {
                        data.flavor = convertFlavor(data.flavor)
                    }
                }
            }
            if (submitType.value == 'edit') {
                ctx.emit("update", data)
            } else if (submitType.value == 'add') {
                ctx.emit("add", data)
            }
            onDrawerClose()
        }
        /**
         * =================抽屉相关=============================================
         */
            //抽屉可视状态
        const drawer_visible = ref<boolean>(false)
        //打开抽屉
        const onDrawerShow = () => {
            drawer_visible.value = true
        }
        //关闭抽屉, 在抽屉关闭时会触发一次
        const onDrawerClose = () => {
            delete flavorSelectData[formState.value.id]
            drawer_visible.value = false
            formRef.value.resetFields();
        }

        /**
         * =================抽屉数据=============================================
         */
        /**
         * 加载从父类传过来的数据
         * @param v 表单数据
         * @param ft 数据类型 -> 菜品或套餐
         * @param st 数据提交方式 -> 新增或更新
         * @param options
         */
        const parseData = (v: FormStateEntity, ft: string, st: string, options) => {
            if (v != null) {
                formState.value = v
                flavorSelectData[v.id] = []
                formState.value.flavor.forEach(i => {
                    let opt = JSON.parse(i.value)
                    let optArr = []
                    if (opt instanceof Array) {
                        opt.forEach(eI => {
                            optArr.push({
                                value: eI,
                            })
                        })
                    }
                    flavorSelectData[v.id].push({
                        key: flavorSelectData[v.id].length,
                        dishId: v.id,
                        name: i.name,
                        options: optArr,
                        value: optArr
                    } as FlavorSelectData)
                })
            }
            formType.value = ft
            submitType.value = st
            categoryOptions.value = options
            if (st == 'add') {
                if (categoryOptions.value.length > 0) {
                    formState.value.category = categoryOptions.value[0].value
                }
            }
            if (ft === '套餐') {
                loadDishList()
            }
            onDrawerShow()
        }

        const convertFlavor = (value) => {
            let data = []
            if (formType.value == '菜品') {
                value.forEach(i => {
                    data.push(
                        {
                            dishId: formState.value.id,
                            name: i.name,
                            value: JSON.stringify(i.value)
                        }
                    )
                })
            } else if (formType.value == '套餐') {
                value.forEach(i => {
                    const d = flavor_options.value.filter(item => i == item.value)[0]
                    data.push(d)
                })
            }
            return data;
        }

        return {
            formType, submitType, categoryOptions,
            flavor_options, fileUpload, loading, beforeUpload, formState, formRef, rules, handleFinish, drawer_visible,
            onDrawerClose, onDrawerShow, parseData, flavorSelectData, addFlavorSlot, removeFlavorSlot
        }
    }
})
</script>
<style>
.flavor-select-content {
    display: flex;
    height: 50px;
}
</style>
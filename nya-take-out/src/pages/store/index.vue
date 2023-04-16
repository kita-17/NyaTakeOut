<template>
    <page-meta :page-style="'overflow:' + (popupShow ? 'hidden' : 'visible')"/>
    <view>
        <view>
            <store-info-card
                    id="business-view-header"
                    :store-id="storeId"
                    :title="storeTitle"
            />
        </view>
        <view class="business-view-container">
            <coupon-card :store-id="storeId"/>
            <custom-tabs type="c1" @change="handlerTabChange">
                <!-- 选择菜品页面 -->
                <custom-tab-pane label="点餐" name="c1_1">
                    <view class="vertical-nav-container">
                        <!--左侧-侧边栏-->
                        <view class="vertical-nav-bar">
                            <scroll-view :enable-back-to-top="true" :scroll-into-view="tabSelectIndex" :scroll-with-animation="true"
                                         :scroll-y="true"
                                         :show-scrollbar="false" :style="'height:' + mainViewHeight + 'px'"
                                         enhanced>
                                <view v-for="(item, index) in dataSource" :key="index">
                                    <view :id="item.navId"
                                          :data-nav-id="item.navId"
                                          :data-nav-scroll-id="item.navId" :style="tabSelectIndex === item.navId ? 'color:#e6005c;' : 'color:#00001a;'" class="vertical-nav-item"
                                          @click="handlerClickTab(item)">
                                        {{ item.name }}
                                    </view>
                                </view>
                            </scroll-view>
                        </view>
                        <!--右侧视图-->
                        <!-- @scrolltolower="scrollToLower" -->
                        <scroll-view :enable-back-to-top="true" :scroll-into-view="mainItemSelectIndex" :scroll-with-animation="true"
                                     :scroll-y="true" :style="'height:' + mainViewHeight + 'px'"
                                     class="goods-main-container">
                            <view v-for="(item, index) in dataSource" :id="item.navId" :key="index"
                                  :data-nav-id="item.navId" :data-nav-scroll-id="item.navId" class="trip goods-item">
                                <view class="category-item">
                                    <!--菜品分类标题-->
                                    <view :id="'title-' + index" class="category-title">
                                        {{ item.name }}
                                    </view>
                                    <!--菜品卡片-->
                                    <view v-for="(dish, dishIndex) in item.data" :key="dishIndex">
                                        <uni-card :border="false" :is-shadow="false" :isFull="true"
                                                  class="goods-card-container">
                                            <view class="n-goods-view">
                                                <image
                                                        :src="getImageUrl(dish.image)"
                                                        style="width: 176rpx; height: 176rpx;border-radius: 10px"/>
                                                <view class="x-food-view-box">
                                                    <view class="n-goods-view-box-title">{{ dish.name }}</view>
                                                    <view class="n-goods-view-box-desc">{{ dish.description }}</view>
                                                    <view class="n-goods-view-box-sales">月售 {{ dish.sale }}</view>
                                                    <view class="n-goods-view-box-price">{{ dish.price }}元</view>
                                                    <uni-number-box
                                                            v-if="showFlavorSelectButton(dish, dish.type)"
                                                            :value="getItemAmount(dish.id)"
                                                            class="n-goods-view-box-button"
                                                            @change="handlerAddItemRToCart(dish, $event)"/>
                                                    <button
                                                            v-else
                                                            class="n-goods-view-box-button item-select-flavor-btn"
                                                            @click="handlerSelectFlavor(dish)">
                                                        <text>选规格</text>
                                                    </button>
                                                </view>
                                            </view>
                                        </uni-card>
                                    </view>
                                </view>
                            </view>
                        </scroll-view>
                        <!--左侧视图结束-->
                    </view>
                </custom-tab-pane>
            </custom-tabs>
            <!-- 评论 -->
            <custom-tab-pane label="评论" name="c1_2">
                <CommentCard :dataSource="commentData"/>
            </custom-tab-pane>
            <!-- 评论结束 -->
            <!--口味选择界面-->
            <uni-popup ref="FlavorSelect" :mask-click="false" type="dialog">
                <view class="item-flavor-select-container">
                    <view class="item-flavor-select-title">
                        <text>请选择您喜好的口味</text>
                        <uni-icons type="closeempty" @click="handlerCloseFlavorSelect"/>
                    </view>
                    <view class="item-flavor-select-content">
                        <view v-for="(flavor, index) in getItemFlavorData(flavorItemIndex)" :key="index">
                            <view class="flavor-tag-title">
                                <text>{{ flavor.name }}</text>
                            </view>
                            <view class="flavor-tag-container">
                                <view v-for="(tag, i) in JSON.parse(flavor.value)" :key="i">
                                    <view class="flavor-item-tag">
                                        <uni-tag
                                                :inverted="!getItemFlavorSelected(flavor.id, tag)"
                                                :text="tag"
                                                type="primary"
                                                @click="handlerAddFlavorToItem(flavor.id, tag)"
                                        />
                                    </view>
                                </view>

                            </view>
                        </view>
                    </view>
                </view>
                <view class="item-flavor-select-footer">
                    <button class="item-flavor-btn" @click="handlerAddItemWithFlavorToCart">
                        <text style="font-size: 32rpx;">加入购物车</text>
                    </button>
                </view>
            </uni-popup>
            <!--口味选择界面结束-->
            <!-- 购物车开始 -->
            <view v-if="cartShow" class="shopping-cart" @click="handlerCartClick">
                <image class="cart-icon" src="../../static/cargo.png"/>
                <view class="cart-popup-box-info">
                    <view>
                        <text class="cart-price-smb">¥</text>
                        <text class="cart-total-price">{{ cartTotalPrice }}</text>
                    </view>
                    <text class="cart-delivery-price">另需配送费约10¥</text>
                </view>
                <button :style="cartDataSource.length > 0 ? 'background: #53a5ff;' : 'background: #757575;'" class="cart-order-btn" style="border: none;"
                        @click="handlerSubmitOrder">
                    去结算
                </button>
            </view>
            <!-- 购物车结束 -->
            <!--购物车弹窗-->
            <uni-popup v-if="cartShow" ref="ShoppingCart" class="cart-popup" type="bottom" @mask-click="cartClose">
                <view class="shopping-cart-title">
                    <view>
                        已选商品
                    </view>
                    <view>
                        <text @click="clearCartData">清空</text>
                    </view>
                </view>
                <scroll-view :enable-back-to-top="true" :enable-flex="true" :scroll-with-animation="true"
                             :scroll-y="true"
                             :style="'height:' + cartPopupHeight + 'px'">
                    <view v-for="(item, index) in cartDataSource" :key="index" class="cart-container">
                        <uni-card :border="false" :is-full="true" :is-shadow="false">
                            <view class="cart">
                                <view>
                                    <image :src="getImageUrl(item.image)" class="cart-goods-image"/>
                                </view>
                                <view class="cart-box">
                                    <view class="cart-box-goods-name">{{ item.name }}</view>
                                    <view class="cart-box-goods-flavor">
                                        <view v-for="(flavor, fIndex) in item.flavor" :key="fIndex">
                                            <text style="padding-left: 5rpx;padding-right: 5rpx">
                                                {{ flavor.selected }}
                                            </text>
                                        </view>
                                    </view>
                                    <view class="cart-box-goods-price">¥{{ item.price * item.amount }}</view>
                                </view>
                                <uni-number-box :value="getItemAmount(item.dishId)" class="cart-box-goods-button"
                                                @change="handlerChangeAmount(item, index, $event)"/>
                            </view>
                        </uni-card>
                    </view>
                    <view style="height: 50px;background-color: #fff"/>
                </scroll-view>
            </uni-popup>
            <!--购物车结束-->
        </view>
    </view>
</template>
<script lang="ts" setup>
import {onMounted, reactive, ref, UnwrapRef} from "vue";
import {CartEntity, CategoryEntity, DishEntity, FlavorEntity} from "@/entitys/nEntity";
import StoreInfoCard from "@/components/StoreWidget/StoreHeaderCard.vue";
import UniPopup from "@/uni_modules/uni-popup/components/uni-popup/uni-popup.vue";
import UniNumberBox from "@/uni_modules/uni-number-box/components/uni-number-box/uni-number-box.vue";
import UniIcons from "@/uni_modules/uni-icons/components/uni-icons/uni-icons.vue";
import {service} from "@/api/api";
import {cloneDeep} from "lodash";
import {onShow} from "@dcloudio/uni-app";
import CommentCard from "@/components/comment/CommentCard.vue";
import UserStorage from "@/storage/UserStorage";
import CouponCard from "@/components/coupon/CouponCard.vue";

const props = defineProps({
    storeTitle: {
        type: String || null
    },
    storeId: {
        type: String || Number,
    },
    storeDescription: {
        type: String
    }
})
const dataSource = ref<CategoryEntity[]>([])
const commentData = ref([])
//主视图高度
const mainViewHeight = ref();
//左侧当前选中的元素
const tabSelectIndex = ref();
//右侧区间当前显示的元素位置
const mainItemSelectIndex = ref();
//购物车数组
const cartDataSource = ref<CartEntity[]>([]);
//购物车popup组件高度
const cartPopupHeight = ref(150);
const popupShow = ref(false);
//购物车总价钱
const cartTotalPrice = ref(0);
//当前需要选择口味规格的物品id
const flavorItemIndex = ref();
//临时的口味信息, 用户选择口味时暂存, 确定添加到购物车后再复制到 cartDataSource 里然后删除这里的数据
//物品ID -> FlavorEntity[]
const flavorTempData: UnwrapRef<Record<string, FlavorEntity[]>> = reactive({});
const ShoppingCart = ref();
const FlavorSelect = ref();
let uid = UserStorage.getUserId();
//默认的地址信息
const defaultAddress = ref('')
//初始化
const initialization = async () => {
    let categoryData: any[] = []
    let dishData: any[] = []
    let setmealData: any[] = []
    let finalData = []

    //获取分类数据
    await service({url: '/category', method: 'get', data: {storeId: props.storeId}}).then(
        res => {
            categoryData = res.data.data;
        }
    )
    //获取菜品数据
    await service({url: '/dish', method: 'get', data: {storeId: props.storeId}}).then(
        res => {
            dishData = res.data.data;
            console.log(dishData)
        }
    )
    //获取套餐数据
    await service({url: '/setmeal/list', method: 'get', data: {storeId: props.storeId}}).then(
        res => {
            setmealData = res.data.data
        }
    )
    //合并
    for (let category of categoryData) {
        let tempData = [];
        tempData = dishData.filter(i => i.category == category.id)
        if (category.type == 1) {
            tempData = dishData.filter(i => i.category == category.id)
        }
        //套餐判断
        else if (category.type == 2) {
            tempData = setmealData.filter(i => i.category == category.id)
        }
        finalData.push({
            id: category.id,
            name: category.name,
            navId: "nav-" + category.id,
            data: tempData,
        })
    }
    dataSource.value = finalData;
    console.log(dataSource.value)
    // 获取评论信息
    await service({url: '/comment', method: 'get', data: {storeId: props.storeId, userId: uid}}).then(
        res => {
            commentData.value = res.data.data
        }
    )
}
onShow(() => {
    initialization()
})
//点击左侧导航栏项目触发
const handlerClickTab = (value: any) => {
    tabSelectIndex.value = value.navId;
    mainItemSelectIndex.value = value.navId;
}
//点击购物车
const handlerCartClick = () => {
    if (cartDataSource.value.length > 0) {
        ShoppingCart.value.open()
        popupShow.value = true;
    }
}
//购物车关闭
const cartClose = () => {
    ShoppingCart.value.close()
    popupShow.value = false;
}
//返回商品数量
const getItemAmount = (itemId: string) => {
    let data = cartDataSource.value.filter(i => i.dishId == itemId)[0]
    let count = 0;
    if (data) {
        if (data.amount) {
            count = data.amount;
        }
    }
    return count
}
//返回商品口味信息
const getItemFlavorData = (itemId: string) => {
    let data = null;
    for (let value of dataSource.value) { //value -> 每个商品分类的所有数据
        let a = value.data.filter(eI => eI.id == itemId)[0]; // eI -> 单个商品的数据
        if (a) {
            data = a;
            break;
        }
    }
    let flavor: { id: string; value: string; name: string, selected: string }[] = [];
    if (data != null) {
        flavor = data.flavor
    }
    return flavor;
}
//返回口味标签的选中状态
const
    getItemFlavorSelected = (flavorId: string, tag: string) => {
        let data = flavorTempData[flavorItemIndex.value];
        let selected = false;
        if (data != undefined) {
            selected = data.filter(i => i.id == flavorId)[0].selected == tag
        }
        return selected;
    }
//返回商品图片链接
const getImageUrl = (url: string) => {
    return 'http://127.0.0.1:8080/image/download?name=' + url
}
//菜品是否显示选择口味规格按钮
const showFlavorSelectButton = (dish: DishEntity, type: any) => {
    if (type == 1) { //是菜品
        let hasFlavor = Array.isArray(dish.flavor) && dish.flavor.length > 0;
        if (hasFlavor) { //可选口味
            let item = cartDataSource.value.filter(i => i.dishId == dish.id);
            if (item.length <= 0) {
                return false;
            }
        }
        return true;
    } else return true; //是套餐
}
//清除购物车数据
const clearCartData = () => {
    cartDataSource.value = [];
    cartPopupHeight.value = 150;
    cartTotalPrice.value = 0;
    cartClose();
}
//购物车弹出高度
const changePopupHeight = () => {
    if (cartDataSource.value.length <= 1) {
        cartPopupHeight.value = 150;
    } else if (cartDataSource.value.length >= 5) {
        cartPopupHeight.value = 450
    } else {
        cartPopupHeight.value = (cartDataSource.value.length * 50) + 150
    }
    cartTotalPrice.value = 0;
    cartDataSource.value.map(item => {
        cartTotalPrice.value += item.price * item.amount;
    })
}
//添加物品到购物车以及修改数量
const handlerAddItemRToCart = (item: UnwrapRef<DishEntity>, value: any) => {
    //根据物品ID获取购物车物品
    const cartItemData = cartDataSource.value.filter(i => i.dishId == item.id)[0];
    const cart = {
        id: (cartDataSource.value.length + 10000).toString(),
        dishId: item.id,
        amount: value,
        name: item.name,
        price: item.price,
        image: item.image,
        description: item.description,
        flavor: item.flavor,
        type: item.type
    } as CartEntity;

    if (cartItemData) {//如果存在相同id的物品
        if (value <= 0) {
            cartDataSource.value = cartDataSource.value.filter(i => i.dishId != item.id)
        } else {
            cartItemData.amount = value;
        }
    } else {//如果不存在,新增
        cartDataSource.value.push(cart)
    }
    //如果购物车没数据了, 就自动关闭购物车
    if (cartDataSource.value.length <= 0) {
        clearCartData()
    }
    //根据购物车物品数量更改购物车弹出层高度
    changePopupHeight();
}
//选中的商品数量更改
const handlerChangeAmount = (item: UnwrapRef<CartEntity>, index: any, value: any) => {
    handlerAddItemRToCart({id: item.dishId, flavor: {}} as DishEntity, value)
}

//添加有口味信息的物品
const handlerAddItemWithFlavorToCart = () => {
    let data = null;
    for (let value of dataSource.value) { //value -> 每个商品分类的所有数据
        let a = value.data.filter(eI => eI.id == flavorItemIndex.value)[0]; // eI -> 单个商品的数据
        if (a) {
            data = a;
            break;
        }
    }
    if (data != null) {
        data.flavor = flavorTempData[flavorItemIndex.value].filter(i => i.selected)
        handlerAddItemRToCart(data, 1)
        handlerCloseFlavorSelect();
    }
}
//显示口味选择弹框
const handlerSelectFlavor = (item: DishEntity) => {
    //口味选择弹框展示的数据
    flavorTempData[item.id] = cloneDeep(item.flavor)
    flavorItemIndex.value = item.id
    popupShow.value = true
    FlavorSelect.value.open()
}
//关闭口味选择弹框
const handlerCloseFlavorSelect = () => {
    delete flavorTempData[flavorItemIndex.value];
    popupShow.value = false;
    FlavorSelect.value.close()
}
//选择口味
const handlerAddFlavorToItem = (flavorId: string, tag: string) => {
    flavorTempData[flavorItemIndex.value].filter(i => i.id == flavorId)[0].selected = tag;
}
//提交订单
const handlerSubmitOrder = () => {
    uni.navigateTo({
        url: '/pages/payment/index?id=1&cartData=' + encodeURIComponent(JSON.stringify(cartDataSource.value)) +
            '&totalPrice=' + cartTotalPrice.value + '&storeId=' + props.storeId
    });
}
//计算商品列表的页面高度
onMounted(() => {
    const windowHeight = uni.getSystemInfoSync().windowHeight;
    uni.createSelectorQuery().select('#business-view-header').boundingClientRect((res: any) => {
        mainViewHeight.value = windowHeight - res.height
    }).exec();
})
const cartShow = ref(true);
//标签页切换
const handlerTabChange = (e: any) => {
    if (e.label == '评论') {
        cartShow.value = false;
        return;
    }
    cartShow.value = true;
}
</script>

<style lang="scss" scoped>
@import '../../uni.scss';

.business-view-container {
  margin-top: 120rpx;
}

.vertical-nav {
  &-container {
    // margin-top: 35px;
    background-color: #fff;
    display: flex;
  }

  &-bar {
    width: 25%;
    background-color: #FFF
  }

  &-item {
    width: 100%;
    height: 50px;
    white-space: nowrap;
    overflow: hidden;
    display: flex;
    justify-content: center;
    align-items: center;
    font-weight: 700;
  }
}

.goods {
  &-main-container {
    padding-bottom: 75px;
  }

  &-item {
    margin-bottom: 10rpx;
    padding: 20rpx 30rpx 0 30rpx;
  }

  &-card-container {
    width: 100%;
  }
}

.category {

  &-item {
    width: 100%;
    justify-content: space-between;
  }

  &-title {
    width: 100%;
    height: 50rpx;
    display: flex;
    align-items: center;
    color: #000000;
  }
}

.n-goods-view {
  display: flex;

  //margin-bottom: 32rpx;
  &-box {
    flex-grow: 1;
    @include flex(space-between, flex-start, column);

    &-title {
      width: 125px;
      color: #333642;
      font-size: 30rpx;
      position: relative;
      left: 10rpx;
      top: 0rpx;
      @extend %ellipsis
    }

    &-desc {
      width: 125px;
      color: #9398AD;
      font-size: 20rpx;
      line-height: 20rpx;
      position: relative;
      left: 10rpx;
      top: 15rpx;
      @extend %ellipsis
    }

    &-sales {
      color: #9398AD;
      width: 130rpx;
      height: 20rpx;
      font-size: 17rpx;
      line-height: 20rpx;
      position: relative;
      left: 10rpx;
      top: 35rpx;
      @extend %ellipsis
    }

    &-price {
      width: 130rpx;
      height: 40rpx;
      display: block;
      box-sizing: border-box;
      position: relative;
      left: 10rpx;
      top: 50rpx;
    }

    &-button {
      position: relative;
      left: 120rpx;
      top: 5rpx;
    }
  }
}

.item-select-flavor-btn {
  width: 140rpx;
  height: auto;
  font-size: 24rpx;
  border-radius: 30rpx;
  color: #FFFFFF;
  background: #007aff;
}

.shopping-cart {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  height: 120rpx;
  z-index: 9999;
  display: flex;
  background: #fff;
  box-shadow: 0px -5px 10px -5px rgba(52, 47, 47, 0.53);
}

.shopping-cart-title {
  padding: 7px 10px 15px 10px;
  white-space: nowrap;
  overflow: hidden;
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  background-color: #fff;
  border-top-left-radius: 10px;
  border-top-right-radius: 10px;
}

.cart {
  display: flex;

  &-container {
    width: 100%;
    background-color: #f5d7d6;
  }

  &-goods-image {
    margin-right: 10px;
    width: 130rpx;
    height: 130rpx;
    border-radius: 4px
  }

  &-box {
    flex-grow: 1;
    @include flex(space-between, flex-start, column);

    &-goods-name {
      width: 200rpx;
      font-weight: bold;
      position: relative;
      @extend %ellipsis;
    }

    &-goods-description {
      width: 300rpx;
      color: #8f939c;
      font-size: 10px;
      position: relative;
      top: -10rpx;
      @extend %ellipsis
    }

    &-goods-price {
      font-size: 15px;
      color: #e43d33;
      font-weight: bold;
      position: relative;
      top: -10rpx;
    }

    &-goods-button {
      position: relative;
      top: 35rpx;
    }

    &-goods-flavor {
      display: flex;
    }
  }
}

.cart-popup {
  width: 100%;
  background-color: #fff;
  border-top-left-radius: 10px;
  border-top-right-radius: 10px;
  position: absolute;
  bottom: 35rpx;
}

.cart-icon {
  height: 100px;
  width: 100px;
  position: relative;
  top: -50rpx;
  left: -30rpx;
}

.cart-popup-box-info {
  display: flex;
  flex-grow: 1;
  @include flex(space-between, flex-start, column);
}

.cart-price-smb {
  height: 25rpx;
  font-size: 20rpx;
  font-weight: bold;
  position: relative;
  padding: 10rpx;
  top: 10rpx;
  left: -50rpx;
}

.cart-total-price {
  color: #000000;
  height: 50rpx;
  font-size: 30rpx;
  font-weight: bold;
  position: relative;
  top: 10rpx;
  left: -70rpx;
  padding: 15rpx;
}

.cart-delivery-price {
  font-size: 20rpx;
  font-weight: bold;
  position: relative;
  color: #2a2a2a;
  top: -30rpx;
  left: -55rpx;
  padding: 10rpx;
}

.cart-order-btn {
  width: 200rpx;
  height: 80rpx;
  position: relative;
  top: 20rpx;
  right: 20rpx;
  color: #fff;
  border-radius: 30rpx;
  display: flex;
  justify-content: center;
  align-items: center;
}

.item-flavor-select-container {
  height: 700rpx;
  width: 550rpx;
  background-color: #FFFFFF;
  padding: 20rpx;
  border-radius: 20rpx;
}

.item-flavor-select-title {
  display: flex;
  font-size: 32rpx;
  font-weight: bold;
  color: #212121;
  margin: 15rpx;
  justify-content: space-between;
  height: 40rpx;
}

.item-flavor-select-content {
  display: flex;
  flex-direction: column;
  margin-left: 15px;
  padding: 10rpx;
  height: calc(100% - 300rpx);
}

.item-flavor-select-footer {
  position: absolute;
  bottom: 0;
  height: 125rpx;
  width: 100%;
  background-color: #fff;
  margin-top: 15rpx;
  border-radius: 20rpx;
}

.item-flavor-btn {
  width: 230rpx;
  height: auto;
  position: absolute;
  bottom: 10rpx;
  right: 10rpx;
  color: #FFFFFF;
  background-color: #007aff;
}

.flavor-tag-container {
  display: flex;
}

.flavor-item-tag {
  margin-left: 10rpx;
  margin-right: 10rpx;
}

.flavor-tag-title {
  margin: 32rpx 0 32rpx 0;
}
</style>
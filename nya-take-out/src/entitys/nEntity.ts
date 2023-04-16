//左侧分类数据
export interface CategoryEntity {
    //分类id
    id: string,
    //左侧导航栏定位id
    navId: string,
    //分类名称
    name: string,
    //该分类对应的右侧数据
    data: DishEntity[],
}

//右侧菜品数据
export interface DishEntity {
    id: string,
    name: string,
    category: string,
    image: string,
    type: string,
    price: number,
    description: string,
    sale: number,
    flavor: FlavorEntity[]
}

//菜品口味 | 套餐分类
export interface FlavorEntity {
    id: string,
    name: string,
    value: string,
    selected: string,
}

//购物车数据
export interface CartEntity {
    id: string,
    //该物品类型 [菜品|套餐]
    type: string,
    //菜品id
    dishId: string,
    //套餐id
    setmealId: string,
    //物品名称
    name: string,
    //图片
    image: string,
    //物品简介
    description: string,
    //单价
    price: number,
    //数量
    amount: number,
    //口味
    flavor: FlavorEntity[]
}

export interface AddressEntity {
    id: string;
    userId: string,
    //省
    provinceName: string,
    provinceCode: string,
    //城
    cityName: string,
    cityCode: string,
    //区|县
    districtName: string,
    districtCode: string,
    //详细地址
    detail: string,
    //收货人
    consignee: string,
    //电话号码
    phone: string,
    //是否默认
    default: boolean,
}

//用户信息
export interface UserProfile {
    id: string,
    username: string,
    avatar: string,
}
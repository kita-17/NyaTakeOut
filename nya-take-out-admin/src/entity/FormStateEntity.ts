export interface FormStateEntity {
    //表格需要的key
    key: Number,
    //菜品或套餐id
    id: Number,
    //菜品或套餐名称
    name: string,
    //菜品或套餐所属分类ID
    category: Number,
    //分类名称
    categoryName: string,
    //菜品或套餐描述
    description: string,
    //展示的图片名称
    image: string,
    //菜品或套餐价格
    price: Number,
    //菜品口味
    flavor: FlavorEntity[]
    //菜品或套餐的售卖状态
    status: boolean,
    storeId: string | number
}

interface FlavorEntity {
    id: number
    setmealId: string
    dishId: number
    name: string
    value: string
}
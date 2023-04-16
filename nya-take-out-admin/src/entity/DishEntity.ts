export interface DishEntity {
    id: Number;
    key: Number,
    name: string;
    image: string;
    category: Number;
    categoryName: string;
    price: Number;
    flavor: [],
    status: boolean;
    description: string;
    storeId: string | number;
}
import service from "../common/CommonService";

const testRenderAPI = (params) => {
    return service({
        url: '/test/employee',
        method: 'GET',
        data: params
    })
}

const testNewEmployee = (params) => {
    return service({
        url: '/test/employee',
        method: 'POST',
        data: params
    })
}

const testDishData = (params) => {
    return service({
        url: '/test/dish',
        method: 'GET',
        data: params
    })
}

const testCategoryData = (params) => {
    return service({
        url: '/test/category',
        method: 'GET',
        data: params
    })
}

const testSetmealData = (params) => {
    return service({
        url: '/test/setmeal',
        method: 'GET',
        data: params
    })
}

const testOrdersData = (params) => {
    return service({
        url: '/test/orders',
        method: 'GET',
        data: params
    })
}

export default {
    testRenderAPI,
    testNewEmployee,
    testDishData,
    testCategoryData,
    testSetmealData,
    testOrdersData
}
import service from "../common/CommonService";

const getCouponFromStore = (params: any) => {
    return service({
        url: '/coupon/store',
        method: 'GET',
        params
    })
}

const addCouponToStore = (params: any) => {
    return service({
        url: '/coupon/add/store',
        method: 'POST',
        data: params
    })
}

const removeCouponFromStore = (ids: any) => {
    return service({
        url: '/coupon/store',
        method: 'DELETE',
        params: ids
    })
}

export default {
    getCouponFromStore, addCouponToStore, removeCouponFromStore
}
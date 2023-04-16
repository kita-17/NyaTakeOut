import service from "../common/CommonService";

const list = (params) => {
    return service({
        url: '/orders/page',
        method: 'GET',
        params
    })
}

export default {
    list
}

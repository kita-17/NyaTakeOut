import service from "../common/CommonService";

const get = (params) => {
    return service({
        url: '/setmeal/list',
        method: 'GET',
        params
    })
}

const set = (params) => {
    return service({
        url: '/setmeal/update',
        method: 'PUT',
        data: params
    })
}

const add = (params) => {
    return service({
        url: '/setmeal/add',
        method: 'POST',
        data: params
    })
}
const del = (params) => {
    return service({
        url: '/setmeal/remove',
        method: 'DELETE',
        params
    })
}


export default {
    get, set, add, del
}
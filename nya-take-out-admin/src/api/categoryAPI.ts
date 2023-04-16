import service from "../common/CommonService";

const get = (params) => {
    return service({
        url: '/category',
        method: 'GET',
        params
    })
}

const add = (params) => {
    return service({
        url: '/category',
        method: 'POST',
        data: params
    })
}

const set = (params) => {
    return service({
        url: '/category',
        method: 'PUT',
        data: params
    })
}
const list = (params) => {
    return service({
        url: '/category/list',
        method: 'GET',
        data: params
    })
}
const del = (ids) => {
    return service({
        url: '/category',
        method: 'DELETE',
        data: ids
    })
}
export default {
    get, set, del, add, list
}

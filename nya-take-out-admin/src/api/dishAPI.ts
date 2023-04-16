import service from "../common/CommonService";

const get = (params) => {
    return service({
        url: '/dish',
        method: 'GET',
        params
    })
}

const set = (params) => {
    return service({
        url: '/dish/update',
        method: 'PUT',
        data: params
    })
}

const add = (params) => {
    return service({
        url: '/dish/add',
        method: 'POST',
        data: params
    })
}
const del = (params) => {
    return service({
        url: '/dish/remove',
        method: 'DELETE',
        params
    })
}


export default {
    get, set, add, del
}
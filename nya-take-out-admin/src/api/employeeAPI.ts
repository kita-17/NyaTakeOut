import service from "../common/CommonService";

const login = (params) => {
    return service({
        url: '/employee/login',
        method: 'POST',
        data: params
    })
}

const get = (params) => {
    return service({
        url: '/employee',
        method: 'GET',
        params
    })
}

const set = (params) => {
    return service({
        url: '/employee',
        method: 'PUT',
        data: params
    })
}

const add = (params) => {
    return service({
        url: '/employee',
        method: 'POST',
        data: params
    })
}

export default {
    login, set, add, get
}


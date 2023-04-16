const isCellPhone = (val) => {
    return /^1([345678])\d{9}$/.test(val);
}

//校验账号
const checkUserName = (value) => {
    return value.length > 20 || value.length < 3
}

//校验姓名
const checkName = (value) => {
    return value.length > 12
}

const checkPhone = (value) => {
    return isCellPhone(value)
}

const validID = (value) => {
    // 身份证号码为15位或者18位，15位时全为数字，18位前17位为数字，最后一位是校验位，可能为数字或字符X
    let reg = /(^\d{15}$)|(^\d{18}$)|(^\d{17}(\d|X|x)$)/
    return reg.test(value)
}

export default {
    checkName, checkUserName, checkPhone, validID
}
// 验证手机号
const validatorPhone = (value: string) => {
    const regPhone: RegExp = /^1[3-9]\d{9}$/;
    // 验证座机号码
    const regLandline: RegExp = /^\d{7,8}$/;
    if (!value || regPhone.test(value) || regLandline.test(value)) {
        return true;
    }
    return false
}

export default {
    validatorPhone
}
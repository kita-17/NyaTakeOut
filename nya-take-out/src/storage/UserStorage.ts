import {UserProfile} from "@/entitys/nEntity";

const login = (token: string) => {
    uni.setStorage({
        key: 'UserProfile',
        data: token,
    });
}
//Authorization

const logout = () => {
    uni.removeStorage({key: 'UserProfile'});
}

const checkAuthorization = () => {
    const value = uni.getStorageSync('UserProfile');
    if (value && value != '') {
        const auth = value.token;
        if (auth && auth != '') {
            return true;
        }
    }
    return false;
}

const getToken = () => {
    const value = uni.getStorageSync('UserProfile');
    if (value && value != '') {
        const auth = value.token;
        if (auth && auth != '') {
            return auth;
        }
    }
    return '';
}

const getUserProfile = () => {
    const value = uni.getStorageSync('UserProfile');
    return {
        id: value.id,
        username: value.username,
        avatar: value.avatar
    } as UserProfile
}

const getUserId = () => {
    const value = uni.getStorageSync('UserProfile');
    if (value && value != '') {
        const id = value.id;
        if (id && id != '') {
            return id;
        }
    }
    return ''
}

export default {
    login, logout, checkAuthorization, getToken, getUserProfile, getUserId
}
function get(key, defaultValue) {
    const value = localStorage.getItem(key);
    return value ? JSON.parse(value) : defaultValue;
}

function set(key, defaultValue) {
    if (defaultValue === null) {
        localStorage.removeItem(key)
    } else {
        localStorage.setItem(key, JSON.stringify(defaultValue))
    }
}

function setStoreId(storeId) {
    localStorage.setItem("storeId", storeId)
}

function getStoreId() {
    return localStorage.getItem("storeId")
}

export default {
    set, get, setStoreId, getStoreId
}

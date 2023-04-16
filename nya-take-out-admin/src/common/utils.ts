const handleSort = (property) => {
    return function (a, b) {
        const val1 = a[property];
        const val2 = b[property];
        return val1 - val2;
    }
}

export default {
    handleSort
}
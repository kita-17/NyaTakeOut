const getImageUrl = (url: string) => {
    return 'http://127.0.0.1:8080/image/download?name=' + url
}

export {
    getImageUrl
}
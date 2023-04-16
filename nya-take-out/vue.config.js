// vue.config.js
const UniappDevtoolLauncherWebpackPlugin = require("uniapp-devtool-launcher-webpack-plugin");

module.exports = {
    configureWebpack: {
        plugins: [
            new UniappDevtoolLauncherWebpackPlugin()
        ]
    },
    transpileDependencies:['@dcloudio/uni-ui'],
    css: {
        loaderOptions: {
            // 给 less-loader 传递 Less.js 相关选项
            less: {}
        }
    },
}
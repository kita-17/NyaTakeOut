import {createApp} from 'vue';
import Antd from 'ant-design-vue';

import 'ant-design-vue/dist/antd.css';
import App from './App.vue';
import DefaultLayout from './layouts/Default.vue'
import DashboardLayout from './layouts/MainLayout.vue'
import STable from '@surely-vue/table'
import router from './router/index'

import './scss/app.scss';
import '@surely-vue/table/dist/index.less'

const app = createApp(App);

app.use(Antd)
    .use(router)
    .use(STable)
    .component("layout-default", DefaultLayout)
    .component("layout-dashboard", DashboardLayout)
    .mount('#app');
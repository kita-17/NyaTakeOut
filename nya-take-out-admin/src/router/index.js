import {createRouter, createWebHistory} from "vue-router";
import storage from "@/common/storage";

let routes = [
    {
        // will match everything
        path: '/:pathMatch(.*)',
        component: () => import('@/views/404.vue')
    },
    {
        path: '/',
        name: '主页',
        redirect: '/dish'
    },
    {
        path: '/employee',
        name: '员工管理',
        layout: "dashboard",
        meta: {
            authorization: true,
            permissions: 1000
        },
        component: () => import('@/views/Employee.vue')
    },
    {
        path: '/dish',
        name: '菜品管理',
        layout: "dashboard",
        meta: {
            authorization: true
        },
        component: () => import('@/views/DishList.vue')
    },
    {
        path: '/category',
        name: '分类管理',
        layout: "dashboard",
        meta: {
            authorization: true
        },
        component: () => import('@/views/CategoryList.vue')
    },
    {
        path: '/coupon',
        name: '优惠券管理',
        layout: "dashboard",
        meta: {
            authorization: true
        },
        component: () => import('@/views/CouponList.vue')
    },
    {
        path: '/setmeal',
        name: '套餐管理',
        layout: "dashboard",
        meta: {
            authorization: true
        },
        component: () => import('@/views/SetmealList.vue')
    },
    {
        path: '/orders',
        name: '订单管理',
        layout: "dashboard",
        meta: {
            authorization: true
        },
        component: () => import('@/views/OrdersList.vue')
    },
    {
        path: '/sign-in',
        name: '登入页面',
        component: () => import('@/views/Sign-In.vue')
    }
    // {
    //     path: '/sign-up',
    //     name: '注册页面',
    //     component: () => import('@/views/Sign-Up.vue'),
    // }
];

// Adding layout property from each route to the meta
// object so it can be accessed later.
function addLayoutToRoute(route, parentLayout = "default") {
    route.meta = route.meta || {};
    route.meta.layout = route.layout || parentLayout;

    if (route.children) {
        route.children = route.children.map((childRoute) => addLayoutToRoute(childRoute, route.meta.layout));
    }
    return route;
}

routes = routes.map((route) => addLayoutToRoute(route));

const router = createRouter({
    history: createWebHistory("/"),
    routes
});
// 全局前置守卫
router.beforeEach((to, from, next) => {
    //登陆判断
    //获取登录信息
    const user = storage.get("authorization", "");
    //当没有登陆时
    if (!user || user === '') {
        //判断前往的页面是否需要登录验证
        if (to.meta.authorization) {
            next("/sign-in");
        } else next();
    } else {
        //当有登录信息时, 判断前往的页面是登陆页面
        if (to.path === '/sign-in') next('/');
        //权限足够时
        if (to.meta.permissions > user.permissions) {
            next(from.path);
        } else next();
    }
});

// 全局后置钩子
router.afterEach((to, from) => {
    const _title = to.name;
    if (_title) {
        window.document.title = window.document.title + " | " + _title;
    }
});

export default router;

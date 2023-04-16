package com.watson.common;

public class CacheConstant {
    public static final String LOGIN_CODE = "login:code:";
    public static final String EMPLOYEE_TOKEN = "employee:token:";
    //空对象缓存有效时间
    public static final long CACHE_NULL_TTL = 1L;

    //优惠券库存
    public static final String COUPON_COUNT = "coupon:count:";

    // ================ 与用户信息相关的缓存 ================
    //用户token信息
    public static final String USER_TOKEN = "user:token:";
    //用户地址信息
    public static final String USER_ADDRESS = "user:address:";
    //用户收藏的店铺信息
    public static final String USER_FAVORITE = "user:favorite:";
    //用户评论信息
    public static final String USER_COMMENT = "user:comment:";
    //用户所持有的优惠券
    public static final String USER_COUPON = "user:coupon:";
    //用户所拥有的订单
    public static final String USER_ORDERS = "user:orders:";
    // ================ 与店铺信息相关的缓存 ================
    //店铺信息
    public static final String STORE_DATA = "store:data:";
    //店铺菜品分类
    public static final String STORE_CATEGORY = "store:category:";
    //店铺菜品信息
    public static final String STORE_DISH = "store:dish:";
    //店铺套餐信息
    public static final String STORE_SETMEAL = "store:setmeal:";
    //店铺优惠券信息
    public static final String STORE_COUPON = "store:coupon:";
    //店铺评论信息
    public static final String STORE_COMMENT = "store:comment:";

    // ================ 与店铺信息相关的缓存 ================
    public static final String ORDER_DATA = "order:data:";

    // ================ 与评论相关的缓存 ================
    //用户点赞的评论信息
    public static final String COMMENT_LIKED = "comment:liked:";
}

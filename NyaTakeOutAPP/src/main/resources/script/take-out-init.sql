create table if not exists address_book
(
    id            bigint                       not null comment '主键'
        primary key,
    user_id       bigint                       not null comment '用户id',
    consignee     varchar(50)                  not null comment '收货人',
    sex           tinyint                      null comment '性别 0 女 1 男',
    phone         varchar(11)                  not null comment '手机号',
    province_code varchar(12) charset utf8mb4  null comment '省级区划编号',
    province_name varchar(32) charset utf8mb4  null comment '省级名称',
    city_code     varchar(12) charset utf8mb4  null comment '市级区划编号',
    city_name     varchar(32) charset utf8mb4  null comment '市级名称',
    district_code varchar(12) charset utf8mb4  null comment '区级区划编号',
    district_name varchar(32) charset utf8mb4  null comment '区级名称',
    detail        varchar(200) charset utf8mb4 null comment '详细地址',
    is_default    tinyint(1) default 0         not null comment '默认 0 否 1是',
    create_time   datetime                     not null comment '创建时间',
    update_time   datetime                     not null comment '更新时间',
    create_user   bigint                       not null comment '创建人',
    update_user   bigint                       not null comment '修改人'
)
    comment '地址管理' collate = utf8mb3_bin;

create table if not exists category
(
    id          bigint        not null comment '主键'
        primary key,
    store_id    bigint        not null comment '分类所属商店Id',
    type        int           null comment '类型   1 菜品分类 2 套餐分类',
    name        varchar(64)   not null comment '分类名称',
    sort        int default 0 not null comment '顺序',
    create_time datetime      not null comment '创建时间',
    update_time datetime      not null comment '更新时间',
    create_user bigint        not null comment '创建人',
    update_user bigint        not null comment '修改人'
)
    comment '菜品及套餐分类' collate = utf8mb3_bin;

create table if not exists comment
(
    id          bigint auto_increment
        primary key,
    user_id     bigint        not null,
    store_id    bigint        not null,
    order_id    bigint        not null,
    content     varchar(240)  null,
    likes       int default 0 not null,
    create_date date          not null
)
    comment '评论表';

create table if not exists comment_image
(
    id         bigint auto_increment
        primary key,
    comment_id bigint       not null comment '该图片对应的评论Id',
    file_name  varchar(120) not null comment '图片文件名'
)
    comment '评论关联的图片';

create table if not exists comment_like
(
    id          int auto_increment
        primary key,
    comment_id  bigint not null comment '评论ID',
    user_id     bigint not null comment '用户id',
    create_date date   null comment '点赞时间'
)
    comment '用户点赞其他人评论记录';

create table if not exists coupon
(
    id               bigint                             not null comment '主键'
        primary key,
    storeId          bigint                             not null comment '关联店铺Id',
    name             varchar(30)                        not null comment '优惠券名字',
    type             int      default 0                 not null comment '优惠券类型。0 普通券 | 1 限购券',
    rule             varchar(100)                       null comment '优惠券使用规则（优惠券描述）',
    discount         int      default 0                 not null comment '优惠后金额(20减10中的10)',
    coupon_condition int      default 0                 not null comment '使用条件(20减10中的20)',
    count            int      default 0                 not null comment '优惠券库存',
    valid_days       int      default 0                 not null comment '有效天数',
    start_date       datetime default CURRENT_TIMESTAMP null comment '优惠券开始售卖的时间',
    end_date         datetime default CURRENT_TIMESTAMP null comment '优惠券售卖结束的时间'
)
    comment '优惠券表';

create table if not exists dish
(
    id          bigint         not null comment '主键'
        primary key,
    name        varchar(64)    not null comment '菜品名称',
    store_id    bigint         null comment '菜品所属商店Id',
    category_id bigint         not null comment '菜品分类id',
    price       decimal(10, 2) null comment '菜品价格',
    image       varchar(200)   not null comment '图片',
    description varchar(400)   null comment '描述信息',
    status      int default 1  not null comment '0 停售 1 起售',
    sort        int default 0  not null comment '顺序',
    create_time datetime       not null comment '创建时间',
    update_time datetime       not null comment '更新时间',
    create_user bigint         not null comment '创建人',
    update_user bigint         not null comment '修改人',
    sale        int default 0  not null comment '商品月售数量',
    type        int default 1  not null comment '商品类型'
)
    comment '菜品管理' collate = utf8mb3_bin;

create table if not exists dish_flavor
(
    id          bigint       not null comment '主键'
        primary key,
    dish_id     bigint       not null comment '菜品',
    name        varchar(64)  not null comment '口味名称',
    value       varchar(500) null comment '口味数据list',
    create_time datetime     not null comment '创建时间',
    update_time datetime     not null comment '更新时间',
    create_user bigint       not null comment '创建人',
    update_user bigint       not null comment '修改人'
)
    comment '菜品口味关系表' collate = utf8mb3_bin;

create table if not exists employee
(
    id          bigint        not null comment '主键'
        primary key,
    name        varchar(32)   not null comment '姓名',
    storeId     bigint        not null comment '该员工所属店铺Id',
    username    varchar(32)   not null comment '用户名',
    password    varchar(64)   not null comment '密码',
    phone       varchar(11)   not null comment '手机号',
    sex         varchar(2)    not null comment '性别',
    id_number   varchar(18)   not null comment '身份证号',
    status      int default 1 not null comment '状态 0:禁用，1:正常',
    create_time datetime      not null comment '创建时间',
    update_time datetime      not null comment '更新时间',
    create_user bigint        not null comment '创建人',
    update_user bigint        not null comment '修改人',
    permissions int default 1 not null comment '权限等级',
    constraint idx_username
        unique (username)
)
    comment '管理端员工信息' collate = utf8mb3_bin;

create table if not exists order_detail
(
    id          bigint         not null comment '主键'
        primary key,
    name        varchar(50)    null comment '名字',
    image       varchar(100)   null comment '图片',
    order_id    bigint         not null comment '订单id',
    dish_id     bigint         null comment '菜品id',
    setmeal_id  bigint         null comment '套餐id',
    dish_flavor varchar(50)    null comment '口味',
    amount      int default 1  not null comment '数量',
    price       decimal(10, 2) not null comment '金额'
)
    comment '订单明细表' collate = utf8mb3_bin;

create table if not exists orders
(
    id              bigint         not null comment '主键'
        primary key,
    number          varchar(50)    null comment '订单号',
    store_id        bigint         null comment '订单关联的店铺Id',
    status          int default 1  not null comment '订单状态 1待付款，2待派送，3已派送，4已完成，5已取消',
    user_id         bigint         not null comment '下单用户',
    couponId        bigint         null comment '本订单使用的优惠券Id',
    address_book_id bigint         not null comment '地址id',
    order_time      datetime       not null comment '下单时间',
    checkout_time   datetime       not null comment '结账时间',
    pay_method      int default 1  not null comment '支付方式 1微信,2支付宝',
    price           decimal(10, 2) not null comment '实收金额',
    remark          varchar(100)   null comment '备注'
)
    comment '订单表' collate = utf8mb3_bin;

create table if not exists setmeal
(
    id          bigint         not null comment '主键'
        primary key,
    category_id bigint         not null comment '菜品分类id',
    store_id    bigint         null comment '套餐所属商店Id',
    name        varchar(64)    not null comment '套餐名称',
    price       decimal(10, 2) not null comment '套餐价格',
    status      int            null comment '状态 0:停用 1:启用',
    description varchar(512)   null comment '描述信息',
    sale        int default 0  not null,
    image       varchar(255)   null comment '图片',
    create_time datetime       not null comment '创建时间',
    update_time datetime       not null comment '更新时间',
    create_user bigint         not null comment '创建人',
    update_user bigint         not null comment '修改人',
    type        int default 2  not null comment '商品类型'
)
    comment '套餐' collate = utf8mb3_bin;

create table if not exists setmeal_dish
(
    id          bigint      not null comment '主键'
        primary key,
    setmeal_id  varchar(32) not null comment '套餐id ',
    dish_id     varchar(32) not null comment '菜品id',
    create_time datetime    not null comment '创建时间',
    update_time datetime    not null comment '更新时间',
    create_user bigint      not null comment '创建人',
    update_user bigint      not null comment '修改人'
)
    comment '套餐菜品关系' collate = utf8mb3_bin;

create table if not exists store
(
    id          bigint       not null comment '商店id'
        primary key,
    title       varchar(50)  null comment '商店名称',
    ownerId     bigint       not null comment '商店所有者Id, 关联Employee的ID',
    icon        varchar(50)  null comment '商店图标地址',
    description varchar(200) null comment '商店描述',
    status      char         null comment '商店状态'
)
    comment '商店表';

create table if not exists user
(
    id       bigint           not null comment '主键'
        primary key,
    nickname varchar(20)      not null comment '用户昵称',
    phone    char(11)         not null comment '用户手机号码',
    avatar   varchar(50)      null comment '用户头像地址',
    status   char default '0' null comment '账号状态 0 启用 | 1禁用',
    constraint phone
        unique (phone)
)
    comment '小程序端用户表';

create table if not exists user_coupon
(
    id       int auto_increment
        primary key,
    userId   bigint           not null comment '用户id',
    couponId bigint           not null comment '优惠券id',
    status   char default '0' not null comment '是否已使用 0 否 | 1是',
    date     datetime         null comment '优惠券到期时间'
)
    comment '用户拥有的优惠券表';

create table if not exists user_store_favorite
(
    id      bigint auto_increment
        primary key,
    userId  bigint not null comment '用户Id',
    storeId bigint not null comment '收藏的店铺Id'
)
    comment '用户收藏的店铺';



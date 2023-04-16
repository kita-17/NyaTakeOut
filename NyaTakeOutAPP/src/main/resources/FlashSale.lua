-- 优惠券Id
local couponId = ARGV[1]
-- 用户Id
local userId = ARGV[2]
-- 订单Id
local orderId = ARGV[3]
-- 优惠券key
local couponKey = 'coupon:count:' .. couponId
-- 订单key
local orderKey = 'coupon:order:' .. couponId
-- 3.1.判断库存是否充足 get stockKey
if (tonumber(redis.call('get', couponKey)) <= 0) then
    -- 3.2.库存不足，返回1
    return 1
end
-- 3.2.判断用户是否下单 SISMEMBER orderKey userId
if (redis.call('sismember', orderKey, userId) == 1) then
    -- 3.3.存在，说明是重复下单，返回2
    return 2
end
-- 3.4.扣库存 incrby stockKey -1
redis.call('incrby', couponKey, -1)
-- 3.5.下单（保存用户）sadd orderKey userId
redis.call('sadd', orderKey, userId)
-- 添加到消息队列
redis.call('xadd', 'stream.coupon.orders', '*', 'id', couponId, 'userId', userId, 'orderId', orderId)
return 0
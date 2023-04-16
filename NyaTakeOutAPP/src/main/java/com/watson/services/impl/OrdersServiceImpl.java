package com.watson.services.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.watson.common.CacheConstant;
import com.watson.common.Result;
import com.watson.common.ResultEnum;
import com.watson.dto.OrderDTO;
import com.watson.entity.*;
import com.watson.exception.SqlCustomException;
import com.watson.services.*;
import com.watson.mapper.OrdersMapper;
import com.watson.utils.RedisUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author verrat
 * @description 针对表【orders(订单表)】的数据库操作Service实现
 * @createDate 2023-02-23 13:45:23
 */
@Slf4j
@Service
public class OrdersServiceImpl extends ServiceImpl<OrdersMapper, OrderEntity> implements OrdersService {

    @Resource
    private OrderDetailServiceImpl detailService;

    @Resource
    private AddressBookServiceImpl addressBookService;

    @Resource
    private DishServiceImpl dishService;

    @Resource
    private SetmealServiceImpl setmealService;

    @Resource
    private IStoreService storeService;

    @Resource
    private ICommentService commentService;

    @Resource
    private UserCouponService userCouponService;

    @Resource
    private RedisUtil redisUtil;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    @Transactional
    public Result onSaveOrders(OrderDTO ordersDTO) {
        if (ordersDTO.getStoreId() == null || ordersDTO.getAddressBookId() == null) {
            return Result.error("下单失败，订单信息异常");
        }
        Long orderId = IdUtil.getSnowflakeNextId();
        //订单流水号
        String format = DateUtil.format(new Date(), "yyyyMMddHHmmss");
        String numbers = RandomUtil.randomNumbers(5);
        String orderNumber = format + numbers;

        ordersDTO.setId(orderId);
        ordersDTO.setCheckoutTime(LocalDateTime.now());
        ordersDTO.setOrderTime(LocalDateTime.now());
        ordersDTO.setNumber(orderNumber);

        //对订单内的每个商品进行处理入库
        List<OrderDetailEntity> detail = ordersDTO.getDetail().stream().peek(item -> {
            item.setOrderId(orderId);
            //该订单没项商品的数量
            Integer dishAmount = item.getAmount();
            //更新菜品销量
            if (item.getType() == 1) {//如果订单物品是商品
                LambdaUpdateWrapper<DishEntity> updateWrapper = new LambdaUpdateWrapper<>();
                updateWrapper.eq(DishEntity::getId, item.getDishId());
                updateWrapper.setSql("sale = sale + " + dishAmount);
                boolean update = dishService.update(updateWrapper);
                if (!update) {
                    throw new SqlCustomException(ResultEnum.FAIL, "下单失败");
                }
            }
            //更新套餐销量
            else if (item.getType() == 2) {//如果订单物品是套餐
                LambdaUpdateWrapper<SetmealEntity> updateWrapper = new LambdaUpdateWrapper<>();
                updateWrapper.eq(SetmealEntity::getId, item.getDishId());
                updateWrapper.setSql("sale = sale + " + dishAmount);
                boolean update = setmealService.update(updateWrapper);
                if (!update) {
                    throw new SqlCustomException(ResultEnum.FAIL, "下单失败");
                }
            }
        }).toList();
        //订单等数据写入数据库
        boolean ordersSaveResult = save(ordersDTO);
        boolean detailSaveResult = detailService.saveBatch(detail);
        if (!ordersSaveResult && !detailSaveResult) {
            throw new SqlCustomException(ResultEnum.FAIL, "下单失败");
        }
        //优惠券扣除
        if (ordersDTO.getCouponId() != null) {
            LambdaUpdateWrapper<UserCouponEntity> couponUpdateWrapper = new LambdaUpdateWrapper<>();
            couponUpdateWrapper.eq(UserCouponEntity::getCouponId, ordersDTO.getCouponId());
            couponUpdateWrapper.eq(UserCouponEntity::getUserId, ordersDTO.getUserId());
            couponUpdateWrapper.setSql("status=1");
            boolean couponRes = userCouponService.update(couponUpdateWrapper);
            if (!couponRes) {
                throw new RuntimeException("优惠券扣除失败");
            }
        }
        stringRedisTemplate.delete(CacheConstant.USER_ORDERS + ordersDTO.getUserId());
        return Result.success("下单成功");
    }

    @Override
    public void onUpdateOrders(OrderDTO ordersDTO) {

    }

    /**
     * 用户端展示的订单
     *
     * @param id
     *
     * @return
     */
    @Override
    public List<OrderDTO> onListOrders(Long id) {
        String key = CacheConstant.USER_ORDERS + id;
        return redisUtil.getList(key, OrderDTO.class, 30L, TimeUnit.MINUTES, db -> {
            LambdaQueryWrapper<OrderEntity> orderLWQ = new LambdaQueryWrapper<>();
            orderLWQ.eq(OrderEntity::getUserId, id);
            //根据下单时间倒序排序
            orderLWQ.orderByDesc(OrderEntity::getOrderTime);
            List<OrderEntity> ordersList = list(orderLWQ);
            return getOrdersDTO(ordersList);
        });
    }

    /**
     * 给店铺后台用的，这个操作频率会比较少，就不做缓存了
     *
     * @param storeId
     * @param value
     *
     * @return
     */
    @Override
    public List<OrderDTO> onPageOrders(Long storeId, String value) {
        LambdaQueryWrapper<OrderEntity> orderLWQ = new LambdaQueryWrapper<>();
        orderLWQ.eq(OrderEntity::getStoreId, storeId);
        //根据下单时间倒序排序
        orderLWQ.orderByDesc(OrderEntity::getOrderTime);
        List<OrderEntity> ordersList = list(orderLWQ);
        return getOrdersDTO(ordersList);
    }

    /**
     * 装配订单的细节信息
     *
     * @param ordersEntities
     *
     * @return
     */
    private List<OrderDTO> getOrdersDTO(List<OrderEntity> ordersEntities) {
        return ordersEntities.stream().map(
                i -> {
                    OrderDTO dto = new OrderDTO();
                    BeanUtils.copyProperties(i, dto);
                    //装载订单商品详情信息
                    LambdaQueryWrapper<OrderDetailEntity> detailLWQ = new LambdaQueryWrapper<>();
                    detailLWQ.eq(OrderDetailEntity::getOrderId, dto.getId());
                    List<OrderDetailEntity> detailEntities = detailService.list(detailLWQ);
                    dto.setDetail(detailEntities);
                    //装载订单地址信息
                    LambdaQueryWrapper<AddressBookEntity> addressLWQ = new LambdaQueryWrapper<>();
                    addressLWQ.eq(AddressBookEntity::getId, dto.getAddressBookId());
                    AddressBookEntity addressBook = addressBookService.getOne(addressLWQ);
                    dto.setAddress(addressBook);
                    //设置key前端用
                    dto.setKey(dto.getId().toString());
                    //订单对应的店铺信息
                    String storeName = storeService.getById(i.getStoreId()).getTitle();
                    dto.setStoreName(storeName);
                    //订单是否已被评论
                    LambdaQueryWrapper<CommentEntity> commentQuery = new LambdaQueryWrapper<>();
                    commentQuery
                            .eq(CommentEntity::getUserId, i.getUserId())
                            .eq(CommentEntity::getOrderId, i.getId())
                            .eq(CommentEntity::getStoreId, i.getStoreId());
                    CommentEntity one = commentService.getOne(commentQuery);
                    dto.setCommented(one != null);
                    return dto;
                }
        ).toList();
    }
}
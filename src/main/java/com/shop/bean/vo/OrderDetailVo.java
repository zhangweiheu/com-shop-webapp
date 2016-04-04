package com.shop.bean.vo;

import com.shop.bean.BaseObject;

import java.util.Date;

/**
 * Created by zhang on 2016/4/1.
 */
public class OrderDetailVo extends BaseObject{
    /**  */
    private Integer id;

    /** 用户id:购物车时才有值 */
    private Integer uid;

    /** 关联订单id */
    private Integer orderId;

    /** 商品id */
    private Integer goodsId;

    /** 商品数量 */
    private Integer goodsCount;

    /** 商品价格 */
    private Double goodsPrice;

    /** 本单总价 */
    private Double totalPrice;

    /** 更新时间 */
    private Date updateAt;

    /** 创建时间 */
    private Date createAt;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUid() {
        return uid;
    }

    public void setUid(Integer uid) {
        this.uid = uid;
    }

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public Integer getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(Integer goodsId) {
        this.goodsId = goodsId;
    }

    public Integer getGoodsCount() {
        return goodsCount;
    }

    public void setGoodsCount(Integer goodsCount) {
        this.goodsCount = goodsCount;
    }

    public Double getGoodsPrice() {
        return goodsPrice;
    }

    public void setGoodsPrice(Double goodsPrice) {
        this.goodsPrice = goodsPrice;
    }

    public Double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(Double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public Date getUpdateAt() {
        return updateAt;
    }

    public void setUpdateAt(Date updateAt) {
        this.updateAt = updateAt;
    }

    public Date getCreateAt() {
        return createAt;
    }

    public void setCreateAt(Date createAt) {
        this.createAt = createAt;
    }
}

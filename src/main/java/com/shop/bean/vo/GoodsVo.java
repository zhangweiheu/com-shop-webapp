package com.shop.bean.vo;

import com.shop.bean.BaseObject;
import com.shop.core.mybatis.GoodsStatusEnum;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;

/**
 * Created by zhang on 2016/3/25.
 */
public class GoodsVo extends BaseObject {
    /**
     * 主键
     */
    private Integer id;

    /**
     * 商品名称
     */
    private String name;

    /**
     * 价格
     */
    private Double price;

    /**
     * 剩余数量
     */
    private Integer remain;

    /**
     * 销售量
     */
    private Integer sellCount;

    /**
     * 打折
     */
    private Double discount;

    /**
     * 状态：0正常 | 1下架 | 2推荐
     */
    private GoodsStatusEnum status;

    /**
     * 提供商ID
     */
    private Integer providerId;

    /**
     * 提供商姓名
     */
    private String providerName;

    /**
     * 提供商联系方式
     */
    private Integer providerPhone;

    /**
     * 创建时间
     */
    private Date createAt;

    /**
     * 更新时间
     */
    private Date updateAt;

    /**file*/
    private MultipartFile file;

    /** 商品图片 */
    private String linkPhoto;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Integer getRemain() {
        return remain;
    }

    public void setRemain(Integer remain) {
        this.remain = remain;
    }

    public Integer getSellCount() {
        return sellCount;
    }

    public void setSellCount(Integer sellCount) {
        this.sellCount = sellCount;
    }

    public Double getDiscount() {
        return discount;
    }

    public void setDiscount(Double discount) {
        this.discount = discount;
    }

    public GoodsStatusEnum getStatus() {
        return status;
    }

    public void setStatus(GoodsStatusEnum status) {
        this.status = status;
    }

    public Integer getProviderId() {
        return providerId;
    }

    public void setProviderId(Integer providerId) {
        this.providerId = providerId;
    }

    public String getProviderName() {
        return providerName;
    }

    public void setProviderName(String providerName) {
        this.providerName = providerName;
    }

    public Integer getProviderPhone() {
        return providerPhone;
    }

    public void setProviderPhone(Integer providerPhone) {
        this.providerPhone = providerPhone;
    }

    public Date getCreateAt() {
        return createAt;
    }

    public void setCreateAt(Date createAt) {
        this.createAt = createAt;
    }

    public Date getUpdateAt() {
        return updateAt;
    }

    public void setUpdateAt(Date updateAt) {
        this.updateAt = updateAt;
    }

    public MultipartFile getFile() {
        return file;
    }

    public void setFile(MultipartFile file) {
        this.file = file;
    }

    public String getLinkPhoto() {
        return linkPhoto;
    }

    public void setLinkPhoto(String linkPhoto) {
        this.linkPhoto = linkPhoto;
    }
}

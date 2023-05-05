package org.example.entity;

import java.io.Serializable;
import java.sql.Time;
import java.sql.Timestamp;

public class ProductSale implements Serializable {
    private String productId;

    private String productCode;

    private Integer count;

    private Integer unitPrice;

    private Integer total;

    private Timestamp saleTimeStamp;

    public ProductSale() {
    }

    public ProductSale(String productId, String productCode, Integer count, Integer unitPrice, Integer total) {
        this.productId = productId;
        this.productCode = productCode;
        this.count = count;
        this.unitPrice = unitPrice;
        this.total = total;
    }

    public ProductSale(String productId, String productCode, Integer count, Integer unitPrice, Integer total, Timestamp saleTimeStamp) {
        this.productId = productId;
        this.productCode = productCode;
        this.count = count;
        this.unitPrice = unitPrice;
        this.total = total;
        this.saleTimeStamp = saleTimeStamp;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public Integer getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(Integer unitPrice) {
        this.unitPrice = unitPrice;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public Timestamp getSaleTimeStamp() {
        return saleTimeStamp;
    }

    public void setSaleTimeStamp(Timestamp saleTimeStamp) {
        this.saleTimeStamp = saleTimeStamp;
    }
}

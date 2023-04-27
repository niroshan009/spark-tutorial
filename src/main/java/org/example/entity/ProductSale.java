package org.example.entity;

public class ProductSale {
    private String productId;

    private Integer count;

    private Integer unitPrice;

    private Integer total;

    public ProductSale(String productId, Integer count, Integer unitPrice, Integer total) {
        this.productId = productId;
        this.count = count;
        this.unitPrice = unitPrice;
        this.total = total;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
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
}

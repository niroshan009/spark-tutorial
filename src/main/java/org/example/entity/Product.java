package org.example.entity;

import java.io.Serializable;

public class Product implements Serializable {

    private String productId;
    private String productCode;
    private Integer quantity;

    public Product() {
    }

    public Product(String productId, String productCode, Integer quantity) {
        this.productId = productId;
        this.productCode = productCode;
        this.quantity = quantity;
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

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}

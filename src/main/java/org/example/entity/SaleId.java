package org.example.entity;

import java.io.Serializable;
import java.sql.Timestamp;

public class SaleId implements Serializable {

    private String productId;

    private String productCode;

    private Timestamp saleTimeSatamp;

    public SaleId(String productId, String productCode) {
        this.productId = productId;
        this.productCode = productCode;
    }

    public SaleId(String productId, String productCode, Timestamp saleTimeSatamp) {
        this.productId = productId;
        this.productCode = productCode;
        this.saleTimeSatamp = saleTimeSatamp;
    }

    public Timestamp getSaleTimeSatamp() {
        return saleTimeSatamp;
    }

    public void setSaleTimeSatamp(Timestamp saleTimeSatamp) {
        this.saleTimeSatamp = saleTimeSatamp;
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
}

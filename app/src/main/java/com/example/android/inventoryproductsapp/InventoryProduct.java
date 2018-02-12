package com.example.android.inventoryproductsapp;

/**
 * Created by UFO_24 on 12-02-2018.
 */

public class InventoryProduct {

    private String productName;
    private int productQuantity;
    private String productPrice;
    private String supplierName;
    private String supplierPhone;
    private String supplierEmail;
    private String image;

    public InventoryProduct(String productName, int productQuantity, String productPrice, String supplierName,
                            String supplierEmail, String supplierPhone, String image) {

       this.productName = productName;
        this.productQuantity = productQuantity;
       this.productPrice = productPrice;
        this.supplierEmail = supplierEmail;
        this.supplierName = supplierName;
        this.supplierPhone = supplierPhone;
        this.image = image;


    }

    public String getProductName() {
        return productName;
    }

    public int getProductQuantity() {
        return productQuantity;
    }

    public String getProductPrice() {
        return productPrice;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public String getSupplierPhone() {
        return supplierPhone;
    }

    public String getSupplierEmail() {
        return supplierEmail;
    }

    public String getImage() {
        return image;
    }


}

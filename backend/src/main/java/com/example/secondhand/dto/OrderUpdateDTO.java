package com.example.secondhand.dto;

public class OrderUpdateDTO {

    private Integer status;

    private String paymentMethod;

    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
}

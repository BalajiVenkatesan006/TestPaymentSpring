package com.practise.demo.paymentdemo.eventrabbitmq;

import com.fasterxml.jackson.databind.util.JSONPObject;
import com.practise.demo.paymentdemo.model.Account;
import com.sun.xml.internal.ws.developer.Serialization;

import java.io.Serializable;

public class Notification implements Serializable {

    public Notification(String notificationType, String accountNumber,String totalAmount) {
        this.notificationType=notificationType;
        this.accountNumber=accountNumber;
        this.totalAmount=totalAmount;
    }
    private String notificationType;
    private String accountNumber;
    private String totalAmount;

    public String getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(String notificationType) {
        this.notificationType = notificationType;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(String totalAmount) {
        this.totalAmount = totalAmount;
    }
}
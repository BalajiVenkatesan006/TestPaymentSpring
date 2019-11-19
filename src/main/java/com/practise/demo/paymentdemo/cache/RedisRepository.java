package com.practise.demo.paymentdemo.cache;

import com.practise.demo.paymentdemo.model.Account;

public interface RedisRepository {
    void addAccounttocache(String accountnumber,String totalAmount);
    String getAccountfromCache(String key);
}

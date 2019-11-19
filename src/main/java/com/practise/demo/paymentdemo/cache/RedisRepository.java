package com.practise.demo.paymentdemo.cache;

import com.practise.demo.paymentdemo.model.Account;

public interface RedisRepository {
    void addAccounttocache(Account account);
    String getAccountfromCache(String key);
}

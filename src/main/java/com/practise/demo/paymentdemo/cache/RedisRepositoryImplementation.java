package com.practise.demo.paymentdemo.cache;

import com.practise.demo.paymentdemo.model.Account;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;

@Repository
public class RedisRepositoryImplementation implements RedisRepository  {

    private RedisTemplate<String,String> redisTemplate;
    private ValueOperations valueOperations;

    @Autowired
    public RedisRepositoryImplementation(RedisTemplate<String, String> redisTemplate){
        this.redisTemplate = redisTemplate;
    }

    @PostConstruct
    private void init(){
        valueOperations = redisTemplate.opsForValue();
    }

    @Override
    public void addAccounttocache(String accountnumber,String totalAmount) {
        String key = "accountNumber::"+accountnumber;
        valueOperations.set(key,String.valueOf(totalAmount));
    }

    @Override
    public String getAccountfromCache(String key) {
        return valueOperations.get("accountNumber::"+key).toString();
    }
}

package com.practise.demo.paymentdemo.controller;

import com.practise.demo.paymentdemo.cache.RedisRepositoryImplementation;
import com.practise.demo.paymentdemo.exception.ResourceNotFoundException;
import com.practise.demo.paymentdemo.model.Account;
import com.practise.demo.paymentdemo.respository.AccountRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class AccountController {
    Logger LOGGER = LoggerFactory.getLogger(AccountController.class);
    @Autowired
    AccountRepo accountRepo;
    @Autowired
    RedisRepositoryImplementation redisRepositoryImplementation;

    @GetMapping("/account/{accountNumber}")
    public String getTotalSumfromAccount(@PathVariable(value = "accountNumber") String accountNumber){
        String totalamount = redisRepositoryImplementation.getAccountfromCache(accountNumber);
        if(totalamount!=null)
            return totalamount;
        Account account =  accountRepo.getOne(accountNumber);
        LOGGER.debug("Got the value from the DB");
        if(account==null){
             throw new ResourceNotFoundException("Account","Account number","not found");
        }
        return account.getTotalAmount().toString();
    }

}

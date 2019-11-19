package com.practise.demo.paymentdemo.controller;

import com.practise.demo.paymentdemo.Response.ServerResponse;
import com.practise.demo.paymentdemo.cache.RedisRepositoryImplementation;
import com.practise.demo.paymentdemo.common.TypeEnum;
import com.practise.demo.paymentdemo.model.Account;
import com.practise.demo.paymentdemo.model.Transaction;
import com.practise.demo.paymentdemo.model.UserAccount;
import com.practise.demo.paymentdemo.respository.AccountRepo;
import com.practise.demo.paymentdemo.respository.TransactionRepo;
import com.practise.demo.paymentdemo.respository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/pay")
public class TransactionController {

    @Autowired
    TransactionRepo transactionRepo;

    @Autowired
    UserRepo userRepo;

    @Autowired
    AccountRepo accountRepo;

    @Autowired
    RedisRepositoryImplementation redisRepositoryImplementation;

    @PostMapping("/paydata")
    public ServerResponse pay(@Valid  @RequestBody Transaction transaction){
        UserAccount fromuserAccount = userRepo.findUserAccountByAccountNumber(transaction.getFromAccountNumber());
        UserAccount toUserAccount = userRepo.findUserAccountByAccountNumber(transaction.getToAccountNumber());
        if(fromuserAccount ==null){
            return new ServerResponse(false," From User Account doesnt exist");
        }
        if(toUserAccount==null){
            return new ServerResponse(false,"To Account doesnt exist");
        }
        if(transaction.getType().equals(TypeEnum.SALARY.name())){
            if(fromuserAccount.getPassKey().equals("admin123") && fromuserAccount.getName().equals("admin")){
                Account toaccount = accountRepo.getOne(transaction.getToAccountNumber());
                transactionRepo.save(transaction);
                toaccount.setTotalAmount(toaccount.getTotalAmount()+transaction.getTransactionAmount());
                accountRepo.save(toaccount);
                redisRepositoryImplementation.addAccounttocache(toaccount);
                return new ServerResponse(true,"Transaction Complete success fully Transaction Id is :  "+transaction.getTransactionId());
            }
            else{
                 return new ServerResponse(false,"Invalid admin credentials");
            }
        }
        if(transaction.getPassKey().equals(fromuserAccount.getPassKey())){
            if(transaction.getType().equals(TypeEnum.DEBIT.name())){
                Account Fromaccount = accountRepo.getOne(transaction.getFromAccountNumber());
                Account toaccount = accountRepo.getOne(transaction.getToAccountNumber());
                if(toaccount==null){
                    return new ServerResponse(false,"Please specify the to Account Number");
                }
                if(transaction.getTransactionAmount()<=Fromaccount.getTotalAmount()){
                    transactionRepo.save(transaction);
                    Fromaccount.setTotalAmount(Fromaccount.getTotalAmount()-transaction.getTransactionAmount());
                    toaccount.setTotalAmount(toaccount.getTotalAmount()+transaction.getTransactionAmount());
                    accountRepo.save(Fromaccount);
                    accountRepo.save(toaccount);
                    redisRepositoryImplementation.addAccounttocache(Fromaccount);
                    redisRepositoryImplementation.addAccounttocache(toaccount);
                    return new ServerResponse(true,"Transaction Complete success fully Transaction Id is :  "+transaction.getTransactionId());
                }
                else{
                    return new ServerResponse(false,"Insufficient Funds");
                }
            }
            if(transaction.getType().equals(TypeEnum.CREDIT.name())){
                Account Fromaccount = accountRepo.getOne(transaction.getFromAccountNumber());
                transactionRepo.save(transaction);
                Fromaccount.setTotalAmount(Fromaccount.getTotalAmount()+transaction.getTransactionAmount());
                accountRepo.save(Fromaccount);
                redisRepositoryImplementation.addAccounttocache(Fromaccount);
                return new ServerResponse(true,"Transaction Complete success fully Transaction Id is :  "+transaction.getTransactionId());
            }
        }
        else{
            return new ServerResponse(false,"Invalid credentials");
        }
        return new ServerResponse(false,"Server Error ");

    }
    @Cacheable(value = "accountNumber",key = "#account.accountNumber")
    public Account cacheThisAccount(Account account){
        return account;
    }
}

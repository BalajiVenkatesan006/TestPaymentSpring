package com.practise.demo.paymentdemo.controller;

import com.practise.demo.paymentdemo.cache.RedisRepositoryImplementation;
import com.practise.demo.paymentdemo.exception.ResourceNotFoundException;
import com.practise.demo.paymentdemo.model.Account;
import com.practise.demo.paymentdemo.model.UserAccount;
import com.practise.demo.paymentdemo.respository.AccountRepo;
import com.practise.demo.paymentdemo.respository.UserRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import javax.validation.Valid;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api")
public class UserController {

    private Logger LOGGER = LoggerFactory.getLogger(UserController.class);
    @Autowired
    UserRepo userRepo;

    @Autowired
    AccountRepo accountRepo;

    @Autowired
    RedisRepositoryImplementation redisRepositoryImplementation;

    @GetMapping("/users")
    @ResponseBody
    public List<UserAccount> getAllNotes() {
        return userRepo.findAll();
    }

    @PostMapping("/users")
    public UserAccount createUser(@Valid @RequestBody UserAccount userAccount) {
        Long id = userRepo.findMaxId();
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
        String strDate = formatter.format(date);
        strDate =strDate.replaceAll("/","");
        String accountNumber;
        if(id!=null)
            accountNumber = strDate+(id+1);
        else
            accountNumber = strDate+1;
        userAccount.setAccountNumber(accountNumber);
        UserAccount savedUser = userRepo.save(userAccount);
        if(savedUser!=null){
            Account account = new Account();
            account.setAccountNumber(accountNumber);
            account.setTotalAmount(Long.valueOf(0));
            accountRepo.save(account);
            redisRepositoryImplementation.addAccounttocache(account);
        }
        return savedUser;
    }

    @Cacheable(value = "users", key = "#accountUserID", unless = "#result.id > 20")
    @GetMapping("/users/{accountNumber}")
    public UserAccount getUserByID(@PathVariable(value = "accountNumber") String accountUserID) {
        UserAccount userAccount = userRepo.findUserAccountByAccountNumber(accountUserID);
        if(userAccount!=null)
            return userAccount;
        else
            throw  new ResourceNotFoundException("Users","accountNumber",accountUserID);
    }

    @CachePut(value = "users", key = "#accountUserID")
    @PutMapping("/users/{accountNumber}")
    public UserAccount updateUser(@PathVariable(value = "accountNumber") String accountUserID,
                           @Valid @RequestBody UserAccount accountDetails) {

        UserAccount userAccount = userRepo.findUserAccountByAccountNumber(accountUserID);
       if(userAccount==null)
            throw  new ResourceNotFoundException("Users","accountNumber",accountUserID);
        userAccount.setName(accountDetails.getName());
        userAccount.setPassKey(accountDetails.getPassKey());
        UserAccount updatedUser = userRepo.save(userAccount);
        return updatedUser;
    }

    @CacheEvict(value = "users", allEntries=true)
    @DeleteMapping("/users/{accountNumber}")
    public ResponseEntity<?> deleteUserAccount(@PathVariable(value = "accountNumber") String accountUserID) {
        UserAccount userAccount = userRepo.findUserAccountByAccountNumber(accountUserID);
        if(userAccount==null)
            throw  new ResourceNotFoundException("Users","accountNumber",accountUserID);
        userRepo.delete(userAccount);
        return ResponseEntity.ok().build();
    }
}

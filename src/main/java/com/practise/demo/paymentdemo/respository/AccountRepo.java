package com.practise.demo.paymentdemo.respository;

import com.practise.demo.paymentdemo.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepo extends JpaRepository<Account,String> {
}

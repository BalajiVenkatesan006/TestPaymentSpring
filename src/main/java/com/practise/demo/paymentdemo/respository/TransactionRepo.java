package com.practise.demo.paymentdemo.respository;

import com.practise.demo.paymentdemo.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepo  extends JpaRepository<Transaction,Long> {
}

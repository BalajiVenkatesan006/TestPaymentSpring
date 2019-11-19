package com.practise.demo.paymentdemo.respository;

import com.practise.demo.paymentdemo.model.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepo extends JpaRepository<UserAccount,Long> {

    @Query("SELECT max(id) FROM UserAccount ")
    Long findMaxId();

    @Query("SELECT u from UserAccount u  where u.accountNumber = ?1")
    UserAccount findUserAccountByAccountNumber(String accountNumber);
}

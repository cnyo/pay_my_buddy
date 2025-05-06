package com.yoann.pay_my_buddy.repository;

import com.yoann.pay_my_buddy.model.Transaction;
import com.yoann.pay_my_buddy.model.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends CrudRepository<Transaction, Integer> {

    @Query("FROM Transaction t JOIN FETCH t.senderUser JOIN FETCH t.receiverUser WHERE t.senderUser = :user OR t.receiverUser = :user")
    List<Transaction> findAllByUser(@Param("user") User user);
}

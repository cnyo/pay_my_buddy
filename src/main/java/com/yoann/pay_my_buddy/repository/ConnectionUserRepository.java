package com.yoann.pay_my_buddy.repository;

import com.yoann.pay_my_buddy.model.ConnectionUser;
import com.yoann.pay_my_buddy.model.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConnectionUserRepository extends CrudRepository<ConnectionUser, Integer> {
    Iterable<ConnectionUser> findByUser(User user);

    Iterable<ConnectionUser> findByAssociatedUser(User user);
}

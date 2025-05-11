package com.yoann.pay_my_buddy.repository;

import com.yoann.pay_my_buddy.model.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {
    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    @Query(value="SELECT * FROM users u JOIN connection_users c ON (c.user_id_1 = 2 AND c.user_id_2 = u.id) OR (c.user_id_1 = u.id AND c.user_id_2 = 2)", nativeQuery = true)
    List<User> getConnectedUsersFromUser(@Param("userId") Long userId);
}

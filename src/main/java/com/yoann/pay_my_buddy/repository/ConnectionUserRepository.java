package com.yoann.pay_my_buddy.repository;

import com.yoann.pay_my_buddy.model.ConnectionUser;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ConnectionUserRepository extends CrudRepository<ConnectionUser, Integer> {
    @Query("SELECT COUNT(c) FROM ConnectionUser c " +
            "WHERE (c.user1.id = :currentUserId AND c.user2.id = :userRelationId) OR (c.user1.id = :userRelationId AND c.user2.id = :currentUserId)")
    Integer countRelationForUsersId(@Param("currentUserId") Long currentUserId, @Param("userRelationId") Long userRelationId);
}

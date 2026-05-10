
package com.example.secondhand.repository;

import com.example.secondhand.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Integer> {

    List<Message> findByProductIdOrderByCreatedAtDesc(Integer productId);

    List<Message> findByReceiverIdOrderByCreatedAtDesc(Integer receiverId);

    List<Message> findByReceiverIdAndStatus(Integer receiverId, Integer status);

    @Query("SELECT COUNT(m) FROM Message m WHERE m.receiverId = :receiverId AND m.status = 0")
    Integer countUnreadByReceiverId(@Param("receiverId") Integer receiverId);

    @Modifying
    @Query("UPDATE Message m SET m.status = 1 WHERE m.receiverId = :receiverId AND m.status = 0")
    void markAllAsRead(@Param("receiverId") Integer receiverId);
}

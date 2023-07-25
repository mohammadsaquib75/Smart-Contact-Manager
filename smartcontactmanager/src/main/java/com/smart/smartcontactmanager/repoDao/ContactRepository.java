package com.smart.smartcontactmanager.repoDao;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.smart.smartcontactmanager.Entities.Contact;

public interface ContactRepository extends JpaRepository<Contact, Integer> {
    
    //pagination   

    @Query("from Contact as c where c.user.id=:userId")
    //currentPage -page
    //Contact paer page- 5
    public Page<Contact> findContactsByUser(@Param("userId")Integer userId,Pageable pageable);
}

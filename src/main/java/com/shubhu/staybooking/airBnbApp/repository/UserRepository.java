package com.shubhu.staybooking.airBnbApp.repository;

import com.shubhu.staybooking.airBnbApp.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

}

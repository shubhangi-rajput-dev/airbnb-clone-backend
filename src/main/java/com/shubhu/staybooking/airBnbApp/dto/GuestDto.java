package com.shubhu.staybooking.airBnbApp.dto;

import com.shubhu.staybooking.airBnbApp.entity.User;
import com.shubhu.staybooking.airBnbApp.entity.enums.Gender;
import lombok.Data;

@Data
public class GuestDto {
    private Long id;
    private User user;
    private String name;
    private Gender gender;
    private Integer age;
}

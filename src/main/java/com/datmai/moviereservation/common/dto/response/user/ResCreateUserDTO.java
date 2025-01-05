package com.datmai.moviereservation.common.dto.response.user;

import java.time.Instant;

import com.datmai.moviereservation.common.constant.GenderEnum;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResCreateUserDTO {
    
    private long id;
    private String email;
    private GenderEnum gender;
    private String name;
    private long age;
    private String address;
    private Instant createdAt;
    private String createdBy;

}

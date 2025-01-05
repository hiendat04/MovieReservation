package com.datmai.moviereservation.common.dto.response.user;

import java.time.Instant;

import com.datmai.moviereservation.common.constant.GenderEnum;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResponseUserDTO {
    private long id;
    private GenderEnum gender;
    private String name;
    private long age;
    private String address;
    private Instant createdAt;
    private String createdBy;
    private Instant updatedAt;
    private String updatedBy;  
}

package com.datmai.moviereservation.util.dto.response.user;

import java.time.Instant;

import com.datmai.moviereservation.util.constant.GenderEnum;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResUpdateUserDTO {
    private long id;
    private GenderEnum gender;
    private String name;
    private long age;
    private String address;
    private Instant updatedAt;
    private String updatedBy;
}

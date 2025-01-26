package com.datmai.moviereservation.common.dto.response.user;

import com.datmai.moviereservation.common.constant.Gender;
import com.datmai.moviereservation.common.constant.UserStatus;
import com.datmai.moviereservation.common.constant.UserType;
import com.datmai.moviereservation.domain.Address;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDate;

@SuperBuilder
@Getter
@Setter
public class UserResAbstract implements Serializable {
    private Long id;
    private String firstName;
    private String lastName;
    private String username;
    private String phone;
    private Gender gender;
    private LocalDate dateOfBirth;
    private UserType type;
    private UserStatus status;
    private Address address;
}

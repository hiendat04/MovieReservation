package com.datmai.moviereservation.common.dto.response.user;

import java.time.Instant;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
@Setter
public class CreateUserRes extends UserResAbstract{
    private Instant createdAt;
    private String createdBy;
}

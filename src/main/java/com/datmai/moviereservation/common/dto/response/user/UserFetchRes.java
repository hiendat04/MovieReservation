package com.datmai.moviereservation.common.dto.response.user;

import java.time.Instant;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
@Setter
public class UserFetchRes extends UserResAbstract {
    private Instant createdAt;
    private String createdBy;
    private Instant updatedAt;
    private String updatedBy;  
}

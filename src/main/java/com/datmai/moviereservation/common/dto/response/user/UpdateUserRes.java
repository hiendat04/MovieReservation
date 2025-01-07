package com.datmai.moviereservation.common.dto.response.user;

import java.io.Serializable;
import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;


@SuperBuilder
@Getter
@Setter
public class UpdateUserRes extends UserResAbstract{
    private String updatedBy;
    private Instant updatedAt;
}

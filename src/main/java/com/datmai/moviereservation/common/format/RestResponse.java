package com.datmai.moviereservation.common.format;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RestResponse<T> {
    private int status;
    private String error;
    private Object message;
    private T data;
}

package com.datmai.moviereservation.common.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@AllArgsConstructor
public enum ScreenFormat {
    TWO_D(10.00),
    THREE_D(15.00),
    IMAX(20.00);

    private final Double price;
}

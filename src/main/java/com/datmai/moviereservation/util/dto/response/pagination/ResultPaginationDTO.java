package com.datmai.moviereservation.util.dto.response.pagination;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResultPaginationDTO {
    private Meta meta;
    private Object result;

    @Getter
    @Setter
    public static class Meta {
        private int currentPage;
        private int pageSize;
        private int totalPages;
        private long totalElements;
    }
}

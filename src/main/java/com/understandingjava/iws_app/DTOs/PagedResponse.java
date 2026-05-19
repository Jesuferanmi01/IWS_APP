package com.understandingjava.iws_app.DTOs;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.domain.Page;

import java.util.List;

@Data
@Builder
public class PagedResponse <T> {

    private List<T> content;
    private int      page;
    private int      size;
    private long     totalElements;
    private int      totalPages;
    private boolean  hasNext;
    private boolean  hasPrevious;

    public static <T> PagedResponse<T> from(Page<T> springPage) {
        return PagedResponse.<T>builder()
                .content(springPage.getContent())
                .page(springPage.getNumber())
                .size(springPage.getSize())
                .totalElements(springPage.getTotalElements())
                .totalPages(springPage.getTotalPages())
                .hasNext(springPage.hasNext())
                .hasPrevious(springPage.hasPrevious())
                .build();
    }
}
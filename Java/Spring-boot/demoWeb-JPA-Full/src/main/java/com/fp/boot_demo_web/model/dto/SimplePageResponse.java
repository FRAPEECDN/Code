package com.fp.boot_demo_web.model.dto;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

/** OpenAPI shape of the JSON serialization returned for a Spring Data page. */
@Schema(description = "Paged simple-record results and pagination metadata")
public record SimplePageResponse(
        @Schema(description = "Records on this page")
        List<SimpleDto> content,
        @Schema(description = "Page request metadata")
        PageableMetadata pageable,
        int totalPages,
        long totalElements,
        boolean last,
        int size,
        int number,
        SortMetadata sort,
        int numberOfElements,
        boolean first,
        boolean empty) {

    /** Page request metadata serialized by Spring Data. */
    public record PageableMetadata(
            int pageNumber,
            int pageSize,
            SortMetadata sort,
            long offset,
            boolean paged,
            boolean unpaged) {
    }

    /** Sort state serialized by Spring Data. */
    public record SortMetadata(
            boolean empty,
            boolean sorted,
            boolean unsorted) {
    }
}

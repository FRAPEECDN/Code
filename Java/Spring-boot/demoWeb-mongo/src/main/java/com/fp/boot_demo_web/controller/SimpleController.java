package com.fp.boot_demo_web.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.fp.boot_demo_web.model.dto.SimpleDto;
import com.fp.boot_demo_web.service.SimpleService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * Exposes HTTP endpoints for creating, reading, updating, and deleting simple
 * records.
 */
@RestController
@RequestMapping(path = "/api/simple")
@RequiredArgsConstructor
@Tag(name = "Simple", description = "Simple controller API (CRUD)")
public class SimpleController {

        private static final String EXAMPLE = "{\"id\":null,\"name\":\"apple\"}";

        private final SimpleService service;

        /**
         * Retrieves a record by its identifier.
         *
         * @param id identifier of the requested record
         * @return the matching record
         */
        @GetMapping(value = "/{id}")
        @Operation(summary = "Read a simple record", description = "Read simple record by providing id to look up.", operationId = "readsimple")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "OK"),
                        @ApiResponse(responseCode = "404", description = "Not found"),
                        @ApiResponse(responseCode = "500", description = "Internal Server Error")
        })
        public SimpleDto getOne(
                        @Parameter(description = "MongoDB ObjectId for a simple record", example = "65f000000000000000000001", required = true) @PathVariable("id") String id) {
                return service.getOne(id);
        }

        /**
         * Retrieves all records.
         *
         * @return every stored record
         */
        @GetMapping
        @Operation(summary = "Read all existing simple records", description = "Read all of the simple record", operationId = "allsimple")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "OK"),
                        @ApiResponse(responseCode = "500", description = "Internal Server Error")
        })
        public List<SimpleDto> getAll() {
                return service.getAll();
        }

        /**
         * Retrieves a page from the current set of records.
         *
         * @param page zero-based page index
         * @param size maximum number of records in the page
         * @return the requested page and total record count
         */
        @GetMapping("/search")
        @Operation(summary = "Search through simple records", description = "Pagable search for retrieving paged simple records.", operationId = "searchsimple")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "OK"),
                        @ApiResponse(responseCode = "500", description = "Internal Server Error")
        })
        public Page<SimpleDto> getSearch(
                        @Parameter(description = "Current Page", example = "1") @RequestParam(defaultValue = "0") int page,
                        @Parameter(description = "Page size", example = "10") @RequestParam(defaultValue = "10") int size) {
                List<SimpleDto> returned = service.getAll();

                int totalSize = returned.size();
                int startIndex = page * size;
                int endIndex = Math.min(startIndex + size, totalSize);

                List<SimpleDto> pageContent = returned.subList(startIndex, endIndex);
                return new PageImpl<>(pageContent, PageRequest.of(page, size), totalSize);
        }

        /**
         * Creates and stores a record.
         *
         * @param input record data to create
         * @return the created record
         */
        @PostMapping
        @ResponseStatus(HttpStatus.CREATED)
        @Operation(summary = "Create simple record", description = "Create new simple record and store to database.", operationId = "createsimple")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "OK"),
                        @ApiResponse(responseCode = "500", description = "Internal Server Error")
        })
        public SimpleDto createOne(
                        @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Simple record to create", required = true, content = @Content(mediaType = "application/json", schema = @Schema(implementation = SimpleDto.class), examples = @ExampleObject(value = EXAMPLE))) @RequestBody @Valid SimpleDto input) {
                return service.createOne(input);
        }

        /**
         * Replaces the record identified by {@code id}.
         *
         * @param id identifier of the record to replace
         * @param input replacement record data
         * @return the updated record
         */
        @PutMapping(value = "/{id}")
        @Operation(summary = "Update simple record", description = "Update the simple record by providing an id.", operationId = "updatesimple")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "OK"),
                        @ApiResponse(responseCode = "404", description = "Not found"),
                        @ApiResponse(responseCode = "500", description = "Internal Server Error")
        })
        public SimpleDto updateOne(
                        @Parameter(description = "MongoDB ObjectId for a simple record", example = "65f000000000000000000001", required = true) @PathVariable("id") String id,
                        @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Simple record to update", required = true, content = @Content(mediaType = "application/json", schema = @Schema(implementation = SimpleDto.class), examples = @ExampleObject(value = EXAMPLE))) @Valid @RequestBody SimpleDto input) {
                return service.updateOne(id, input);
        }

        /**
         * Applies the supplied record data to the record identified by {@code id}.
         *
         * @param id identifier of the record to update
         * @param input record data to apply
         * @return the updated record
         */
        @PatchMapping(value = "/{id}")
        @Operation(summary = "Partial update simple record", description = "Update the simple record by providing an id (as partial).", operationId = "partialupdatesimple")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "OK"),
                        @ApiResponse(responseCode = "404", description = "Not found"),
                        @ApiResponse(responseCode = "500", description = "Internal Server Error")
        })
        public SimpleDto partialUpdateOne(
                        @Parameter(description = "MongoDB ObjectId for a simple record", example = "65f000000000000000000001", required = true) @PathVariable("id") String id,
                        @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Simple record to partialy update", required = true, content = @Content(mediaType = "application/json", schema = @Schema(implementation = SimpleDto.class), examples = @ExampleObject(value = EXAMPLE))) @Valid @RequestBody SimpleDto input) {
                return service.updateOne(id, input);
        }

        /**
         * Deletes the record identified by {@code id}.
         *
         * @param id identifier of the record to delete
         */
        @DeleteMapping(value = "/{id}")
        @ResponseStatus(HttpStatus.NO_CONTENT)
        @Operation(summary = "Delete simple record", description = "Delete an existing simple record from database.", operationId = "deletesimple")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "OK"),
                        @ApiResponse(responseCode = "404", description = "Not found"),
                        @ApiResponse(responseCode = "500", description = "Internal Server Error")
        })
        public void deleteOne(
                        @Parameter(description = "MongoDB ObjectId for a simple record", example = "65f000000000000000000001", required = true) @PathVariable("id") String id) {
                service.deleteOne(id);
        }

}

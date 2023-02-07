package com.thudani.codingchallenge.controller;

import com.thudani.codingchallenge.command.SaveBatteriesCommand;
import com.thudani.codingchallenge.dto.BatteryDto;
import com.thudani.codingchallenge.dto.BatteryResultDto;
import com.thudani.codingchallenge.service.BatteryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for battery operations
 */
@RestController
@RequestMapping("/api/batteries")
public class BatteryController {

    private static final Logger logger = LoggerFactory.getLogger(BatteryController.class);

    private final BatteryService batteryService;

    public BatteryController(BatteryService batteryService) {
        this.batteryService = batteryService;
    }

    /**
     * Save a list of batteries to the database
     *
     * @param command {@link SaveBatteriesCommand} object containing the information of batteries to save
     * @return the saved batteries
     */
    @Operation(summary = "Save a list of batteries to the DB")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "All the batteries are saved",
                    content = {@Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = BatteryDto.class)))}),
            @ApiResponse(responseCode = "400", description = "Invalid save command",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal sever error",
                    content = @Content)})
    @PostMapping
    public ResponseEntity<List<BatteryDto>> saveBatteries(@Valid @RequestBody SaveBatteriesCommand command) {
        logger.info("Save batteries called for {}", command);
        return new ResponseEntity<>(batteryService.saveBatteries(command), HttpStatus.CREATED);
    }

    /**
     * Fetch a list of batteries falling within a given range of postcodes
     *
     * @param postcode1 first postcode of the range
     * @param postcode2 second postcode of the range
     * @return {@link BatteryResultDto} object containing the names of the batteries and other statistics
     */
    @Operation(summary = "Search for batteries in a given postcode range")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Search operation was successful",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = BatteryResultDto.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid postcode range",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal sever error",
                    content = @Content)})
    @GetMapping
    public ResponseEntity<BatteryResultDto> getBatteries(@Parameter(description = "The first postcode of the range")
                                                         @RequestParam String postcode1,
                                                         @Parameter(description = "The second postcode of the range")
                                                         @RequestParam String postcode2) {
        logger.info("Get batteries called for {} and {}", postcode1, postcode2);
        return new ResponseEntity<>(batteryService.getBatteries(postcode1, postcode2), HttpStatus.OK);
    }
}

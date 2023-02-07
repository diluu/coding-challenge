package com.thudani.codingchallenge.dto;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * The DTO representing the results of a battery search
 */
@Data
@Builder
public class BatteryResultDto {
    @ArraySchema(arraySchema = @Schema(
            description = "Names of the first 50 batteries in the postcode range in alphabetical order",
            example = "[\"battery 1\", \"battery 2\"]"))
    private List<String> batteryNames;

    @Schema(type = "double", description = "Total watt capacity of the batteries in the postcode range", example = "120.0")
    private Double totalWattCapacity;

    @Schema(type = "double", description = "Average watt capacity of the batteries in the postcode range", example = "15.0")
    private Double averageWattCapacity;
}

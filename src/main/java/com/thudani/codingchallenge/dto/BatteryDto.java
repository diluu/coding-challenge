package com.thudani.codingchallenge.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

/**
 * The DTO representing a battery
 */
@Data
@Builder
public class BatteryDto {
    @Schema(type = "string", description = "Db id of the battery", example = "63dcffb2d40b880117ef77eb")
    private String id;

    @Schema(type = "string", description = "Name of the battery", example = "Battery 1")
    @NotBlank(message = "Battery name must be provided")
    private String name;

    @Schema(type = "string", description = "Postcode of the battery", example = "2000")
    @NotBlank(message = "Postcode must be provided")
    private String postcode;

    @Schema(type = "double", description = "Watt capacity of the battery", example = "20.0")
    @NotNull(message = "Watt capacity must be provided")
    private Double wattCapacity;
}

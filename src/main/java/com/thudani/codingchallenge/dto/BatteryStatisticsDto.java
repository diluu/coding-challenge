package com.thudani.codingchallenge.dto;

import lombok.Builder;
import lombok.Data;

/**
 * Internal DTO representing statistics of batteries in a given postcode range
 */
@Data
@Builder
public class BatteryStatisticsDto {
    private Double totalWattCapacity;
    private Long batteryCount;
}

package com.thudani.codingchallenge.command;

import com.thudani.codingchallenge.dto.BatteryDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Command object representing a request to save a list of batteries
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SaveBatteriesCommand {
    @NotEmpty(message = "Input battery list cannot be empty")
    private List<@Valid BatteryDto> batteries;
}

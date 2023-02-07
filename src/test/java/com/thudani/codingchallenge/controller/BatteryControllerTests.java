package com.thudani.codingchallenge.controller;

import com.thudani.codingchallenge.command.SaveBatteriesCommand;
import com.thudani.codingchallenge.dto.BatteryDto;
import com.thudani.codingchallenge.dto.BatteryResultDto;
import com.thudani.codingchallenge.service.BatteryService;
import com.thudani.codingchallenge.util.AppTestConfiguration;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Tests for {@link BatteryController}
 */
@SpringBootTest
@Import(AppTestConfiguration.class)
class BatteryControllerTests {
    @MockBean
    private BatteryService batteryService;

    @Autowired
    private BatteryController batteryController;

    @TestConfiguration
    class BatteryControllerTestContextConfiguration {

        @Bean
        public BatteryController batteryController() {
            return new BatteryController(batteryService);
        }
    }

    @Test
    void savingAValidListOfBatteries() {
        SaveBatteriesCommand command = SaveBatteriesCommand.builder()
                .batteries(List.of(
                        BatteryDto.builder()
                                .name("battery1")
                                .postcode("2000")
                                .wattCapacity(10.0)
                                .build(),
                        BatteryDto.builder()
                                .name("battery2")
                                .postcode("2020")
                                .wattCapacity(20.0)
                                .build()
                ))
                .build();

        List<BatteryDto> savedBatteries = List.of(
                BatteryDto.builder()
                        .id("1111")
                        .name("battery1")
                        .postcode("2000")
                        .wattCapacity(10.0)
                        .build(),
                BatteryDto.builder()
                        .id("2222")
                        .name("battery2")
                        .postcode("2020")
                        .wattCapacity(20.0)
                        .build()
        );

        Mockito.when(batteryService.saveBatteries(command)).thenReturn(savedBatteries);

        ResponseEntity<List<BatteryDto>> result = batteryController.saveBatteries(command);
        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertEquals(savedBatteries, result.getBody());
    }

    @Test
    void searchingBatteries_validParameters() {
        String postcode1 = "2000";
        String postcode2 = "3000";
        List<String> batteryNames = List.of("battery1", "battery2");
        Mockito.when(batteryService.getBatteries(postcode1, postcode2))
                .thenReturn(
                        BatteryResultDto.builder()
                                .batteryNames(batteryNames)
                                .totalWattCapacity(30.0)
                                .averageWattCapacity(15.0)
                                .build());

        ResponseEntity<BatteryResultDto> result = batteryController.getBatteries(postcode1, postcode2);
        assertNotNull(result.getBody());
        assertEquals(batteryNames, result.getBody().getBatteryNames());
        assertEquals(30.0, result.getBody().getTotalWattCapacity());
        assertEquals(15.0, result.getBody().getAverageWattCapacity());
    }
}

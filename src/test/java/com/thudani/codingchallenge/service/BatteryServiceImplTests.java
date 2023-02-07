package com.thudani.codingchallenge.service;

import com.thudani.codingchallenge.command.SaveBatteriesCommand;
import com.thudani.codingchallenge.dto.BatteryDto;
import com.thudani.codingchallenge.dto.BatteryResultDto;
import com.thudani.codingchallenge.dto.BatteryStatisticsDto;
import com.thudani.codingchallenge.model.Battery;
import com.thudani.codingchallenge.repository.BatteryRepository;
import com.thudani.codingchallenge.util.AppTestConfiguration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.internal.verification.VerificationModeFactory.times;

/**
 * Tests for {@link BatteryServiceImpl}
 */
@SpringBootTest
@Import(AppTestConfiguration.class)
class BatteryServiceImplTests {
    @MockBean
    private BatteryRepository batteryRepository;

    @Autowired
    private BatteryService batteryService;

    @Captor
    ArgumentCaptor<List<Battery>> valueCaptor;

    @TestConfiguration
    class BatteryServiceImplTestContextConfiguration {

        @Bean
        public BatteryService batteryService() {
            return new BatteryServiceImpl(batteryRepository);
        }
    }

    @Test
    void savingAValidListOfBatteries() {
        SaveBatteriesCommand command = SaveBatteriesCommand.builder()
                .batteries(List.of(
                        BatteryDto.builder()
                                .name("Battery1")
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
        List<Battery> batteries = List.of(Battery.builder()
                        .name("Battery1")
                        .lowercaseName("battery1")
                        .postcode("2000")
                        .wattCapacity(10.0)
                        .build(),
                Battery.builder()
                        .name("battery2")
                        .lowercaseName("battery2")
                        .postcode("2020")
                        .wattCapacity(20.0)
                        .build());

        batteryService.saveBatteries(command);
        Mockito.verify(batteryRepository).saveAll(batteries);
    }

    @Test
    void savingAnEmptyListOfBatteries() {
        SaveBatteriesCommand command = SaveBatteriesCommand.builder()
                .batteries(List.of())
                .build();

        Exception exception = assertThrows(ResponseStatusException.class, () -> batteryService.saveBatteries(command));
        String expectedMessage = "Battery list cannot be empty";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @ParameterizedTest
    @MethodSource("batteries")
    void savingAListOfBatteries_InvalidBattery(String name, String postcode, Double wattCapacity) {
        SaveBatteriesCommand command = SaveBatteriesCommand.builder()
                .batteries(List.of(
                        BatteryDto.builder()
                                .name(name)
                                .postcode(postcode)
                                .wattCapacity(wattCapacity)
                                .build()
                ))
                .build();

        Exception exception = assertThrows(ResponseStatusException.class, () -> batteryService.saveBatteries(command));
        String expectedMessage = "Batteries should contain all the required fields";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void savingMoreThanOneBatchOfBatteries() {
        List<BatteryDto> batteries = new ArrayList<>();
        for (int i = 0; i < 350; i++) {
            batteries.add(BatteryDto.builder()
                    .name("battery" + i)
                    .postcode(i % 2 == 0 ? "2000" : "2020")
                    .wattCapacity(10.0)
                    .build());
        }
        SaveBatteriesCommand command = SaveBatteriesCommand.builder()
                .batteries(batteries)
                .build();

        batteryService.saveBatteries(command);
        Mockito.verify(batteryRepository, times(2)).saveAll(valueCaptor.capture());
    }

    @Test
    void searchingBatteries_validParameters() {
        String postcode1 = "2000";
        String postcode2 = "3000";
        Mockito.when(batteryRepository.findWithinPostCodeRangeOrderByName(postcode1, postcode2, 0, 50))
                .thenReturn(List.of(Battery.builder()
                                .id("1111")
                                .name("battery1")
                                .postcode("2010")
                                .wattCapacity(10.0)
                                .build(),
                        Battery.builder()
                                .id("2222")
                                .name("battery2")
                                .postcode("2020")
                                .wattCapacity(20.0)
                                .build()));
        Mockito.when(batteryRepository.findTotalWattCapacityWithinPostcodeRange(postcode1, postcode2))
                .thenReturn(Optional.of(BatteryStatisticsDto.builder().totalWattCapacity(30.0).batteryCount(2L).build()));
        List<String> batteryNames = List.of("battery1", "battery2");

        BatteryResultDto result = batteryService.getBatteries(postcode1, postcode2);
        assertEquals(batteryNames, result.getBatteryNames());
        assertEquals(30.0, result.getTotalWattCapacity());
        assertEquals(15.0, result.getAverageWattCapacity());
    }

    @Test
    void searchingBatteries_emptyResultSet() {
        String postcode1 = "2000";
        String postcode2 = "3000";
        Mockito.when(batteryRepository.findWithinPostCodeRangeOrderByName(postcode1, postcode2, 0, 50))
                .thenReturn(List.of());
        Mockito.when(batteryRepository.findTotalWattCapacityWithinPostcodeRange(postcode1, postcode2))
                .thenReturn(Optional.empty());

        BatteryResultDto result = batteryService.getBatteries(postcode1, postcode2);
        assertTrue(result.getBatteryNames().isEmpty());
        assertEquals(0.0, result.getTotalWattCapacity());
        assertNull(result.getAverageWattCapacity());
    }

    @ParameterizedTest
    @MethodSource("postcodes")
    void searchingBatteries_InvalidParameters(String postcode1, String postcode2) {
        Exception exception = assertThrows(ResponseStatusException.class, () -> batteryService.getBatteries(postcode1, postcode2));
        String expectedMessage = "A valid postcode range must be provided";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    private static Collection<Object[]> batteries() {
        return Arrays.asList(new Object[][]{
                {null, "2000", 20.00},
                {"", "2000", 20.00},
                {" ", "2000", 20.00},
                {"battery1", null, 20.00},
                {"battery1", "", 20.00},
                {"battery1", " ", 20.00},
                {"battery1", "2000", null}
        });
    }

    private static Collection<Object[]> postcodes() {
        return Arrays.asList(new Object[][]{
                {null, "3000"},
                {"2000", null},
                {null, null}
        });
    }
}

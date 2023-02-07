package com.thudani.codingchallenge.validation;

import com.thudani.codingchallenge.command.SaveBatteriesCommand;
import com.thudani.codingchallenge.dto.BatteryDto;
import com.thudani.codingchallenge.util.AppTestConfiguration;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Validation tests for {@link SaveBatteriesCommand}
 */
@SpringBootTest
@Import(AppTestConfiguration.class)
class SaveBatteriesCommandTests {

    @Autowired
    private Validator validator;

    @Test
    void ensureValidatorIsLoaded() {
        assertNotNull(validator);
    }

    @ParameterizedTest
    @MethodSource("batteries")
    void commandWithInvalidBatteries(String name, String postcode, Double wattCapacity) {
        SaveBatteriesCommand command = SaveBatteriesCommand.builder()
                .batteries(List.of(
                        BatteryDto.builder()
                                .name(name)
                                .postcode(postcode)
                                .wattCapacity(wattCapacity)
                                .build()
                ))
                .build();

        final var violations = validator.validate(command);
        assertEquals(1, violations.size());
    }

    @Test
    void commandWithNoBatteries() {
        SaveBatteriesCommand command = SaveBatteriesCommand.builder()
                .batteries(List.of())
                .build();

        final var violations = validator.validate(command);
        assertEquals(1, violations.size());
    }

    @Test
    void validCommand() {
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

        final var violations = validator.validate(command);
        assertTrue(violations.isEmpty());
    }

    private static Collection<Object[]> batteries() {
        return Arrays.asList(new Object[][]{
                {null, "2000", 20.00, "Battery name must be provided"},
                {"", "2000", 20.00, "Battery name must be provided"},
                {" ", "2000", 20.00, "Battery name must be provided"},
                {"battery1", null, 20.00, "Postcode must be provided"},
                {"battery1", "", 20.00, "Postcode must be provided"},
                {"battery1", " ", 20.00, "Postcode must be provided"},
                {"battery1", "2000", null, "Watt capacity must be provided"}
        });
    }
}

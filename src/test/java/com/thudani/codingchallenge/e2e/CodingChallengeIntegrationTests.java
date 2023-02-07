package com.thudani.codingchallenge.e2e;


import com.thudani.codingchallenge.command.SaveBatteriesCommand;
import com.thudani.codingchallenge.dto.BatteryDto;
import com.thudani.codingchallenge.repository.BatteryRepository;
import com.thudani.codingchallenge.util.AppTestConfiguration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration tests for the REST api
 */
@AutoConfigureMockMvc
@Import(AppTestConfiguration.class)
@SpringBootTest
class CodingChallengeIntegrationTests {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private BatteryRepository batteryRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @AfterEach
    void cleanUp() {
        batteryRepository.deleteAll();
    }

    @Test
    void savingAValidListOfBatteriesAndPerformSearch() throws Exception {
        SaveBatteriesCommand command = SaveBatteriesCommand.builder()
                .batteries(List.of(
                        BatteryDto.builder()
                                .name("battery1")
                                .postcode("2010")
                                .wattCapacity(10.0)
                                .build(),
                        BatteryDto.builder()
                                .name("battery2")
                                .postcode("2020")
                                .wattCapacity(20.0)
                                .build()
                ))
                .build();

        mvc.perform(post("/api/batteries")
                        .content(objectMapper.writeValueAsString(command))
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name", is("battery1")))
                .andExpect(jsonPath("$[0].postcode", is("2010")))
                .andExpect(jsonPath("$[0].wattCapacity", is(10.0)))
                .andExpect(jsonPath("$[1].name", is("battery2")))
                .andExpect(jsonPath("$[1].postcode", is("2020")))
                .andExpect(jsonPath("$[1].wattCapacity", is(20.0)));

        mvc.perform(get("/api/batteries?postcode1=2010&postcode2=2020"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.batteryNames", hasSize(2)))
                .andExpect(jsonPath("$.batteryNames[0]", is("battery1")))
                .andExpect(jsonPath("$.batteryNames[1]", is("battery2")))
                .andExpect(jsonPath("$.totalWattCapacity", is(30.0)))
                .andExpect(jsonPath("$.averageWattCapacity", is(15.0)));
    }

    @Test
    void savingAnEmptyListOfBatteries() throws Exception {
        SaveBatteriesCommand command = SaveBatteriesCommand.builder()
                .batteries(List.of())
                .build();

        mvc.perform(post("/api/batteries")
                        .content(objectMapper.writeValueAsString(command))
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @ParameterizedTest
    @MethodSource("batteries")
    void savingAListOfBatteries_InvalidBattery(String name, String postcode, Double wattCapacity) throws Exception {
        SaveBatteriesCommand command = SaveBatteriesCommand.builder()
                .batteries(List.of(
                        BatteryDto.builder()
                                .name(name)
                                .postcode(postcode)
                                .wattCapacity(wattCapacity)
                                .build()
                ))
                .build();

        mvc.perform(post("/api/batteries")
                        .content(objectMapper.writeValueAsString(command))
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void searchingBatteries_noResult() throws Exception {
        mvc.perform(get("/api/batteries?postcode1=5000&postcode2=6000"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.batteryNames", hasSize(0)))
                .andExpect(jsonPath("$.totalWattCapacity", is(0.0)));
    }

    @Test
    void searchingBatteries_InvalidParameters() throws Exception {
        mvc.perform(get("/api/batteries"))
                .andExpect(status().isBadRequest());
        mvc.perform(get("/api/batteries?postcode1=2000"))
                .andExpect(status().isBadRequest());
        mvc.perform(get("/api/batteries?postcode2=2000"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void savingMoreThanOneBatchOfBatteries() throws Exception {
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

        mvc.perform(post("/api/batteries")
                        .content(objectMapper.writeValueAsString(command))
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$", hasSize(350)));
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
}

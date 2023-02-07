package com.thudani.codingchallenge.repository;

import com.thudani.codingchallenge.dto.BatteryStatisticsDto;
import com.thudani.codingchallenge.model.Battery;
import com.thudani.codingchallenge.util.AppTestConfiguration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.AutoConfigureDataMongo;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Import;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for {@link BatteryRepository}
 */
@DataMongoTest
@AutoConfigureDataMongo
@Import(AppTestConfiguration.class)
class BatteryRepositoryTests {

    @Autowired
    private BatteryRepository batteryRepository;

    @BeforeEach
    void setup() {
        batteryRepository.save(Battery.builder()
                .name("Bca")
                .lowercaseName("bca")
                .postcode("2000")
                .wattCapacity(10.0)
                .build());
        batteryRepository.save(Battery.builder()
                .name("Def")
                .lowercaseName("def")
                .postcode("2001")
                .wattCapacity(10.0)
                .build());
        batteryRepository.save(Battery.builder()
                .name("xac")
                .lowercaseName("xac")
                .postcode("2002")
                .wattCapacity(10.0)
                .build());
        batteryRepository.save(Battery.builder()
                .name("Pqr")
                .lowercaseName("pqr")
                .postcode("2003")
                .wattCapacity(10.0)
                .build());
        batteryRepository.save(Battery.builder()
                .name("acb")
                .lowercaseName("acb")
                .postcode("2004")
                .wattCapacity(10.0)
                .build());
        batteryRepository.save(Battery.builder()
                .name("aac")
                .lowercaseName("aac")
                .postcode("2005")
                .wattCapacity(10.0)
                .build());
    }

    @AfterEach
    void cleanUp() {
        batteryRepository.deleteAll();
    }

    @Test
    void findWithinPostCodeRangeOrderByName() {
        List<Battery> result = batteryRepository.findWithinPostCodeRangeOrderByName("2000", "2004", 0, 3);
        assertEquals(3, result.size());
        assertEquals("acb", result.get(0).getName());
        assertEquals("2004", result.get(0).getPostcode());
        assertEquals(10.0, result.get(0).getWattCapacity());
        assertEquals("Bca", result.get(1).getName());
        assertEquals("2000", result.get(1).getPostcode());
        assertEquals(10.0, result.get(1).getWattCapacity());
        assertEquals("Def", result.get(2).getName());
        assertEquals("2001", result.get(2).getPostcode());
        assertEquals(10.0, result.get(2).getWattCapacity());
    }

    @Test
    void findTotalWattCapacityWithinPostcodeRange() {
        Optional<BatteryStatisticsDto> statisticsDto = batteryRepository.findTotalWattCapacityWithinPostcodeRange("2000", "2004");
        assertTrue(statisticsDto.isPresent());
        assertEquals(50.0, statisticsDto.get().getTotalWattCapacity());
        assertEquals(5, statisticsDto.get().getBatteryCount());
    }
}

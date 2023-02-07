package com.thudani.codingchallenge.service;

import com.thudani.codingchallenge.command.SaveBatteriesCommand;
import com.thudani.codingchallenge.dto.BatteryDto;
import com.thudani.codingchallenge.dto.BatteryResultDto;
import com.thudani.codingchallenge.model.Battery;
import com.thudani.codingchallenge.repository.BatteryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of {@link BatteryService}
 */
@Service
public class BatteryServiceImpl implements BatteryService {
    private static final Logger logger = LoggerFactory.getLogger(BatteryServiceImpl.class);
    private static final int BATCH_SIZE = 300;
    private static final int MAX_RESULT_COUNT = 50;

    private final BatteryRepository batteryRepository;

    public BatteryServiceImpl(BatteryRepository batteryRepository) {
        this.batteryRepository = batteryRepository;
    }

    @Override
    @Transactional
    public List<BatteryDto> saveBatteries(SaveBatteriesCommand saveBatteriesCommand) {
        logger.debug("saveBatteries called for {}", saveBatteriesCommand);
        var allBatteries = saveBatteriesCommand.getBatteries();
        List<BatteryDto> savedBatteries = new ArrayList<>();
        //Second level of validations in the service layer even though the controller already does the validations
        if (allBatteries.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Battery list cannot be empty");
        }
        var startIndex = 0;
        var endIndex = Math.min(allBatteries.size(), BATCH_SIZE);
        while (startIndex < endIndex) {
            List<BatteryDto> batteryBatch = allBatteries.subList(startIndex, endIndex);
            if (batteryBatch.stream().anyMatch(b -> b.getName() == null || b.getName().isBlank() || b.getPostcode() == null || b.getPostcode().isBlank() || b.getWattCapacity() == null)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Batteries should contain all the required fields");
            }

            var batteries = batteryBatch.stream()
                    .map(dto -> Battery.builder()
                            .name(dto.getName())
                            .lowercaseName(dto.getName().toLowerCase())
                            .postcode(dto.getPostcode())
                            .wattCapacity(dto.getWattCapacity())
                            .build()).toList();
            savedBatteries.addAll(batteryRepository.saveAll(batteries)
                    .stream()
                    .map(b -> BatteryDto.builder()
                            .id(b.getId())
                            .name(b.getName())
                            .postcode(b.getPostcode())
                            .wattCapacity(b.getWattCapacity())
                            .build()).toList());
            startIndex = endIndex;
            endIndex = Math.min(allBatteries.size(), endIndex + BATCH_SIZE);
        }
        return savedBatteries;
    }

    @Override
    public BatteryResultDto getBatteries(String postcode1, String postcode2) {
        logger.debug("getBatteries called for {} and {}", postcode1, postcode2);
        if (postcode1 == null || postcode2 == null || postcode1.compareTo(postcode2) > 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A valid postcode range must be provided");
        }
        var batteries = batteryRepository.findWithinPostCodeRangeOrderByName(postcode1, postcode2, 0, MAX_RESULT_COUNT);
        var batteryStatistics = batteryRepository.findTotalWattCapacityWithinPostcodeRange(postcode1, postcode2);

        Double totalWattCapacity;
        Double averageWattCapacity;
        if (batteryStatistics.isPresent()) {
            totalWattCapacity = batteryStatistics.get().getTotalWattCapacity();
            averageWattCapacity = (double) Math.round(totalWattCapacity / batteryStatistics.get().getBatteryCount());
        } else {
            totalWattCapacity = 0.0;
            averageWattCapacity = null;
        }
        return BatteryResultDto.builder()
                .batteryNames(batteries.stream().map(Battery::getName).toList())
                .totalWattCapacity(totalWattCapacity)
                .averageWattCapacity(averageWattCapacity)
                .build();
    }
}

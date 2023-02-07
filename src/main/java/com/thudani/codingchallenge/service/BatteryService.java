package com.thudani.codingchallenge.service;

import com.thudani.codingchallenge.command.SaveBatteriesCommand;
import com.thudani.codingchallenge.dto.BatteryDto;
import com.thudani.codingchallenge.dto.BatteryResultDto;

import java.util.List;

/**
 * Service interface for operations on batteries
 */
public interface BatteryService {

    /**
     * Save a list of batteries to the database
     *
     * @param saveBatteriesCommand {@link SaveBatteriesCommand} object containing the information of batteries to save
     * @return the saved batteries
     */
    List<BatteryDto> saveBatteries(SaveBatteriesCommand saveBatteriesCommand);

    /**
     * Fetch a list of batteries falling within a given range of postcodes
     *
     * @param postcode1 first postcode of the range
     * @param postcode2 second postcode of the range
     * @return {@link BatteryResultDto} object containing the names of the batteries and other statistics
     */
    BatteryResultDto getBatteries(String postcode1, String postcode2);
}

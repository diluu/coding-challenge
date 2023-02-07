package com.thudani.codingchallenge.repository;

import com.thudani.codingchallenge.dto.BatteryStatisticsDto;
import com.thudani.codingchallenge.model.Battery;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

/**
 * Mongo repository to operate on {@link Battery}
 */
public interface BatteryRepository extends MongoRepository<Battery, String> {

    /**
     * Returns a paged result of batteries within a given post code range ordered by name ASC
     *
     * @param postcode1 start postcode of the range
     * @param postcode2 end postcode of the range
     * @param skip      page number of the paginated response
     * @param limit     page size of the paginated response
     * @return a list of {@link Battery}
     */
    @Aggregation(pipeline = {
            "{ '$match': {'postcode' : { $gte: ?0, $lte: ?1 } } }",
            "{ '$sort' : {'lowercaseName' : 1} }",
            "{ '$skip' : ?2 }",
            "{ '$limit' : ?3 }"
    })
    List<Battery> findWithinPostCodeRangeOrderByName(String postcode1, String postcode2, int skip, int limit);

    /**
     * Calculate the total watt capacity and the total number of batteries in a given post code range
     *
     * @param postcode1 start postcode of the range
     * @param postcode2 end postcode of the range
     * @return {@link BatteryStatisticsDto} object containing the total watt capacity and total number of batteries
     */
    @Aggregation(pipeline = {
            "{$match: {'postcode' : { $gte: ?0, $lte: ?1 } }}",
            "{$group: { _id: '', totalWattCapacity: {$sum: $wattCapacity}, batteryCount: {$sum: 1}}}"
    })
    Optional<BatteryStatisticsDto> findTotalWattCapacityWithinPostcodeRange(String postcode1, String postcode2);
}

package com.thudani.codingchallenge.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Model class representing a battery
 */
@Data
@Document
@Builder
@CompoundIndex(name = "postcode_name_idx", def = "{'postcode' : 1, 'lowercaseName' : 1}")
public class Battery {

    @Id
    private String id;

    @NotBlank
    private String name;

    //This is used for case-insensitive sorting
    @NotBlank
    private String lowercaseName;

    @NotBlank
    private String postcode;

    @NotNull
    private Double wattCapacity;
}

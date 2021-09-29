package com.pharm.pharmfinder.model.search_and_filter;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class SearchAndFilterRequest {
    private String username;
//    all following attributes are optional
    private String pzn;
    private String friendlyName;
    private String medicineForm;
    private String amount;
    private String sortBy;
    private boolean descending;
}

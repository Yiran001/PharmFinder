package com.pharm.pharmfinder.model;

import lombok.Data;

@Data
public class MedicineView {
    private String pzn;
    private String friendlyName;
    private MedicineForm medicineForm;
    private long amount;
}

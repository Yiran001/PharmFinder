package com.pharm.pharmfinder.model.search_and_filter;

import com.pharm.pharmfinder.model.MedicineForm;
import lombok.Data;

@Data
public class MedicineView {
    private String pzn;
    private String friendlyName;
    private MedicineForm medicineForm;
    private long amount;
}

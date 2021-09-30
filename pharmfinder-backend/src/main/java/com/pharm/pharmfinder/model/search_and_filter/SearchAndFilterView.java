package com.pharm.pharmfinder.model.search_and_filter;

import com.pharm.pharmfinder.model.Medicine;
import lombok.Data;

import java.util.List;

@Data
public class SearchAndFilterView {
    private List<Medicine> medicines;
}

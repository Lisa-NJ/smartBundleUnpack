package com.ade.lisa.challenge.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class OrderResult {
    private final List<BundleBreakdown> list;
}

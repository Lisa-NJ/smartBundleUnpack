package com.ade.lisa.challenge.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class BundleBreakdown {
    private String orderType;
    private int orderNumber;
    private int totalNumber;
    private double totalPrice;
    private int[] solution;

    public int[] combineSolution(int[] inArray) {
        for (int i = 0; i < solution.length; i++) {
            solution[i] += inArray[i];
        }

        return solution;
    }

    public void setArrayItem(int i, int v) {
        solution[i] = v;
    }

    public double calTotalPrice(List<NumPrice> npArray) {
        setTotalPrice(0);
        for (int i = 0; i < solution.length; i++) {
            totalPrice += npArray.get(i).getPrice() * solution[i];
        }

        return totalPrice;
    }
}
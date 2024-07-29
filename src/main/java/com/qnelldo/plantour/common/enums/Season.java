package com.qnelldo.plantour.common.enums;

import java.util.Arrays;
import java.util.List;

public enum Season {
    SPRING(3, 4, 5),
    SUMMER(6, 7, 8),
    AUTUMN(9, 10, 11),
    WINTER(12, 1, 2);

    private final List<Integer> months;

    Season(Integer... months) {
        this.months = Arrays.asList(months);
    }

    public static Season fromMonth(int month) {
        return Arrays.stream(Season.values())
                .filter(season -> season.months.contains(month))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid month"));
    }
}
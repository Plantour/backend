package com.qnelldo.plantour.common.enums;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public enum Season {
    SPRING(Map.of("ENG", "SPRING", "KOR", "봄"), 3, 4, 5),
    SUMMER(Map.of("ENG", "SUMMER", "KOR", "여름"), 6, 7, 8),
    AUTUMN(Map.of("ENG", "AUTUMN", "KOR", "가을"), 9, 10, 11),
    WINTER(Map.of("ENG", "WINTER", "KOR", "겨울"), 12, 1, 2);

    private final List<Integer> months;
    private final Map<String, String> names;

    Season(Map<String, String> names, Integer... months) {
        this.names = names;
        this.months = Arrays.asList(months);
    }

    public static Season fromMonth(int month) {
        return Arrays.stream(Season.values())
                .filter(season -> season.months.contains(month))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid month"));
    }

    public String getName(String languageCode) {
        return names.getOrDefault(languageCode, names.get("ENG")); // 기본값으로 영어를 사용
    }

}
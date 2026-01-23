package com.pgc.sideproj.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Set;

public class RegionValidator implements ConstraintValidator<ValidRegion, String> {
    private static final Set<String> VALID_REGIONS = Set.of(
        "", // 전체
        "서울특별시",
        "경기도",
        "인천광역시",
        "강원특별자치도",
        "충청남도",
        "충청북도",
        "대전광역시",
        "세종특별자치시",
        "전북특별자치도",
        "전라남도",
        "광주광역시",
        "경상북도",
        "경상남도",
        "대구광역시",
        "울산광역시",
        "부산광역시",
        "제주특별자치도"
    );

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        // null이거나 빈 문자열이거나 유효한 지역 목록에 포함되어 있으면 유효
        return value == null || VALID_REGIONS.contains(value);
    }
}

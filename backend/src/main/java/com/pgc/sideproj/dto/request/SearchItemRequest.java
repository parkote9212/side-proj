package com.pgc.sideproj.dto.request;

import com.pgc.sideproj.validation.ValidRegion;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SearchItemRequest {

    @Size(max = 100, message = "검색어는 100자 이하여야 합니다")
    private String keyword;

    @ValidRegion(message = "유효하지 않은 지역입니다")
    private String region;

    @Min(value = 1, message = "페이지는 1 이상이어야 합니다")
    @Max(value = 1000, message = "페이지는 1000 이하여야 합니다")
    private Integer page;

    @Min(value = 1, message = "페이지 크기는 1 이상이어야 합니다")
    @Max(value = 100, message = "페이지 크기는 100 이하여야 합니다")
    private Integer size;
}

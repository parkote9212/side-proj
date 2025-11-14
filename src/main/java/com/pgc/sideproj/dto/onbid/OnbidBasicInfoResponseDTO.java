package com.pgc.sideproj.dto.onbid;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JacksonXmlRootElement(localName = "response") // ◀ 1. 루트는 "response"
public class OnbidBasicInfoResponseDTO {

    @JsonProperty("body")
    private Body body;

    @Getter
    @NoArgsConstructor
    public static class Body {
        // 2. 래퍼(<items>) 없이 단일 "item"을 바로 파싱
        @JsonProperty("item")
        private OnbidBasicInfoDTO item;
    }
}
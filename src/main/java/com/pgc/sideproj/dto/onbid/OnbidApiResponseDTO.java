package com.pgc.sideproj.dto.onbid;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@JacksonXmlRootElement(localName = "response")
public class OnbidApiResponseDTO {

    @JsonProperty("header")
    private Header header;

    @JsonProperty("body")
    private Body body;

    // Header 중첩 클래스
    @Getter
    @NoArgsConstructor
    public static class Header {
        @JsonProperty("resultCode")
        private String resultCode;

        @JsonProperty("resultMsg")
        private String resultMsg;
    }

    @Getter
    @NoArgsConstructor
    public static class Body {
        // <items> 태그 안의 <item> 리스트를 매핑
        @JacksonXmlElementWrapper(localName = "items")
        @JsonProperty("item")
        private List<OnbidItemDTO> items;

        @JsonProperty("numOfRows")
        private int numOfRows;

        @JsonProperty("pageNo")
        private int pageNo;

        @JsonProperty("totalCount")
        private int totalCount;
    }

}

package com.pgc.sideproj.dto.onbid;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

// OnbidApiResponseDTO와 구조가 동일합니다. (response > body > items > item) [cite: 76]
@Getter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JacksonXmlRootElement(localName = "response")
public class OnbidFileInfoResponseDTO {

    @JsonProperty("body")
    private Body body;

    @Getter
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Body {
        @JsonProperty("pageNo")
        private String pageNo;
        
        @JacksonXmlElementWrapper(localName = "items")
        @JsonProperty("fileItem") // ◀ 주의: item이 아니라 fileItem [cite: 76]
        private List<OnbidFileInfoDTO> files;
    }

    @Getter
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class OnbidFileInfoDTO {
        @JsonProperty("ATCH_FILE_NM")
        private String atchFileNm; // 첨부파일명 [cite: 73]

        @JsonProperty("FILE_PTH_CNTN")
        private String filePthCntn; // 파일경로내용 [cite: 73]

        // (참고) 실제 다운로드 URL은 filePthCntn을 조합해서 만들어야 할 수 있습니다.
    }
}
package by.system.gethired.dto;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class HhVacancyDto {

    private Long id;
    private String name;

    @JsonProperty("alternate_url")
    private String url;

    private HhArea area;
    private HhSalary salary;
    private HhSnippet snippet;
    private HhEmployer employer;

    @JsonProperty("published_at")
    private String publishedAt;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class HhArea {
        private Integer id;
        private String name;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class HhSalary {
        private Integer from;
        private Integer to;
        private String currency;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class HhSnippet {
        private String requirement;
        private String responsibility;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class HhEmployer {
        private Long id;
        private String name;
    }
}
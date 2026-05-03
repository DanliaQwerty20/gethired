package by.system.gethired.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class HhResponse {
    private List<HhVacancyDto> items;
    private int page;
    private int pages;
    private int found;
}
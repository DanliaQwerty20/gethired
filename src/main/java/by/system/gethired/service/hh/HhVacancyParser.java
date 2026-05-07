package by.system.gethired.service.hh;

import by.system.gethired.client.HhApiClient;
import by.system.gethired.dto.HhResponse;
import by.system.gethired.dto.HhVacancyDto;
import by.system.gethired.entity.UserFilter;
import by.system.gethired.entity.Vacancy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
public class HhVacancyParser {

    private final HhApiClient apiClient;
    private final HhAreaResolver areaResolver;

    public List<Vacancy> fetchVacancies(UserFilter filter, int maxPages, int perPage) {
        List<Vacancy> result = new ArrayList<>();
        int area = areaResolver.resolve(filter.getLocation()).orElse(113); // Россия по умолчанию
        for (int page = 0; page < maxPages; page++) {
            Map<String, Object> params = new LinkedHashMap<>();
            params.put("text", filter.getJobTitle() != null ? filter.getJobTitle() : "");
            params.put("area", area);
            params.put("per_page", perPage);
            params.put("page", page);
            if (filter.getSalaryFrom() != null) {
                params.put("salary", filter.getSalaryFrom());
            }

            HhResponse hhResponse = apiClient.get("/vacancies", HhResponse.class, params);
            if (hhResponse == null || hhResponse.getItems() == null || hhResponse.getItems().isEmpty()) {
                break;
            }
            for (HhVacancyDto dto : hhResponse.getItems()) {
                Vacancy vac = mapToVacancy(dto, filter.getJobTitle());
                result.add(vac);
            }
            if (hhResponse.getPage() >= hhResponse.getPages() - 1) {
                break; // последняя страница
            }
        }
        return result;
    }

    private Vacancy mapToVacancy(HhVacancyDto dto, String keyword) {
        Vacancy v = new Vacancy();
        v.setExternalId(dto.getId());
        v.setTitle(dto.getName());
        v.setUrl(dto.getUrl());
        v.setLocation(dto.getArea() != null ? dto.getArea().getName() : "Не указано");
        v.setSalary(formatSalary(dto.getSalary()));
        v.setDescription(buildDescription(dto));
        if (dto.getPublishedAt() != null) {
            v.setPublishedAt(parseDateTime(dto.getPublishedAt()));
        }
        return v;
    }

    private String formatSalary(HhVacancyDto.HhSalary salary) {
        if (salary == null) return "не указана";
        StringBuilder sb = new StringBuilder();
        if (salary.getFrom() != null) sb.append("от ").append(salary.getFrom()).append(" ");
        if (salary.getTo() != null) sb.append("до ").append(salary.getTo()).append(" ");
        if (salary.getCurrency() != null) sb.append(salary.getCurrency());
        return sb.toString().trim();
    }

    private String buildDescription(HhVacancyDto dto) {
        StringBuilder desc = new StringBuilder();
        if (dto.getSnippet() != null) {
            if (dto.getSnippet().getRequirement() != null) {
                desc.append("Требования: ").append(dto.getSnippet().getRequirement()).append("\n");
            }
            if (dto.getSnippet().getResponsibility() != null) {
                desc.append("Обязанности: ").append(dto.getSnippet().getResponsibility()).append("\n");
            }
        }
        return desc.toString().trim();
    }

    private LocalDateTime parseDateTime(String dateStr) {
        try {
            return LocalDateTime.parse(dateStr, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        } catch (Exception e) {
            return null;
        }
    }
}
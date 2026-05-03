package by.system.gethired.service.hh;

import by.system.gethired.dto.HhResponse;
import by.system.gethired.dto.HhVacancyDto;
import by.system.gethired.entity.UserFilter;
import by.system.gethired.entity.Vacancy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HeadHunterServiceImpl implements HeadHunterService {

    private final RestClient restClient;

    @Override
    public List<Vacancy> search(UserFilter filter, int page, int perPage) {
        URI uri = UriComponentsBuilder.fromUriString("https://api.hh.ru/vacancies")
                .queryParam("text", filter.getJobTitle() != null ? filter.getJobTitle() : "")
                .queryParam("area", mapArea(filter.getLocation()))
                .queryParam("salary", mapSalary(filter))
                .queryParam("per_page", perPage)
                .queryParam("page", page)
                .build(false)
                .toUri();

        HhResponse response = restClient.get()
                .uri(uri)
                .retrieve()
                .body(HhResponse.class);

        return mapToVacancies(response);
    }

    private Integer mapArea(String location) {
        if (location == null) return null;
        return switch (location.toLowerCase()) {
            case "москва" -> 1;
            case "санкт-петербург" -> 2;
            case "россия" -> 113;
            default -> null;
        };
    }

    private String mapSalary(UserFilter filter) {
        if (filter.getSalaryFrom() != null && filter.getSalaryTo() != null) {
            return filter.getSalaryFrom() + "-" + filter.getSalaryTo();
        } else if (filter.getSalaryFrom() != null) {
            return filter.getSalaryFrom().toString();
        } else if (filter.getSalaryTo() != null) {
            return filter.getSalaryTo().toString();
        }
        return null;
    }

    private List<Vacancy> mapToVacancies(HhResponse response) {
        if (response == null || response.getItems() == null) {
            return List.of();
        }
        List<Vacancy> result = new ArrayList<>();
        for (HhVacancyDto dto : response.getItems()) {
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
            result.add(v);
        }
        return result;
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
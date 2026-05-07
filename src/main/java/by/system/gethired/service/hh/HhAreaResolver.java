package by.system.gethired.service.hh;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class HhAreaResolver {

    private final RestClient restClient;
    private final Map<String, Integer> nameToId = new ConcurrentHashMap<>();
    private volatile boolean loaded = false;

    public HhAreaResolver(RestClient restClient) {
        this.restClient = restClient;
    }

    @PostConstruct
    public void loadAreas() {
        try {
            List<Map<String, Object>> areas = restClient.get()
                    .uri("https://api.hh.ru/areas")
                    .header("User-Agent", "GetHiredBot/1.0")
                    .retrieve()
                    .body(List.class);
            walk(areas);
            loaded = true;
            log.info("Loaded {} areas from HH", nameToId.size());
        } catch (Exception e) {
            log.error("Failed to load HH areas", e);
        }
    }

    @SuppressWarnings("unchecked")
    private void walk(List<Map<String, Object>> nodes) {
        for (var node : nodes) {
            String name = ((String) node.get("name")).toLowerCase();
            Object idObj = node.get("id");
            if (idObj instanceof Number) {
                nameToId.put(name, ((Number) idObj).intValue());
            }
            Object sub = node.get("areas");
            if (sub instanceof List) {
                walk((List<Map<String, Object>>) sub);
            }
        }
    }

    public Optional<Integer> resolve(String location) {
        if (location == null) return Optional.empty();
        String key = location.trim().toLowerCase();
        if (key.isEmpty()) return Optional.empty();
        return Optional.ofNullable(nameToId.get(key));
    }
}
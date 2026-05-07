package by.system.gethired.service.hh;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;

@Component
public class TokenStorage {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final File tokenFile;

    public TokenStorage(
            @Value("${app.file-storage.upload-dir}")
            String uploadDir
    ) {
        File dir = new File(uploadDir);

        if (!dir.exists()) {
            dir.mkdirs();
        }

        this.tokenFile = new File(dir, "hh_token.json");
    }

    public synchronized TokenData load() {

        if (!tokenFile.exists()) {
            return null;
        }

        try {
            return objectMapper.readValue(
                    tokenFile,
                    TokenData.class
            );
        } catch (IOException e) {
            return null;
        }
    }

    public synchronized void save(TokenData tokenData) {

        try {
            objectMapper.writeValue(tokenFile, tokenData);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public record TokenData(String accessToken) {
    }
}
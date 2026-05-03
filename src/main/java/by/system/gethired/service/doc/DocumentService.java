package by.system.gethired.service.doc;


import by.system.gethired.entity.CoverLetterTemplate;
import by.system.gethired.entity.Resume;
import by.system.gethired.entity.User;

public interface DocumentService {

    Resume processResumeFile(User user, byte[] content, String originalFilename, String mimeType);

    Resume saveResumeText(User user, String text);

    CoverLetterTemplate processCoverLetterFile(User user, byte[] content, String originalFilename, String mimeType);

    CoverLetterTemplate saveCoverLetterText(User user, String text);

    Resume getLatestResume(User user);

    CoverLetterTemplate getLatestCoverLetter(User user);
}
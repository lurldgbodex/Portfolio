package tech.sgcor.portfolio.language.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import tech.sgcor.portfolio.exceptions.BadRequestException;
import tech.sgcor.portfolio.exceptions.ResourceNotFound;
import tech.sgcor.portfolio.language.dto.LanguageDto;
import tech.sgcor.portfolio.language.dto.LanguageUpdate;
import tech.sgcor.portfolio.language.entity.Language;
import tech.sgcor.portfolio.language.repository.LanguageRepository;
import tech.sgcor.portfolio.shared.CustomResponse;
import tech.sgcor.portfolio.shared.SharedService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LanguageService {
    private final LanguageRepository repository;

    public Language createLanguage(LanguageDto request) {
        var language = new Language();

        language.setLang(request.getLang());
        language.setLevel(request.getLevel());
        language.setUserId(request.getUser_id());
        language.setProficiencyScore(request.getProficiency_score());

        return repository.save(language);
    }

    public List<Language> getLanguages(Long userId) {
        return repository.findByUserId(userId);
    }

    public CustomResponse updateLanguage(long id, LanguageUpdate request) {
        var language = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFound("Resource not found with id"));

        if (request.getLang() == null && request.getLevel() == null) {
            throw new BadRequestException("you need to provide field to update");
        }

        language.setLang(SharedService.isNotBlank(request.getLang()) ? request.getLang() : language.getLang());
        language.setLevel(SharedService.isNotBlank(request.getLevel()) ? request.getLevel() :  language.getLevel());
        language.setProficiencyScore(SharedService.isNotBlank(String.valueOf(request.getProficiency_score()))
                ? request.getProficiency_score() : language.getProficiencyScore());

        repository.save(language);

        return new CustomResponse(200, "Language updated successfully", HttpStatus.OK);
    }

    public CustomResponse deleteLanguage(long id) {
        var language = repository.findById(id)
                .orElseThrow(()-> new ResourceNotFound("Resource not found with id"));

        repository.delete(language);

        return new CustomResponse(200, "language deleted successfully", HttpStatus.OK);
    }
}

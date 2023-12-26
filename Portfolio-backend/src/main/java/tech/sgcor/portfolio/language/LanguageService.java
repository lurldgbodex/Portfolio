package tech.sgcor.portfolio.language;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import tech.sgcor.portfolio.exceptions.ResourceNotFound;
import tech.sgcor.portfolio.shared.CustomResponse;
import tech.sgcor.portfolio.shared.SharedService;

import java.util.List;

@Service
@Validated
@RequiredArgsConstructor
public class LanguageService {
    private final LanguageRepository repository;

    public Language createLanguage(@Valid LanguageDto request) {
        var language = new Language();

        language.setLang(request.getLang());
        language.setLevel(request.getLevel());
        language.setUserId(request.getUser_id());

        return repository.save(language);
    }

    public List<Language> getLanguages(Long userId) {
        return repository.findByUserId(userId);
    }

    public CustomResponse updateLanguage(
            long id, @Valid LanguageUpdate request) throws ResourceNotFound, BadRequestException {
        var language = repository.findById(id)
                .orElseThrow(() ->new ResourceNotFound("Resource not found with id"));

        if (request.getLang() == null && request.getLevel() == null) {
            throw new BadRequestException("you need to provide field to update");
        }

        language.setLang(SharedService.isNotBlank(request.getLang()) ? request.getLang() : language.getLang());
        language.setLevel(SharedService.isNotBlank(request.getLevel()) ? request.getLevel() :  language.getLevel());

        repository.save(language);

        return new CustomResponse(200, "Language updated successfully", HttpStatus.OK);
    }

    public CustomResponse deleteLanguage(long id) throws ResourceNotFound {
        var language = repository.findById(id)
                .orElseThrow(()-> new ResourceNotFound("Resource not found with id"));

        repository.delete(language);

        return new CustomResponse(200, "language deleted successfully", HttpStatus.OK);
    }
}

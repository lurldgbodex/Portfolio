package tech.sgcor.portfolio.language;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LanguageDto {
    private long id;

    @NotNull(message = "user_id is required")
    private Long user_id;

    @NotBlank(message = "lang is required and should not be blank")
    private String lang;

    @NotBlank(message = "level is required and should not be blank")
    private String level;
}

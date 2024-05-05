package tech.sgcor.portfolio.language.dto;

import lombok.Data;

@Data
public class LanguageUpdate {

    private String lang;
    private String level;
    private int proficiency_score;
}

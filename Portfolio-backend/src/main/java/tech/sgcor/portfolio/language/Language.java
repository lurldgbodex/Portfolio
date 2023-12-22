package tech.sgcor.portfolio.language;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "languages")
@NoArgsConstructor
public class Language {
    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private String lang;

    @Column(nullable = false)
    private String level;
}

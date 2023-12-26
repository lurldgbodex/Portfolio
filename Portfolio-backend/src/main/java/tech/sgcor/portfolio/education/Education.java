package tech.sgcor.portfolio.education;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Entity
@Table(name = "educations")
@NoArgsConstructor
public class Education {
    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private String school;

    @Column(nullable = false)
    private String degree;

    @Column(nullable = false)
    private String course;

    @Column(nullable = false)
    private String grade;

    @Column
    private String description;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;
}

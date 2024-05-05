package tech.sgcor.portfolio.certification.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@Entity
@NoArgsConstructor
@Table(name = "certifications")
public class Certification {
    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String body;

    @Column(nullable = false)
    private LocalDate date;

    @OneToMany(mappedBy = "certification", cascade = CascadeType.ALL)
    private List<CertificationDetails> details;
}

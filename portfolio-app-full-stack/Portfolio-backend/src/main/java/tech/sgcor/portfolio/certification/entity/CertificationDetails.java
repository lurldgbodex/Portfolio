package tech.sgcor.portfolio.certification.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import tech.sgcor.portfolio.certification.entity.Certification;

@Data
@Entity
@NoArgsConstructor
@Table(name = "certification_details")
public class CertificationDetails {
    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private String details;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "certification_id", nullable = false)
    private Certification certification;
}

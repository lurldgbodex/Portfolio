package tech.sgcor.portfolio.certification;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

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

package tech.sgcor.portfolio.project.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@Table(name = "project_details")
public class ProjectDetails {
    @Id
    @GeneratedValue
    private Long id;

    @Column
    private String details;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;
}

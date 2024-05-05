package tech.sgcor.portfolio.project.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Entity
@Table(name = "projects")
@NoArgsConstructor
public class Project {
    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String type;

    @Column(nullable = false)
    private String url;

    @Column(nullable = false, length = 500)
    private String description;

    @Column(nullable = false)
    private String imageLink;

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL)
    private List<ProjectDetails> details;
}

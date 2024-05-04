package tech.sgcor.portfolio.skills;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "skills")
@NoArgsConstructor
public class Skill {
    @Id
    @GeneratedValue
    private long id;

    @Column(nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name = "skill_type_id", nullable = false)
    @JsonIgnore
    private SkillType skillType;
}

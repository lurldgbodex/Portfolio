package tech.sgcor.portfolio.skills.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Entity
@Table(name = "skill_types")
@NoArgsConstructor
public class SkillType {
    @Id
    @GeneratedValue
    private long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private String name;

    @OneToMany(mappedBy = "skillType", cascade = CascadeType.ALL)
    private List<Skill> skills;
}

package tech.sgcor.portfolio.about.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import tech.sgcor.portfolio.user.entity.User;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "abouts")
public class About {
    @Id
    @GeneratedValue
    private Long id;
    @Column(nullable = false)
    private String address;
    @Column(nullable = false)
    private LocalDate dob;
    @Column(nullable = false)
    private String title;
    @Column(nullable = false)
    private String phoneNumber;
    @Column(nullable = false, length = 1000)
    private String summary;
    @Column(nullable = false)
    private String email;
    @Column(nullable = false)
    private String github;
    @Column(nullable = false)
    private String linkedin;
    @Column(nullable = false)
    private String cvUrl;

    @Column
    private String twitter;
    @Column
    private String medium;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}

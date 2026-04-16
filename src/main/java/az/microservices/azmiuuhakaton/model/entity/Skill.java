package az.microservices.azmiuuhakaton.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "skills")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Skill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String category;

    @Column(length = 4000)
    private String description;

    private String difficulty;

    private String iconUrl;

    @Column
    @Builder.Default
    private Boolean isActive = true;

    @Column
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime updatedAt;

    @ManyToMany(mappedBy = "skills")
    @Builder.Default
    private List<User> users = new ArrayList<>();

    @OneToMany(mappedBy = "skill")
    @Builder.Default
    private List<Task> tasks = new ArrayList<>();

    @PrePersist
    private void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.isActive == null) {
            this.isActive = true;
        }
    }

    @PreUpdate
    private void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    @PostLoad
    private void onPostLoad() {
        // Protect against legacy rows where is_active could be NULL.
        if (this.isActive == null) {
            this.isActive = true;
        }
    }
}


package tn.univ.onlineuniv.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String title;
    private String subject;
    @Column(length = 500)
    private String description;
    private String thumbnailUrl;
    private String videoUrl;
    private boolean published;
    @CreationTimestamp
    @Column(updatable = false)
    Timestamp createdAt;
    @UpdateTimestamp
    Timestamp lastModified;
    @ManyToOne
    private User user;

    public Course(String title, String subject, String description, boolean published,String thumbnailUrl,String videoUrl, User user) {
        this.title = title;
        this.subject = subject ;
        this.description = description ;
        this.published = published ;
        this.thumbnailUrl = thumbnailUrl;
        this.videoUrl = videoUrl;
        this.user = user ;
    }

}

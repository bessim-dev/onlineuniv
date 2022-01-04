package tn.univ.onlineuniv.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

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
    private String description;
    private String thumbnailUrl;
    private String videoUrl;
    private boolean published;
    @ManyToOne
    private User user;

    public Course(String title, String subject, String description, boolean published,String thumbnailUrl,String videoUrl, User user) {
        this.title = title;
        this.subject = subject ;
        this. description = description ;
        this.published = published ;
        this.thumbnailUrl = thumbnailUrl;
        this.videoUrl = videoUrl;
        this.user = user ;
    }

}

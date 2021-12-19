package tn.univ.onlineuniv.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

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
    private boolean published;
    @ManyToOne
    private User user;
    @OneToMany
    private List<Comment> comments = new ArrayList<>();
    @OneToMany
    private List<Rate> rates = new ArrayList<>();

    public Course(String title, String subject, String description, boolean published, User user) {
        this.title = title;
        this.subject = subject ;
        this. description = description ;
        this.published = published ;
        this.user = user ;
    }

}

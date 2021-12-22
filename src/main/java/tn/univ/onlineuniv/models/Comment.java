package tn.univ.onlineuniv.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@NoArgsConstructor

@Entity
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String title;
    private String text;
    private Integer rate;
    @ManyToOne
    private User user;
    @ManyToOne
    private Course course;
    public Comment(String text,Integer rate, User user,Course course) {
        this.title = user.getFirstName();
        this.text = text;
        this.rate = rate;
        this.user = user;
        this.course = course;
    }
}

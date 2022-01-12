package tn.univ.onlineuniv.models;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Id;
import java.time.LocalDateTime;

@NoArgsConstructor
@Data
public class PublicCourse {
    @Id
    private Long id;
    private String title;
    private String subject;
    private String description;
    private String thumbnailUrl;
    private String author;
    private LocalDateTime createdAt;
}

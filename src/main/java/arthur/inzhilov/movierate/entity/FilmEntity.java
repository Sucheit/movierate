package arthur.inzhilov.movierate.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.time.LocalDate;

@Data
@Entity
@Table(name= "films")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@DynamicUpdate
public class FilmEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "film_id")
    private Long id;

    @Size(max = 1000)
    @Column(name = "name")
    private String name;

    @Size(max = 1000)
    @Column(name = "description")
    private String description;

    @Column(name = "release_date")
    private LocalDate releaseDate;

    @Column(name = "duration")
    private int duration;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name="genre_id", nullable=false)
    private GenreEntity genre;

    @Lob
    @Column(columnDefinition = "MEDIUMBLOB", name = "image")
    private String image;
}

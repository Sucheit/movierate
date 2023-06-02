package arthur.inzhilov.movierate.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "film_rating")
@Builder
public class FilmRatingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "film_rating_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "film_id", referencedColumnName = "film_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private FilmEntity filmEntity;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private UserEntity userEntity;

    private Integer rating;
}

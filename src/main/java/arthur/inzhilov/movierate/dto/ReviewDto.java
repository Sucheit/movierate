package arthur.inzhilov.movierate.dto;

import arthur.inzhilov.movierate.entity.FilmEntity;
import arthur.inzhilov.movierate.entity.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReviewDto {

    private Long id;

    private Long filmId;

    private FilmEntity film;

    private Long userId;

    private UserEntity user;

    private String text;

    private String postDate;
}

package arthur.inzhilov.movierate.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FilmRatingDto {


    private Long filmId;

    private Integer rating;
}

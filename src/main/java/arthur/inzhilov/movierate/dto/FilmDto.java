package arthur.inzhilov.movierate.dto;

import arthur.inzhilov.movierate.entity.GenreEntity;
import arthur.inzhilov.movierate.validation.ReleaseDateValidation;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FilmDto {

    private Long id;

    @NotBlank(message = "Название не может быть пустым")
    @Size(max = 50, message = "Название не может быть больше 50 символов.")
    private String name;

    @NotBlank(message = "Описание не может быть пустым")
    @Size(max = 1000, message = "Описание фильма не может быть больше 1000 символов.")
    private String description;

    @ReleaseDateValidation(message = "Дата неправильного формата или раньше 28.12.1895")
    private String releaseDate;

    @Positive(message = "Продолжительность должна быть положительной.")
    private int duration;

//    @GenreIdValidation(message = "Неверный Id жанра.")
    private Long genreId;

    private GenreEntity genre;

    private MultipartFile file;

    private String image;
}

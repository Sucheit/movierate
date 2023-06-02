package arthur.inzhilov.movierate.validation;

import arthur.inzhilov.movierate.entity.GenreEntity;
import arthur.inzhilov.movierate.service.FilmService;
import lombok.RequiredArgsConstructor;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class GenreIdValidator implements ConstraintValidator<GenreIdValidation, Long>{

    private final FilmService filmService;

    @Override
    public boolean isValid(Long genreId, ConstraintValidatorContext constraintValidatorContext) {
        List<GenreEntity> genreEntityList = filmService.getGenres();
        Set<Long> genreIds = genreEntityList.stream()
                .map(GenreEntity::getId)
                .collect(Collectors.toSet());
        return genreIds.contains(genreId);
    }
}

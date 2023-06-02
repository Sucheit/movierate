package arthur.inzhilov.movierate.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;

import static arthur.inzhilov.movierate.utility.Constants.FILM_RELEASE_DATE_FORMATTER;
import static arthur.inzhilov.movierate.utility.Constants.MIN_FILM_RELEASE_DATE;

public class ReleaseDateValidator implements ConstraintValidator<ReleaseDateValidation, String> {

    @Override
    public boolean isValid(String date, ConstraintValidatorContext constraintValidatorContext) {
        try {
            LocalDate localDate = LocalDate.parse(date, FILM_RELEASE_DATE_FORMATTER);
            return localDate.isAfter(MIN_FILM_RELEASE_DATE);
        }
        catch (Exception e) {
            return false;
        }
    }
}

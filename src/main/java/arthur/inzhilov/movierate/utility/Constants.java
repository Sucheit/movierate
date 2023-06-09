package arthur.inzhilov.movierate.utility;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class Constants {

    public final static Integer NUMBER_OF_RECOMMENDED_FILMS = 5;

    public final static LocalDate MIN_FILM_RELEASE_DATE = LocalDate.of(1895, 12, 28);

    public final static List<Integer> RATINGS = List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

    public final static DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#.#");

    public final static int IMAGE_WIDTH = 600;

    public final static int IMAGE_HEIGHT = 600;

    public final static DateTimeFormatter FILM_RELEASE_DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    public final static DateTimeFormatter REVIEW_POST_DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
}

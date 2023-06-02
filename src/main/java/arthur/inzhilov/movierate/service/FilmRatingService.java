package arthur.inzhilov.movierate.service;

import arthur.inzhilov.movierate.entity.FilmEntity;
import arthur.inzhilov.movierate.entity.FilmRatingEntity;
import arthur.inzhilov.movierate.entity.UserEntity;
import arthur.inzhilov.movierate.exception.NotFoundException;
import arthur.inzhilov.movierate.dao.FilmRatingRepository;
import arthur.inzhilov.movierate.dao.FilmRepository;
import arthur.inzhilov.movierate.dao.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static arthur.inzhilov.movierate.utility.Constants.DECIMAL_FORMAT;

@Service
@RequiredArgsConstructor
public class FilmRatingService {

    private final FilmRatingRepository filmRatingRepository;

    private final FilmRepository filmRepository;

    private final UserRepository userRepository;

    public String getFilmRatingByFilmId(Long filmId) {
        Double avgRating = filmRatingRepository.getAvgRating(filmId);
        String result;
        if (avgRating == null) {
            result = "0";
        } else {
            result = DECIMAL_FORMAT.format(avgRating);
        }
        return result;
    }

    public FilmRatingEntity findByFilmIdAndUserId(Long filmId, Long userId) {
        FilmEntity filmEntity = filmRepository.findById(filmId)
                .orElseThrow(() -> new NotFoundException("Фильм не найден."));
        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Фильм не найден."));
        return filmRatingRepository.findByFilmEntityAndUserEntity(filmEntity, userEntity);
    }

    public FilmRatingEntity saveFilmRating(Long filmId, Long userId, Integer rating) {
        FilmEntity filmEntity = filmRepository.findById(filmId)
                .orElseThrow(() -> new NotFoundException("Фильм не найден."));
        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден."));
        if (filmRatingRepository.existsByFilmEntityAndUserEntity(filmEntity, userEntity)) {
            FilmRatingEntity filmRatingEntity = filmRatingRepository.findByFilmEntityAndUserEntity(filmEntity, userEntity);
            filmRatingRepository.deleteById(filmRatingEntity.getId());
        }
        return filmRatingRepository.save(FilmRatingEntity.builder()
                .filmEntity(filmEntity)
                .userEntity(userEntity)
                .rating(rating)
                .build());
    }
}

package arthur.inzhilov.movierate.dao;

import arthur.inzhilov.movierate.entity.FilmEntity;
import arthur.inzhilov.movierate.entity.FilmRatingEntity;
import arthur.inzhilov.movierate.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface FilmRatingRepository extends JpaRepository<FilmRatingEntity, Long> {

    @Query(value = "SELECT AVG(RATING) FROM FILM_RATING fr WHERE fr.FILM_ID = ?1", nativeQuery = true)
    Double getAvgRating(Long filmId);

    FilmRatingEntity findByFilmEntityAndUserEntity(FilmEntity filmEntity, UserEntity userEntity);

    Boolean existsByFilmEntityAndUserEntity(FilmEntity filmEntity, UserEntity userEntity);
}

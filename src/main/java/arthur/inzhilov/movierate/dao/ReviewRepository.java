package arthur.inzhilov.movierate.dao;

import arthur.inzhilov.movierate.entity.ReviewEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewRepository extends JpaRepository<ReviewEntity, Long> {

    List<ReviewEntity> findByFilmIdOrderByPostDateDesc(Long filmId);

    ReviewEntity findByFilmIdAndUserId(Long filmId, Long userId);
}

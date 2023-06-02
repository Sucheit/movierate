package arthur.inzhilov.movierate.dao;

import arthur.inzhilov.movierate.entity.FilmEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FilmRepository extends JpaRepository<FilmEntity, Long> {

    List<FilmEntity> findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(String name, String description);

}

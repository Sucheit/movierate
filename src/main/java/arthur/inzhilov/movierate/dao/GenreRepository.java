package arthur.inzhilov.movierate.dao;

import arthur.inzhilov.movierate.entity.GenreEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GenreRepository extends JpaRepository<GenreEntity, Long> {
}

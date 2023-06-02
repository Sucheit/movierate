package arthur.inzhilov.movierate.dao;

import arthur.inzhilov.movierate.entity.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<RoleEntity, Long> {
}

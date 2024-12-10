package nz.roag.archerylogbook.db;

import nz.roag.archerylogbook.db.model.Shot;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShotRepository extends JpaRepository<Shot, Long> {
}
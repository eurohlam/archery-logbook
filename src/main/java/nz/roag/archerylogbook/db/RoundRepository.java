package nz.roag.archerylogbook.db;

import nz.roag.archerylogbook.db.model.Round;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoundRepository extends JpaRepository<Round, Long> {
}
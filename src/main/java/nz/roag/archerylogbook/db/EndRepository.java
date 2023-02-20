package nz.roag.archerylogbook.db;

import nz.roag.archerylogbook.db.model.End;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EndRepository extends JpaRepository<End, Long> {
}
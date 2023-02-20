package nz.roag.archerylogbook.db;

import nz.roag.archerylogbook.db.model.Club;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClubRepository extends JpaRepository<Club, Long> {
}
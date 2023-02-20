package nz.roag.archerylogbook.db;

import nz.roag.archerylogbook.db.model.Archer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArcherRepository extends JpaRepository<Archer, Long> {
}
package nz.roag.archerylogbook.db;

import nz.roag.archerylogbook.db.model.Archer;
import nz.roag.archerylogbook.db.model.Bow;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BowRepository extends JpaRepository<Bow, Long> {

    List<Bow> findByArcherId(long archerId, Sort sort);
}
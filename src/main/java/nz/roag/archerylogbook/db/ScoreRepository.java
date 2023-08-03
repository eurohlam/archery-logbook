package nz.roag.archerylogbook.db;

import nz.roag.archerylogbook.db.model.Score;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ScoreRepository extends JpaRepository<Score, Long> {

    List<Score> findByArcherId(long archerId, Sort sort);

    List<Score> findByArcherId(long archerId, Pageable page);

}
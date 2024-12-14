package nz.roag.archerylogbook.db;

import nz.roag.archerylogbook.db.model.Competition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CompetitionRepository extends JpaRepository<Competition, Long> {

    List<Competition> findByArcherId(long archerId, Sort sort);

    /**
     * Returns all competitions for current archerId ignoring `archived` flag
     * @param archerId
     * @param page
     * @return all competitions for current archerId
     */
    Page<Competition> findByArcherId(long archerId, Pageable page);

    /**
     * Returns all competitions of current competitionType for current archerId
     * @param archerId
     * @param competitionType
     * @param page
     * @return all competitions of current competitionType for current archerId
     */
    Page<Competition> findByArcherIdAndCompetitionType(long archerId, Competition.CompetitionType competitionType, Pageable page);
}

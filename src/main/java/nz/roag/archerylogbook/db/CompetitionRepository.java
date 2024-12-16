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
     * @param archived - if archived=false then return competitions that are actual. If archived=true then returns competitions that have been marked as removed by user
     * @param page
     * @return all competitions for current archerId
     */
    Page<Competition> findByArcherIdAndArchived(long archerId, boolean archived, Pageable page);

    /**
     * Returns all competitions of current competitionType for current archerId
     * @param archerId
     * @param competitionType
     * @param archived - if archived=false then return competitions that are actual. If archived=true then returns competitions that have been marked as removed by user
     * @param page
     * @return all competitions of current competitionType for current archerId
     */
    Page<Competition> findByArcherIdAndCompetitionTypeAndArchived(long archerId,
                                                                  Competition.CompetitionType competitionType,
                                                                  boolean archived,
                                                                  Pageable page);
}

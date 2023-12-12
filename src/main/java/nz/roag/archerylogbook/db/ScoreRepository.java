package nz.roag.archerylogbook.db;

import nz.roag.archerylogbook.db.model.Score;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ScoreRepository extends JpaRepository<Score, Long> {

    List<Score> findByArcherId(long archerId, Sort sort);

    /**
     * Returns all scores for current archerId ignoring `archived` flag
     * @param archerId
     * @param page
     * @return all scores for current archerId
     */
    Page<Score> findByArcherId(long archerId, Pageable page);

    /**
     * Returns scores for current archerId filtered by `archived` flag
     * @param archerId
     * @param archived - if archived=false then return scores that are actual. If archived=true then returns scores that have been marked as removed by user
     * @param page
     * @return scores for current archerId filtered by `archived` flag
     */
    Page<Score> findByArcherIdAndArchived(long archerId, boolean archived, Pageable page);

    @Modifying
    @Query("update Score set archived=?1 where id=?2")
    void setArchivedForScoreId(boolean archived, long scoreId);
}
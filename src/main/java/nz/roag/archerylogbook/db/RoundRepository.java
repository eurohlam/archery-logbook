package nz.roag.archerylogbook.db;

import nz.roag.archerylogbook.db.model.Round;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RoundRepository extends JpaRepository<Round, Long> {

    List<Round> findByArcherId(long archerId, Sort sort);

    /**
     * Returns all rounds for current archerId ignoring `archived` flag
     * @param archerId
     * @param page
     * @return all rounds for current archerId
     */
    Page<Round> findByArcherId(long archerId, Pageable page);

    /**
     * Returns rounds for current archerId filtered by `archived` flag
     * @param archerId
     * @param archived - if archived=false then return rounds that are actual. If archived=true then returns rounds that have been marked as removed by user
     * @param page
     * @return rounds for current archerId filtered by `archived` flag
     */
    Page<Round> findByArcherIdAndArchived(long archerId, boolean archived, Pageable page);

    @Modifying
    @Query("update Round set archived=?1 where id=?2")
    void setArchivedForRoundId(boolean archived, long roundId);
}
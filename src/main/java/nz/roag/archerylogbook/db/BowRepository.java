package nz.roag.archerylogbook.db;

import nz.roag.archerylogbook.db.model.Bow;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BowRepository extends JpaRepository<Bow, Long> {

    /**
     * Returns all bows for current archerId
     * @param archerId
     * @param sort
     * @return all bows for current archerId
     */
    List<Bow> findByArcherId(long archerId, Sort sort);

    /**
     * Returns bows for current archerId filtered by `archived` flag
     * @param archerId
     * @param archived - if archived=false then return bows that are actual. If archived=true then returns bows that have been marked as removed by user
     * @param sort
     * @return bows for current archerId filtered by `archived` flag
     */
    List<Bow> findByArcherIdAndArchived(long archerId, boolean archived, Sort sort);

    @Modifying
    @Query("update Bow set archived=?1 where id=?2")
    void setArchivedForBowId(boolean archived, long bowId);
}
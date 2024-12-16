package nz.roag.archerylogbook.db;

import nz.roag.archerylogbook.db.model.Archer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ArcherRepository extends JpaRepository<Archer, Long> {

    Page<Archer> findByClubIdOrderByLastNameAsc(long clubId, Pageable page);

    /**
     * Returns archers filtered by `archived` flag
     * @param archived - if archived=false then return archers that are active. If archived=true then returns archers that have been marked as removed by user
     * @param sort
     * @return archers filtered by `archived` flag
     */
    List<Archer> findByArchived(boolean archived, Sort sort);

    /**
     * Returns archers filtered by `archived` flag
     * @param archived - if archived=false then return archers that are active. If archived=true then returns archers that have been marked as removed by user
     * @param page
     * @return archers filtered by `archived` flag
     */
    Page<Archer> findByArchived(boolean archived, Pageable page);
}
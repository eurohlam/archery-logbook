package nz.roag.archerylogbook.db;

import nz.roag.archerylogbook.db.model.Round;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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

    /**
     * Returns rounds for current archerId filtered by `distance`
     * @param archerId
     * @param distance
     * @param page
     * @return rounds for current archerId filtered by `distance`
     */
    Page<Round> findByArcherIdAndDistance(long archerId, String distance, Pageable page);

    /**
     * Returns rounds for current archerId filtered by `distance` and `archived` flag
     * @param archerId
     * @param distance
     * @param archived - if archived=false then return rounds that are actual. If archived=true then returns rounds that have been marked as removed by user
     * @param page
     * @return rounds for current archerId filtered by `distance` and `archived` flag
     */
    Page<Round> findByArcherIdAndDistanceAndArchived(long archerId, String distance, boolean archived, Pageable page);

    @Modifying
    @Query("update Round set archived=:archived where id=:roundId")
    void setArchivedForRoundId(@Param("archived") boolean archived, @Param("roundId") long roundId);

    /*** Dashboard methods ***/

    @Query("select count(1) from Round where archerId=:archerId and archived=false")
    int getTotalRoundsByArcherId(@Param("archerId") long archerId);

    @NativeQuery("select count(1) from archery_round where archer_id = ?1 and archived=false and round_date >= (CURRENT_DATE - INTERVAL '1' MONTH)")
    int getTotalRoundsLastMonthByArcherId(long archerId);

    @NativeQuery("""
                select * from
                         (select sums.*,
                                  row_number() over (partition by sums.distance order by sums.round_sum desc) ord
                           from (select distinct r.*,
                                                 sum(s.shot_score) over (partition by r.id order by null) as round_sum
                                 from archery_round r,
                                      archery_end e,
                                      archery_shot s
                                 where r.id = e.round_id
                                   and e.id = s.end_id
                                   and r.archer_id = ?1
                                   and r.archived = false) sums) mx
                where mx.ord = 1
                """)
    List<Round> getBestRoundsByDistanceForArcherId(long archerId);
}
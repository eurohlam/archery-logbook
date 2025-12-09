package nz.roag.archerylogbook.db;

import nz.roag.archerylogbook.db.model.Round;
import nz.roag.archerylogbook.db.model.TargetFace;
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

    /*** Filtering by bow, targetFace and combinations ***/

    /**
     * Returns rounds for current archerId filtered by `bowId`
     * @param archerId
     * @param bowId
     * @param page
     * @return rounds for current archerId filtered by `bowId`
     */
    Page<Round> findByArcherIdAndBowId(long archerId, long bowId, Pageable page);

    /**
     * Returns rounds for current archerId filtered by `bowId` and `archived` flag
     * @param archerId
     * @param bowId
     * @param archived - if archived=false then return rounds that are actual. If archived=true then returns rounds that have been marked as removed by user
     * @param page
     * @return rounds for current archerId filtered by `bowId` and `archived` flag
     */
    Page<Round> findByArcherIdAndBowIdAndArchived(long archerId, long bowId, boolean archived, Pageable page);

    /**
     * Returns rounds for current archerId filtered by `targetFace`
     * @param archerId
     * @param targetFace
     * @param page
     * @return rounds for current archerId filtered by `targetFace`
     */
    Page<Round> findByArcherIdAndTargetFace(long archerId, TargetFace targetFace, Pageable page);

    /**
     * Returns rounds for current archerId filtered by `targetFace` and `archived` flag
     * @param archerId
     * @param targetFace
     * @param archived - if archived=false then return rounds that are actual. If archived=true then returns rounds that have been marked as removed by user
     * @param page
     * @return rounds for current archerId filtered by `targetFace` and `archived` flag
     */
    Page<Round> findByArcherIdAndTargetFaceAndArchived(long archerId, TargetFace targetFace, boolean archived, Pageable page);

    /**
     * Returns rounds for current archerId filtered by `bowId` and `distance`
     * @param archerId
     * @param bowId
     * @param distance
     * @param page
     * @return rounds for current archerId filtered by `bowId` and `distance`
     */
    Page<Round> findByArcherIdAndBowIdAndDistance(long archerId, long bowId, String distance, Pageable page);

    /**
     * Returns rounds for current archerId filtered by `bowId`, `distance` and `archived` flag
     * @param archerId
     * @param bowId
     * @param distance
     * @param archived - if archived=false then return rounds that are actual. If archived=true then returns rounds that have been marked as removed by user
     * @param page
     * @return rounds for current archerId filtered by `bowId`, `distance` and `archived` flag
     */
    Page<Round> findByArcherIdAndBowIdAndDistanceAndArchived(long archerId, long bowId, String distance, boolean archived, Pageable page);

    /**
     * Returns rounds for current archerId filtered by `bowId` and `targetFace`
     * @param archerId
     * @param bowId
     * @param targetFace
     * @param page
     * @return rounds for current archerId filtered by `bowId` and `targetFace`
     */
    Page<Round> findByArcherIdAndBowIdAndTargetFace(long archerId, long bowId, TargetFace targetFace, Pageable page);

    /**
     * Returns rounds for current archerId filtered by `bowId`, `targetFace` and `archived` flag
     * @param archerId
     * @param bowId
     * @param targetFace
     * @param archived - if archived=false then return rounds that are actual. If archived=true then returns rounds that have been marked as removed by user
     * @param page
     * @return rounds for current archerId filtered by `bowId`, `targetFace` and `archived` flag
     */
    Page<Round> findByArcherIdAndBowIdAndTargetFaceAndArchived(long archerId, long bowId, TargetFace targetFace, boolean archived, Pageable page);

    /**
     * Returns rounds for current archerId filtered by `distance` and `targetFace`
     * @param archerId
     * @param distance
     * @param targetFace
     * @param page
     * @return rounds for current archerId filtered by `distance` and `targetFace`
     */
    Page<Round> findByArcherIdAndDistanceAndTargetFace(long archerId, String distance, TargetFace targetFace, Pageable page);

    /**
     * Returns rounds for current archerId filtered by `distance`, `targetFace` and `archived` flag
     * @param archerId
     * @param distance
     * @param targetFace
     * @param archived - if archived=false then return rounds that are actual. If archived=true then returns rounds that have been marked as removed by user
     * @param page
     * @return rounds for current archerId filtered by `distance`, `targetFace` and `archived` flag
     */
    Page<Round> findByArcherIdAndDistanceAndTargetFaceAndArchived(long archerId, String distance, TargetFace targetFace, boolean archived, Pageable page);

    /**
     * Returns rounds for current archerId filtered by `bowId`, `distance` and `targetFace`
     * @param archerId
     * @param bowId
     * @param distance
     * @param targetFace
     * @param page
     * @return rounds for current archerId filtered by `bowId`, `distance` and `targetFace`
     */
    Page<Round> findByArcherIdAndBowIdAndDistanceAndTargetFace(long archerId, long bowId, String distance, TargetFace targetFace, Pageable page);

    /**
     * Returns rounds for current archerId filtered by `bowId`, `distance`, `targetFace` and `archived` flag
     * @param archerId
     * @param bowId
     * @param distance
     * @param targetFace
     * @param archived - if archived=false then return rounds that are actual. If archived=true then returns rounds that have been marked as removed by user
     * @param page
     * @return rounds for current archerId filtered by `bowId`, `distance`, `targetFace` and `archived` flag
     */
    Page<Round> findByArcherIdAndBowIdAndDistanceAndTargetFaceAndArchived(long archerId, long bowId, String distance, TargetFace targetFace, boolean archived, Pageable page);

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
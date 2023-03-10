package nz.roag.archerylogbook.db.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "ARCHERY_DISTANCE_SETTINGS")
public class DistanceSettings {

    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Getter @Setter
    @Column(name = "bow_id", nullable = false)
    private long bowId;

    @Getter @Setter
    @Column(nullable = false)
    private int distance;
    @Getter @Setter
    @Column(nullable = false)
    private int sight;

    @Getter @Setter
    @Column(nullable = false)
    private boolean isTested = false;

    @Override
    public String toString() {
        return String.format("{ distance: %s, sight: %d }", distance, sight);
    }
}

package nz.roag.archerylogbook.db.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "ARCHERY_DISTANCE_SETTINGS")
public class DistanceSettings implements Comparable<DistanceSettings> {

    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Getter @Setter
    @Column(nullable = false)
    private int distance;

    @Getter @Setter
    @Column(nullable = false)
    private String sight;

    @Getter @Setter
    @Column(nullable = false)
    private Boolean isTested = false;

    @Override
    public String toString() {
        return String.format("{ distance: %s, sight: %s }", distance, sight);
    }

    @Override
    public int compareTo(DistanceSettings ds) {
        return Integer.compare(this.getDistance(), ds.getDistance());
    }
}

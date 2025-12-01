package nz.roag.archerylogbook.db.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "ARCHERY_ROUND")
public class Round {

    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Getter @Setter
    @Column(name = "archer_id", nullable = false)
    private long archerId;

    @Getter @Setter
    @Column(name = "bow_id", nullable = false)
    private long bowId;

    @Getter
    @OneToOne(targetEntity = Bow.class)
    @JoinColumn(name = "bow_id", referencedColumnName = "id", insertable = false, updatable = false)
    private Bow bow;

    @Getter @Setter
    @Column(unique = true, nullable = false)
    private Date roundDate = new Date();

    @Getter @Setter @NonNull
    @Column(nullable = false)
    private String distance;

    @Getter @Setter @NonNull
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TargetFace targetFace;

    @Getter @Setter
    @Column
    private String comment;

    @Getter @Setter
    @Column
    private String country;

    @Getter @Setter
    @Column
    private String city;

    @Getter @Setter
    @JsonIgnore
    private Boolean archived = false;

    @Getter @Setter
    @OneToMany(targetEntity = End.class, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "round_id", nullable = false)
    private List<End> ends = new ArrayList<>();

    public int getEndsCount() {
        return ends.size();
    }

    public int getShotsCount() {
        return ends.stream().reduce(0, (count, end) -> count + end.getShotsCount(), Integer::sum);
    }

    public int getSum() {
        return ends.stream().reduce(0, (sum, end) -> sum + end.getSum(), Integer::sum);
    }

    public String getAvg() {
        var sum = ends.stream().reduce((double)0, (avgSum, end) -> avgSum + Double.parseDouble(end.getAvg()), Double::sum);
        return String.format("%.2f", sum / getEndsCount());
    }

    @Override
    public String toString() {
        return String.format("{ id: %d, roundDate: %s, distance: %s, targetFace: %s, shots: %d, score: %d, avg: %s }", getId(), getRoundDate(), getDistance(), getTargetFace(), getShotsCount(), getSum(), getAvg());
    }
}

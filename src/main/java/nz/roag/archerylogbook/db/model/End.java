package nz.roag.archerylogbook.db.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "ARCHERY_END")
public class End {

    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Getter @Setter
    @Column(nullable = false)
    private short endNumber;

    @Getter @Setter
    @OneToMany(targetEntity = Shot.class, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "end_id", nullable = false)
    private List<Shot> shots = new ArrayList<>();

    public int getShotsCount() {
        return shots.size();
    }

    public int getSum() {
        return shots.stream().reduce(0, (sum, shot) -> sum + shot.getShotScore(), Integer::sum);
    }

    public String getAvg() {
        return String.format("%.2f", (double)getSum()/ getShotsCount());
    }
}

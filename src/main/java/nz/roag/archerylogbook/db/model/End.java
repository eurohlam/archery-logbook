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
    @Column(name = "score_id", nullable = false)
    private long scoreId;

    @Getter @Setter
    @Column(nullable = false)
    private short endNumber;

    @Getter @Setter
    @OneToMany(targetEntity = Round.class, cascade = CascadeType.ALL)
    @JoinColumn(name = "end_id")
    private List<Round> rounds = new ArrayList<>();

    public int getRoundsCount() {
        return rounds.size();
    }

    public int getSum() {
        return rounds.stream().reduce(0, (sum, round) -> sum + round.getRoundScore(), Integer::sum);
    }

    public String getAvg() {
        return String.format("%.2f", (double)getSum()/ getRoundsCount());
    }
}

package nz.roag.archerylogbook.db.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "ARCHERY_SCORE")
public class Score {

    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Getter @Setter
    @Column(name = "archer_id", nullable = false)
    private long archerId;

    @Getter @Setter
    @Column(name = "bow_id")
    private Long bowId;

    @Getter
    @OneToOne(targetEntity = Bow.class)
    @JoinColumn(name = "bow_id", referencedColumnName = "id", insertable = false, updatable = false)
    private Bow bow;

    @Getter @Setter
    @Column(unique = true)
    private Date scoreDate = new Date();

    @Getter @Setter @NonNull
    @Column(name= "`match`", nullable = false)
    private String match;

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
    @OneToMany(targetEntity = End.class, cascade = CascadeType.ALL)
    @JoinColumn(name = "score_id")
    private List<End> ends = new ArrayList<>();

    public int getEndsCount() {
        return ends.size();
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
        return String.format("{ id: %d, scoreDate: %s, match: %s, ends: %d, sum: %d, avg: %s }", getId(), getScoreDate(), getMatch(), getEndsCount(), getSum(), getAvg());
    }
}

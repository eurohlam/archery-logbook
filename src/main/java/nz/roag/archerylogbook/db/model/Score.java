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
    private long bowId;

    @Getter @Setter
    @Column(unique = true)
    private Date scoreDate = new Date();

    @Getter @Setter @NonNull
    @Column(nullable = false)
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
        return String.format("%.2f", (double)getSum()/ getEndsCount());
    }

    @Override
    public String toString() {
        return String.format("{ id: %d, scoreDate: %s, ends: %d, sum: %d, avg: %f }", getId(), getScoreDate(), getEndsCount(), getSum(), getAvg());
    }
}

package nz.roag.archerylogbook.db.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonValue;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "ARCHERY_COMPETITION")
public class Competition {

    public enum CompetitionType {
        WA1440("WA1440"),
        WA720("WA720"),
        CANADIAN_1200("Canadian 1200"),
        SHORT_CANADIAN_1200("Short Canadian 1200"),
        CANADIAN_900("Canadian 900"),
        SHORT_CANADIAN_900("Short Canadian 900"),
        BURTON("Burton"),
        SHORT_BURTON("Short Burton"),
        SILVER_FERN("Silver Fern");

        private final String type;

        CompetitionType(final String competitionType) {
            this.type = competitionType;
        }

        @Override
        @JsonValue
        public String toString() {
            return this.type;
        }
    }

    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Getter @Setter
    @Column(name = "archer_id", nullable = false)
    private long archerId;

    @Getter @Setter
    @Column(nullable = false)
    private LocalDate competitionDate = LocalDate.now();

    @Getter @Setter
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private CompetitionType competitionType;

    @Getter @Setter
    @Column
    private String ageClass;

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
    @OneToMany(targetEntity = Round.class, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "competition_id")
    private List<Round> rounds = new ArrayList<>();

    public int getRoundsCount() {
        return rounds.size();
    }

    public int getShotsCount() {
        return rounds.stream().reduce(0, (count, round) -> count + round.getShotsCount(), Integer::sum);
    }

    public int getSum() {
        return rounds.stream().reduce(0, (sum, round) -> sum + round.getSum(), Integer::sum);
    }

    public String getAvg() {
        var sum = rounds.stream().reduce((double)0, (avgSum, round) -> avgSum + Double.parseDouble(round.getAvg()), Double::sum);
        return String.format("%.2f", sum / getRoundsCount());
    }

    public String getRoundsSummary() {
        var s = new StringBuilder();
        for (int i =0 ; i < rounds.size(); i++) {
            var round = rounds.get(i);
            s.append("Round #" + (i + 1) + ": distance: " + round.getDistance() + "m; sum: " + round.getSum() + "\n");
        }
        return s.toString();
    }

    @Override
    public String toString() {
        return String.format("{ id: %d, competitionType: %s, competitionDate: %s, shots: %d, sum: %d, avg: %s }", getId(), getCompetitionType(), getCompetitionDate(), getShotsCount(), getSum(), getAvg());
    }
}

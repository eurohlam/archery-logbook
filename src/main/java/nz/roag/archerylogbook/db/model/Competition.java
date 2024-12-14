package nz.roag.archerylogbook.db.model;

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
        CANADIAN_1200("Canadian 1200"),
        SHORT_CANADIAN_1200("Short Canadian 1200"),
        CANADIAN_900("Canadian 900"),
        SHORT_CANADIAN_900("Short Canadian 900"),
        BURTON("Burton"),
        SHORT_BURTON("Short Burton"),
        SILVER_FERN("Silver Fern");

        private final String competitionType;

        CompetitionType(final String competitionType) {
            this.competitionType = competitionType;
        }

        @Override
        @JsonValue
        public String toString() {
            return this.competitionType;
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
    private String comment;

    @Getter @Setter
    @Column
    private String country;

    @Getter @Setter
    @Column
    private String city;

    @Getter @Setter
    @OneToMany(targetEntity = Round.class, cascade = CascadeType.ALL)
    @JoinColumn(name = "competition_id")
    private List<Round> rounds = new ArrayList<>();
}

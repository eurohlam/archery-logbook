package nz.roag.archerylogbook.db.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "ARCHERY_ROUND")
public class Round {

    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Getter @Setter
    @Column(name = "end_id", nullable = false)
    private long endId;

    @Getter @Setter
    @Column(nullable = false)
    private short roundNumber;

    @Getter @Setter
    @Column(nullable = false)
    private short roundScore;
}

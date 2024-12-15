package nz.roag.archerylogbook.db.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "ARCHERY_SHOT")
public class Shot {

    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Getter @Setter
    @Column(nullable = false)
    private short shotNumber;

    @Getter @Setter
    @Column(nullable = false)
    private short shotScore;
}

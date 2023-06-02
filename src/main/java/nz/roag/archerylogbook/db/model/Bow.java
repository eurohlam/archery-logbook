package nz.roag.archerylogbook.db.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.SortedSet;
import java.util.TreeSet;

@Entity
@Table(name = "ARCHERY_BOW")
public class Bow {

    public enum Type {COMPOUND, RECURVE, TRADITIONAL};
    public enum Level {BEGINNER, INTERMEDIATE, ADVANCED}

    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Getter @Setter
    @Column(name = "archer_id", nullable = false)
    private long archerId;

    @Getter @Setter
    @Column(nullable = false)
    private String name;

    @Getter @Setter
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Type type;

    @Getter @Setter
    @Enumerated(EnumType.STRING)
    private Level level = Level.INTERMEDIATE;

    @Getter @Setter
    private String poundage;

    @Getter @Setter
    private String compoundModel;

    @Getter @Setter
    private String riserModel;

    @Getter @Setter
    private String limbsModel;

    @Getter @Setter
    private String traditionalModel;

    @Getter @Setter
    @OneToMany(targetEntity = DistanceSettings.class, cascade = CascadeType.ALL)
    @JoinColumn(name = "bow_id")
    private SortedSet<DistanceSettings> distanceSettingsList = new TreeSet<>();

    @Override
    public String toString() {
        return String.format("{ id: %d, name: %s, type: %s, poundage: %s }", id, name, type, poundage);
    }

}

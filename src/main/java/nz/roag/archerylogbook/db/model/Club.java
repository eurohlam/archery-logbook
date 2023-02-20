package nz.roag.archerylogbook.db.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "ARCHERY_CLUB")
public class Club {

    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Getter @Setter
    @Column(nullable = false, unique = true)
    private String name;

    @Getter @Setter
    @OneToMany(targetEntity = Archer.class, cascade = CascadeType.DETACH)
    @JoinColumn(name = "club_id")
    private Set<Archer> archers = new HashSet<>();

    @Override
    public String toString() {
        return String.format("{ id: %d, name: %s }", id, name);
    }
}
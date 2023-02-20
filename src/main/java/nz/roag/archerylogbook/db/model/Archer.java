package nz.roag.archerylogbook.db.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "ARCHERY_ARCHER")
public class Archer {

    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Getter @Setter
    @Column(nullable = false, unique = true)
    private String username;

    @Getter @Setter
    @Column(nullable = false)
    private String password;

    @Getter @Setter
    private String firstName;

    @Getter @Setter
    private String lastName;

    @Getter @Setter
    @Column(name = "club_id")
    private long clubId;

    @Getter @Setter
    @OneToMany(targetEntity = Bow.class, cascade = CascadeType.ALL)
    @JoinColumn(name = "archer_id")
    private List<Bow> bowList = new ArrayList<>();

    @Getter @Setter
    @OneToMany(targetEntity = Score.class, cascade = CascadeType.ALL)
    @JoinColumn(name = "archer_id")
    private List<Score> scoreList = new ArrayList<>();

    @Override
    public String toString() {
        return String.format("{ id: %d, username: %s, firstName: %s, lastName: %s }", id, username, firstName, lastName);
    }
}

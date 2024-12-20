package nz.roag.archerylogbook.db.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "ARCHERY_ARCHER")
public class Archer {

    @Getter @Setter
    @Id
    //@GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Getter @Setter
    @Column(nullable = false)
    private String firstName;

    @Getter @Setter
    private String lastName;

    @Getter @Setter
    @Column(nullable = false, unique = true)
    private String email;

    @Getter @Setter
    @Column(name = "club_id")
    @JsonIgnore
    private Long clubId;

    @Getter
    @OneToOne(targetEntity = Club.class)
    @JoinColumn(name = "club_id", referencedColumnName = "id", insertable = false, updatable = false)
    private Club club;

    @Getter @Setter
    @JsonIgnore
    private Boolean archived = false;

    @Getter @Setter
    @OneToMany(targetEntity = Bow.class, cascade = CascadeType.ALL)
    @JoinColumn(name = "archer_id")
    private List<Bow> bowList = new ArrayList<>();

    @Getter @Setter
    @OneToMany(targetEntity = Round.class, cascade = CascadeType.ALL)
    @JoinColumn(name = "archer_id")
    private List<Round> roundList = new ArrayList<>();

    @Getter @Setter
    @OneToMany(targetEntity = Competition.class, cascade = CascadeType.ALL)
    @JoinColumn(name = "archer_id")
    private List<Competition> competitionList = new ArrayList<>();

    @Override
    public String toString() {
        return String.format("{ id: %d, firstName: %s, lastName: %s, email: %s }", id, firstName, lastName, email);
    }

    /*
    We want to use some transient fields that come from registration form
    to create a new club during the registration process
    */
    @Getter @Setter
    @Transient
    private String clubName;

    @Getter @Setter
    @Transient
    private String country;

    @Getter @Setter
    @Transient
    private String city;

}

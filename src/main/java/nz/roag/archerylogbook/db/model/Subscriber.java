package nz.roag.archerylogbook.db.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "ARCHERY_API_SUBSCRIBER")
public class Subscriber {

    @Id
    @Getter @Setter
    private String accessKey;

    @Getter @Setter
    @Column(nullable = false, unique = true)
    private String secretKey;
}

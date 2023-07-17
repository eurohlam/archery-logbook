package nz.roag.archerylogbook.db;

import nz.roag.archerylogbook.db.model.Subscriber;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubscriberRepository extends JpaRepository<Subscriber, String> {
}
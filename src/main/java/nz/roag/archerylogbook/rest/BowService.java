package nz.roag.archerylogbook.rest;

import jakarta.transaction.Transactional;
import nz.roag.archerylogbook.db.ArcherRepository;
import nz.roag.archerylogbook.db.BowRepository;
import nz.roag.archerylogbook.db.model.Bow;
import nz.roag.archerylogbook.db.model.DistanceSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class BowService {

    private final Logger logger = LoggerFactory.getLogger(BowService.class);

    @Autowired
    private BowRepository bowRepository;

    @Autowired
    private ArcherRepository archerRepository;

    public List<Bow> listAllBows(long archerId) throws NoSuchElementException {
        logger.debug("Getting bows for archer with id {}", archerId);
        var archer = archerRepository.findById(archerId).orElseThrow(() -> new NoSuchElementException("Archer not found. archerId=" + archerId));
        return bowRepository.findByArcherId(archer.getId(), Sort.by("name").ascending());
    }

    @Transactional
    public void addBow(long archerId, Bow bow) throws NoSuchElementException {
        logger.debug("Adding a new bow {} for archerId {}", bow, archerId);
        var archer = archerRepository.findById(archerId).orElseThrow(() -> new NoSuchElementException("Archer not found. archerId=" + archerId));
        bow.setArcherId(archer.getId());
        bowRepository.save(bow);
    }

    public Bow getBow(long bowId) throws NoSuchElementException {
        logger.debug("Getting bow by id {}", bowId);
        return bowRepository.findById(bowId).orElseThrow(() -> new NoSuchElementException("Bow not found. bowId=" + bowId));
    }

    @Transactional
    public void updateBow(long bowId, Bow bow) {
        logger.debug("Updating bow data {} for bowId={}", bow, bowId);
        Bow storedBow = bowRepository.findById(bowId).orElseThrow(() -> new NoSuchElementException("Bow not found. bowId=" + bowId));
        storedBow.setName(bow.getName());
        storedBow.setType(bow.getType());
        storedBow.setPoundage(bow.getPoundage());
        storedBow.setLevel(bow.getLevel());
        if (bow.getType() == Bow.Type.RECURVE) {
            storedBow.setRiserModel(bow.getRiserModel());
            storedBow.setLimbsModel(bow.getLimbsModel());
        }
        if (bow.getType() == Bow.Type.COMPOUND) {
            storedBow.setCompoundModel(bow.getCompoundModel());
        }
        if (bow.getType() == Bow.Type.TRADITIONAL) {
            storedBow.setTraditionalModel(bow.getTraditionalModel());
        }
        bowRepository.save(storedBow);
    }

    @Transactional
    public void deleteBow(long id) {
        logger.warn("Deleting bow with id {}", id);
        bowRepository.deleteById(id);
    }

    @Transactional
    public void addDistanceSettings(long bowId, DistanceSettings distanceSettings) throws NoSuchElementException {
        logger.debug("Adding distance setting {} for bowId={}", distanceSettings, bowId);
        var bow = getBow(bowId);
        distanceSettings.setBowId(bowId);

        if (bow.getDistanceSettingsList()
                .stream()
                .anyMatch(ds -> ds.getDistance() == distanceSettings.getDistance())) {
            DistanceSettings ds = bow.getDistanceSettingsList()
                    .stream()
                    .filter(el -> el.getDistance() == distanceSettings.getDistance())
                    .findFirst()
                    .orElseThrow(
                            () -> new NoSuchElementException("Distance not found. bowId=" + bowId + "; distance=" + distanceSettings.getDistance()));
            ds.setSight(distanceSettings.getSight());
            ds.setIsTested(distanceSettings.getIsTested());
        } else {
            bow.getDistanceSettingsList().add(distanceSettings);
        }
        bowRepository.save(bow);
    }
}

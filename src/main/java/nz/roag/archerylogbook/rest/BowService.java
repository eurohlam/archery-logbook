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
@Transactional
public class BowService {

    private final Logger logger = LoggerFactory.getLogger(BowService.class);

    @Autowired
    private BowRepository bowRepository;

    @Autowired
    private ArcherRepository archerRepository;

    public List<Bow> listAllBows(long archerId) throws NoSuchElementException {
        var archer = archerRepository.findById(archerId).orElseThrow(NoSuchElementException::new);
        return bowRepository.findByArcherId(archer.getId(), Sort.by("name").ascending());
        //return archerRepository.findById(archerId).orElseThrow(NoSuchElementException::new).getBowList();
    }

    public void addBow(long archerId, Bow bow) throws NoSuchElementException {
        logger.debug("Adding a new bow {} for archerId {}", bow, archerId);
        var archer = archerRepository.findById(archerId).orElseThrow(NoSuchElementException::new);
        bow.setArcherId(archer.getId());
        bowRepository.save(bow);
    }

    public Bow getBow(long bowId) throws NoSuchElementException {
        return bowRepository.findById(bowId).orElseThrow(NoSuchElementException::new);
    }

    public void deleteBow(long id) {
        bowRepository.deleteById(id);
    }

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
                    .orElseThrow();
            ds.setSight(distanceSettings.getSight());
            ds.setTested(distanceSettings.isTested());
        } else {
            bow.getDistanceSettingsList().add(distanceSettings);
        }
        bowRepository.save(bow);
    }
}
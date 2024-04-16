package dst.ass2.service.trip;

import dst.ass1.jpa.dao.IDAOFactory;
import dst.ass1.jpa.dao.IDriverDAO;
import dst.ass1.jpa.model.IDriver;
import dst.ass2.service.api.match.IMatchingService;
import dst.ass2.service.api.trip.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.ManagedBean;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.List;

@ManagedBean
@Transactional
public class MatchingService implements IMatchingService {

    @Inject
    private ITripService tripService;

    @Inject
    private IDAOFactory daoFactory;

    private IDriverDAO driverDAO;

    private int driverIndex;

    private static final Logger LOG = LoggerFactory.getLogger(MatchingService.class);
    private List<IDriver> all;

    @PostConstruct
    public void setup() {
        driverDAO = daoFactory.createDriverDAO();
    }

    @Override
    public MoneyDTO calculateFare(TripDTO trip) throws InvalidTripException {
        LOG.info("Calculate fare for trip: {}", trip.getId());
        BigDecimal value = Math.random() > 0.5 ? BigDecimal.ONE : BigDecimal.TEN;
        MoneyDTO moneyDTO = new MoneyDTO();
        moneyDTO.setCurrency("EUR");
        moneyDTO.setValue(value);
        return moneyDTO;
    }

    @Override
    public void queueTripForMatching(Long tripId) {
        if (all == null) {
            all = driverDAO.findAll();
            driverIndex = all.size() - 2;
        }
        TripDTO trip = tripService.find(tripId);
        try {
            LOG.info("Queue trip {} for matching", tripId);
            IDriver driver = all.get(driverIndex++);
            if (driverIndex >= all.size()) {
                driverIndex = 0;
            }

            MatchDTO matchDTO = new MatchDTO();
            matchDTO.setDriverId(driver.getId());
            matchDTO.setVehicleId(driver.getVehicle().getId());
            matchDTO.setFare(trip.getFare());
            tripService.match(tripId, matchDTO);
        } catch (EntityNotFoundException | DriverNotAvailableException e) {
            // We ignore the exception in this implementation because the queuing and matching should be in
            // a real implementation be done asynchronously. However for testing purposes we call the service
            // directly (i.e., synchronously).
            //LOG.error("Error during matching", e);
        }
    }
}

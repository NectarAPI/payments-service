package ke.co.nectar.payments.service.credits.impl;

import ke.co.nectar.payments.controllers.credits.ConsumptionDetails;
import ke.co.nectar.payments.entity.Credits;
import ke.co.nectar.payments.entity.CreditsConsumption;
import ke.co.nectar.payments.repository.CreditsConsumptionRepository;
import ke.co.nectar.payments.repository.CreditsRepository;
import ke.co.nectar.payments.service.credits.CreditsService;
import ke.co.nectar.payments.utils.AppUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class CreditsServiceImpl implements CreditsService {

    @Autowired
    private CreditsRepository creditsRepository;

    @Autowired
    private CreditsConsumptionRepository creditsConsumptionRepository;

    @Override
    public Credits getCreditsByRef(String ref) {
        return creditsRepository.getByRef(ref);
    }

    @Override
    public Map<String, Object> getCredits(String userRef, boolean all,
                                          boolean detailed) {
        Map<String, Object> output = new LinkedHashMap<>();

        Double credits = creditsRepository.getUnitsForUserRef(userRef);
        Double consumption = creditsConsumptionRepository.getCreditsConsumptionUnitsForUser(userRef);

        if (!all) {
            credits -= consumption;
        }
        output.put("credits", credits);

        if (detailed) {
            List<Credits> userCredits = creditsRepository.getByUserRef(userRef);
            List<CreditsConsumption> userCreditsConsumption = creditsConsumptionRepository.getByUserRef(userRef);
            output.put("purchase", userCredits);
            output.put("consumption", userCreditsConsumption);
        }
        return output;
    }

    @Override
    public void save(Credits credits) {
        creditsRepository.save(credits);
    }

    @Override
    public List<TimelineRequest> getCreditsTimelineRequests(String userRef, int months)
            throws Exception {
        return creditsRepository.getTimelineRequests(userRef, months);
    }

    @Override
    public List<TimelineRequest> getConsumptionTimelineRequests(String userRef, int months)
            throws Exception {
        return creditsConsumptionRepository.getTimelineRequests(userRef, months);
    }

    @Override
    public String postConsumption(ConsumptionDetails consumptionDetails,
                                  String userRef)
        throws Exception {
        CreditsConsumption consumption = new CreditsConsumption();
        String ref = AppUtils.generateRef();
        consumption.setRef(ref);
        consumption.setConsumptionDate(consumptionDetails.getConsumptionDate());
        consumption.setUserRef(userRef);
        consumption.setUnits(consumptionDetails.getUnits());
        consumption.setTokenRef(consumptionDetails.getTokenRef());
        creditsConsumptionRepository.save(consumption);
        return ref;
    }
}

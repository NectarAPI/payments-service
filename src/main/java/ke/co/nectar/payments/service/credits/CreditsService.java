package ke.co.nectar.payments.service.credits;

import ke.co.nectar.payments.controllers.credits.ConsumptionDetails;
import ke.co.nectar.payments.entity.Credits;
import ke.co.nectar.payments.service.credits.impl.TimelineRequest;

import java.util.List;
import java.util.Map;

public interface CreditsService {

    Map<String, Object> getCredits(String userRef, boolean all,
                                   boolean detailed);

    Credits getCreditsByRef(String ref);

    List<TimelineRequest> getCreditsTimelineRequests(String userRef, int months) throws Exception;

    List<TimelineRequest> getConsumptionTimelineRequests(String userRef, int months) throws Exception;

    void save(Credits credits);

    String postConsumption(ConsumptionDetails consumptionDetails,
                           String userRef) throws Exception;
}

package ke.co.nectar.payments.service;

import ke.co.nectar.payments.NectarPaymentsServiceApplication;
import ke.co.nectar.payments.annotation.NotificationProcessor;
import ke.co.nectar.payments.annotation.aspect.NotifyAspect;
import ke.co.nectar.payments.controllers.credits.ConsumptionDetails;
import ke.co.nectar.payments.entity.Credits;
import ke.co.nectar.payments.entity.CreditsConsumption;
import ke.co.nectar.payments.entity.Payment;
import ke.co.nectar.payments.repository.CreditsConsumptionRepository;
import ke.co.nectar.payments.repository.CreditsRepository;
import ke.co.nectar.payments.repository.PaymentsRepository;
import ke.co.nectar.payments.service.credits.CreditsService;
import ke.co.nectar.payments.service.credits.impl.TimelineRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = NectarPaymentsServiceApplication.class)
@AutoConfigureMockMvc
public class CreditsServiceTest {

    @Autowired
    private CreditsService creditsService;

    @Autowired
    private CreditsRepository creditsRepository;

    @Autowired
    private PaymentsRepository paymentsRepository;

    @Autowired
    private CreditsConsumptionRepository creditsConsumptionRepository;

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private ProceedingJoinPoint proceedingJoinPoint;

    @MockBean
    private NotificationProcessor notificationProcessor;

    private NotifyAspect notifyAspect =  new NotifyAspect();

    private final long EPOCH_TIME = 1606754076302L;

    @Before
    public void setup() {
        Payment payment = new Payment("3b867c14-00de-453e-8cd5-c3ca03f30fed", Instant.ofEpochMilli(EPOCH_TIME),
                                    Instant.ofEpochMilli(EPOCH_TIME), "0", "SUCCESSFUL", "MPESA", 5d,
                            "5063d4bd-c5a6-4fde-8328-4bd536e83867");

        paymentsRepository.save(payment);

        Credits credits = new Credits("a4b36fc2-6ee8-4cf0-b5c9-ff2bb6a8ce1b",
                                    Instant.ofEpochMilli(EPOCH_TIME), "5063d4bd-c5a6-4fde-8328-4bd536e83867", 5d, "KES",
                                5d, payment);

        creditsRepository.save(credits);

        CreditsConsumption creditsConsumption
                = new CreditsConsumption("bcddc350-d149-461d-a458-39015d6838dd", Instant.ofEpochMilli(EPOCH_TIME),
                            "5063d4bd-c5a6-4fde-8328-4bd536e83867", 1d,
                            "aa697bbf-72b6-4f49-b577-1bce68f7a533");

        creditsConsumptionRepository.save(creditsConsumption);
    }

    @After
    public void cleanUp() {
        creditsRepository.deleteAll();
        paymentsRepository.deleteAll();
        creditsConsumptionRepository.deleteAll();
    }
    
    @Test
    public void testThatCreditsByRefAreReturned() throws Exception {
        Credits credits =
                creditsService.getCreditsByRef("a4b36fc2-6ee8-4cf0-b5c9-ff2bb6a8ce1b");

        Assert.assertEquals("a4b36fc2-6ee8-4cf0-b5c9-ff2bb6a8ce1b", credits.getRef());
        Assert.assertEquals(Instant.ofEpochMilli(EPOCH_TIME), credits.getPurchaseDate());
        Assert.assertEquals("5063d4bd-c5a6-4fde-8328-4bd536e83867", credits.getUserRef());
        Assert.assertEquals((Double) 5.0, credits.getValue());
        Assert.assertEquals("KES", credits.getCurrency());
        Assert.assertEquals((Double) 5.0, credits.getUnits());
    }

    @Test
    public void testThatCreditsAreReturned() throws Exception {
        Map<String, Object> credits =
                creditsService.getCredits("5063d4bd-c5a6-4fde-8328-4bd536e83867",
                        true, false);

        Assert.assertEquals(5.0, credits.get("credits"));
        Assert.assertNull(credits.get("purchase"));
        Assert.assertNull(credits.get("consumption"));
    }

    @Test
    public void testThatCreditRemainingDetailsAreReturned() throws Exception {
        Map<String, Object> credits =
                creditsService.getCredits("5063d4bd-c5a6-4fde-8328-4bd536e83867",
                        false, false);
        Assert.assertEquals(4.0, credits.get("credits"));
        Assert.assertNull(credits.get("purchase"));
        Assert.assertNull(credits.get("consumption"));
    }

    @Test
    public void testThatCreditDetailsDetailsAreReturned() throws Exception {
        Map<String, Object> credits =
                creditsService.getCredits("5063d4bd-c5a6-4fde-8328-4bd536e83867",
                        false, true);

        List<Credits> userCredits = (List<Credits>) credits.get("purchase");
        List<CreditsConsumption> userCreditsConsumption = (List<CreditsConsumption>) credits.get("consumption");

        Assert.assertEquals(4.0, credits.get("credits"));
        Assert.assertEquals(1, userCredits.size());
        Assert.assertEquals("a4b36fc2-6ee8-4cf0-b5c9-ff2bb6a8ce1b", userCredits.get(0).getRef());
        Assert.assertEquals("5063d4bd-c5a6-4fde-8328-4bd536e83867", userCredits.get(0).getUserRef());
        Assert.assertEquals((Double) 5d, userCredits.get(0).getUnits());
        Assert.assertEquals((Double) 5d, userCredits.get(0).getValue());
        Assert.assertEquals("KES", userCredits.get(0).getCurrency());
        
        Assert.assertEquals("3b867c14-00de-453e-8cd5-c3ca03f30fed", userCredits.get(0).getPayment().getRef());
        Assert.assertEquals("0", userCredits.get(0).getPayment().getResultCode());
        Assert.assertEquals("SUCCESSFUL", userCredits.get(0).getPayment().getResultDesc());
        Assert.assertEquals("MPESA", userCredits.get(0).getPayment().getType());
        Assert.assertEquals((Double) 5d, userCredits.get(0).getPayment().getValue());
        Assert.assertEquals("5063d4bd-c5a6-4fde-8328-4bd536e83867", userCredits.get(0).getPayment().getUserRef());

        Assert.assertEquals(1, userCreditsConsumption.size());
        Assert.assertEquals("bcddc350-d149-461d-a458-39015d6838dd", userCreditsConsumption.get(0).getRef());
        Assert.assertEquals("5063d4bd-c5a6-4fde-8328-4bd536e83867", userCreditsConsumption.get(0).getUserRef());
        Assert.assertEquals((Double) 1d, userCreditsConsumption.get(0).getUnits());
        Assert.assertEquals("aa697bbf-72b6-4f49-b577-1bce68f7a533", userCreditsConsumption.get(0).getTokenRef());

    }

    @Test
    public void testThatCreditTimelineRequestsAreReturned() throws Exception {
        List<TimelineRequest> timelineRequests
                = creditsService.getCreditsTimelineRequests("5063d4bd-c5a6-4fde-8328-4bd536e83867",
                                                            1);

        Assert.assertEquals(1, timelineRequests.size());
        Assert.assertEquals(5, timelineRequests.get(0).getUnits());
        Assert.assertEquals(2020, timelineRequests.get(0).getYear());
        Assert.assertEquals(11, timelineRequests.get(0).getMonth());
    }

    @Test
    public void testThatCreditsConsumptionRequestsAreReturned() throws Exception {
        List<TimelineRequest> timelineRequests
                = creditsService.getConsumptionTimelineRequests("5063d4bd-c5a6-4fde-8328-4bd536e83867",
                                                                        1);

        Assert.assertEquals(1, timelineRequests.size());
        Assert.assertEquals(1, timelineRequests.get(0).getUnits());
        Assert.assertEquals(2020, timelineRequests.get(0).getYear());
        Assert.assertEquals(11, timelineRequests.get(0).getMonth());
    }

    @Test
    public void testThatConsumptionIsPosted() throws Exception {
        ConsumptionDetails consumptionDetails = new ConsumptionDetails(Instant.ofEpochMilli(EPOCH_TIME),
                                                        1d, "token-ref");
        final String USER_REF = "5063d4bd-c5a6-4fde-8328-4bd536e83867";
        String consumptionRef = creditsService.postConsumption(consumptionDetails, USER_REF);
        Double consumptionCredits = creditsConsumptionRepository
                                        .getCreditsConsumptionUnitsForUser(
                                                "5063d4bd-c5a6-4fde-8328-4bd536e83867");

        Assert.assertNotNull(consumptionRef);
        Assert.assertEquals((Double) 2.0, consumptionCredits);
    }

}

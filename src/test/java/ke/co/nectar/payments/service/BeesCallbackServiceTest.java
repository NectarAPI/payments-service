package ke.co.nectar.payments.service;

import ke.co.nectar.payments.NectarPaymentsServiceApplication;
import ke.co.nectar.payments.entity.Payment;
import ke.co.nectar.payments.entity.UnitCurrencyValue;
import ke.co.nectar.payments.repository.CreditsRepository;
import ke.co.nectar.payments.repository.PaymentsRepository;
import ke.co.nectar.payments.repository.UnitCurrencyValueRepository;
import ke.co.nectar.payments.service.bees.BeesCallbackService;
import ke.co.nectar.payments.service.credits.CreditsService;
import ke.co.nectar.payments.service.payment.PaymentsService;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.Instant;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = NectarPaymentsServiceApplication.class)
@AutoConfigureMockMvc
public class BeesCallbackServiceTest {

    @Autowired
    private BeesCallbackService beesCallbackService;

    @Autowired
    private PaymentsRepository paymentsRepository;

    @Autowired
    private PaymentsService paymentsService;

    @Autowired
    private UnitCurrencyValueRepository unitCurrencyValueRepository;

    @Autowired
    private CreditsService creditsService;

    @Autowired
    private CreditsRepository creditsRepository;

    @After
    public void cleanup() {
        unitCurrencyValueRepository.deleteAll();
        creditsRepository.deleteAll();
        paymentsRepository.deleteAll();
    }

    @Test
    public void testThatBeesCallbackPaymentMessageIsProcessed() throws Exception {
        Payment payment = new Payment("3b867c14-00de-453e-8cd5-c3ca03f30fed", Instant.now(),
                null, "", "",
                "MPESA", 5d, "5063d4bd-c5a6-4fde-8328-4bd536e83867");
        paymentsRepository.save(payment);

        UnitCurrencyValue unitCurrencyValue = new UnitCurrencyValue("KES", 2.0);
        unitCurrencyValueRepository.save(unitCurrencyValue);

        String paymentCallbackResponse = "{\"result_code\":0," +
                "\"result_desc\":\"The service request is processed successfully.\"," +
                "\"ref\":\"3b867c14-00de-453e-8cd5-c3ca03f30fed\"}";

        beesCallbackService.processPaymentResult(paymentCallbackResponse);

        Payment obtainedPayment = paymentsService.findByRef("3b867c14-00de-453e-8cd5-c3ca03f30fed");

        Assert.assertNotNull(payment);
        Assert.assertEquals("3b867c14-00de-453e-8cd5-c3ca03f30fed", obtainedPayment.getRef());
        Assert.assertEquals("0", obtainedPayment.getResultCode());
        Assert.assertEquals("The service request is processed successfully.", obtainedPayment.getResultDesc());
        Assert.assertEquals("MPESA", obtainedPayment.getType());
        Assert.assertEquals((Double) 5d, obtainedPayment.getValue());
        Assert.assertEquals("5063d4bd-c5a6-4fde-8328-4bd536e83867", obtainedPayment.getUserRef());

        Map<String, Object> credits = creditsService
                .getCredits("5063d4bd-c5a6-4fde-8328-4bd536e83867", true, false);

        Assert.assertNotNull(credits);
        Assert.assertEquals(2.5,  credits.get("credits"));

    }


}

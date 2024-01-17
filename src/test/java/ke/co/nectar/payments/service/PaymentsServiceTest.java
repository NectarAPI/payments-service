package ke.co.nectar.payments.service;

import ke.co.nectar.payments.NectarPaymentsServiceApplication;
import ke.co.nectar.payments.controllers.payment.PaymentRequest;
import ke.co.nectar.payments.entity.Payment;
import ke.co.nectar.payments.repository.PaymentsRepository;
import ke.co.nectar.payments.service.payment.PaymentsService;
import ke.co.nectar.payments.service.payment.impl.PaymentsResultsDistributionCount;
import ke.co.nectar.payments.service.payment.manager.MpesaPaymentManager;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = NectarPaymentsServiceApplication.class)
@AutoConfigureMockMvc
public class PaymentsServiceTest {

    @Autowired
    private PaymentsService paymentsService;

    @MockBean
    private MpesaPaymentManager manager;

    @Autowired
    private PaymentsRepository paymentsRepository;

    @Autowired
    private MockMvc mockMvc;

    @Value("${bees.callback-url}")
    private String callbackURL;

    @Before
    public void setup() throws Exception {
        Payment payment = new Payment("3b867c14-00de-453e-8cd5-c3ca03f30fed", Instant.now(),
                Instant.now(), "0", "SUCCESSFUL", "MPESA", 5d,
                "5063d4bd-c5a6-4fde-8328-4bd536e83867");

        paymentsService.savePayment(payment);
    }

    @After
    public void cleanup() {
        paymentsRepository.deleteAll();
    }

    @Test
    public void testThatPaymentIsSaved() throws Exception {
        Payment payment = paymentsService.findByRef("3b867c14-00de-453e-8cd5-c3ca03f30fed");

        Assert.assertNotNull(payment);
        Assert.assertEquals("3b867c14-00de-453e-8cd5-c3ca03f30fed", payment.getRef());
        Assert.assertEquals("0", payment.getResultCode());
        Assert.assertEquals("SUCCESSFUL", payment.getResultDesc());
        Assert.assertEquals("MPESA", payment.getType());
        Assert.assertEquals((Double) 5d, payment.getValue());
        Assert.assertEquals("5063d4bd-c5a6-4fde-8328-4bd536e83867", payment.getUserRef());
    }

    @Test
    public void testThatPaymentsResultsDistributionIsReturned() throws Exception {
        List<PaymentsResultsDistributionCount> results =
                paymentsService.getPaymentsRequestsDistributionCount("5063d4bd-c5a6-4fde-8328-4bd536e83867");

        Assert.assertEquals(1, results.size());
        Assert.assertEquals("SUCCESSFUL", results.get(0).getResult());
        Assert.assertEquals(5.0, results.get(0).getValue(), 0);
    }

    @Test
    public void testThatPaymentTotalIsObtained() throws Exception {
        Double paymentsTotal = paymentsService.getPaymentsTotalByUserRef("5063d4bd-c5a6-4fde-8328-4bd536e83867");

        Assert.assertEquals((Double) 5.0, paymentsTotal);
    }

    @Test
    public void testThatPaymentIsScheduled() throws Exception {

        when(manager.schedule(any(), any())).thenReturn("transaction_ref");

        Map<Object, Object> data = new HashMap<>();
        data.put("phone_no", "phone_no");

        PaymentRequest request
                = new PaymentRequest("MPESA", 10d, Optional.of(data));
        String transactionRef = paymentsService.schedulePayment("request_id",
                                            "user_ref",
                                                    request);

        Payment payment = paymentsService.findByRef(transactionRef);

        Assert.assertNotNull(payment);
        Assert.assertEquals("transaction_ref", payment.getRef());
        Assert.assertEquals("MPESA", payment.getType());
        Assert.assertEquals((Double) 10d, payment.getValue());
        Assert.assertEquals("user_ref", payment.getUserRef());
    }
}

package ke.co.nectar.payments.controllers;

import ke.co.nectar.payments.NectarPaymentsServiceApplication;
import ke.co.nectar.payments.annotation.NotificationProcessor;
import ke.co.nectar.payments.controllers.payment.PaymentsController;
import ke.co.nectar.payments.entity.Payment;
import ke.co.nectar.payments.service.payment.PaymentsService;
import ke.co.nectar.payments.service.payment.impl.PaymentsResultsDistributionCount;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = NectarPaymentsServiceApplication.class)
@AutoConfigureMockMvc
public class PaymentsControllerTest {

    @InjectMocks
    private PaymentsController paymentsController;

    @MockBean
    private PaymentsService paymentsService;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private NotificationProcessor notificationProcessor;

    private final long EPOCH_TIME = 1606754076302l;

    @Test
    public void contextLoads() {
        Assert.assertNotNull(paymentsController);
    }

    @Test
    public void testThatPaymentsAreReturned() throws Exception {

        Payment payment = new Payment("3b867c14-00de-453e-8cd5-c3ca03f30fed", Instant.ofEpochMilli(EPOCH_TIME),
                Instant.ofEpochMilli(EPOCH_TIME), "0", "SUCCESSFUL", "MPESA", 5d,
                "5063d4bd-c5a6-4fde-8328-4bd536e83867");

        when(paymentsService.findByRef(any())).thenReturn(payment);

        mockMvc.perform(MockMvcRequestBuilders
                .get("/v1/payments?ref=ref")
                .param("request_id", "requestid")
                .with(httpBasic("payments_service", "password")))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(content().json("{'status':{'code':200,'message':'Obtained payment','requestId':'requestid'},'data':{'payment':{'createdAt':null,'updatedAt':null,'ref':'3b867c14-00de-453e-8cd5-c3ca03f30fed','scheduled':'2020-11-30T16:34:36.302Z','fulfilled':'2020-11-30T16:34:36.302Z','resultCode':'0','resultDesc':'SUCCESSFUL','type':'MPESA','value':5.0,'userRef':'5063d4bd-c5a6-4fde-8328-4bd536e83867'}}}"));
    }

    @Test
    public void testThatPaymentsTotalsAreReturned() throws Exception {
        when(paymentsService.getPaymentsTotalByUserRef(anyString())).thenReturn(2d);

        mockMvc.perform(MockMvcRequestBuilders
                .get("/v1/payments?user_ref=ref")
                .param("request_id", "requestid")
                .with(httpBasic("payments_service", "password")))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(content().json("{'status':{'code':200,'message':'Obtained payments total','requestId':'requestid'},'data':{'payments':2.0}}"));
    }

    @Test
    public void testThatMpesaPaymentIsScheduled() throws Throwable {

        String paymentRequestStr = "{\"type\":\"MPESA\"," +
                                    "\"amount\":5," +
                                    "\"phone_no\":\"+254703133896\"}";

        when(paymentsService.schedulePayment(anyString(), anyString(), any())).thenReturn("transaction_ref");
        when(notificationProcessor.process(any())).thenReturn(true);

        mockMvc.perform(MockMvcRequestBuilders
                .post("/v1/payments")
                .param("user_ref", "user-ref")
                .param("request_id", "requestid")
                .contentType(MediaType.APPLICATION_JSON)
                .content(paymentRequestStr)
                .with(httpBasic("payments_service", "password")))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(content().json("{'status':{'code':200,'message':'Payment scheduled','requestId':'requestid'},'data':{'transaction_ref':'transaction_ref'}}"))
                .andReturn();
    }

    @Test
    public void testThatPaymentResultsDistributionIsReturned() throws Exception {

        PaymentsResultsDistributionCount details =
                new PaymentsResultsDistributionCount() {
                    @Override
                    public String getResult() {
                        return "MPESA";
                    }

                    @Override
                    public double getValue() {
                        return 20;
                    }
                };

        List<PaymentsResultsDistributionCount> results = new ArrayList<>();
        results.add(details);

        when(paymentsService.getPaymentsRequestsDistributionCount(anyString())).thenReturn(results);

        mockMvc.perform(MockMvcRequestBuilders
                .get("/v1/payments")
                .param("user_ref", "user-ref")
                .param("request_id", "requestid")
                .param("detailed_param", "payments-results")
                .with(httpBasic("payments_service", "password")))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(content().json("{'status':{'code':200,'message':'Obtained payments total','requestId':'requestid'},'data':{'payments':[{'result':'MPESA','value':20.0}]}}"));
    }
}

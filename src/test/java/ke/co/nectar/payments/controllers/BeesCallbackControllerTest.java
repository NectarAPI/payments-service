package ke.co.nectar.payments.controllers;

import ke.co.nectar.payments.NectarPaymentsServiceApplication;
import ke.co.nectar.payments.service.bees.BeesCallbackService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = NectarPaymentsServiceApplication.class)
@AutoConfigureMockMvc
public class BeesCallbackControllerTest {

    @InjectMocks
    private BeesCallbackController callbackController;

    @MockBean
    private BeesCallbackService callbackService;

    @Autowired
    private MockMvc mockMvc;

    @Value("${bees.callback-url}")
    private String callbackURL;

    @Test
    public void contextLoads() {
        Assert.assertNotNull(callbackController);
    }

    @Test
    public void testThatBeesPaymentCallbackIsProcessed() throws Exception {

        when(callbackService.processPaymentResult(anyString())).thenReturn("transaction_ref");

        mockMvc.perform(MockMvcRequestBuilders
                .post(callbackURL)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}")
                .with(httpBasic("payments_service", "password")))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(content().json("{'status':{'code':200,'message':'Callback received','requestId':'transaction_ref'}}"))
                .andReturn();

    }

}

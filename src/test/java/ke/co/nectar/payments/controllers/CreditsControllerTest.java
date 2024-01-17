package ke.co.nectar.payments.controllers;

import ke.co.nectar.payments.NectarPaymentsServiceApplication;
import ke.co.nectar.payments.annotation.NotificationProcessor;
import ke.co.nectar.payments.controllers.credits.CreditsController;
import ke.co.nectar.payments.entity.Credits;
import ke.co.nectar.payments.entity.CreditsConsumption;
import ke.co.nectar.payments.entity.Payment;
import ke.co.nectar.payments.service.credits.CreditsService;
import ke.co.nectar.payments.service.credits.impl.TimelineRequest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = NectarPaymentsServiceApplication.class)
@AutoConfigureMockMvc
public class CreditsControllerTest {

    @InjectMocks
    private CreditsController creditsController;

    @MockBean
    private CreditsService creditsService;

    @MockBean
    private NotificationProcessor notificationProcessor;

    @Autowired
    private MockMvc mockMvc;

    private final long EPOCH_TIME = 1606754076302L;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void contextLoads() {
        Assert.assertNotNull(creditsController);
    }

    @Test
    public void testThatCreditsFromRefAreObtained() throws Exception {
        Payment payment = new Payment("3b867c14-00de-453e-8cd5-c3ca03f30fed", Instant.ofEpochMilli(EPOCH_TIME),
                                        Instant.ofEpochMilli(EPOCH_TIME), "0", "SUCCESSFUL", "MPESA", 5d,
                                "5063d4bd-c5a6-4fde-8328-4bd536e83867");

        Credits credits = new Credits("a4b36fc2-6ee8-4cf0-b5c9-ff2bb6a8ce1b",
                                        Instant.ofEpochMilli(EPOCH_TIME), "5063d4bd-c5a6-4fde-8328-4bd536e83867", 5d, "KES",
                                    5d, payment);

        when(creditsService.getCreditsByRef(anyString())).thenReturn(credits);

        mockMvc.perform(MockMvcRequestBuilders
                .get("/v1/credits")
                .param("request_id", "requestid")
                .param("ref", "ref")
                .with(httpBasic("payments_service", "password")))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(content().json("{'status':{'code':200,'message':'Obtained credits','requestId':'requestid'},'data':{'credits':{'createdAt':null,'updatedAt':null,'ref':'a4b36fc2-6ee8-4cf0-b5c9-ff2bb6a8ce1b','value':5.0,'currency':'KES','units':5.0,'purchase_date':'2020-11-30T16:34:36.302Z','user_ref':'5063d4bd-c5a6-4fde-8328-4bd536e83867'}}}"));
    }

    @Test
    public void testThatCreditsAreObtained() throws Exception {
        Map<String, Object> credits = new HashMap<>();
        credits.put("credits", 5);

        when(creditsService.getCredits(anyString(),
                        anyBoolean(), anyBoolean())).thenReturn(credits);

        mockMvc.perform(MockMvcRequestBuilders
                .get("/v1/credits")
                .param("request_id", "requestid")
                .param("user_ref", "user_ref")
                .param("all", "false")
                .param("detailed", "false")
                .with(httpBasic("payments_service", "password")))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(content().json("{'status':{'code':200,'message':'Obtained credits','requestId':'requestid'},'data':{'credits':{'credits':5}}}"));
    }

    @Test
    public void testThatDetailedCreditsAreObtained() throws Exception {
        Payment payment = new Payment("3b867c14-00de-453e-8cd5-c3ca03f30fed", Instant.ofEpochMilli(EPOCH_TIME),
                Instant.ofEpochMilli(EPOCH_TIME), "0", "SUCCESSFUL", "MPESA", 5d,
                "5063d4bd-c5a6-4fde-8328-4bd536e83867");

        List<Credits> userCredits = new ArrayList<>();
        userCredits.add(new Credits("a4b36fc2-6ee8-4cf0-b5c9-ff2bb6a8ce1b",
                Instant.ofEpochMilli(EPOCH_TIME), "5063d4bd-c5a6-4fde-8328-4bd536e83867", 5d, "KES",
                5d, payment));

        List<CreditsConsumption> creditsConsumption = new ArrayList<>();
        creditsConsumption.add(new CreditsConsumption("bcddc350-d149-461d-a458-39015d6838dd", Instant.ofEpochMilli(EPOCH_TIME),
                "5063d4bd-c5a6-4fde-8328-4bd536e83867", 1d,
                "aa697bbf-72b6-4f49-b577-1bce68f7a533"));

        Map<String, Object> credits = new HashMap<>();
        credits.put("credits", 5);
        credits.put("purchase", userCredits);
        credits.put("consumption", creditsConsumption);

        when(creditsService.getCredits(anyString(),
                anyBoolean(), anyBoolean())).thenReturn(credits);

        mockMvc.perform(MockMvcRequestBuilders
                .get("/v1/credits")
                .param("request_id", "requestid")
                .param("user_ref", "user_ref")
                .param("all", "false")
                .param("detailed", "false")
                .with(httpBasic("payments_service", "password")))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(content().json("{'status':{'code':200,'message':'Obtained credits','requestId':'requestid'},'data':{'credits':{'credits':5,'purchase':[{'createdAt':null,'updatedAt':null,'ref':'a4b36fc2-6ee8-4cf0-b5c9-ff2bb6a8ce1b','value':5.0,'currency':'KES','units':5.0,'purchase_date':'2020-11-30T16:34:36.302Z','user_ref':'5063d4bd-c5a6-4fde-8328-4bd536e83867'}],'consumption':[{'createdAt':null,'updatedAt':null,'ref':'bcddc350-d149-461d-a458-39015d6838dd','units':1.0,'consumption_date':'2020-11-30T16:34:36.302Z','user_ref':'5063d4bd-c5a6-4fde-8328-4bd536e83867','token_ref':'aa697bbf-72b6-4f49-b577-1bce68f7a533'}]}}}"));
    }

    @Test
    public void testThatTimelineRequestsAreReturned() throws Exception {

        TimelineRequest request = new TimelineRequest() {
            @Override
            public int getMonth() {
                return 10;
            }

            @Override
            public int getYear() {
                return 2021;
            }

            @Override
            public int getUnits() {
                return 5;
            }
        };

        List<TimelineRequest> requests = new ArrayList<>();
        requests.add(request);

        when(creditsService.getCreditsTimelineRequests(anyString(), anyInt())).thenReturn(requests);
        when(creditsService.getConsumptionTimelineRequests(anyString(), anyInt())).thenReturn(requests);

        mockMvc.perform(MockMvcRequestBuilders
                .get("/v1/credits")
                .param("request_id", "requestid")
                .param("user", "user_ref")
                .param("months", "6")
                .with(httpBasic("payments_service", "password")))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(content().json("{'status':{'code':200,'message':'Obtained credits','requestId':'requestid'},'data':{'credits':[{'year':2021,'units':5,'month':10}],'consumption':[{'year':2021,'units':5,'month':10}]}}"));
    }

    @Test
    public void testThatCreditsConsumedAreRecorded() throws Throwable {
        String creditsConsumptionRequestStr = "{\"consumption_date\":\"123456789012\"," +
                                                "\"units\":0," +
                                                "\"token_ref\":\"token_ref\"}";

        when(creditsService.postConsumption(any(), anyString())).thenReturn("consumption_ref");
        when(notificationProcessor.process(any())).thenReturn(true);

        mockMvc.perform(MockMvcRequestBuilders
                .post("/v1/consumption")
                .param("request_id", "requestid")
                .param("user_ref", "userref")
                .contentType(MediaType.APPLICATION_JSON)
                .content(creditsConsumptionRequestStr)
                .with(httpBasic("payments_service", "password")))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(content().json("{'status':{'code':200,'message':'Consumption recorded','requestId':'requestid'},'data':{'consumption':'consumption_ref'}}"))
                .andReturn();
    }
}

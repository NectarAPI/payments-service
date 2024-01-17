package ke.co.nectar.payments.annotation;

import ke.co.nectar.payments.NectarPaymentsServiceApplication;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = NectarPaymentsServiceApplication.class)
@AutoConfigureMockMvc
public class NotificationProcessorTest {

    @Autowired
    private NotificationUtils notificationUtils;

    @Test
    public void testTemplating() throws Exception {
        Map<String, Object> params = new HashMap<>();
        params.put("firstname", "Reagan");
        params.put("second_name", "Mbitiru");

        String test = "Welcome {firstname} {second_name}.";
        test = notificationUtils.template(test, params);

        Assert.assertEquals("Welcome Reagan Mbitiru.", test);



    }

}

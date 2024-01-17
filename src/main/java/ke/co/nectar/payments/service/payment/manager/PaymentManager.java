package ke.co.nectar.payments.service.payment.manager;

import java.util.Map;
import java.util.Optional;

public interface PaymentManager {

    String schedule(Double amount,
                    Optional<Map<Object, Object>> responsePayload)
            throws Exception;

    String processCallback(String responsePayload)
            throws Exception;
}

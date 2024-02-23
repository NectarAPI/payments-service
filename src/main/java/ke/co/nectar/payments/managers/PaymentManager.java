package ke.co.nectar.payments.managers;

import ke.co.nectar.payments.controllers.payment.PaymentRequest;
import ke.co.nectar.payments.controllers.payment.PaymentResponse;

public interface PaymentManager {

    String schedulePayment(String requestId,
                           String userRef,
                           PaymentRequest paymentRequest)
            throws Exception;

    String processCallback(PaymentResponse responsePayload) throws Exception;

    String getPaymentStatus(String paymentRef) throws Exception;

    void reversePayment(String paymentRef) throws Exception;
}

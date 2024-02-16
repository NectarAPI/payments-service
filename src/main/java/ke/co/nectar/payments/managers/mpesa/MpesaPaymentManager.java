package ke.co.nectar.payments.managers.mpesa;

import ke.co.nectar.payments.controllers.payment.PaymentRequest;
import ke.co.nectar.payments.controllers.payment.PaymentResponse;
import ke.co.nectar.payments.managers.Manager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class MpesaPaymentManager implements Manager {

    @Autowired
    private MpesaPayment mpesaPayment;

    @Override
    public String schedulePayment(String requestId,
                                   String userRef,
                                   PaymentRequest paymentRequest)
            throws Exception {
        Instant currentTimestamp = Instant.now();
        Double amount = paymentRequest.getAmount();
        String phoneNo = (String) paymentRequest.getData().get().get("phone_no");
        String accountRef = (String) paymentRequest.getData().get().get("account_ref");
        String transDesc = (String) paymentRequest.getData().get().get("trans_desc");
        return mpesaPayment.STKPushSimulation(currentTimestamp, amount, phoneNo, accountRef, transDesc);
    }

    @Override
    public String processCallback(PaymentResponse responsePayload)
            throws Exception {
        return mpesaPayment.processCallback();
    }

    @Override
    public String getPaymentStatus(String paymentRef) throws Exception {
        return mpesaPayment.STKPushTransactionStatus();
    }

    @Override
    public void reversePayment(String paymentRef) throws Exception {
        return mpesaPayment.reverse();
    }

}

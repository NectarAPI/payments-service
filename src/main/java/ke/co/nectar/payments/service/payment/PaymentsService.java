package ke.co.nectar.payments.service.payment;

import ke.co.nectar.payments.controllers.payment.PaymentRequest;
import ke.co.nectar.payments.entity.Payment;
import ke.co.nectar.payments.service.payment.impl.PaymentsResultsDistributionCount;

import java.util.List;

public interface PaymentsService {

    Payment findByRef(String ref) throws Exception;

    String schedulePayment(String requestId,
                           String userRef,
                           PaymentRequest paymentRequest) throws Exception;

    Payment processSchedulePaymentcallback(String requestId, String paymentResponse) throws Exception;

    String processPaymentTimeout(String requestId, String paymentResult) throws Exception;

    String validatePayment(String requestId, String paymentRef) throws Exception;

    Payment savePayment(Payment payment);

    Payment processPaymentValidateResult(String requestId, String paymentResult) throws Exception;

    Double getPaymentsTotalByUserRef(String userRef) throws Exception;

    List<PaymentsResultsDistributionCount> getPaymentsRequestsDistributionCount(String userRef);
}

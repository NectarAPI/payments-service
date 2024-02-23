package ke.co.nectar.payments.service.payment.impl;

import ke.co.nectar.payments.constants.StringConstants;
import ke.co.nectar.payments.controllers.payment.PaymentRequest;
import ke.co.nectar.payments.entity.Payment;
import ke.co.nectar.payments.repository.PaymentsRepository;
import ke.co.nectar.payments.service.payment.PaymentsService;
import ke.co.nectar.payments.service.payment.impl.exceptions.InvalidPaymentRefException;
import ke.co.nectar.payments.service.payment.impl.exceptions.UnsupportedPaymentRequestTypeException;
import ke.co.nectar.payments.service.payment.manager.MpesaPaymentManager;
import ke.co.nectar.payments.service.payment.manager.PaymentManager;
import org.apache.commons.text.CaseUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.time.Instant;
import java.util.List;

@Service
public class PaymentsServiceImpl implements PaymentsService {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private PaymentsRepository paymentsRepository;

    @Autowired
    private MpesaPaymentManager manager;

    @Override
    public Payment findByRef(String ref) throws Exception {
        Payment payment = paymentsRepository.findByRef(ref);
        if (payment != null) {
            return payment;
        } else {
            throw new InvalidPaymentRefException(StringConstants.INVALID_MSG_PAYMENT_BY_REF);
        }
    }

    @Override
    public List<PaymentsResultsDistributionCount> getPaymentsRequestsDistributionCount(String userRef) {
        return paymentsRepository.getPaymentsRequestsDistributionCount(userRef);
    }

    @Override
    public Double getPaymentsTotalByUserRef(String userRef) throws Exception {
        return paymentsRepository.getPaymentsTotalForUserRef(userRef);
    }

    @Override
    public String schedulePayment(String requestId,
                                  String userRef,
                                  PaymentRequest paymentRequest)
            throws Exception {
        String paymentRequestType = paymentRequest.getType();
        PaymentManager manager = (PaymentManager) Class.forName(
                        String.format("ke.co.nectar.payments.managers.%s.%sPaymentManager",
                                paymentRequestType.toLowerCase(),
                                CaseUtils.toCamelCase(paymentRequestType, true, ' ')))
                .getDeclaredConstructor().newInstance();
        String transactionRef = manager.schedule(paymentRequest.getAmount(),
                paymentRequest.getData());

        Payment payment = new Payment();
        payment.setRef(transactionRef);
        payment.setScheduled(Instant.now());
        payment.setType(paymentRequest.getType());
        payment.setValue(paymentRequest.getAmount());
        payment.setUserRef(userRef);
        paymentsRepository.save(payment);

        return transactionRef;
    }

    @Override
    public Payment processSchedulePaymentcallback(String requestId,
                                                    String paymentResponse)
            throws Exception {
        Payment payment = getPaymentManager(paymentResponse)
                .processCallback(paymentResponse);
        return paymentsRepository.save(payment);
    }


    @Override
    public String processPaymentTimeout(String requestId, String paymentResult)
            throws Exception {
        Payment payment = getPaymentManager(paymentResult);
        return savePayment(payment).getRef();
    }

    @Override
    public String validatePayment(String requestId, String paymentRef) throws Exception {
        Payment payment = paymentsRepository.findByRef(paymentRef);
        if (payment != null)  {
            PaymentManager manager = getPaymentManager(payment.getType());
            manager.validatePayment(payment);
            return payment.getRef();
        }
        throw new InvalidPaymentRefException(paymentRef);
    }

    @Override
    public Payment savePayment(Payment payment) {
        return paymentsRepository.save(payment);
    }

    @Override
    public Payment processPaymentValidateResult(String requestId, String paymentResult) throws Exception {
        Payment payment = getPaymentManager(paymentResult)
                .processPaymentValidateResult(paymentResult);
        return savePayment(payment);
    }


}

package ke.co.nectar.payments.service.payment.impl;

import ke.co.nectar.payments.constants.StringConstants;
import ke.co.nectar.payments.controllers.payment.PaymentRequest;
import ke.co.nectar.payments.entity.Payment;
import ke.co.nectar.payments.repository.PaymentsRepository;
import ke.co.nectar.payments.service.payment.PaymentsService;
import ke.co.nectar.payments.service.payment.impl.exceptions.InvalidPaymentRefException;
import ke.co.nectar.payments.service.payment.impl.exceptions.UnsupportedPaymentRequestTypeException;
import ke.co.nectar.payments.service.payment.manager.MpesaPaymentManager;
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
       if (paymentRequest.getType().equals("MPESA")) {
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
       throw new UnsupportedPaymentRequestTypeException(String.format("Invalid payment request type %s",
                                                                        paymentRequest.getType()));
    }

    @Override
    public Payment savePayment(Payment payment) {
        return paymentsRepository.save(payment);
    }
}

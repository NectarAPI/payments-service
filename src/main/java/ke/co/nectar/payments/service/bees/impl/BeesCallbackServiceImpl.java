package ke.co.nectar.payments.service.bees.impl;

import ke.co.nectar.payments.repository.PaymentsRepository;
import ke.co.nectar.payments.service.bees.BeesCallbackService;
import ke.co.nectar.payments.service.payment.manager.MpesaPaymentManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Service
public class BeesCallbackServiceImpl implements BeesCallbackService {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private PaymentsRepository paymentsRepository;

    @Autowired
    private MpesaPaymentManager manager;

    @Override
    public String processPaymentResult(String jsonResponse)
        throws Exception {
        return manager.processCallback(jsonResponse);
    }

}

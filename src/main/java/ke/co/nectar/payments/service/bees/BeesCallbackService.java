package ke.co.nectar.payments.service.bees;

public interface BeesCallbackService {

    String processPaymentResult(String jsonResponse) throws Exception;
}

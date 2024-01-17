package ke.co.nectar.payments.service.payment.impl.exceptions;

public class UnsupportedPaymentRequestTypeException extends Exception {

    public UnsupportedPaymentRequestTypeException(String message) {
        super(message);
    }
}

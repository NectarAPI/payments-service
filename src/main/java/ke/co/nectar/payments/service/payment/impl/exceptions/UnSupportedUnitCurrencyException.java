package ke.co.nectar.payments.service.payment.impl.exceptions;

public class UnSupportedUnitCurrencyException extends Exception {
    public UnSupportedUnitCurrencyException(String message) {
        super(message);
    }
}

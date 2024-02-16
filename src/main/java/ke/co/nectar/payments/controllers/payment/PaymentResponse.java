package ke.co.nectar.payments.controllers.payment;

import java.util.Map;
import java.util.Optional;

public class PaymentResponse {
    private Optional<Map<Object, Object>> data;

    public PaymentResponse() {}

    public PaymentResponse(Optional<Map<Object, Object>> data) {
        setData(data);
    }

    public Optional<Map<Object, Object>> getData() {
        return data;
    }

    public void setData(Optional<Map<Object, Object>> data) {
        this.data = data;
    }
}

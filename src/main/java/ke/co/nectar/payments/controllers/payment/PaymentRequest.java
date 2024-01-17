package ke.co.nectar.payments.controllers.payment;

import java.util.Map;
import java.util.Optional;

public class PaymentRequest {

    private String type;

    private Double amount;

    private Optional<Map<Object, Object>> data;

    public PaymentRequest() {}

    public PaymentRequest(String type, Double amount,
                          Optional<Map<Object, Object>> data) {
        setType(type);
        setAmount(amount);
        setData(data);
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Optional<Map<Object, Object>> getData() {
        return data;
    }

    public void setData(Optional<Map<Object, Object>> data) {
        this.data = data;
    }
}

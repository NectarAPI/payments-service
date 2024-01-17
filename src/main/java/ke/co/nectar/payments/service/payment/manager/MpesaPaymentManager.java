package ke.co.nectar.payments.service.payment.manager;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import ke.co.nectar.payments.constants.StringConstants;
import ke.co.nectar.payments.entity.Credits;
import ke.co.nectar.payments.entity.Payment;
import ke.co.nectar.payments.entity.UnitCurrencyValue;
import ke.co.nectar.payments.repository.PaymentsRepository;
import ke.co.nectar.payments.repository.UnitCurrencyValueRepository;
import ke.co.nectar.payments.service.credits.CreditsService;
import ke.co.nectar.payments.service.payment.impl.exceptions.InvalidPaymentRefException;
import ke.co.nectar.payments.service.payment.impl.exceptions.UnSupportedUnitCurrencyException;
import ke.co.nectar.payments.service.payment.manager.exceptions.BeesException;
import ke.co.nectar.payments.utils.AppUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.bees.java.Bees;
import software.bees.java.env.Env;
import software.bees.java.paymenttypes.PaymentType;

import java.net.URL;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;

@Component
public class MpesaPaymentManager implements PaymentManager {

    @Value("${bees.key}")
    private String key;

    @Value("${bees.secret}")
    private String secret;

    @Value("${bees.account-ref}")
    private String accountRef;

    @Value("${bees.api-gateway-callback-base-url}")
    private String apiGatewayCallbackUrl;

    @Value("${bees.api-gateway-callback-url}")
    private String apiGatewayCallbackURL;

    @Value("${bees.env}")
    private String environment;

    @Autowired
    private PaymentsRepository paymentsRepository;

    @Autowired
    private CreditsService creditsService;

    @Autowired
    private UnitCurrencyValueRepository unitCurrencyValueRepository;

    @Override
    public String schedule(Double amount, Optional<Map<Object, Object>> data)
        throws Exception {

        String phoneNo = (String) data.get().get("phone_no");
        Env env = environment.equals("prod") ? Env.live : Env.sandbox;

        Bees bees = new Bees(key, secret);
        String mpesaResponse = bees.getSTKPushTransactionFactory()
                                    .promptSTKPushPayment(PaymentType.mpesa_stk_push,
                                                            phoneNo,
                                                            new URL(String.format("%s%s", apiGatewayCallbackUrl, apiGatewayCallbackURL)),
                                                            "Token",
                                                            String.format("Reload Nectar API"),
                                                            amount,
                                                            accountRef,
                                                            env);

        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.readTree(mpesaResponse);

        int status = jsonNode.get("status").asInt();
        if (status == 200) {
            return jsonNode.get("details").get("transaction_ref").asText();
        } else {
            String message = String.format("%s - %s",
                                jsonNode.get("message").asText(),
                                jsonNode.get("details").get("message"));
            throw new BeesException(message);
        }
    }

    public String processCallback(String responsePayload)
        throws Exception {

        JSONObject beesResp = new JSONObject(responsePayload);
        String transactionRef = (String) beesResp.get("ref");

        Payment payment = paymentsRepository.findByRef(transactionRef);
        if (payment != null &&
                (payment.getResultCode() == null ||
                        payment.getResultCode().equals(""))) {

            String resultCode = String.valueOf(beesResp.get("result_code"));
            String resultDesc = (String) beesResp.get("result_desc");

            if (!resultCode.isBlank()) {
                payment.setFulfilled(Instant.now());
                payment.setResultCode(resultCode);
                payment.setResultDesc(resultDesc);
                payment = paymentsRepository.save(payment);

                if (resultCode.equals("0")) {
                    Credits credits = new Credits();
                    credits.setRef(AppUtils.generateRef());
                    credits.setPurchaseDate(Instant.now());
                    credits.setUserRef(payment.getUserRef());
                    credits.setValue(payment.getValue());
                    credits.setCurrency("KES");
                    credits.setUnits(convertToUnits("KES", payment.getValue()));
                    credits.setPayment(payment);
                    creditsService.save(credits);
                }
            }

        } else {
            throw new InvalidPaymentRefException(
                    String.format("%s %s",
                            StringConstants.INVALID_MSG_PAYMENT_BY_REF, transactionRef));
        }

        return transactionRef;
    }

    private double convertToUnits(String currency, double val)
        throws UnSupportedUnitCurrencyException {
        UnitCurrencyValue unitCurrencyValue = unitCurrencyValueRepository.findByCurrency(currency);

        if (unitCurrencyValue != null) {
            return  val/unitCurrencyValue.getValue();
        }
        throw new UnSupportedUnitCurrencyException(
                String.format("%s %s", StringConstants.UNSUPPORTED_UNIT_CURRENCY,
                        currency));
    }
}

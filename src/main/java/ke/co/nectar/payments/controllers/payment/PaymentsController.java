package ke.co.nectar.payments.controllers.payment;

import ke.co.nectar.payments.annotation.Notify;
import ke.co.nectar.payments.constants.StringConstants;
import ke.co.nectar.payments.controllers.payment.exception.InvalidParamException;
import ke.co.nectar.payments.entity.Payment;
import ke.co.nectar.payments.response.ApiResponse;
import ke.co.nectar.payments.service.payment.PaymentsService;
import ke.co.nectar.payments.service.payment.impl.PaymentsResultsDistributionCount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.constraints.NotNull;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/v1")
public class PaymentsController {

    @Autowired
    private PaymentsService paymentsService;

    @GetMapping("/payments")
    public ApiResponse getPayment(@RequestParam(value = "request_id") @NotNull String requestId,
                                  @RequestParam @NotNull String ref) {
        ApiResponse apiResponse;
        try {
            if (ref != null && !ref.isBlank()) {

                Payment payment = paymentsService.findByRef(ref);

                Map<String, Object> output = new LinkedHashMap<>();
                output.put("payment", payment);

                apiResponse = new ApiResponse(StringConstants.SUCCESS_CODE,
                        StringConstants.SUCCESS_MSG_OBTAINED_PAYMENT,
                        requestId,
                        output);
            } else {
                apiResponse = new ApiResponse(StringConstants.SUCCESS_CODE,
                        StringConstants.EMPTY_REF_VALUE,
                        requestId);
            }

        } catch (Exception e) {
            apiResponse = new ApiResponse(StringConstants.INTERNAL_SERVER_ERROR,
                    e.getMessage(),
                    requestId);
        }
        return apiResponse;
    }

    @GetMapping(value = "/payments", params = { "user_ref", "detailed_param" })
    public ApiResponse getPaymentsDetails(@RequestParam(value = "request_id") @NotNull String requestId,
                                          @RequestParam(value = "user_ref") @NotNull String userRef,
                                          @RequestParam(value = "detailed_param") @NotNull String detailedParam) {
        ApiResponse apiResponse;
        try {
            Map<String, Object> output = new LinkedHashMap<>();

            switch (detailedParam){
                case ("payments-results"):
                    List<PaymentsResultsDistributionCount> payments
                            = paymentsService.getPaymentsRequestsDistributionCount(userRef);
                    output.put("payments", payments);
                    break;
                default:
                    throw new InvalidParamException(
                            String.format("%s %s", StringConstants.INVALID_PAYMENTS_PARAM, detailedParam));
            }

            apiResponse = new ApiResponse(StringConstants.SUCCESS_CODE,
                                        StringConstants.SUCCESS_MSG_OBTAINED_PAYMENT_TOTAL,
                                        requestId,
                                        output);

        } catch (Exception e) {
            apiResponse = new ApiResponse(StringConstants.INTERNAL_SERVER_ERROR,
                    e.getMessage(),
                    requestId);
        }
        return apiResponse;
    }

    @GetMapping(value = "/payments", params = "user_ref")
    public ApiResponse getPaymentsTotalForUser(@RequestParam(value = "request_id") @NotNull String requestId,
                                  @RequestParam(value = "user_ref") @NotNull String userRef) {
        ApiResponse apiResponse;
        try {
            if (userRef != null && !userRef.isBlank()) {

                Double payments = paymentsService.getPaymentsTotalByUserRef(userRef);

                Map<String, Object> output = new LinkedHashMap<>();
                output.put("payments", payments);

                apiResponse = new ApiResponse(StringConstants.SUCCESS_CODE,
                        StringConstants.SUCCESS_MSG_OBTAINED_PAYMENT_TOTAL,
                        requestId,
                        output);
            } else {
                apiResponse = new ApiResponse(StringConstants.SUCCESS_CODE,
                        StringConstants.EMPTY_REF_VALUE,
                        requestId);
            }

        } catch (Exception e) {
            apiResponse = new ApiResponse(StringConstants.INTERNAL_SERVER_ERROR,
                    e.getMessage(),
                    requestId);
        }
        return apiResponse;
    }

    @PostMapping(value = "/payments", consumes = "application/json" )
    @Notify(category = "SCHEDULE_PAYMENT",
            description = "Scheduled payment request [Request-ID: {requestId}]")
    public ApiResponse schedulePayment(@RequestParam(value = "request_id") @NotNull String requestId,
                                        @RequestParam(value = "user_ref") @NotNull String userRef,
                                        @RequestBody PaymentRequest paymentRequest) {
        ApiResponse apiResponse;
        try {
                String scheduledTransactionRef = paymentsService.schedulePayment(requestId,
                                                                                userRef,
                                                                                paymentRequest);
                if (!scheduledTransactionRef.isBlank()) {
                    Map<String, Object> output = new LinkedHashMap<>();
                    output.put("transaction_ref", scheduledTransactionRef);

                    apiResponse = new ApiResponse(StringConstants.SUCCESS_CODE,
                            StringConstants.PAYMENT_SCHEDULED_MSG,
                            requestId,
                            output);
                } else {
                    apiResponse = new ApiResponse(StringConstants.INVALID_REQUEST,
                            StringConstants.ERROR_SCHEDULING_PAYMENT_MSG,
                            requestId);
                }

        } catch (Exception e) {
            apiResponse = new ApiResponse(StringConstants.INTERNAL_SERVER_ERROR,
                    e.getMessage(),
                    requestId);
        }
        return apiResponse;
    }

}

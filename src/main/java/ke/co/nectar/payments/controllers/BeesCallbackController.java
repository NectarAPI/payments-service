package ke.co.nectar.payments.controllers;


import ke.co.nectar.payments.constants.StringConstants;
import ke.co.nectar.payments.response.ApiResponse;
import ke.co.nectar.payments.service.bees.BeesCallbackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.constraints.NotNull;

@RestController
public class BeesCallbackController {

    @Autowired
    private BeesCallbackService service;

    @PostMapping(value = "${bees.callback-url}", consumes = "application/json" )
    public ApiResponse processCallbackResult(@RequestBody @NotNull String response) {
        ApiResponse apiResponse;
        try {
            String transactionRef = service.processPaymentResult(response);
            apiResponse = new ApiResponse(StringConstants.SUCCESS_CODE,
                                            StringConstants.CALLBACK_RECEIVED,
                                            transactionRef);

        } catch (Exception e) {
            apiResponse = new ApiResponse(StringConstants.INTERNAL_SERVER_ERROR,
                                            e.getMessage(),
                                    "");
        }
        return apiResponse;
    }
}
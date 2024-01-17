package ke.co.nectar.payments.controllers.credits;

import ke.co.nectar.payments.annotation.Notify;
import ke.co.nectar.payments.constants.StringConstants;
import ke.co.nectar.payments.response.ApiResponse;
import ke.co.nectar.payments.service.credits.CreditsService;
import ke.co.nectar.payments.service.credits.impl.TimelineRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.constraints.NotNull;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/v1")
public class CreditsController {

    @Autowired
    private CreditsService creditsService;

    @GetMapping(value = "/credits", params =  {"ref"})
    public ApiResponse getCredits(@RequestParam(value = "request_id") @NotNull String requestId,
                                  @RequestParam(value = "ref") @NotNull String ref) {
        ApiResponse apiResponse;
        try {
            if (ref != null && !ref.isBlank()) {
                Map<String, Object> output = new LinkedHashMap<>();
                output.put("credits", creditsService
                        .getCreditsByRef(ref));
                apiResponse = new ApiResponse(StringConstants.SUCCESS_CODE,
                        StringConstants.SUCCESS_MSG_OBTAINED_CREDITS,
                        requestId,
                        output);
            } else {
                apiResponse = new ApiResponse(StringConstants.SUCCESS_CODE,
                        StringConstants.EMPTY_USER_ID_VALUE,
                        requestId);
            }

        } catch (Exception e) {
            apiResponse = new ApiResponse(StringConstants.INTERNAL_SERVER_ERROR,
                    e.getMessage(),
                    requestId);
        }
        return apiResponse;
    }

    @GetMapping(value = "/credits", params =  {"user_ref", "request_id"})
    public ApiResponse getCredits(@RequestParam(value = "request_id") @NotNull String requestId,
                                  @RequestParam(value = "user_ref") @NotNull String userRef,
                                  @RequestParam Optional<Boolean> all,
                                  @RequestParam Optional<Boolean> detailed) {
        ApiResponse apiResponse;
        try {
            if (userRef != null && !userRef.isBlank()) {
                Map<String, Object> output = new LinkedHashMap<>();
                output.put("credits", creditsService
                                            .getCredits(userRef,
                                                    all.isPresent() && all.get(),
                                                    detailed.isPresent() && detailed.get()));
                apiResponse = new ApiResponse(StringConstants.SUCCESS_CODE,
                                                StringConstants.SUCCESS_MSG_OBTAINED_CREDITS,
                                                requestId,
                                                output);
            } else {
                apiResponse = new ApiResponse(StringConstants.SUCCESS_CODE,
                        StringConstants.EMPTY_USER_ID_VALUE,
                        requestId);
            }

        } catch (Exception e) {
            apiResponse = new ApiResponse(StringConstants.INTERNAL_SERVER_ERROR,
                    e.getMessage(),
                    requestId);
        }
        return apiResponse;
    }

    @GetMapping(value = "/credits", params =  {"user", "months"})
    public ApiResponse getTimelineRequests(@RequestParam(value = "request_id") @NotNull String requestId,
                                           @RequestParam(value = "user") @NotNull String userRef,
                                           @RequestParam(value = "months") Optional<Integer> months) {
        ApiResponse apiResponse;
        try {
            if (months.isEmpty()) months = Optional.of(5);
            List<TimelineRequest> credits = creditsService.getCreditsTimelineRequests(userRef, months.get());
            List<TimelineRequest> consumption = creditsService.getConsumptionTimelineRequests(userRef, months.get());
            Map<String, Object> output = new LinkedHashMap<>();
            output.put("credits", credits);
            output.put("consumption", consumption);
            apiResponse = new ApiResponse(StringConstants.SUCCESS_CODE,
                                            StringConstants.SUCCESS_MSG_OBTAINED_CREDITS,
                                            requestId,
                                            output);

        } catch (Exception e) {
            return new ApiResponse(StringConstants.INTERNAL_SERVER_ERROR,
                                    e.getMessage(),
                                    requestId);
        }
        return apiResponse;
    }

    @PostMapping(value = "/consumption")
    @Notify(category = "POST_CONSUMPTION",
            description = "Update consumption details [Request-ID: {requestId}]")
    public ApiResponse postConsumption(@RequestParam(value = "request_id") @NotNull String requestId,
                                       @RequestParam(value = "user_ref") @NotNull String userRef,
                                       @NotNull @RequestBody ConsumptionDetails consumptionDetails) {
        ApiResponse apiResponse;
        try {
            String consumptionRef = creditsService.postConsumption(consumptionDetails, userRef);

            if (!consumptionRef.isBlank()) {
                Map<String, Object> output = new LinkedHashMap<>();
                output.put("consumption", consumptionRef);

                apiResponse = new ApiResponse(StringConstants.SUCCESS_CODE,
                                                StringConstants.CONSUMPTION_RECORDED,
                                                requestId,
                                                output);
            } else {
                apiResponse = new ApiResponse(StringConstants.INVALID_REQUEST,
                                                StringConstants.ERROR_RECORDING_CONSUMPTION_MSG,
                                                requestId);
            }

        } catch (Exception e) {
            return new ApiResponse(StringConstants.INTERNAL_SERVER_ERROR,
                    e.getMessage(),
                    requestId);
        }
        return apiResponse;
    }

}

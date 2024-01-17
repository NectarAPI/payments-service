package ke.co.nectar.payments.constants;

public interface StringConstants {

    // -- Generic Error Codes
    int SUCCESS_CODE = 200;
    int BAD_REQUEST = 400;
    int INVALID_REQUEST = 405;
    int INTERNAL_SERVER_ERROR = 500;
    int EXCEPTION_CODE=600;
    int ALREADY_EXIST_CODE=700;
    int PHONE_NUMBER_MAX_LENGTH=10;

    // -- Specific Error Messages
    String EMPTY_REF_VALUE="Ref value should not be empty";
    String EMPTY_USER_ID_VALUE ="User ID should not be empty";
    String SUCCESS_MSG_OBTAINED_PAYMENT = "Obtained payment";
    String SUCCESS_MSG_OBTAINED_PAYMENT_TOTAL = "Obtained payments total";
    String INVALID_MSG_PAYMENT_BY_REF = "Invalid payment ref";
    String PAYMENT_SCHEDULED_MSG = "Payment scheduled";
    String CONSUMPTION_RECORDED = "Consumption recorded";
    String ERROR_SCHEDULING_PAYMENT_MSG = "Error scheduling payment";
    String ERROR_RECORDING_CONSUMPTION_MSG = "Error recording consumption";
    String SUCCESS_MSG_OBTAINED_CREDITS = "Obtained credits";
    String CALLBACK_RECEIVED = "Callback received";
    String UNSUPPORTED_UNIT_CURRENCY = "Unsupported unit currency";
    String INVALID_PAYMENTS_PARAM = "Invalid payments param";

}

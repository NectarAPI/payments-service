package ke.co.nectar.payments.managers.mpesa;

public interface Urls {

    String AUTH = "https://sandbox.safaricom.co.ke/oauth/v1/generate?grant_type=client_credentials";
    String STK = "https://sandbox.safaricom.co.ke/mpesa/stkpush/v1/processrequest";
    String STK_QUERY = "https://sandbox.safaricom.co.ke/mpesa/stkpushquery/v1/query";
    String REVERSALS = "https://sandbox.safaricom.co.ke/safaricom/reversal/v1/request";
}

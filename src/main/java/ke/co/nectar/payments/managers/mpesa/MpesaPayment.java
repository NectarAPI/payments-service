package ke.co.nectar.payments.managers.mpesa;

import java.io.IOException;

import okhttp3.*;
import org.json.*;
import org.springframework.beans.factory.annotation.Value;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Base64;

public class MpesaPayment {

    @Value("${mpesa.key}")
    private String key;

    @Value("${mpesa.secret}")
    private String secret;

    @Value("${mpesa.businessShortCode}")
    private String businessShortCode;

    @Value("${mpesa.passKey")
    private String passKey;

    @Value("${mpesa.transactionType}")
    private String transactionType;

    @Value("${mpesa.callbackURL}")
    private String callbackURL;

    @Value("${mpesa.queueTimeOutURL}")
    private String queueTimeOutURL;

    @Value("${mpesa.initiator}")
    private String initiator;

    @Value("{mpesa.password}")
    private String password;

    @Value("${mpesa.publicKeyPath}")
    private String publicKeyPath;

    @Value("${mpesa.resultURL}")
    private String resultURL;

    public String authenticate() throws IOException {
        String appKeySecret = String.format("%s:%s", key, secret);
        byte[] bytes = appKeySecret.getBytes(StandardCharsets.ISO_8859_1);
        String encoded = Base64.getEncoder().encodeToString(bytes);

        OkHttpClient client = new OkHttpClient().newBuilder().build();
        Request request = new Request.Builder()
                .url(Urls.AUTH)
                .method("GET", null)
                .addHeader("authorization", "Basic " + encoded)
                .addHeader("cache-control", "no-cache")
                .build();

        Response response = client.newCall(request).execute();
        JSONObject jsonObject = new JSONObject(response.body().string());
        return jsonObject.getString("access_token");
    }

    public String STKPushSimulation(Instant timestamp, double amount, String phoneNumber,
                                    String accountReference, String transactionDesc)
            throws IOException {

        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObject = new JSONObject();

        jsonObject.put("BusinessShortCode", businessShortCode);
        jsonObject.put("Password", generatePassword(businessShortCode, passKey, timestamp));
        jsonObject.put("Timestamp", formatTimestamp(timestamp));
        jsonObject.put("TransactionType", transactionType);
        jsonObject.put("Amount", amount);
        jsonObject.put("PhoneNumber", phoneNumber);
        jsonObject.put("PartyA", phoneNumber);
        jsonObject.put("PartyB", businessShortCode);
        jsonObject.put("CallBackURL", callbackURL);
        jsonObject.put("AccountReference", accountReference);
        jsonObject.put("QueueTimeOutURL", queueTimeOutURL);
        jsonObject.put("TransactionDesc", transactionDesc);

        jsonArray.put(jsonObject);

        String requestJson = jsonArray.toString().replaceAll("[\\[\\]]", "");

        OkHttpClient client = new OkHttpClient();
        String url = Urls.STK;
        MediaType mediaType = MediaType.parse(Media.JSON);
        RequestBody body = RequestBody.create(requestJson, mediaType);
        Request request = new Request.Builder()
                            .url(url)
                            .post(body)
                            .addHeader("content-type", Media.JSON)
                            .addHeader("authorization", "Bearer " + authenticate())
                            .addHeader("cache-control", "no-cache")
                            .build();

        Response response = client.newCall(request).execute();
        return response.body().toString();
    }

    public String STKPushTransactionStatus(String businessShortCode, String password,
                                           String timestamp, String checkoutRequestID)
            throws IOException {

        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("BusinessShortCode", businessShortCode);
        jsonObject.put("Password", password);
        jsonObject.put("Timestamp", timestamp);
        jsonObject.put("CheckoutRequestID", checkoutRequestID);

        jsonArray.put(jsonObject);

        String requestJson = jsonArray.toString().replaceAll("[\\[\\]]", "");

        OkHttpClient client = new OkHttpClient();

        MediaType mediaType = MediaType.parse(Media.JSON);
        RequestBody body = RequestBody.create(requestJson, mediaType);
        Request request = new Request.Builder()
                                .url(Urls.STK_QUERY)
                                .post(body)
                                .addHeader("authorization", "Bearer " + authenticate())
                                .addHeader("content-type", Media.JSON)
                                .build();

        Response response = client.newCall(request).execute();
        return response.body().toString();
    }

    public String reverse(String transactionID,
                          float amount,
                          String remarks,
                          String ocassion)
            throws Exception {

        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("Initiator", initiator);
        jsonObject.put("SecurityCredential", encrypt(password, publicKeyPath));
        jsonObject.put("CommandID", "TransactionReversal");
        jsonObject.put("TransactionID", transactionID);
        jsonObject.put("Amount", amount);
        jsonObject.put("ReceiverParty", businessShortCode);
        jsonObject.put("RecieverIdentifierType", "11");
        jsonObject.put("QueueTimeOutURL", queueTimeOutURL);
        jsonObject.put("ResultURL", resultURL);
        jsonObject.put("Remarks", remarks);
        jsonObject.put("Occasion", ocassion);

        jsonArray.put(jsonObject);

        String requestJson = jsonArray.toString().replaceAll("[\\[\\]]", "");

        OkHttpClient client = new OkHttpClient();

        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(requestJson, mediaType);
        Request request = new Request.Builder()
                                .url(Urls.REVERSALS)
                                .post(body)
                                .addHeader("content-type", Media.JSON)
                                .addHeader("authorization", "Bearer " + authenticate())
                                .addHeader("cache-control", "no-cache")
                                .build();

        Response response = client.newCall(request).execute();
        return response.body().string();
    }

    public String processCallback() {

    }

    private String generatePassword(String shortCode, String passKey, Instant timestamp) {
        String password = String.format("%s%s%s", shortCode, passKey, formatTimestamp(timestamp));
        return Base64.getEncoder().encodeToString(password.getBytes(StandardCharsets.ISO_8859_1));
    }

    private String formatTimestamp(Instant timestamp) {
        final String PATTERN_FORMAT = "ddMMyyyyHHMMSS";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(PATTERN_FORMAT);
        return formatter.format(timestamp);
    }

    private byte[] readFileBytes(String filename) throws IOException
    {
        Path path = Paths.get(filename);
        return Files.readAllBytes(path);
    }

    private PublicKey readPublicKey(String filename) throws IOException,
            NoSuchAlgorithmException, InvalidKeySpecException
    {
        X509EncodedKeySpec publicSpec = new X509EncodedKeySpec(readFileBytes(filename));
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePublic(publicSpec);
    }
    private String encrypt(String password, String publicKeyPath)
        throws IOException, NoSuchAlgorithmException,
            InvalidKeySpecException, NoSuchPaddingException,
            InvalidKeyException, IllegalBlockSizeException,
            BadPaddingException {
        Cipher encrypt = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        encrypt.init(Cipher.ENCRYPT_MODE, readPublicKey(publicKeyPath));
        byte[] encryptedMessage = encrypt.doFinal(password.getBytes(StandardCharsets.UTF_8));
        return new String(encryptedMessage);
    }
}

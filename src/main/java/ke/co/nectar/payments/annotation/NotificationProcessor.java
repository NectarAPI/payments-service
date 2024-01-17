package ke.co.nectar.payments.annotation;

import ke.co.nectar.payments.annotation.exception.InvalidTemplateException;
import ke.co.nectar.payments.annotation.exception.UnImplementedAnnotationException;
import ke.co.nectar.payments.response.ApiResponse;
import ke.co.nectar.payments.utils.request.ApiResponseException;
import ke.co.nectar.payments.utils.request.BasicAuthCredentials;
import ke.co.nectar.payments.utils.request.Payload;
import ke.co.nectar.payments.utils.request.RequestUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.Map;

@Component
public class NotificationProcessor {

    @Value("${endpoints.users.host}")
    private String usersEndpoint;

    @Value("${endpoints.users.username}")
    private String usersBasicAuthUsername;

    @Value("${endpoints.users.password}")
    private String usersBasicAuthPassword;

    @Autowired
    private RequestUtils requestUtils;

    @Autowired
    private NotificationUtils notificationUtils;

    public boolean process(ProceedingJoinPoint joinPoint) throws Throwable {
        Map<String, Object> params = getNotificationParams(joinPoint);
        return sendNotification(params);
    }

    private Map<String, Object> getNotificationParams(ProceedingJoinPoint joinPoint)
            throws InvalidTemplateException, UnImplementedAnnotationException {
        Method method = getMethod(joinPoint);
        Notify notify = method.getAnnotation(Notify.class);

        if (notify != null) {
            String category = notify.category();
            String description = notify.description();

            Map<String, Object> functionParams = getFunctionParams(joinPoint);
            description = template(description, functionParams);

            Map<String, Object> params = new HashMap<>();
            params.put("category", category);
            params.put("description", description);
            params.put("user_ref", functionParams.get("userRef"));
            params.put("request_id", functionParams.get("requestId"));
            return params;
        }
        throw new UnImplementedAnnotationException(
                "Annotation @Notify not implemented"
        );
    }

    private Method getMethod(ProceedingJoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        return signature.getMethod();
    }

    private Map<String, Object> getFunctionParams(ProceedingJoinPoint joinPoint) {
        Method method = getMethod(joinPoint);
        Parameter[] parameters = method.getParameters();
        Object[] args = joinPoint.getArgs();

        Map<String, Object> functionParams = new HashMap<>();
        for (int c = 0; c < parameters.length; c++) {
            functionParams.put(parameters[c].getName(),
                    args[c]);
        }
        return functionParams;
    }

    private String template(String description,
                            Map<String, Object> functionParams)
            throws InvalidTemplateException {
        return notificationUtils.template(description, functionParams);
    }

    private boolean sendNotification(Map<String, Object> params)
            throws ApiResponseException {
        ApiResponse response = requestUtils.post(
                new BasicAuthCredentials(usersBasicAuthUsername,
                        usersBasicAuthPassword),
                String.format("%s/%s/activity?request_id=%s",
                        usersEndpoint,
                        params.get("user_ref"),
                        params.get("request_id")),
                new Payload(params).toJson().toString());
        return extract(response);
    }

    private boolean extract(ApiResponse response)
            throws ApiResponseException {
        if (response.getStatus().getCode() != 200) {
            throw new ApiResponseException(response.getStatus().getMessage());
        }
        return true;
    }
}

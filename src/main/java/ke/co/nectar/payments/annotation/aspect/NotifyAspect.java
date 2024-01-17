package ke.co.nectar.payments.annotation.aspect;

import ke.co.nectar.payments.annotation.NotificationProcessor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class NotifyAspect {

    @Autowired
    private NotificationProcessor processor;

    @Around("@annotation(ke.co.nectar.payments.annotation.Notify)")
    public Object notify(ProceedingJoinPoint joinPoint) throws Throwable {
        processor.process(joinPoint);
        return joinPoint.proceed();
    }
}

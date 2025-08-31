package ua.edu.dnu.warehouse.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import ua.edu.dnu.warehouse.model.Action;
import ua.edu.dnu.warehouse.model.Employee;
import ua.edu.dnu.warehouse.service.EventLogService;

import java.lang.reflect.Method;

@Aspect
@Component
public class LogAspect {
    private final EventLogService eventLogService;

    public LogAspect(EventLogService eventLogService){
        this.eventLogService = eventLogService;
    }

    @Around("@annotation(LogAction)")
    public Object logExecution(ProceedingJoinPoint joinPoint) throws Throwable {
        Class<?> clazz = joinPoint.getTarget().getClass();
        LogCategory logCategory = clazz.getAnnotation(LogCategory.class);
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        LogAction logAction = method.getAnnotation(LogAction.class);
        Object result;
        try {
            //Виклик методу
            result = joinPoint.proceed();
            if (result == null) {
                return null;
            }
            //Логування результату
            createEventLog(result, logCategory, logAction);
        } catch (Exception exception) {
            //Логування помилки
            createErrorLog(exception, logCategory, logAction);
            throw exception;
        }
        return result;
    }

    private void createEventLog(Object result, LogCategory logCategory, LogAction logAction){
        String resultString = String.format("%s: {%s}", logAction.message(), result);
        if (logAction.value() == Action.LOGIN  && result instanceof Employee employee) {
            eventLogService.createEventLog(logAction.value(), logCategory.value(), resultString, employee);
        } else {
            eventLogService.createEventLog(logAction.value(), logCategory.value(), resultString);
        }
    }

    private void createErrorLog(Exception exception, LogCategory logCategory, LogAction logAction){
        String errorResult = "Помилка при виконанні операції: " + exception.getMessage();
        eventLogService.createEventLog(logAction.value(), logCategory.value(), errorResult);
    }
}

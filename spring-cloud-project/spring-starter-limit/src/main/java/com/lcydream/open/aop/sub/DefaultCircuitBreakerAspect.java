package com.lcydream.open.aop.sub;

import com.lcydream.open.annotation.SemaphoreCircuitBreaker;
import com.lcydream.open.annotation.SemaphoreTimeOutCircuitBreaker;
import com.lcydream.open.annotation.TimeOutCircuitBreaker;
import com.lcydream.open.aop.CircuitBreakerAspect;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

import java.lang.reflect.Method;

import static com.lcydream.open.Utils.AopUtils.getMethodFromTarget;

@Aspect
public class DefaultCircuitBreakerAspect<T> extends CircuitBreakerAspect<T> {
    public DefaultCircuitBreakerAspect() {
    }

    public DefaultCircuitBreakerAspect(int executorCount,int maxExecutorCount) throws IllegalAccessException {
        super(executorCount, maxExecutorCount);
    }

    public DefaultCircuitBreakerAspect(int executorCount,int maxExecutorCount,String globalTarget) throws IllegalAccessException {
        super(executorCount, maxExecutorCount, globalTarget);
    }

    public DefaultCircuitBreakerAspect(int executorCount,int maxExecutorCount,T t) throws IllegalAccessException {
        super(executorCount, maxExecutorCount, t);
    }



    /*@Override
    @Around("execution(public * *(..))")
    public Object advancedInTimeout(ProceedingJoinPoint point) {
        Long timeout = null;
        Integer semaphore = null;
        Object[] args = null;
        Method method=null;
        if (point instanceof MethodInvocationProceedingJoinPoint) {
            args = point.getArgs();
            MethodInvocationProceedingJoinPoint methodPoint = (MethodInvocationProceedingJoinPoint) point;
            MethodSignature signature = (MethodSignature) methodPoint.getSignature();
            method = signature.getMethod();
            SemaphoreTimeOutCircuitBreaker semaphoreTimeOutCircuitBreaker = method.getAnnotation(SemaphoreTimeOutCircuitBreaker.class);
            TimeOutCircuitBreaker timeOutCircuitBreaker = method.getAnnotation(TimeOutCircuitBreaker.class);
            SemaphoreCircuitBreaker semaphoreCircuitBreaker = method.getAnnotation(SemaphoreCircuitBreaker.class);
            if(semaphoreTimeOutCircuitBreaker != null){
                timeout = semaphoreTimeOutCircuitBreaker.timeout();
                semaphore = semaphoreTimeOutCircuitBreaker.value();
            }else if(timeOutCircuitBreaker != null && semaphoreCircuitBreaker!=null){
                timeout = timeOutCircuitBreaker.timeout();
                semaphore = semaphoreCircuitBreaker.value();
            }else if(timeOutCircuitBreaker != null){
                timeout = timeOutCircuitBreaker.timeout();
            }else if(semaphoreCircuitBreaker != null){
                semaphore = semaphoreCircuitBreaker.value();
            }
        }
        if(timeout == null && semaphore == null) {
            try {
                return point.proceed(args);
            } catch (Throwable throwable) {
                throwable.printStackTrace();
                return returnObject() == null ? CircuitBreakerAspect.otherTips : returnObject();
            }
        }
        return doInvoke(point,method, args, timeout,semaphore);
    }*/

    /*@Override
    //@Around("execution(* com.*.*(..)) && @annotation(timeOutCircuitBreaker)")
    @Around("@annotation(timeOutCircuitBreaker)")
    public Object advancedInTimeout(ProceedingJoinPoint point,
                                    TimeOutCircuitBreaker timeOutCircuitBreaker) {
        return advanced(point);
    }

    @Override
    @Around("@annotation(semaphoreCircuitBreaker)")
    public Object advancedInSemaphore(ProceedingJoinPoint point,
                                      SemaphoreCircuitBreaker semaphoreCircuitBreaker) {
        return advanced(point);
    }

    @Override
    //@Around("execution(public * *(..)) && @annotation(semaphoreTimeOutCircuitBreaker)")
    @Around("@annotation(semaphoreTimeOutCircuitBreaker)")
    public Object advancedInSemaphoreTimeout(ProceedingJoinPoint point,
                                             SemaphoreTimeOutCircuitBreaker semaphoreTimeOutCircuitBreaker) {
        return advanced(point);
    }*/

    @Override
    @Around("timeOutCircuitBreaker() || semaphoreCircuitBreaker() || semaphoreTimeOutCircuitBreaker()")
    public Object advancedInTimeout(ProceedingJoinPoint point) {
        return advanced(point);
    }

    /**
     *  执行拦截点
     * @param point 拦截点
     * @return
     */
    private Object advanced(final ProceedingJoinPoint point){
        Long timeout = null;
        Integer semaphore = null;
        String fallbackMethod = null;
        Object[] args = null;
        Method method = getMethodFromTarget(point);
        if(method != null) {
            args = point.getArgs();
            SemaphoreTimeOutCircuitBreaker semaphoreTimeOutCircuitBreaker = method.getAnnotation(SemaphoreTimeOutCircuitBreaker.class);
            TimeOutCircuitBreaker timeOutCircuitBreaker = method.getAnnotation(TimeOutCircuitBreaker.class);
            SemaphoreCircuitBreaker semaphoreCircuitBreaker = method.getAnnotation(SemaphoreCircuitBreaker.class);
            if (semaphoreTimeOutCircuitBreaker != null) {
                timeout = semaphoreTimeOutCircuitBreaker.timeout();
                semaphore = semaphoreTimeOutCircuitBreaker.value();
                fallbackMethod = semaphoreTimeOutCircuitBreaker.fallbackMethod();
            } else if (timeOutCircuitBreaker != null && semaphoreCircuitBreaker != null) {
                timeout = timeOutCircuitBreaker.timeout();
                semaphore = semaphoreCircuitBreaker.value();
                fallbackMethod = timeOutCircuitBreaker.fallbackMethod();
                if(StringUtils.isBlank(fallbackMethod)){
                    fallbackMethod = semaphoreCircuitBreaker.fallbackMethod();
                }
            } else if (timeOutCircuitBreaker != null) {
                timeout = timeOutCircuitBreaker.timeout();
                fallbackMethod = timeOutCircuitBreaker.fallbackMethod();
            } else if (semaphoreCircuitBreaker != null) {
                semaphore = semaphoreCircuitBreaker.value();
                fallbackMethod = semaphoreCircuitBreaker.fallbackMethod();
            }
            if (timeout == null && semaphore == null) {
                try {
                    return point.proceed(args);
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                    return returnObject() == null ? CircuitBreakerAspect.otherTips : returnObject();
                }
            }
            return doInvoke(point,method, args, timeout,semaphore,fallbackMethod);
        }else {
            return returnObject() == null ? CircuitBreakerAspect.otherTips : returnObject();
        }
    }

}

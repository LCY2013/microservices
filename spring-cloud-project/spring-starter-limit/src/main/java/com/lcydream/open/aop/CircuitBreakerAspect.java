package com.lcydream.open.aop;

import cn.hutool.core.thread.ExecutorBuilder;
import cn.hutool.core.thread.ThreadUtil;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.*;

/**
 * @ClassName: CircuitBreakerAspect<T> 熔断实现切面实现
 * @author: LuoChunYun
 * @Date: 2019/4/20 22:22
 * @Description: 熔断AOP
 */
public abstract class CircuitBreakerAspect<T> {

    private Logger logger = LoggerFactory.getLogger(CircuitBreakerAspect.class);

    /**
     * 默认线程池的实现是20个线程
     */
    private static ThreadPoolExecutor executorService;

    /**
     * 全局方法执行类
     */
    private static Object globalTargetMethodClass;

    /**
     * 全局执行方法
     */
    private static Method globalTargetMethod;

    private T t;

    protected static String otherTips = "温馨提示! 亲,发生了未知情况呀!请刷新重试。";

    private static String timeOutTips = "温馨提示! 亲,你的网络开小差了呀!请刷新重试。";

    private static String semaphoreTips = "温馨提示! 亲,你的请求太频繁了!请稍后重试。";

    private volatile static Map<String, Semaphore> semaphoreMap = new ConcurrentHashMap<>(8);

    public CircuitBreakerAspect() {
        //executorService = ThreadUtil.newExecutor(20,200);
        //ThreadUtil.newExecutor(20,200);
        executorService =
                ExecutorBuilder.create().setCorePoolSize(20).setMaxPoolSize(200)
                        .setKeepAliveTime(60, TimeUnit.SECONDS)
                        .setAllowCoreThreadTimeOut(true) //允许线程执行超时回收线程
                        .build();
    }

    public CircuitBreakerAspect(int executorCount, int maxExecutorCount) throws IllegalAccessException {
        if (executorCount < 0) {
            throw new IllegalAccessException("参数不合法");
        }
        //executorService = ThreadUtil.newExecutor(executorCount,maxExecutorCount);
        //ThreadUtil.newExecutor(executorCount,maxExecutorCount);
        executorService =
                ExecutorBuilder.create().setCorePoolSize(executorCount)
                        .setMaxPoolSize(maxExecutorCount)
                        .setKeepAliveTime(60, TimeUnit.SECONDS) //设置线程的最大存活时间
                        .setAllowCoreThreadTimeOut(true) //允许线程执行超时回收线程
                        .build();
    }

    public CircuitBreakerAspect(int executorCount, int maxExecutorCount, String globalTarget) throws IllegalAccessException {
        if (executorCount < 0 && maxExecutorCount < 0) {
            throw new IllegalAccessException("参数不合法");
        }
        //executorService = ThreadUtil.newExecutor(executorCount,maxExecutorCount);
        //ThreadUtil.newExecutor(executorCount,maxExecutorCount);
        executorService =
                ExecutorBuilder.create().setCorePoolSize(executorCount)
                        .setMaxPoolSize(maxExecutorCount)
                        .setKeepAliveTime(60, TimeUnit.SECONDS) //设置线程的最大存活时间
                        .setAllowCoreThreadTimeOut(true) //允许线程执行超时回收线程
                        .build();
        if (StringUtils.isNotBlank(globalTarget)) {
            final String[] target = StringUtils.split(globalTarget, "#");
            if (target.length == 2) {
                try {
                    final Class<?> targetClass = Class.forName(target[0]);
                    for (Method method : targetClass.getDeclaredMethods()) {
                        if (StringUtils.equals(target[1], method.getName())) {
                            if (method.getParameterTypes().length != 0) {
                                throw new RuntimeException("提供的全局限流熔断的方法不允许带有参数信息");
                            }
                            CircuitBreakerAspect.globalTargetMethod = method;
                            CircuitBreakerAspect.globalTargetMethodClass = targetClass.newInstance();
                        }
                    }
                } catch (ClassNotFoundException e) {
                    logger.error("没有找到全局限流熔断的类信息");
                } catch (InstantiationException e) {
                    logger.error("没有找到全局限流熔断的类的可用无参构造函数");
                }
                if (CircuitBreakerAspect.globalTargetMethod == null || CircuitBreakerAspect.globalTargetMethodClass == null) {
                    throw new RuntimeException("请提供有效的全局限流熔断的方法信息，eg: classPath#methodName");
                }
            } else {
                logger.error("全局限流熔断方法不合法");
            }
        }
    }

    public CircuitBreakerAspect(int executorCount, int maxExecutorCount, T t) throws IllegalAccessException {
        if (executorCount < 0) {
            throw new IllegalAccessException("参数不合法");
        }
        //executorService = ThreadUtil.newExecutor(executorCount,maxExecutorCount);
        //ThreadUtil.newExecutor(executorCount,maxExecutorCount);
        executorService =
                ExecutorBuilder.create().setCorePoolSize(executorCount)
                        .setMaxPoolSize(maxExecutorCount)
                        .setKeepAliveTime(60, TimeUnit.SECONDS)
                        .setAllowCoreThreadTimeOut(true) //允许线程执行超时回收线程
                        .build();
        this.t = t;
    }

    public CircuitBreakerAspect(String otherTips, String timeOutTips, String semaphoreTips) {
        //executorService = ThreadUtil.newExecutor(20,200);
        //ThreadUtil.newExecutor(20,200);
        executorService =
                ExecutorBuilder.create().setCorePoolSize(20)
                        .setMaxPoolSize(200)
                        .setKeepAliveTime(60, TimeUnit.SECONDS)
                        .setAllowCoreThreadTimeOut(true) //允许线程执行超时回收线程
                        .build();
        if (otherTips != null) {
            CircuitBreakerAspect.otherTips = otherTips;
        }
        if (timeOutTips != null) {
            CircuitBreakerAspect.timeOutTips = timeOutTips;
        }
        if (semaphoreTips != null) {
            CircuitBreakerAspect.semaphoreTips = semaphoreTips;
        }
    }

    public CircuitBreakerAspect(int executorCount, int maxExecutorCount, String otherTips, String timeOutTips, String semaphoreTips) throws IllegalAccessException {
        if (executorCount < 0) {
            throw new IllegalAccessException("参数不合法");
        }
        //executorService = ThreadUtil.newExecutor(executorCount,maxExecutorCount);
        //ThreadUtil.newExecutor(executorCount,maxExecutorCount);
        executorService =
                ExecutorBuilder.create().setCorePoolSize(executorCount)
                        .setMaxPoolSize(maxExecutorCount)
                        .setKeepAliveTime(60, TimeUnit.SECONDS)
                        .setAllowCoreThreadTimeOut(true) //允许线程执行超时回收线程
                        .build();
        CircuitBreakerAspect.otherTips = otherTips;
        CircuitBreakerAspect.timeOutTips = timeOutTips;
        CircuitBreakerAspect.semaphoreTips = semaphoreTips;
        if (otherTips != null) {
            CircuitBreakerAspect.otherTips = otherTips;
        }
        if (timeOutTips != null) {
            CircuitBreakerAspect.timeOutTips = timeOutTips;
        }
        if (semaphoreTips != null) {
            CircuitBreakerAspect.semaphoreTips = semaphoreTips;
        }
    }

    /**
     * 熔断限流操作
     */
    /*public abstract Object advancedInTimeout(ProceedingJoinPoint point);*/
    @Pointcut("@annotation(com.lcydream.open.annotation.TimeOutCircuitBreaker)")
    public void timeOutCircuitBreaker() {
    }

    @Pointcut("@annotation(com.lcydream.open.annotation.SemaphoreCircuitBreaker)")
    public void semaphoreCircuitBreaker() {
    }

    @Pointcut("@annotation(com.lcydream.open.annotation.SemaphoreTimeOutCircuitBreaker)")
    public void semaphoreTimeOutCircuitBreaker() {
    }

    public abstract Object advancedInTimeout(ProceedingJoinPoint point);

    /**
     *  熔断操作
     */
    /*public abstract Object advancedInTimeout(ProceedingJoinPoint point,
                                             TimeOutCircuitBreaker timeOutCircuitBreaker);*/

    /**
     *  限流操作
     */
   /* public abstract Object advancedInSemaphore(ProceedingJoinPoint point,
                                               SemaphoreCircuitBreaker semaphoreCircuitBreaker);*/

    /**
     *  熔断和限流操作
     */
    /*public abstract Object advancedInSemaphoreTimeout(ProceedingJoinPoint point,
                                                      SemaphoreTimeOutCircuitBreaker semaphoreTimeOutCircuitBreaker);*/

    /**
     * * {@link #doInvoke(ProceedingJoinPoint, Method, Object[], Long, Integer, String)}
     *
     * @param point   连接点
     * @param object  参数
     * @param timeout 超时时间
     * @return 返回结果集
     */
    protected Object doInvoke(ProceedingJoinPoint point, Method method,
                              Object[] object, long timeout, String fallbackMethod) {
        return doInvoke(point, method, object, timeout, null, fallbackMethod);
    }

    /**
     * * {@link #doInvoke(ProceedingJoinPoint, Method, Object[], Long, Integer, String)}
     *
     * @param point     连接点
     * @param object    参数
     * @param semaphore 限流数
     * @return 返回结果集
     */
    protected Object doInvoke(ProceedingJoinPoint point, Method method,
                              Object[] object, int semaphore, String fallbackMethod) {
        return doInvoke(point, method, object, null, semaphore, fallbackMethod);
    }

    /**
     * {@link #doInvoke(ProceedingJoinPoint, Method, Object[], Long, Integer, String)}
     *
     * @param point     连接点
     * @param object    参数
     * @param timeout   超时时间
     * @param semaphore 限流数
     * @return 返回结果集
     */
    protected Object doInvoke(ProceedingJoinPoint point, Method method,
                              Object[] object, Long timeout, Integer semaphore, String fallbackMethod) {
        //判断这个方法是否存在
        if (method == null || (timeout == null && semaphore == null)) {
            return returnObject() == null ? CircuitBreakerAspect.otherTips : returnObject();
        }
        //获取这个连接点的限流信息
        if (semaphore != null && timeout == null) {
            //返回值
            Object returnValue = null;
            Semaphore semaphoreMethod = geSemaphore(method.getDeclaringClass() + "." + method.getName(), semaphore);
            boolean acquire = false;
            try {
                if (acquire = semaphoreMethod.tryAcquire()) {
                    returnValue = point.proceed(object);
                } else {
                    //returnValue = returnObject() == null ? CircuitBreakerAspect.semaphoreTips : returnObject();
                    return fallbackMethod(fallbackMethod, method, object, CircuitBreakerAspect.semaphoreTips);
                }
                return returnValue;
            } catch (Throwable throwable) {
                //return returnObject() == null ? CircuitBreakerAspect.otherTips : returnObject();
                return fallbackMethod(fallbackMethod, method, object, CircuitBreakerAspect.otherTips);
            } finally {
                if (acquire) {
                    semaphoreMethod.release();
                }
            }
        } else if (semaphore == null && timeout != null) {
            return createResultVal(point, object, timeout, fallbackMethod, method);
        } else {
            //返回值
            Object returnVal = null;
            Semaphore semaphoreMethod = geSemaphore(method.getDeclaringClass() + "." + method.getName(), semaphore);
            boolean acquire = false;
            try {
                if (acquire = semaphoreMethod.tryAcquire()) {
                    returnVal = createResultVal(point, object, timeout, fallbackMethod, method);
                } else {
                    //returnVal = returnObject() == null ? CircuitBreakerAspect.semaphoreTips : returnObject();
                    return fallbackMethod(fallbackMethod, method, object, CircuitBreakerAspect.semaphoreTips);
                }
                return returnVal;
            } catch (Throwable throwable) {
                //return returnObject() == null ? CircuitBreakerAspect.otherTips : returnObject();
                return fallbackMethod(fallbackMethod, method, object, CircuitBreakerAspect.otherTips);
            } finally {
                if (acquire) {
                    semaphoreMethod.release();
                }
            }
        }
    }

    /**
     * 执行熔断操作
     *
     * @param point   连接点
     * @param object  参数
     * @param timeout 超时时间
     * @return 返回值
     */
    private Object createResultVal(ProceedingJoinPoint point,
                                   Object[] object, Long timeout, String fallbackMethod, Method method) {
        Object returnVal = null;
        //Future<Object> future = executorService.submit(() -> {
        Future<Object> future = ThreadUtil.execAsync(() -> {
            //返回值
            Object returnValue = null;
            try {
                //没有被取消就执行
                if(!Thread.currentThread().isInterrupted()) {
                    returnValue = point.proceed(object);
                }
            } catch (Throwable ex) {
            }
            return returnValue;
        });
        try {
            returnVal = future.get(timeout, TimeUnit.MILLISECONDS);
        } catch (TimeoutException | InterruptedException | ExecutionException e) {
            future.cancel(true); // 取消执行
            //returnVal = returnObject() == null ? CircuitBreakerAspect.timeOutTips : returnObject();
            returnVal = fallbackMethod(fallbackMethod, method, object, CircuitBreakerAspect.timeOutTips);
        }
        return returnVal;
    }

    /**
     * 获取信号量
     *
     * @param methodName 某个方法
     * @param semaphore  信号量的值
     * @return
     */
    private Semaphore geSemaphore(String methodName, int semaphore) {
        Semaphore semaphoreMethod = CircuitBreakerAspect.semaphoreMap.get(methodName);
        if (semaphoreMethod != null) {
            return semaphoreMethod;
        }
        synchronized (CircuitBreakerAspect.class) {
            semaphoreMethod = CircuitBreakerAspect.semaphoreMap.get(methodName);
            if (semaphoreMethod == null) {
                semaphoreMethod = new Semaphore(semaphore);
                CircuitBreakerAspect.semaphoreMap.put(methodName, semaphoreMethod);
            }
            return semaphoreMethod;
        }
    }

    /**
     * 执行失败的方法
     *
     * @param fallbackMethod 失败方法
     * @param objects        参数
     * @param retVal         不存在失败方法是调用
     * @return
     */
    private Object fallbackMethod(String fallbackMethod, Method targetMethod, Object[] objects, String retVal) {
        if (StringUtils.isBlank(fallbackMethod)) {
            if (isInvoked()){
                return invokeGlobalMethod(retVal);
            }
            return retVal;
        }
        if (StringUtils.contains(fallbackMethod, "#")) {
            final String[] fallbackClass = StringUtils.split(fallbackMethod, "#");
            if (fallbackClass.length == 2) {
                String className = fallbackClass[0];
                String methodName = fallbackClass[1];
                try {
                    final Class<?> targetClass = Class.forName(className);
                    for (Method method : targetClass.getDeclaredMethods()) {
                        method.setAccessible(true);
                        if (StringUtils.equals(method.getName(), methodName)) {
                            try {
                                return method.invoke(targetClass.newInstance(), objects);
                            } catch (IllegalAccessException | InvocationTargetException | InstantiationException e) {
                                if (isInvoked()){
                                    return invokeGlobalMethod(retVal);
                                }
                                return retVal;
                            }
                        }
                    }
                } catch (ClassNotFoundException e) {
                    if (isInvoked()){
                        return invokeGlobalMethod(retVal);
                    }
                    return retVal;
                }
            } else if (isInvoked()){
                return invokeGlobalMethod(retVal);
            }
            return retVal;
        } else {
            final Class<?> declaringClass = targetMethod.getDeclaringClass();
            for (Method method : declaringClass.getDeclaredMethods()) {
                method.setAccessible(true);
                if (StringUtils.equals(fallbackMethod, method.getName())) {
                    try {
                        return method.invoke(declaringClass.newInstance(), objects);
                    } catch (IllegalAccessException | InvocationTargetException | InstantiationException e) {
                        if (isInvoked()){
                            return invokeGlobalMethod(retVal);
                        }
                        return retVal;
                    }
                }
            }
            if (isInvoked()){
                return invokeGlobalMethod(retVal);
            }
            return retVal;
        }
    }

    /**
     * 执行全局的方法
     *
     * @param retVal 返回提示信息
     * @return
     */
    private Object invokeGlobalMethod(String retVal) {
        try {
            return CircuitBreakerAspect.globalTargetMethod.invoke(CircuitBreakerAspect.globalTargetMethodClass, null);
        } catch (IllegalAccessException | InvocationTargetException e) {
        }
        return retVal;
    }

    /**
     * 是否可以执行全局方法
     *
     * @return
     */
    private boolean isInvoked() {
        if(CircuitBreakerAspect.globalTargetMethodClass != null && CircuitBreakerAspect.globalTargetMethod != null) {
            return true;
        }
        return false;
    }

    /**
     * 定义返回值类型
     *
     * @return
     */
    protected T returnObject() {
        return this.t;
    }

    public static void shutDown() {
        executorService.shutdown();
        if (semaphoreMap != null) {
            semaphoreMap.clear();
        }
    }
}

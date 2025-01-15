package com.sky.aspect;

import com.sky.annotation.AutoFill;
import com.sky.constant.AutoFillConstant;
import com.sky.context.BaseContext;
import com.sky.enumeration.OperationType;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDateTime;

/**
 * 自定义切面，实现公共字段自动填充
 * 1 AOP切面的概念
 *   切面（Aspect）：切面是模块化的横切关注点（例如事务处理、日志记录等）。在 AOP 中，切面通常由切点和通知组成。
 *   切点（Pointcut）：切点是指程序中方法执行的某些位置或时机。它定义了哪些方法会被增强，即哪些方法会执行切面中的通知。
 *   通知（Advice）：通知是切面中的实际功能逻辑，定义了在切点位置执行的操作。通知有不同的执行时机（例如，方法执行之前、之后，或者抛出异常时）
 *   连接点（Joinpoint）：连接点是程序执行过程中的一个点，通常指方法执行的位置。每个方法执行都可能是一个连接点。切点是连接点的集合。
 * 2 切面何时会被执行？
 *   切面会在符合切点条件的连接点处被执行。切点表达式（Pointcut Expression）用于指定切面应用的时机（如方法执行时）。
 * 当应用程序的控制流程到达某个匹配的连接点时，通知将被触发。
 * 3 常见的通知类型
 *   前置通知（Before）；在目标方法执行之前执行。前置通知不能阻止目标方法的执行。
 *   后置通知（After）：在目标方法执行之后执行，无论目标方法是否抛出异常。
 *   返回通知（After Returning）：在目标方法成功执行并返回结果后执行。
 *   异常通知（After Throwing）：在目标方法抛出异常时执行。
 * 4 切点表达式
 *   切点表达式用来指定在哪些方法上应用通知。它可以包括以下内容
 *   方法签名：execution(* com.example.service.*.*(..)) 表示匹配 com.example.service 包中所有类的所有方法。
 *   方法参数：可以指定方法的参数类型。
 *   注解：可以根据方法是否使用某个注解来定义切点。
 * 示例切点表达式
 *   execution(public * com.example.service.UserService.*(..))：匹配 UserService 类中所有公共方法。
 *   execution(* com.example.service.*.save*(..))：匹配所有以 save 开头的方法。
 *   @annotation(com.example.annotations.Loggable)：匹配带有 @Loggable 注解的方法。
 */
@Aspect
@Component
@Slf4j
public class AutoFillAspect {
    /**
     * 切入点
     */
    @Pointcut("execution(* com.sky.mapper.*.*(..)) && @annotation(com.sky.annotation.AutoFill)")
    public void autoFillPointcut() {}

    @Before("autoFillPointcut()")
    public void autoFill(JoinPoint joinPoint){
        log.info("开始进行公共字段自动填充...");

        //获取到当前被拦截到的方法上的数据库操作类型
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature(); //方法签名对象
        AutoFill autoFill = methodSignature.getMethod().getAnnotation(AutoFill.class); //获得方法上的注解对象
        OperationType operationType = autoFill.value(); //获得数据库操作类型

        //获取到当前被拦截的方法的参数--实体对象
        Object[] args = joinPoint.getArgs();
        if (args == null && args.length == 0) {
            return;
        }

        Object entity = args[0];

        //准备赋值的数据
        LocalDateTime now = LocalDateTime.now();
        Long currentId = BaseContext.getCurrentId();

        //根据当前不同的操作类型，为对应的属性通过反射来赋值
        if(operationType == OperationType.INSERT) {
            //为四个公共字段赋值
            try {
                Method setCreateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_TIME, LocalDateTime.class);
                Method setCreateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_USER, Long.class);
                Method setUpdateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
                Method setUpdateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);

                //通过反射为对象属性赋值
                setCreateTime.invoke(entity, now);
                setCreateUser.invoke(entity, currentId);
                setUpdateTime.invoke(entity, now);
                setUpdateUser.invoke(entity, currentId);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }else if(operationType == OperationType.UPDATE) {
            //为两个公共字段赋值
            try {
                Method setUpdateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
                Method setUpdateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);

                //通过反射为对象属性赋值
                setUpdateTime.invoke(entity, now);
                setUpdateUser.invoke(entity, currentId);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}

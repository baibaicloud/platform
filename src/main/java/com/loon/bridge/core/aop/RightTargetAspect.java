package com.loon.bridge.core.aop;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.loon.bridge.core.comenum.UserType;
import com.loon.bridge.core.utils.RequestUtil;
import com.loon.bridge.core.web.Result;
import com.loon.bridge.service.enterprise.EnterpriseService;
import com.loon.bridge.service.user.UserService;
import com.loon.bridge.uda.entity.User;
import com.loon.bridge.web.security.SecurityUser;

@Aspect
@Order(1)
@Component
public class RightTargetAspect {

    private static Logger logger = LoggerFactory.getLogger(RightTargetAspect.class);

    @Autowired
    private UserService userService;

    @Autowired
    private EnterpriseService enterpriseService;

    @Around(value = "execution(* com.loon.bridge.controller.web..*(..)) && @annotation(com.loon.bridge.core.aop.RightTarget)")
    public Object check(final ProceedingJoinPoint joinPoint) throws Throwable {
        Method method = getMethod(joinPoint);

        try {
            SecurityUser securityUser = RequestUtil.getSecurityUser();
            User user = userService.getUserByUsername(securityUser.getUsername());
            if (user == null) {
                logger.debug("user auth info is null");
                return Result.Error("未授权，请先登录");
            }

            if (RequestUtil.getSecurityUser().getType() == UserType.SOLE) {
                return joinPoint.proceed();
            }

            Annotation p = method.getAnnotation(RightTarget.class);
            Method n = RightTarget.class.getDeclaredMethod("value");
            String right = (String) n.invoke(p);
            if (StringUtils.isEmpty(right)) {
                return joinPoint.proceed();
            }

            boolean pass = enterpriseService.hasRight(securityUser.getTargetId(), user, right);
            if (!pass) {
                return Result.Error("无权限操作，请先授权");
            }

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return Result.Error(e.getMessage());
        }

        return joinPoint.proceed();
    }
    
    private Method getMethod(JoinPoint joinPoint) {
        Method currentMethod = null;

        Signature sig = joinPoint.getSignature();
        if (sig instanceof MethodSignature) {
            try {
                MethodSignature msig = (MethodSignature) sig;
                Object target = joinPoint.getTarget();
                currentMethod = target.getClass().getMethod(msig.getName(), msig.getParameterTypes());
            } catch (Exception e) {
                logger.error("get method from JoinPoint failed, cause: {}", e.getMessage());
            }
        }

        return currentMethod;
    }

}

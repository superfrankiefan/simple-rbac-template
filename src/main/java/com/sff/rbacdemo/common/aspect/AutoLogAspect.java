//package com.sff.rbacdemo.common.aspect;
//
//import com.alibaba.fastjson.JSONObject;
//import com.alibaba.fastjson.serializer.PropertyFilter;
//import com.sff.rbacdemo.common.aspect.annotation.AutoLog;
//import com.sff.rbacdemo.common.enums.CommonConstant;
//import com.sff.rbacdemo.common.enums.ModuleType;
//import com.sff.rbacdemo.common.model.Result;
//import com.sff.rbacdemo.common.utils.IPUtils;
//import com.sff.rbacdemo.common.utils.ObjectConvertUtils;
//import com.sff.rbacdemo.common.utils.SpringContextUtils;
//import com.sff.rbacdemo.system_old.dto.LogDTO;
//import com.sff.rbacdemo.system_old.dto.LoginUserDTO;
//import com.sff.rbacdemo.system_old.service.BaseCommonService;
//import org.apache.shiro.SecurityUtils;
//import org.aspectj.lang.JoinPoint;
//import org.aspectj.lang.ProceedingJoinPoint;
//import org.aspectj.lang.annotation.Around;
//import org.aspectj.lang.annotation.Aspect;
//import org.aspectj.lang.annotation.Pointcut;
//import org.aspectj.lang.reflect.MethodSignature;
//import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
//import org.springframework.stereotype.Component;
//import org.springframework.validation.BindingResult;
//import org.springframework.web.multipart.MultipartFile;
//
//import javax.annotation.Resource;
//import javax.servlet.ServletRequest;
//import javax.servlet.ServletResponse;
//import javax.servlet.http.HttpServletRequest;
//import java.lang.reflect.Method;
//import java.util.Date;
//
//
///**
// * 系统日志，切面处理类
// */
//@Aspect
//@Component
//public class AutoLogAspect {
//
//    @Resource
//    private BaseCommonService baseCommonService;
//
//    @Pointcut("@annotation(org.ims.base.core.aspect.annotation.AutoLog)")
//    public void logPointCut() {
//
//    }
//
//    @Around("logPointCut()")
//    public Object around(ProceedingJoinPoint point) throws Throwable {
//        long beginTime = System.currentTimeMillis();
//        //执行方法
//        Object result = point.proceed();
//        //执行时长(毫秒)
//        long time = System.currentTimeMillis() - beginTime;
//
//        //保存日志
//        saveSysLog(point, time, result);
//
//        return result;
//    }
//
//    private void saveSysLog(ProceedingJoinPoint joinPoint, long time, Object obj) {
//        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
//        Method method = signature.getMethod();
//
//        LogDTO dto = new LogDTO();
//        AutoLog syslog = method.getAnnotation(AutoLog.class);
//        if (syslog != null) {
//            //update-begin-author:taoyan date:
//            String content = syslog.value();
//            if (syslog.module() == ModuleType.ONLINE) {
//                content = getOnlineLogContent(obj, content);
//            }
//            //注解上的描述,操作日志内容
//            dto.setLogType(syslog.logType());
//            dto.setLogContent(content);
//        }
//
//        //请求的方法名
//        String className = joinPoint.getTarget().getClass().getName();
//        String methodName = signature.getName();
//        dto.setMethod(className + "." + methodName + "()");
//
//
//        //设置操作类型
//        if (dto.getLogType() == CommonConstant.LOG_TYPE_2) {
//            dto.setOperateType(getOperateType(methodName, syslog.operateType()));
//        }
//
//        //获取request
//        HttpServletRequest request = SpringContextUtils.getHttpServletRequest();
//        //请求的参数
//        dto.setRequestParam(getReqestParams(request, joinPoint));
//        //设置IP地址
//        dto.setIp(IPUtils.getIpAddr(request));
//        //获取登录用户信息
//        LoginUserDTO sysUser = (LoginUserDTO) SecurityUtils.getSubject().getPrincipal();
//        if (sysUser != null) {
//            dto.setUserid(sysUser.getUsername());
//            dto.setUsername(sysUser.getRealname());
//
//        }
//        //耗时
//        dto.setCostTime(time);
//        dto.setCreateTime(new Date());
//        //保存系统日志
//        baseCommonService.addLog(dto);
//    }
//
//
//    /**
//     * 获取操作类型
//     */
//    private int getOperateType(String methodName, int operateType) {
//        if (operateType > 0) {
//            return operateType;
//        }
//        if (methodName.startsWith("list")) {
//            return CommonConstant.OPERATE_TYPE_1;
//        }
//        if (methodName.startsWith("add")) {
//            return CommonConstant.OPERATE_TYPE_2;
//        }
//        if (methodName.startsWith("edit")) {
//            return CommonConstant.OPERATE_TYPE_3;
//        }
//        if (methodName.startsWith("delete")) {
//            return CommonConstant.OPERATE_TYPE_4;
//        }
//        if (methodName.startsWith("import")) {
//            return CommonConstant.OPERATE_TYPE_5;
//        }
//        if (methodName.startsWith("export")) {
//            return CommonConstant.OPERATE_TYPE_6;
//        }
//        return CommonConstant.OPERATE_TYPE_1;
//    }
//
//    /**
//     * @param request:   request
//     * @param joinPoint: joinPoint
//     * @Description: 获取请求参数
//     * @Return: java.lang.String
//     */
//    private String getReqestParams(HttpServletRequest request, JoinPoint joinPoint) {
//        String httpMethod = request.getMethod();
//        String params = "";
//        if ("POST".equals(httpMethod) || "PUT".equals(httpMethod) || "PATCH".equals(httpMethod)) {
//            Object[] paramsArray = joinPoint.getArgs();
//            // java.lang.IllegalStateException: It is illegal to call this method if the current request is not in asynchronous mode (i.e. isAsyncStarted() returns false)
//            //  https://my.oschina.net/mengzhang6/blog/2395893
//            Object[] arguments = new Object[paramsArray.length];
//            for (int i = 0; i < paramsArray.length; i++) {
//                if (paramsArray[i] instanceof BindingResult || paramsArray[i] instanceof ServletRequest || paramsArray[i] instanceof ServletResponse || paramsArray[i] instanceof MultipartFile) {
//                    //ServletRequest不能序列化，从入参里排除，否则报异常：java.lang.IllegalStateException: It is illegal to call this method if the current request is not in asynchronous mode (i.e. isAsyncStarted() returns false)
//                    //ServletResponse不能序列化 从入参里排除，否则报异常：java.lang.IllegalStateException: getOutputStream() has already been called for this response
//                    continue;
//                }
//                arguments[i] = paramsArray[i];
//            }
//            //update-begin-author:taoyan date:20200724 for:日志数据太长的直接过滤掉
//            PropertyFilter profilter = new PropertyFilter() {
//                @Override
//                public boolean apply(Object o, String name, Object value) {
//                    if (value != null && value.toString().length() > 500) {
//                        return false;
//                    }
//                    return true;
//                }
//            };
//            params = JSONObject.toJSONString(arguments, profilter);
//            //update-end-author:taoyan date:20200724 for:日志数据太长的直接过滤掉
//        } else {
//            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
//            Method method = signature.getMethod();
//            // 请求的方法参数值
//            Object[] args = joinPoint.getArgs();
//            // 请求的方法参数名称
//            LocalVariableTableParameterNameDiscoverer u = new LocalVariableTableParameterNameDiscoverer();
//            String[] paramNames = u.getParameterNames(method);
//            if (args != null && paramNames != null) {
//                for (int i = 0; i < args.length; i++) {
//                    params += "  " + paramNames[i] + ": " + args[i];
//                }
//            }
//        }
//        return params;
//    }
//
//    /**
//     * online日志内容拼接
//     *
//     * @param obj
//     * @param content
//     * @return
//     */
//    private String getOnlineLogContent(Object obj, String content) {
//        if (Result.class.isInstance(obj)) {
//            Result res = (Result) obj;
//            String msg = res.getMessage();
//            String tableName = res.getOnlTable();
//            if (ObjectConvertUtils.isNotEmpty(tableName)) {
//                content += ",表名:" + tableName;
//            }
//            if (res.isSuccess()) {
//                content += "," + (ObjectConvertUtils.isEmpty(msg) ? "操作成功" : msg);
//            } else {
//                content += "," + (ObjectConvertUtils.isEmpty(msg) ? "操作失败" : msg);
//            }
//        }
//        return content;
//    }
//}

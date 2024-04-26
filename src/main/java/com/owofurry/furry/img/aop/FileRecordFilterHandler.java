package com.owofurry.furry.img.aop;

import com.owofurry.furry.img.config.SystemConfiguration;
import com.owofurry.furry.img.vo.R;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.beans.PropertyDescriptor;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Aspect
@Component
@ConditionalOnProperty(prefix = "config.system", name = "show-no-check", havingValue = "false")
public class FileRecordFilterHandler {
    SystemConfiguration systemConfiguration;

    public FileRecordFilterHandler(SystemConfiguration systemConfiguration) {
        this.systemConfiguration = systemConfiguration;
        log.info("开启过滤：不显示未审核的图片");
    }

    @Pointcut("@annotation(com.owofurry.furry.img.aop.FileRecordFilter)")
    public void pointcut() {

    }

    @AfterReturning(pointcut = "pointcut()", returning = "r")
    public void afterReturning(R r) {
        // 过滤掉未审核的图片
        if (!systemConfiguration.isShowNoCheck()) {
            Object data = r.getData();
            if (data instanceof List<?> list) {
                // 判断是否为可过滤数据
                if (!list.isEmpty() && hasReadCheckMethod(list.getFirst())) {
                    // 使用Stream流对数据进行过滤
                    List<?> d = list.stream().filter(e -> {
                        // 使用反射获得数据中  hasChecked 过滤掉为 false 的对象
                        try {
                            PropertyDescriptor descriptor = new PropertyDescriptor("hasChecked", e.getClass());
                            return (boolean) descriptor.getReadMethod().invoke(e);
                        } catch (Exception ex) {
                            throw new RuntimeException(ex);
                        }
                    }).toList();
                    r.setData(d);
                    return;
                }
            }
        }
        log.info("数据不匹配：{}", r);
    }

    /**
     * 通过反射判断是否存在指定字段
     *
     * @param o o
     * @return boolean
     */
    public boolean hasReadCheckMethod(Object o) {
        return Arrays.stream(o.getClass()
                        .getDeclaredFields())
                .anyMatch(e -> e.getName().contains("hasChecked"));
    }
}

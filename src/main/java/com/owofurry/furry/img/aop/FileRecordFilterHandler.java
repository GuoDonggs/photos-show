package com.owofurry.furry.img.aop;

import com.owofurry.furry.img.config.SystemConfiguration;
import com.owofurry.furry.img.entity.FileRecord;
import com.owofurry.furry.img.vo.R;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

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
        if (!systemConfiguration.isShowNoCheck()) {
            Object data = r.getData();
            assert data instanceof List<?>;
            assert ((List<?>) data).getFirst() instanceof FileRecord;
            List<FileRecord> d = ((List<?>) data).stream()
                    .map(o -> o instanceof FileRecord ? (FileRecord) o : null)
                    .filter(Objects::nonNull)
                    .filter(FileRecord::getHasChecked)
                    .toList();
            r.setData(d);
        }
    }
}

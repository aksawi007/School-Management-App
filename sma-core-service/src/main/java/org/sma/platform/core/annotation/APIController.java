package org.sma.platform.core.annotation;

import org.springframework.core.annotation.AliasFor;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@RestController
@ControllerAdvice
@RequestMapping
@CrossOrigin(origins = "*")
public @interface APIController {

    @AliasFor(annotation = RequestMapping.class, attribute = "value")
    String[] value() default {"${api.platform.app.apiconfig.path}"};
}

package com.mewna.yangmal;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author amy
 * @since 2/7/19.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Command {
    String[] names();
    
    String description();
    
    String[] usage() default {"no usage"};
    
    String[] examples() default {"no examples"};
}

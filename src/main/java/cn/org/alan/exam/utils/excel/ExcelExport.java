package cn.org.alan.exam.utils.excel;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ExcelExport {

    
    String value();

    
    int sort() default 0;

    
    String kv() default "";

    
    String example() default "";

}

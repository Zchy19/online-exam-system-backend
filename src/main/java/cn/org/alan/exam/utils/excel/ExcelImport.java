package cn.org.alan.exam.utils.excel;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ExcelImport {

    
    String value();

    
    String kv() default "";

    
    boolean required() default false;

    
    int maxLength() default 255;

    
    boolean unique() default false;

}

package me.codetalk.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by guobxu on 3/1/2018.
 */

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ActivityFeature {

    public static final int HIDEACTIONBAR = 1;
    public static final int HIDESTATUS = 2;
    public static final int HIDEBOTTOMNAV = 4;

    public int value();

    public boolean sticky() default true;

}

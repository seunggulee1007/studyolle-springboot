package com.studyolle.studyolle.commons;

import org.springframework.web.bind.annotation.RestController;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@RestController
@Target( { ElementType.TYPE} )
@Retention( RetentionPolicy.RUNTIME )
public @interface BaseAnnotation {

}

package me.myogoo.extendedterminal.api.annotation;

import me.myogoo.extendedterminal.api.integration.condition.AvaritiaNeoCondition;
import me.myogoo.myotus.api.annotation.MyoMod;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@MyoMod(value = "avaritia", alias = "avaritia-neo", versionRange = "[1.1.5,)", customCondition = AvaritiaNeoCondition.class)
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface AvaritiaNeo {}
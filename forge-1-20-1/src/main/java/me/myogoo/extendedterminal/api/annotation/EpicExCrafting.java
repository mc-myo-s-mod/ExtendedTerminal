package me.myogoo.extendedterminal.api.annotation;

import me.myogoo.extendedterminal.api.condition.EpicExCraftingCondition;
import me.myogoo.myotus.api.annotation.MyoMod;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@MyoMod(value = "extendedcrafting", alias = "epic-excrafting" , mode = MyoMod.IntegrationMode.EXTENDED, customCondition = EpicExCraftingCondition.class)
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface EpicExCrafting {}

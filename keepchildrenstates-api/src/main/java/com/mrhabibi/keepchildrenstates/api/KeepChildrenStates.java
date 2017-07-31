package com.mrhabibi.keepchildrenstates.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by mrhabibi on 3/27/17.
 * Android Annotation plugin that used for keeping children states of ViewGroup inside SparseArray
 * with no id collision (Android bug, it shouldn't collide each children actually)
 * It doesn't break with @InstanceState of official AA
 * Can only be attached in ViewGroup extension
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.TYPE)
public @interface KeepChildrenStates {
}
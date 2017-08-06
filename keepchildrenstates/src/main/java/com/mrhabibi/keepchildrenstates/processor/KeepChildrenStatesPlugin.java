package com.bukalapak.android.keepchildrenstates.processor;

import com.bukalapak.android.keepchildrenstates.processor.handler.KeepChildrenStatesHandler;

import org.androidannotations.AndroidAnnotationsEnvironment;
import org.androidannotations.handler.AnnotationHandler;
import org.androidannotations.plugin.AndroidAnnotationsPlugin;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mrhabibi on 3/27/17.
 */

public class KeepChildrenStatesPlugin extends AndroidAnnotationsPlugin {
    @Override
    public String getName() {
        return "KeepChildrenStates";
    }

    @Override
    public List<AnnotationHandler<?>> getHandlers(AndroidAnnotationsEnvironment androidAnnotationEnv) {
        List<AnnotationHandler<?>> annotationHandlers = new ArrayList<>();
        annotationHandlers.add(new KeepChildrenStatesHandler(androidAnnotationEnv));
        return annotationHandlers;
    }
}

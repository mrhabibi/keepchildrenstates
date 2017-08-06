package com.bukalapak.android.keepchildrenstates.processor.handler;

import com.bukalapak.android.keepchildrenstates.api.KeepChildrenStates;
import com.helger.jcodemodel.AbstractJClass;
import com.helger.jcodemodel.AbstractJType;
import com.helger.jcodemodel.JBlock;
import com.helger.jcodemodel.JDefinedClass;
import com.helger.jcodemodel.JExpr;
import com.helger.jcodemodel.JFieldVar;
import com.helger.jcodemodel.JForLoop;
import com.helger.jcodemodel.JMethod;
import com.helger.jcodemodel.JMod;
import com.helger.jcodemodel.JTypeVar;
import com.helger.jcodemodel.JVar;

import org.androidannotations.AndroidAnnotationsEnvironment;
import org.androidannotations.ElementValidation;
import org.androidannotations.handler.BaseAnnotationHandler;
import org.androidannotations.helper.BundleHelper;
import org.androidannotations.holder.EComponentHolder;
import org.androidannotations.holder.FoundViewHolder;

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

import static com.helger.jcodemodel.JExpr._new;
import static com.helger.jcodemodel.JExpr._super;
import static com.helger.jcodemodel.JExpr._this;
import static com.helger.jcodemodel.JExpr.cast;
import static com.helger.jcodemodel.JExpr.invoke;
import static org.androidannotations.helper.ModelConstants.generationSuffix;

/**
 * Created by mrhabibi on 3/27/17.
 */

public class KeepChildrenStatesHandler extends BaseAnnotationHandler<EComponentHolder> {

    private final static String SPARSE_ARRAY_CLASS_NAME = "android.util.SparseArray";

    private JDefinedClass generatedClass;
    private JVar childrenStateKey;
    private JVar instanceStateKey;

    public KeepChildrenStatesHandler(AndroidAnnotationsEnvironment environment) {
        super(KeepChildrenStates.class, environment);
    }

    @Override
    protected void validate(Element element, ElementValidation validation) {
        validatorHelper.extendsViewGroup(element, validation);
    }

    @Override
    public void process(Element element, EComponentHolder holder) throws Exception {
        generatedClass = holder.getGeneratedClass();

        /*
        public final static String CHILDREN_STATE_KEY = "childrenState";
        */
        childrenStateKey = generatedClass.field(JMod.PUBLIC | JMod.FINAL | JMod.STATIC, getClasses().STRING, "CHILDREN_STATE_KEY")
                .init(JExpr.lit("childrenState"));

        if (!generatedClass.containsField("INSTANCE_STATE_KEY")) {
            /*
            public final static String INSTANCE_STATE_KEY = "instanceState";
            */
            instanceStateKey = generatedClass.field(JMod.PUBLIC | JMod.FINAL | JMod.STATIC, getClasses().STRING, "INSTANCE_STATE_KEY")
                    .init(JExpr.lit("instanceState"));
        }

        setOnSaveInstanceState();
        setOnRestoreInstanceState();

        // Used to remind Android to let us handle keeping children instance state manually
        setDispatchSaveInstanceState();

        // Used to remind Android to let us handle restoring children instance state manually
        setDispatchRestoreInstanceState();
    }

    private void setOnSaveInstanceState() throws Exception {

        // Get method saveInstanceState generated by AA
        JMethod method = generatedClass.getMethod("saveInstanceState", new AbstractJType[]{getClasses().BUNDLE});

        // If there is no method generated then replicate it
        if (method == null) {

            /*
            @Override
            public Parcelable onSaveInstanceState() {
                Parcelable instanceState = super.onSaveInstanceState();
                Bundle bundle_ = new Bundle();
                bundle_.putParcelable(INSTANCE_STATE_KEY, instanceState);
                saveInstanceState(bundle_);
                return bundle_;
            }
            */
            JMethod onSaveMethod = generatedClass.method(JMod.PUBLIC, getClasses().PARCELABLE, "onSaveInstanceState");
            onSaveMethod.annotate(Override.class);
            JBlock onSaveBody = onSaveMethod.body();
            JVar instanceState = onSaveBody.decl(getClasses().PARCELABLE, "instanceState")
                    .init(invoke(_super(), onSaveMethod));
            JVar bundle_ = onSaveBody.decl(getClasses().BUNDLE, "bundle" + generationSuffix())
                    .init(_new(getClasses().BUNDLE));
            onSaveBody.invoke(bundle_, "putParcelable")
                    .arg(instanceStateKey)
                    .arg(instanceState);

            method = generatedClass.method(JMod.PRIVATE, getCodeModel().VOID, "saveInstanceState");
            method.param(getClasses().BUNDLE, "bundle");

            onSaveBody.invoke(method)
                    .arg(bundle_);
            onSaveBody._return(bundle_);
        }

        // Append the method with what we need to keep children states

        /*
        private void saveInstanceState(Bundle bundle) {
            ...
            SparseArray<Parcelable> childrenStates = new SparseArray<>();
            for (int index = 0; (index<getChildCount()); index ++) {
                getChildAt(index).saveHierarchyState(childrenStates);
            }
            bundle.putSparseParcelableArray(CHILDREN_STATE_KEY, childrenStates);
        }
        */
        JVar bundle = method.paramAtIndex(0);
        JVar childrenStates = method.body()
                .decl(getJClass(SPARSE_ARRAY_CLASS_NAME).narrow(getClasses().PARCELABLE), "childrenStates")
                .init(_new(getJClass(SPARSE_ARRAY_CLASS_NAME).narrowEmpty()));
        JForLoop loop = method.body()._for();
        JVar index = loop.init(getCodeModel().INT, "index", JExpr.lit(0));
        loop.test(index.lt(invoke("getChildCount")));
        loop.update(index.incr());
        loop.body().invoke(invoke("getChildAt")
                .arg(index), "saveHierarchyState")
                .arg(childrenStates);
        method.body().invoke(bundle, "putSparseParcelableArray")
                .arg(childrenStateKey)
                .arg(childrenStates);
    }

    private void setOnRestoreInstanceState() throws Exception {

        // Get method onRestoreInstanceState generated by AA
        JMethod method = generatedClass.getMethod("onRestoreInstanceState", new AbstractJType[]{getClasses().PARCELABLE});

        JBlock methodBodyOfAA = null;

        // If there is method generated
        if (method != null) {

            // Keep the body of generated block
            methodBodyOfAA = method.body()
                    .bracesRequired(false)
                    .indentRequired(false);

            // Remove existing generated method
            generatedClass.methods().remove(method);
        }

        // Always make our own replicated method by AA
        method = generatedClass.method(JMod.PUBLIC, getCodeModel().VOID, "onRestoreInstanceState");
        JVar state = method.param(getClasses().PARCELABLE, "state");
        method.annotate(Override.class);

        // Append the method with what we need to keep children states

        /*
        @Override
        public void onRestoreInstanceState(Parcelable state) {
            SparseArray<Parcelable> childrenStates = ((Bundle) state).getSparseParcelableArray(CHILDREN_STATE_KEY);
            for (int index = 0; (index<getChildCount()); index ++) {
                getChildAt(index).restoreHierarchyState(childrenStates);
            }
        }
        */
        JVar childrenStates = method.body()
                .decl(getJClass(SPARSE_ARRAY_CLASS_NAME).narrow(getClasses().PARCELABLE), "childrenStates")
                .init(invoke(cast(getClasses().BUNDLE, state), "getSparseParcelableArray").arg(childrenStateKey));
        JForLoop loop = method.body()._for();
        JVar index = loop.init(getCodeModel().INT, "index", JExpr.lit(0));
        loop.test(index.lt(invoke("getChildCount")));
        loop.update(index.incr());
        loop.body().invoke(invoke("getChildAt")
                .arg(index), "restoreHierarchyState")
                .arg(childrenStates);

        // If there is keeped body of generated block
        if (methodBodyOfAA != null) {

            // Append the body below our own method (because @InstanceState conflicts with @KeepChildrenStates)
            method.body().add(methodBodyOfAA);
        } else {

            // Append replicated AA method if it's not exist

            /*
            @Override
            public void onRestoreInstanceState(Parcelable state) {
                ...
                Bundle bundle_ = ((Bundle) state);
                Parcelable instanceState = bundle_.getParcelable(INSTANCE_STATE_KEY);
                super.onRestoreInstanceState(instanceState);
            }
            */

            JVar bundle_ = method.body().decl(getClasses().BUNDLE, "bundle" + generationSuffix())
                    .init(cast(getClasses().BUNDLE, state));
            JVar instanceState = method.body().decl(getClasses().PARCELABLE, "instanceState")
                    .init(invoke(bundle_, "getParcelable").arg(instanceStateKey));
            method.body().invoke(_super(), method)
                    .arg(instanceState);
        }
    }

    private void setDispatchSaveInstanceState() throws Exception {
        /*
        @Override
        protected void dispatchSaveInstanceState (SparseArray < Parcelable > container) {
            dispatchFreezeSelfOnly(container);
        }
        */
        JMethod method = generatedClass.method(JMod.PROTECTED, getCodeModel().VOID, "dispatchSaveInstanceState");
        JVar container = method.param(getJClass(SPARSE_ARRAY_CLASS_NAME).narrow(getClasses().PARCELABLE), "container");
        method.annotate(Override.class);
        method.body().invoke("dispatchFreezeSelfOnly")
                .arg(container);
    }

    private void setDispatchRestoreInstanceState() throws Exception {
        /*
        @Override
        protected void dispatchRestoreInstanceState (SparseArray < Parcelable > container) {
            dispatchThawSelfOnly(container);
        }
        */
        JMethod method = generatedClass.method(JMod.PROTECTED, getCodeModel().VOID, "dispatchRestoreInstanceState");
        JVar container = method.param(getJClass(SPARSE_ARRAY_CLASS_NAME).narrow(getClasses().PARCELABLE), "container");
        method.annotate(Override.class);
        method.body().invoke("dispatchThawSelfOnly")
                .arg(container);
    }
}
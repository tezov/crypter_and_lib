//package com.tezov.lib.ui.form.component.plain;
//
//import com.tezov.lib.toolbox.debug.DebugLog;
//import com.tezov.lib.toolbox.debug.DebugTrack;
//import com.tezov.lib.type.primitive.IntTo;
//import com.tezov.lib.toolbox.CompareType;
//import com.tezov.lib.type.primitive.ObjectTo;
//import com.tezov.lib.util.UtilsString;
//import com.tezov.lib.toolbox.Clock;
//import com.tezov.lib.database.sqlLite.filter.dbFilterOrder;
//import com.tezov.lib.database.sqlLite.filter.chunk.ChunkCommand;
//import java.util.List;
//import java.util.LinkedList;
//import java.util.Set;
//import com.tezov.lib.type.unit.UnitByte;
//i
//import android.content.AppContext;
//import android.util.AttributeSet;
//import android.widget.SeekBar;
//
//import com.tezov.lib.definition.defEntry;
//import com.tezov.lib.definition.defValidable;
//import com.tezov.lib.toolbox.debug.DebugException;
//import com.tezov.lib.type.ref.Ref;
//import com.tezov.lib.type.ref.SR;
//import com.tezov.lib.type.ref.SRwO;
//import com.tezov.lib.type.ref.WR;
//import com.tezov.lib.type.anonymous.OnSeekBarChangeListenerW;
//import com.tezov.lib.ui.component.plain.SeekBarNorm;
//import com.tezov.lib.ui.form.validator.Validator;
//import com.tezov.lib.ui.misc.AttributeReader;
//import com.tezov.lib.util.UtilsTypeWrapper;
//
//public class FormSeekBar extends SeekBarNorm implements defFormComponent<Float>, defValidable{
//private final SRwO<Float> progress = new SRwO<>();
//private Integer componentType = null;
//private defFormComponent previousForm = null;
//private defFormComponent nextForm = null;
//private Ref<defEntry<?>> entryWR = null;
//private AttributeReader attributes = null;
//
//public FormSeekBar(AppContext context){
//    super(context);
//    init(context, null, 0);
//}
//
//public FormSeekBar(AppContext context, AttributeSet attrs){
//    super(context, attrs);
//    init(context, attrs, 0);
//}
//
//public FormSeekBar(AppContext context, AttributeSet attrs, int defStyleAttr){
//    super(context, attrs, defStyleAttr);
//    init(context, attrs, 0);
//}
//
//@Override
//public AttributeReader getAttribute(Class type){
//    if(type == defFormComponent.class){
//        return attributes;
//    }
//    return null;
//}
//
//@Override
//public void setAttribute(Class type, AttributeReader attributes){
//    if(type == defFormComponent.class){
//        this.attributes = attributes;
//    }
//}
//
//private void init(AppContext context, AttributeSet attrs, int defStyleAttr){
//    observeChange();
//    if(attrs == null){
//        return;
//    }
//    defFormComponent.super.parseAttribute(context, defFormComponent.class, attrs);
//}
//
//protected void observeChange(){
//    setOnSeekBarChangeListener(new OnSeekBarChangeListenerW(){
//        @Override
//        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser){
//            if(fromUser){
//                setValue(Float.class, getValue(), false);
//            }
//        }
//    });
//}
//
//@Override
//public void attach(defEntry<?> entry){
//    setEntry(entry, true);
//}
//@Override
//public void link(defEntry<?> entry){
//    setEntry(entry, false);
//}
//private void setEntry(defEntry<?> entry, boolean weak){
//    if(entry != null){
//        if(weak){
//            entryWR = new WR<>(entry);
//        } else {
//            entryWR = new SR<>(entry);
//        }
//        updateProgress(entry.getValue(getEntryType()), true);
//    } else {
//        entryWR = null;
//    }
//}
//
//@Override
//public defEntry<?> getEntry(){
//    if(Ref.isNull(entryWR)){
//        return null;
//    }
//    return entryWR.get();
//}
//
//@Override
//public Integer getComponentType(){
//    return componentType;
//}
//
//@Override
//public void setComponentType(Integer type){
//    this.componentType = type;
//}
//
//@Override
//public boolean hasPrevious(){
//    return previousForm != null;
//}
//
//@Override
//public <F extends defFormComponent<?>> F getPrevious(){
//    return (F)previousForm;
//}
//
//@Override
//public void setPreviousForm(defFormComponent<?> form){
//    previousForm = form;
//    onSetPrevious(form);
//}
//
//private void onSetPrevious(defFormComponent<?> form){
//    if(form == null){
//        setNextFocusUpId(0);
//        setNextFocusLeftId(0);
//    } else {
//        int id = hasPrevious() ? getPrevious().getId() : 0;
//        setNextFocusUpId(id);
//        setNextFocusLeftId(id);
//    }
//}
//
//@Override
//public boolean hasNext(){
//    return nextForm != null;
//}
//
//@Override
//public <F extends defFormComponent<?>> F getNext(){
//    return (F)nextForm;
//}
//
//@Override
//public void setNextForm(defFormComponent<?> form){
//    nextForm = form;
//    onSetNext(form);
//}
//
//private void onSetNext(defFormComponent<?> form){
//    if(form == null){
//        setNextFocusDownId(0);
//        setNextFocusRightId(0);
//        setNextFocusForwardId(0);
//    } else {
//        int id = hasNext() ? getNext().getId() : 0;
//        setNextFocusDownId(id);
//        setNextFocusRightId(id);
//        setNextFocusForwardId(id);
//    }
//}
//
//@Override
//public <String, K, V extends Validator<String, K>> V getValidator(){
//    return null;
//}
//@Override
//public <S, K> defValidable setValidator(Validator<S, K> validator){
//DebugException.start().notImplemented().end();
//    return null;
//}
//
//@Override
//public Boolean isValid(){
//    return true;
//}
//
//@Override
//public Boolean hasChanged(){
//
//DebugException.start().notImplemented().end();
//
//    return null;
//}
//
//@Override
//public Boolean hasChangedFromInitial(){
//    return progress.hasChangedFromInitial();
//}
//
//@Override
//public boolean hasValidator(){
//    return false;
//}
//
//@Override
//public void showError(){
//
//}
//
//private void updateProgress(float v, boolean updateSeekBar){
//    progress.set(v);
//    if(progress.hasChanged()){
//        if(updateSeekBar){
//            setValue(v);
//        }
//        progress.checked();
//    }
//}
//
//@Override
//public Class<Float> getEntryType(){
//    return Float.class;
//}
//
//@Override
//public <T> boolean isAcceptedType(Class<T> type){
//    boolean result = (type == getEntryType());
//    if(Ref.isNotNull(entryWR)){
//        result = result || getEntry().isAcceptedType(type);
//    }
//    return result;
//}
//
//@Override
//public <T> boolean setValue(Class<T> type, T object){
//    return setValue(type, object, true);
//}
//
//private <T> boolean setValue(Class<T> type, T object, boolean updateSeekBar){
//    if(Ref.isNotNull(entryWR)){
//        defEntry<?> entry = entryWR.get();
//        if(UtilsTypeWrapper.isAcceptEntryType(entry, type)){
//            UtilsTypeWrapper.setTo(entry, type, object);
//            updateProgress(entry.getValue(getEntryType()), updateSeekBar);
//            entry.onSetValue(type);
//            return true;
//        }
//    }
//    if(type == Float.class){
//        updateProgress((Float)object, updateSeekBar);
//        return true;
//    } else {
//        DebugException.start().unknown("type", type.getName()).end();
//        return false;
//    }
//
//
//}
//
//@Override
//public Float getValue(){
//    return progress.get();
//}
//
//@Override
//public <T> T getValue(Class<T> type){
//    if(Ref.isNotNull(entryWR)){
//        defEntry entry = entryWR.get();
//        if(UtilsTypeWrapper.isAcceptEntryType(entry, type)){
//            return UtilsTypeWrapper.getFrom(entry, type);
//        }
//    }
//    if(type == Float.class){
//        return (T)this.getValue();
//    } else {
//
//DebugException.start().unknown("type", type.getName()).end();
//
//        return null;
//    }
//}
//
//@Override
//public boolean setValueFrom(defEntry<?>  entry){
//    Class type = entry.getEntryType();
//    Object value = entry.getValue(type);
//    return UtilsTypeWrapper.setTo(this, type, value);
//}
//@Override
//public boolean setValue(Float value){
//    setValue(Float.class, value);
//    return true;
//}
//
//}

/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java_android.ui.dialog.dialogPicker.picker;

import com.tezov.lib_java.debug.DebugTrack;
import com.tezov.lib_java.type.primitive.ObjectTo;
import com.tezov.lib_java.type.primitive.IntTo;
import com.tezov.lib_java.type.unit.UnitByte;
import com.tezov.lib_java.toolbox.CompareType;
import com.tezov.lib_java.toolbox.Clock;
import com.tezov.lib_java.util.UtilsString;
import java.util.List;
import java.util.LinkedList;
import java.util.Set;
import com.tezov.lib_java_android.database.sqlLite.filter.dbFilterOrder;
import com.tezov.lib_java_android.database.sqlLite.filter.chunk.ChunkCommand;
import androidx.fragment.app.Fragment;
import static com.tezov.lib_java.type.defEnum.Event.ON_KEYBOARD_DONE;
import static com.tezov.lib_java_android.ui.recycler.RecyclerList.PositionSnap.CENTER;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;
import androidx.core.graphics.drawable.DrawableCompat;

import com.tezov.lib_java.async.Handler;
import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java.debug.DebugLog;
import com.tezov.lib_java.debug.DebugString;
import com.tezov.lib_java.toolbox.Nullify;
import com.tezov.lib_java.type.primitive.string.StringTransformer;
import com.tezov.lib_java.type.runnable.RunnableSubscription;
import com.tezov.lib_java.type.runnable.RunnableW;
import com.tezov.lib_java_android.R;
import com.tezov.lib_java_android.application.AppContext;
import com.tezov.lib_java_android.application.AppKeyboard;
import com.tezov.lib_java_android.toolbox.PostToHandler;
import com.tezov.lib_java_android.type.android.ViewTreeEvent;
import com.tezov.lib_java_android.ui.component.plain.EditTextWithIconAction;
import com.tezov.lib_java_android.ui.dialog.dialogPicker.picker.base.DialogPickerBase;
import com.tezov.lib_java_android.ui.form.adapter.FormAdapter;
import com.tezov.lib_java_android.ui.form.adapter.FormManager;
import com.tezov.lib_java_android.ui.recycler.RecyclerList;
import com.tezov.lib_java_android.ui.recycler.RecyclerListItemAnimator;
import com.tezov.lib_java_android.ui.recycler.RecyclerListLayoutLinearVertical;
import com.tezov.lib_java_android.ui.recycler.RecyclerListRowManager;
import com.tezov.lib_java_android.wrapperAnonymous.EditTextOnTextChangedListenerW;
import com.tezov.lib_java_android.wrapperAnonymous.TextViewOnEditorActionListenerW;
import com.tezov.lib_java_android.wrapperAnonymous.ViewOnClickListenerW;

public class DialogPickerStringUnit<UNIT> extends DialogPickerBase<DialogPickerStringUnit.Data<UNIT>>{
private static final Integer ROW_SIZE_DEFAULT = 1;
private static final int ROW_FIRST_POSITION_DEFAULT = 0;
private RecyclerList recycler;
private EditTextWithIconAction editText = null;
private RunnableSubscription subscription = null;

@Override
protected State<UNIT> newState(){
    return new State();
}

@Override
public State<UNIT> getState(){
    return (State)super.getState();
}

@Override
public State<UNIT> obtainState(){
    return (State)super.obtainState();
}

@Override
public Param<UNIT> getParam(){
    return (Param)super.getParam();
}

@Override
public Param<UNIT> obtainParam(){
    return (Param)super.obtainParam();
}

@Override
public Class<Data<UNIT>> getEntryType(){
    return (Class)Data.class;
}

@Nullable
@Override
public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
    View view = super.onCreateView(inflater, container, savedInstanceState);
    getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    return view;
}

@Override
public void onPrepare(boolean hasBeenReconstructed){
    super.onPrepare(hasBeenReconstructed);
    View contentView = getContentView();
    recycler = contentView.findViewById(R.id.recycler);
    recycler.setLayoutManager(new RecyclerListLayoutLinearVertical(ROW_SIZE_DEFAULT));
    recycler.setHasFixedSize(true);
    recycler.enableCenterSnap();
    State state = getState();
    if(!state.hasRecyclerListState()){

DebugException.start().log("recyclerListState is null, You surely forgot to set the RowManager").end();

    } else {
        recycler.restoreState(state.recyclerListState);
    }
    Drawable decoration = getParam().recyclerDecorationDrawable;
    if(decoration == null){
        getParam().setRecyclerDefaultDecorationDrawable();
        decoration = getParam().recyclerDecorationDrawable;
    }

    editText = contentView.findViewById(R.id.edit_text);
    editText.addEditorActionListener(new TextViewOnEditorActionListenerW(){
        @Override
        public boolean onAction(EditText textView, int actionId, KeyEvent event){
            int result = actionId & EditorInfo.IME_MASK_ACTION;
            switch(result){
                case EditorInfo.IME_ACTION_DONE:
                    post(ON_KEYBOARD_DONE, EditorInfo.IME_ACTION_DONE);
                    break;
                case EditorInfo.IME_ACTION_NEXT:
                    post(ON_KEYBOARD_DONE, EditorInfo.IME_ACTION_NEXT);
                    break;
            }
            return false;
        }
    });
    EditTextOnTextChangedListenerW textWatcher = getParam().textWatcher;
    if(textWatcher != null){
        editText.addTextChangedListener(textWatcher);
    }

    FrameLayout btnUpView = contentView.findViewById(R.id.btn_up);
    btnUpView.setBackground(decoration);
    btnUpView.setOnClickListener(new ViewOnClickListenerW(){
        @Override
        public void onClicked(View v){
            unitUp();
        }
    });
    View btnDownView = contentView.findViewById(R.id.btn_down);
    btnDownView.setBackground(decoration);
    btnDownView.setOnClickListener(new ViewOnClickListenerW(){
        @Override
        public void onClicked(View v){
            unitDown();
        }
    });
    if(hasBeenReconstructed){
        editText.onRestoreInstanceState(getState().editTextSavedInstance);
        getState().editTextSavedInstance = null;
        updateKeyBoard();
    }
}

@Override
public void onOpen(boolean hasBeenReconstructed, boolean hasBeenRestarted){
    super.onOpen(hasBeenReconstructed, hasBeenRestarted);
    updateKeyBoard();
    if(!hasBeenReconstructed){
        showKeyBoard();
    }
}

private void updateKeyBoard(){
    editText.setImeOptions(getParam().actionButtonType);
    editText.setInputType(getParam().inputType);
    editText.requestFocus();
}

private void showKeyBoard(){
    if(subscription != null){
        subscription.unsubscribe();
    }
    subscription = new RunnableSubscription(){
        @Override
        public void onComplete(){
            unsubscribe();
            subscription = null;
            PostToHandler.of(editText, Handler.Delay.SHORT.millisecond(), new RunnableW(){
                @Override
                public void runSafe(){
                    editText.requestFocus();
                    AppKeyboard.show(editText);
                }
            });
        }
    };
    ViewTreeEvent.onLayout(editText, subscription);
}

@Override
public void onSaveInstanceState(Bundle savedInstanceState){
    super.onSaveInstanceState(savedInstanceState);
    getState().editTextSavedInstance = editText.onSaveInstanceState();
}

private void unitUp(){
    Integer position = getUnitPosition();
    int maxPosition = getParam().getUnitRowManager().getDataManager().size() - 1;
    if(position == null){
        position = maxPosition;
    } else if(position >= maxPosition){
        position = ROW_FIRST_POSITION_DEFAULT;
    } else {
        position++;
    }
    setUnitPosition(position);
}

private void unitDown(){
    Integer position = getUnitPosition();
    if(position == null){
        position = ROW_FIRST_POSITION_DEFAULT;
    } else {
        if(position <= 0){
            position = getParam().getUnitRowManager().getDataManager().size() - 1;
        } else {
            position--;
        }
    }
    setUnitPosition(position);
}

public Integer getUnitPosition(){
    return recycler.getPosition(CENTER);
}

public void setUnitPosition(Integer position){
    if(position == null){
        position = ROW_FIRST_POSITION_DEFAULT;
    }
    recycler.scrollToPosition(CENTER, position);
}

public UNIT getUnit(){
    Integer index = getUnitPosition();
    if(index == null){
        return null;
    }
    return (UNIT)recycler.getRowManager().getDataManager().get(index);
}

public void setUnit(UNIT unit){
    Integer position;
    if(unit == null){
        position = null;
    } else {
        position = recycler.getRowManager().getDataManager().indexOf(unit);
    }
    setUnitPosition(position);
}

public String getData(){
    StringTransformer transformer = getParam().transformer;
    if(transformer == null){
        return Nullify.string(editText.getText());
    } else {
        return transformer.restore(Nullify.string(editText.getText()));
    }
}

public void setData(String s){
    StringTransformer transformer = getParam().transformer;
    if(transformer != null){
        s = transformer.alter(s);
    }
    editText.setText(s);
    if(s != null){
        editText.moveToEnd();
    }
}

@Override
public boolean setValue(Data<UNIT> data){
    if(data != null){
        setData(data.value);
        setUnit(data.unit);
    } else {
        setData(null);
        setUnit(null);
    }
    return true;
}

@Override
public Data<UNIT> getValue(){
    String data = getData();
    UNIT unit = getUnit();
    if(data != null){
        return new Data<>(data, unit);
    } else {
        return null;
    }
}

@Override
public <T> boolean setValue(Class<T> type, T object){
    if(type == getEntryType()){
        setValue((Data<UNIT>)object);
        return true;
    } else {
DebugException.start().unknown("type", type.getName()).end();
        return false;
    }
}

@Override
public <T> T getValue(Class<T> type){
    if(type == getEntryType()){
        return (T)getValue();
    } else {

DebugException.start().unknown("type", type.getName()).end();


        return null;
    }
}

@Override
public void onDestroy(){
    if(subscription != null){
        subscription.unsubscribe();
    }
    super.onDestroy();
}

public static class Data<UNIT>{
    String value;
    UNIT unit;

    public Data(String value, UNIT unit){
        this.value = value;
        this.unit = unit;
    }

    public String getValue(){
        return value;
    }

    public UNIT getUnit(){
        return unit;
    }

    public DebugString toDebugString(){
        DebugString data = new DebugString();
        data.append("value", value);
        data.append("unit", unit);
        return data;
    }
    public void toDebugLog(){
DebugLog.start().send(toDebugString()).end();
    }

}

public static class State<UNIT> extends DialogPickerBase.State{
    private RecyclerList.State recyclerListState = null;
    private Parcelable editTextSavedInstance = null;

    boolean hasRecyclerListState(){
        return recyclerListState != null;
    }

    RecyclerList.State getRecyclerListState(){
        return recyclerListState;
    }

    RecyclerList.State obtainRecyclerListState(){
        if(recyclerListState == null){
            recyclerListState = new RecyclerList.State();
        }
        return recyclerListState;
    }

    @Override
    protected Param<UNIT> newParam(){
        return new Param();
    }

    @Override
    public Param<UNIT> getParam(){
        return (Param)super.getParam();
    }

    @Override
    public Param<UNIT> obtainParam(){
        return (Param)super.obtainParam();
    }

}

public static class Param<UNIT> extends DialogPickerBase.Param{
    public int actionButtonType = EditorInfo.IME_ACTION_DONE;
    public int inputType = InputType.TYPE_CLASS_TEXT;
    public Drawable recyclerDecorationDrawable;
    public StringTransformer transformer = null;
    public EditTextOnTextChangedListenerW textWatcher = null;

    public Param(){
        setLayoutID(R.layout.dialog_picker_string_unit);
    }

    @Override
    public State getState(){
        return super.getState();
    }

    public StringTransformer getTransformer(){
        return transformer;
    }

    public Param<UNIT> setTransformer(StringTransformer transformer){
        this.transformer = transformer;
        return this;
    }

    public EditTextOnTextChangedListenerW getTextWatcher(){
        return textWatcher;
    }

    public Param<UNIT> setTextWatcher(EditTextOnTextChangedListenerW textWatcher){
        this.textWatcher = textWatcher;
        return this;
    }

    public int getActionButtonType(){
        return actionButtonType;
    }

    public Param<UNIT> setActionButtonType(int actionButtonType){
        this.actionButtonType = actionButtonType;
        return this;
    }

    public int getInputType(){
        return inputType;
    }

    public Param<UNIT> setInputType(int inputType){
        this.inputType = inputType;
        return this;
    }

    public Drawable getRecyclerDecorationDrawable(){
        return recyclerDecorationDrawable;
    }

    public Param<UNIT> setRecyclerDecorationDrawable(Drawable recyclerDecorationDrawable){
        this.recyclerDecorationDrawable = recyclerDecorationDrawable;
        return this;
    }

    public Param<UNIT> setRecyclerDefaultDecorationDrawable(){
        Drawable divider = AppContext.getResources().getDrawable(R.drawable.dialog_picker_arrow_line);
        Integer color = AppContext.getResources().resolveColorARGB(R.attr.colorAccent);
        if(color != null){
            DrawableCompat.wrap(divider).setTint(color);
        }
        this.recyclerDecorationDrawable = divider;
        return this;
    }

    public Class<UNIT> getUnitType(){
        RecyclerListRowManager<UNIT> rowManager = getUnitRowManager();
        if(rowManager == null){
            return null;
        }
        return rowManager.getDataManager().getType();
    }

    public <RCL extends RecyclerListRowManager<UNIT>> RCL getUnitRowManager(){
        if(!hasState()){
            return null;
        }
        if(!getState().hasRecyclerListState()){
            return null;
        }
        if(!getState().getRecyclerListState().hasParam()){
            return null;
        }
        return (RCL)getState().getRecyclerListState().getParam().getRowManager();
    }

    public Param<UNIT> setUnitRowManager(RecyclerListRowManager<UNIT> rowManager){
        getState().obtainRecyclerListState().obtainParam().setRowManager(rowManager);
        return this;
    }

    public <I extends RecyclerListItemAnimator> I getAnimator(){
        if(!hasState()){
            return null;
        }
        if(!getState().hasRecyclerListState()){
            return null;
        }
        if(!getState().getRecyclerListState().hasParam()){
            return null;
        }
        return getState().getRecyclerListState().getParam().getItemAnimator();
    }

    public Param<UNIT> setAnimator(RecyclerListItemAnimator animator){
        getState().obtainRecyclerListState().obtainParam().setItemAnimator(animator);
        return this;
    }

}

public abstract static class FormDataAdapter<UNIT> extends FormAdapter<Data<UNIT>>{
    public FormDataAdapter(FormManager.Target.Is target){
        super(target);
    }

    @Override
    public Class<Data<UNIT>> getEntryType(){
        return (Class)Data.class;
    }

    @Override
    public String valueToString(){
        return valueToString(getValue());
    }

    protected abstract String valueToString(Data<UNIT> data);

    protected abstract Data<UNIT> valueFromString(String s);

    @Override
    public <T> boolean isAcceptedType(Class<T> type){
        return (type == String.class) || (type == Data.class);
    }

    @Override
    public <T> boolean setValue(Class<T> type, T object){
        if(type == String.class){
            setValue(valueFromString((String)object));
            return true;
        } else if(type == Data.class){
            setValue((Data)object);
            return true;
        } else {
DebugException.start().unknown("type", type.getName()).end();
            return false;
        }


    }

    @Override
    public <T> T getValue(Class<T> type){
        if(type == String.class){
            return (T)valueToString();
        }
        if(type == Data.class){
            return (T)getValue();
        }

DebugException.start().unknown("type", type.getName()).end();

        return null;
    }

}

}
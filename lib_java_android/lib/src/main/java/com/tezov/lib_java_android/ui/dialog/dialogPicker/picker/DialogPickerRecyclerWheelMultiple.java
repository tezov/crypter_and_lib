/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java_android.ui.dialog.dialogPicker.picker;

import com.tezov.lib_java.debug.DebugLog;
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
import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static com.tezov.lib_java_android.ui.recycler.RecyclerList.PositionSnap.CENTER;

import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.tezov.lib_java_android.R;
import com.tezov.lib_java_android.application.AppContext;
import com.tezov.lib_java.application.AppUIDGenerator;
import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java.type.collection.ListEntry;
import com.tezov.lib_java.type.primaire.Entry;
import com.tezov.lib_java_android.ui.dialog.dialogPicker.picker.base.DialogPickerBase;
import com.tezov.lib_java_android.ui.form.adapter.FormAdapter;
import com.tezov.lib_java_android.ui.form.adapter.FormManager;
import com.tezov.lib_java_android.ui.recycler.RecyclerList;
import com.tezov.lib_java_android.ui.recycler.RecyclerListDataManager;
import com.tezov.lib_java_android.ui.recycler.RecyclerListLayoutLinearVertical;
import com.tezov.lib_java_android.ui.recycler.RecyclerListLinear;
import com.tezov.lib_java_android.ui.recycler.RecyclerListRowManager;
import com.tezov.lib_java_android.ui.recycler.prebuild.decoration.RecyclerTargetLineDecorationHorizontal;

public class DialogPickerRecyclerWheelMultiple<KEY> extends DialogPickerBase<ListEntry<KEY, Object>>{
final private static int LAYOUT_RECYCLER = AppUIDGenerator.nextInt();
final private static int LAYOUT_TEXTVIEW = AppUIDGenerator.nextInt();

private static final int ROW_SIZE_DEFAULT = 5;
private static final int ROW_FIRST_POSITION_DEFAULT = 0;

@Override
protected State<KEY> newState(){
    return new State();
}

@Override
public State<KEY> getState(){
    return (State<KEY>)super.getState();
}

@Override
public State<KEY> obtainState(){
    return (State)super.obtainState();
}

@Override
public Param<KEY> getParam(){
    return (Param<KEY>)super.getParam();
}

@Override
public Param<KEY> obtainParam(){
    return (Param<KEY>)super.obtainParam();
}

@Override
public Class<ListEntry<KEY, Object>> getEntryType(){
    return (Class)ListEntry.class;
}

@Override
public void onPrepare(boolean hasBeenReconstructed){
    super.onPrepare(hasBeenReconstructed);
    FrameLayout contentLayout = getContentView().findViewById(RES_VIEW_CONTENT);
    ViewGroup pickerView = contentLayout.findViewById(R.id.picker);
    for(Entry<KEY, Integer> e: getParam().layoutIds){
        View view = null;
        if(e.value == LAYOUT_RECYCLER){
            view = newRecycler(e.key, pickerView.getContext());
        } else {
            if(e.value == LAYOUT_TEXTVIEW){
                view = newTextView(e.key, pickerView.getContext());
            } else {
DebugException.start().unknown("type", e.value).end();
            }

        }
        view.setId(getParam().layoutIds.indexOfKey(e.key));
        pickerView.addView(view);
    }
}

private TextView newTextView(KEY key, android.content.Context context){
    TextView textView = new TextView(context, null, R.attr.layoutContentStyle);
    textView.setLayoutParams(new ViewGroup.LayoutParams(WRAP_CONTENT, MATCH_PARENT));
    textView.setGravity(Gravity.CENTER_VERTICAL);
    Integer decorationResourceId = getParam().recyclerDecorationDrawableResourceId;
    if(decorationResourceId != null){
        textView.setBackground(AppContext.getResources().getDrawable(decorationResourceId));
    }
    State<KEY> state = getState();
    if(!state.hasState(key)){

DebugException.start().log("textViewState is null, You surely forgot to set the RowManager").end();

    } else {
        textView.setText(state.getTextViewState(key));
    }
    return textView;
}

private RecyclerList newRecycler(KEY key, android.content.Context context){
    RecyclerListLinear recycler = new RecyclerListLinear(context, null, R.attr.layoutBodyStyle);
    recycler.setLayoutParams(new RecyclerList.LayoutParams(WRAP_CONTENT, MATCH_PARENT));
    recycler.setLayoutManager(new RecyclerListLayoutLinearVertical(getParam().rowSize));
    recycler.setHasFixedSize(true);
    recycler.enableCenterSnap();
    Integer decorationResourceId = getParam().recyclerDecorationDrawableResourceId;
    if(decorationResourceId != null){
        recycler.addItemDecoration(new RecyclerTargetLineDecorationHorizontal(decorationResourceId));
    }
    State<KEY> state = getState();
    if(!state.hasState(key)){

DebugException.start().log("recyclerListState is null, You surely forgot to set the RowManager").end();

    } else {
        recycler.restoreState(state.getRecyclerState(key));
    }
    return recycler;
}

private int getId(KEY key){
    return getParam().layoutIds.indexOfKey(key);
}

private RecyclerList getRecyclerView(int id){
    return getView().findViewById(id);
}

private Integer getPosition(int id){
    return getRecyclerView(id).getPosition(CENTER);
}

public Integer getPosition(KEY key){
    return getRecyclerView(getId(key)).getPosition(CENTER);
}

private void setPosition(int id, Integer position){
    if(position == null){
        position = ROW_FIRST_POSITION_DEFAULT;
    }
    getRecyclerView(id).scrollToPosition(CENTER, position);
}

public void setPosition(KEY key, Integer position){
    setPosition(getId(key), position);
}

private void setData(int id, Object data){
    Integer position;
    if(data == null){
        position = null;
    } else {
        RecyclerListDataManager dataManager = getRecyclerView(id).getRowManager().getDataManager();
        position = dataManager.indexOf(data);
    }
    setPosition(id, position);
}

public void setData(KEY key, Object data){
    setData(getId(key), data);
}

private <T> T getData(int id){
    Integer index = getPosition(id);
    if(index == null){
        return null;
    }
    RecyclerListDataManager dataManager = getRecyclerView(id).getRowManager().getDataManager();
    return (T)dataManager.get(index);
}

public <T> T getData(KEY key){
    return getData(getId(key));
}

private void setValue(int id, Object data){
    setData(id, data);
}

public void setValue(KEY key, Object data){
    setValue(getId(key), data);
}

@Override
public boolean setValue(ListEntry<KEY, Object> datas){
    if(datas != null){
        for(Entry<KEY, Object> e: datas){
            setValue(e.key, e.value);
        }
    } else {
        for(Entry<KEY, Integer> p: getParam().layoutIds){
            if(p.value == LAYOUT_RECYCLER){
                setValue(p.key, null);
            }
        }
    }
    return true;
}

@Override
public ListEntry<KEY, Object> getValue(){
    ListEntry<KEY, Object> datas = new ListEntry<>();
    for(Entry<KEY, Integer> p: getParam().layoutIds){
        if(p.value == LAYOUT_RECYCLER){
            datas.add(p.key, getData(p.key));
        }
    }
    return datas;
}

@Override
public <T> boolean setValue(Class<T> type, T object){
    if(type == getEntryType()){
        setValue((ListEntry<KEY, Object>)object);
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

public static class State<KEY> extends DialogPickerBase.State{
    private final ListEntry<KEY, Object> layoutState = new ListEntry<KEY, Object>();

    boolean hasState(KEY key){
        return layoutState.hasKey(key);
    }

    RecyclerList.State getRecyclerState(KEY key){
        return (RecyclerList.State)layoutState.getValue(key);
    }

    RecyclerList.State obtainRecyclerState(KEY key){
        RecyclerList.State state = (RecyclerList.State)layoutState.getValue(key);
        if(state == null){
            state = new RecyclerList.State();
            layoutState.add(key, state);
        }
        return state;
    }

    String getTextViewState(KEY key){
        return (String)layoutState.getValue(key);
    }

    void putTextViewState(KEY key, String s){
        layoutState.put(key, s);
    }

    @Override
    protected Param<KEY> newParam(){
        return new Param();
    }

    @Override
    public Param<KEY> getParam(){
        return (Param<KEY>)super.getParam();
    }

    @Override
    public Param<KEY> obtainParam(){
        return (Param<KEY>)super.obtainParam();
    }

}

public static class Param<KEY> extends DialogPickerBase.Param{
    public ListEntry<KEY, Integer> layoutIds = new ListEntry<KEY, Integer>();
    public Integer rowSize = ROW_SIZE_DEFAULT;
    public Integer recyclerDecorationDrawableResourceId = null;

    public Param(){
        setLayoutID(R.layout.dialog_picker_recycler_wheel_multiple);
    }

    @Override
    public State getState(){
        return super.getState();
    }

    public int getRowSize(){
        return rowSize;
    }

    public Param<KEY> setRowSize(Integer rowSize){
        this.rowSize = rowSize;
        return this;
    }

    public Integer getRecyclerDecorationDrawableResourceId(){
        return recyclerDecorationDrawableResourceId;
    }

    public Param<KEY> setRecyclerDecorationDrawableResourceId(Integer resourceId){
        this.recyclerDecorationDrawableResourceId = resourceId;
        return this;
    }

    public Param<KEY> setRecyclerDefaultDecorationDrawable(){
        this.recyclerDecorationDrawableResourceId = R.drawable.dialog_picker_target_line;
        return this;
    }

    public <RCL extends RecyclerListRowManager> RCL getRowManager(KEY key){
        if(!hasState()){
            return null;
        }
        RecyclerList.State state = getState().getRecyclerState(key);
        if(state == null){
            return null;
        }
        if(!state.hasParam()){
            return null;
        }
        return (RCL)state.getParam().getRowManager();
    }

    public <T> Param<KEY> setRowManager(KEY key, RecyclerListRowManager<T> rowManager){
        getState().obtainRecyclerState(key).obtainParam().setRowManager(rowManager);
        layoutIds.put(key, LAYOUT_RECYCLER);
        return this;
    }

    public Param<KEY> setSep(KEY key, String s){
        getState().putTextViewState(key, s);
        layoutIds.put(key, LAYOUT_TEXTVIEW);
        return this;
    }

}

public abstract static class FormDataAdapter<KEY> extends FormAdapter<ListEntry<KEY, Object>>{
    public FormDataAdapter(FormManager.Target.Is target){
        super(target);
    }

    @Override
    public Class<ListEntry<KEY, Object>> getEntryType(){
        return (Class)ListEntry.class;
    }

    @Override
    public String valueToString(){
        if(isNULL()){
            return null;
        }
        return valueToString(getValue());
    }

    protected abstract String valueToString(ListEntry<KEY, Object> datas);

    protected abstract ListEntry<KEY, Object> valueFromString(String s);

    @Override
    public <T> boolean isAcceptedType(Class<T> type){
        return (type == String.class) || (type == ListEntry.class);
    }

    @Override
    public <T> boolean setValue(Class<T> type, T object){
        if(type == String.class){
            setValue(valueFromString((String)object));
            return true;
        } else if(type == ListEntry.class){
            setValue((ListEntry)object);
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
        } else if(type == ListEntry.class){
            return (T)getValue();
        } else {

DebugException.start().unknown("type", type.getName()).end();

            return null;
        }
    }

}

}




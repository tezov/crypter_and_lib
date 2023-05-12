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
import static android.view.View.GONE;
import static com.tezov.lib_java.type.defEnum.Event.ON_CLICK_LONG;
import static com.tezov.lib_java.type.defEnum.Event.ON_CLICK_SHORT;
import static com.tezov.lib_java_android.ui.recycler.RecyclerList.PositionSnap.CENTER;

import android.os.Bundle;
import android.os.Parcelable;
import android.text.Editable;
import android.view.View;

import com.tezov.lib_java_android.R;
import com.tezov.lib_java.async.notifier.observer.event.ObserverEvent;
import com.tezov.lib_java.toolbox.Nullify;
import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java_android.wrapperAnonymous.EditTextOnTextChangedListenerW;
import com.tezov.lib_java.type.defEnum.Event;
import com.tezov.lib_java_android.ui.component.plain.EditText;
import com.tezov.lib_java_android.ui.component.plain.EditTextWithIconAction;
import com.tezov.lib_java_android.ui.dialog.dialogPicker.picker.base.DialogPickerBase;
import com.tezov.lib_java_android.ui.recycler.RecyclerList;
import com.tezov.lib_java_android.ui.recycler.RecyclerListDataManager;
import com.tezov.lib_java_android.ui.recycler.RecyclerListItemAnimator;
import com.tezov.lib_java_android.ui.recycler.RecyclerListLayoutLinearVertical;
import com.tezov.lib_java_android.ui.recycler.RecyclerListRowHolder;
import com.tezov.lib_java_android.ui.recycler.RecyclerListRowManager;
import com.tezov.lib_java_android.ui.recycler.prebuild.decoration.RecyclerTargetLineDecorationHorizontal;

//IMPROVE multi select
public class DialogPickerRecyclerWheel<TYPE> extends DialogPickerBase<TYPE>{
private static final Integer ROW_SIZE_DEFAULT = 5;
private static final int ROW_FIRST_POSITION_DEFAULT = 0;
private RecyclerList recycler;
private EditTextWithIconAction editTextFilter = null;

@Override
protected State newState(){
    return new State();
}

@Override
public State getState(){
    return (State)super.getState();
}

@Override
public State obtainState(){
    return (State)super.obtainState();
}

@Override
public Param<TYPE> getParam(){
    return (Param<TYPE>)super.getParam();
}

@Override
public Param<TYPE> obtainParam(){
    return (Param<TYPE>)super.obtainParam();
}

@Override
public Class<TYPE> getEntryType(){
    return getParam().getType();
}

@Override
public void onPrepare(boolean hasBeenReconstructed){
    super.onPrepare(hasBeenReconstructed);
    State state = getState();
    Param param = getParam();
    View contentView = getContentView();
    recycler = contentView.findViewById(R.id.recycler);
    recycler.setLayoutManager(new RecyclerListLayoutLinearVertical(param.rowSize));
    recycler.setHasFixedSize(true);
    recycler.enableCenterSnap();
    Integer decorationResourceId = param.recyclerDecorationDrawableResourceId;
    if(decorationResourceId != null){
        recycler.addItemDecoration(new RecyclerTargetLineDecorationHorizontal(decorationResourceId));
    }
    if(!state.hasRecyclerListState()){

DebugException.start().log("recyclerListState is null, You surely forgot to set the RowManager").end();

    } else {
        recycler.restoreState(state.recyclerListState);
    }
    RecyclerListDataManager<TYPE> dataManager = recycler.getRowManager().getDataManager();
    dataManager.observe(new ObserverEvent<Event.Is, TYPE>(this, ON_CLICK_SHORT){
        @Override
        public void onComplete(Event.Is event, TYPE data){
            RecyclerListRowManager<TYPE> rowManager = recycler.getRowManager();
            int index = rowManager.getDataManager().indexOf(data);
            RecyclerListRowHolder<TYPE> holder = recycler.findRowHolderInAdapter(index);
            int position = holder.getLayoutPosition();
            if(position >= 0){
                if(position == recycler.getPosition(CENTER)){
                    if(param.isConfirmOnShortClickEnabled()){
                        post(ON_CLICK_SHORT, holder.get());
                    }
                } else {
                    if(param.isConfirmOnShortClickEnabled()){
                        recycler.observe(new ObserverEvent<>(this, RecyclerList.Event.ON_STOP_SCROLL){
                            @Override
                            public void onComplete(RecyclerList.Event.Is event, Object object){
                                unsubscribe();
                                post(ON_CLICK_SHORT, holder.get());
                            }
                        });
                    }
                    recycler.scrollToPosition(CENTER, position);
                }
            }
        }
    });
    dataManager.observe(new ObserverEvent<Event.Is, TYPE>(this, ON_CLICK_LONG){
        @Override
        public void onComplete(Event.Is event, TYPE data){
            RecyclerListRowManager<TYPE> rowManager = recycler.getRowManager();
            int index = rowManager.getDataManager().indexOf(data);
            RecyclerListRowHolder<TYPE> holder = recycler.findRowHolderInAdapter(index);
            int position = holder.getLayoutPosition();
            if(position >= 0){
                post(ON_CLICK_LONG, holder.get());
            }
        }
    });

    editTextFilter = contentView.findViewById(R.id.txt_filter);
    RecyclerListDataManager.Filter filter = dataManager.getDefaultFilter();
    if(filter != null){
        editTextFilter.setText((String)dataManager.getDefaultFilter().getValue());
        editTextFilter.addTextChangedListener(new EditTextOnTextChangedListenerW(){
            @Override
            public void onTextChanged(EditText editText, Editable es){
                RecyclerListDataManager dataManager = recycler.getRowManager().getDataManager();
                dataManager.filter(Nullify.string(es));
            }
        });
    } else {
        editTextFilter.setVisibility(GONE);
    }

    if(hasBeenReconstructed){
        editTextFilter.onRestoreInstanceState(getState().editTextSavedInstance);
        getState().editTextSavedInstance = null;
    }
}

@Override
public void onSaveInstanceState(Bundle savedInstanceState){
    super.onSaveInstanceState(savedInstanceState);
    getState().editTextSavedInstance = editTextFilter.onSaveInstanceState();
}

public Integer getPosition(){
    return recycler.getPosition(CENTER);
}
public void setPosition(Integer position){
    if(position == null){
        position = ROW_FIRST_POSITION_DEFAULT;
    }
    recycler.scrollToPosition(CENTER, position);
}

@Override
public boolean setValue(TYPE value){
    Integer position;
    if(value == null){
        position = null;
    } else {
        position = recycler.getRowManager().getDataManager().indexOf(value);
    }
    setPosition(position);
    return true;
}
@Override
public TYPE getValue(){
    Integer index = getPosition();
    if(index == null){
        return null;
    }
    return (TYPE)recycler.getRowManager().getDataManager().get(index);
}
@Override
public <T> boolean setValue(Class<T> type, T object){
    if(type == getEntryType()){
        setValue((TYPE)object);
        return true;
    } else {
DebugException.start().unknown("type", type.getName()).end();
        return false;
    }
}
@Override
public <T> T getValue(Class<T> type){
    if(type == getEntryType()){
        return (T)this.getValue();
    } else {

DebugException.start().unknown("type", type.getName()).end();


        return null;
    }

}

public static class State<TYPE> extends DialogPickerBase.State{
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
    protected Param<TYPE> newParam(){
        return new Param();
    }
    @Override
    public Param<TYPE> getParam(){
        return (Param<TYPE>)super.getParam();
    }
    @Override
    public Param<TYPE> obtainParam(){
        return (Param<TYPE>)super.obtainParam();
    }

}

public static class Param<TYPE> extends DialogPickerBase.Param{
    public Integer rowSize = ROW_SIZE_DEFAULT;
    public boolean confirmOnShortClickEnabled = false;
    public Integer recyclerDecorationDrawableResourceId = null;

    public Param(){
        setLayoutID(R.layout.dialog_picker_recycler_wheel);
    }

    @Override
    public State getState(){
        return super.getState();
    }

    public int getRowSize(){
        return rowSize;
    }

    public Param setRowSize(Integer rowSize){
        this.rowSize = rowSize;
        return this;
    }

    public boolean isConfirmOnShortClickEnabled(){
        return confirmOnShortClickEnabled;
    }

    public Param enableConfirmOnShortClick(boolean flag){
        this.confirmOnShortClickEnabled = flag;
        return this;
    }

    public Integer getRecyclerDecorationDrawableResourceId(){
        return recyclerDecorationDrawableResourceId;
    }

    public Param setRecyclerDecorationDrawableResourceId(Integer resourceId){
        this.recyclerDecorationDrawableResourceId = resourceId;
        return this;
    }

    public Param setRecyclerDefaultDecorationDrawable(){
        this.recyclerDecorationDrawableResourceId = R.drawable.dialog_picker_target_line;
        return this;
    }

    public Class<TYPE> getType(){
        RecyclerListRowManager<TYPE> rowManager = getRowManager();
        if(rowManager == null){
            return null;
        }
        return rowManager.getDataManager().getType();
    }

    public <RCL extends RecyclerListRowManager<TYPE>> RCL getRowManager(){
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

    public Param setRowManager(RecyclerListRowManager<TYPE> rowManager){
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

    public Param setAnimator(RecyclerListItemAnimator animator){
        getState().obtainRecyclerListState().obtainParam().setItemAnimator(animator);
        return this;
    }

}

}




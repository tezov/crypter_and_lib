/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java_android.ui.dialog.modal;

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
import static com.tezov.lib_java.type.defEnum.Event.ON_ACTION;
import static com.tezov.lib_java.type.defEnum.Event.ON_CLICK_LONG;
import static com.tezov.lib_java.type.defEnum.Event.ON_CLICK_SHORT;
import static com.tezov.lib_java.type.defEnum.Event.ON_INSERT;
import static com.tezov.lib_java.type.defEnum.Event.ON_OPEN;
import static com.tezov.lib_java.type.defEnum.Event.ON_REMOVE;
import static com.tezov.lib_java_android.ui.recycler.RecyclerList.PositionSnap.CENTER;

import android.os.Bundle;
import android.os.Parcelable;
import android.text.Editable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.widget.TextViewCompat;

import com.tezov.lib_java_android.R;
import com.tezov.lib_java_android.application.AppContext;
import com.tezov.lib_java_android.application.Application;
import com.tezov.lib_java_android.application.AppDisplay;
import com.tezov.lib_java_android.application.SharedPreferences;
import com.tezov.lib_java.async.notifier.observer.event.ObserverEvent;
import com.tezov.lib_java.toolbox.Compare;
import com.tezov.lib_java.toolbox.Nullify;
import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java_android.wrapperAnonymous.EditTextOnTextChangedListenerW;
import com.tezov.lib_java_android.wrapperAnonymous.ViewOnClickListenerW;
import com.tezov.lib_java.type.defEnum.Event;
import com.tezov.lib_java.type.runnable.RunnableW;
import com.tezov.lib_java_android.ui.component.plain.CheckBox;
import com.tezov.lib_java_android.ui.component.plain.EditText;
import com.tezov.lib_java_android.ui.component.plain.EditTextWithIconAction;
import com.tezov.lib_java_android.ui.dialog.DialogBase;
import com.tezov.lib_java_android.ui.dialog.DialogNavigable;
import com.tezov.lib_java_android.ui.layout.FrameFlipperLayout;
import com.tezov.lib_java_android.ui.layout.FrameLayout;
import com.tezov.lib_java_android.ui.recycler.RecyclerList;
import com.tezov.lib_java_android.ui.recycler.RecyclerListDataManager;
import com.tezov.lib_java_android.ui.recycler.RecyclerListItemAnimator;
import com.tezov.lib_java_android.ui.recycler.RecyclerListLayoutLinearVertical;
import com.tezov.lib_java_android.ui.recycler.RecyclerListRowHolder;
import com.tezov.lib_java_android.ui.recycler.RecyclerListRowManager;
import com.tezov.lib_java_android.ui.recycler.RecyclerListSwipper;
import com.tezov.lib_java_android.ui.recycler.prebuild.decoration.RecyclerTargetLineDecorationHorizontal;

public class DialogModalRecycler<TYPE> extends DialogNavigable{
private static final Integer ROW_SIZE_DEFAULT = 5;
private static final int ROW_FIRST_POSITION_DEFAULT = 0;
private TextView lblTitle = null;
private TextView lblHelper = null;
private Button btnAction = null;
private RecyclerList recycler = null;
private FrameFlipperLayout flipperLayout = null;
private EditTextWithIconAction editTextFilter = null;
private CheckBox checkbox = null;

@Override
protected State<TYPE> newState(){
    return new State();
}
@Override
public State<TYPE> getState(){
    return (State)super.getState();
}

@Override
public Param<TYPE> obtainParam(){
    return super.obtainParam();
}
@Override
public Param<TYPE> getParam(){
    return super.getParam();
}

@Nullable
@Override
public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
    super.onCreateView(inflater, container, savedInstanceState);
    State<TYPE> state = getState();
    Param<TYPE> param = getParam();
    View view = inflater.inflate(R.layout.dialog_modal_recycler, container, false);
    flipperLayout = view.findViewById(R.id.container_frame);
    lblTitle = view.findViewById(R.id.lbl_title);
    lblTitle.setText(param.getTitle());
    if(param.helper != null){
        lblHelper = view.findViewById(R.id.lbl_helper);
        lblHelper.setText(param.getHelper());
    }
    btnAction = view.findViewById(R.id.btn_action);
    if(param.hideActionButton){
        btnAction.setVisibility(GONE);
    } else {
        btnAction.setOnClickListener(new ViewOnClickListenerW(){
            @Override
            public void onClicked(View v){
                post(ON_ACTION, null);
            }
        });
    }
    checkbox = view.findViewById(R.id.checkbox);
    if(param.checkBoxText != null){
        checkbox.setText(param.checkBoxText);
        if(param.checkboxPreferenceKey != null){
            checkbox.setChecked(param.isChecked());
        }
        checkbox.setOnClickListener(new ViewOnClickListenerW(){
            @Override
            public void onClicked(View v){
                if(param.checkboxPreferenceKey != null){
                    param.setChecked(checkbox.isChecked());
                }
                onCheckBoxChange(checkbox.isChecked());
            }
        });
    } else {
        checkbox.setVisibility(View.GONE);
    }

    recycler = view.findViewById(R.id.recycler);
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
                        unsubscribe();
                        post(ON_CLICK_SHORT, holder.get());
                        close();
                    }
                } else {
                    if(param.isConfirmOnShortClickEnabled()){
                        unsubscribe();
                        recycler.observe(new ObserverEvent<>(this, RecyclerList.Event.ON_STOP_SCROLL){
                            @Override
                            public void onComplete(RecyclerList.Event.Is event, Object object){
                                unsubscribe();
                                post(ON_CLICK_SHORT, holder.get());
                                close();
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
    editTextFilter = view.findViewById(R.id.txt_filter);
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
    if(param.initialValue != null){
        setData(param.initialValue);
    }
    if(param.substituteText != null){
        if(dataManager.size() <= 0){
            showSubstitute(param.substituteText, param.substituteViewHeight);
            observeEventInsert();
        } else {
            showRecycler();
            observeEventRemove();
        }
    }
    return view;
}

@Override
public void onPrepare(boolean hasBeenReconstructed){
    super.onPrepare(hasBeenReconstructed);
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

protected void onCheckBoxChange(boolean flag){
    post(Event.ON_CHANGE, flag);
}

private void observeEventInsert(){
    RecyclerListDataManager<TYPE> dataManager = recycler.getRowManager().getDataManager();
    ObserverEvent<Event.Is, TYPE> observerEventInsert = new ObserverEvent<Event.Is, TYPE>(this, ON_INSERT){
        @Override
        public void onComplete(Event.Is event, TYPE dataKey){
            unsubscribe();
            showRecycler();
            observeEventRemove();
        }
    };
    dataManager.observe(observerEventInsert);
}
private void observeEventRemove(){
    RecyclerListDataManager<TYPE> dataManager = recycler.getRowManager().getDataManager();
    ObserverEvent<Event.Is, TYPE> observerEventRemove = new ObserverEvent<Event.Is, TYPE>(this, ON_REMOVE){
        @Override
        public void onComplete(Event.Is event, TYPE dataKey){
            if(dataManager.size() <= 0){
                unsubscribe();
                Param<TYPE> param = getParam();
                showSubstitute(param.substituteText, param.substituteViewHeight);
                observeEventInsert();
            }
        }
    };
    dataManager.observe(observerEventRemove);
}
public void showSubstitute(int resourceId, int height_dp){
    showSubstitute(AppContext.getResources().getString(resourceId), height_dp);
}
public void showSubstitute(String text, int height_dp){
    TextView textView = new TextView(getContext());
    ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, AppDisplay.convertDpToPx(height_dp));
    textView.setLayoutParams(params);
    textView.setText(text);
    TextViewCompat.setTextAppearance(textView, R.style.TxtGiant);
    textView.setGravity(Gravity.CENTER);
    showSubstitute(textView);
}
public void showSubstitute(View view){
    if(getView() != null){
        showSubstituteRunnable(view).run();
    } else {
        observe(new ObserverEvent<Event.Is, Object>(this, ON_OPEN){
            @Override
            public void onComplete(Event.Is event, Object object){
                unsubscribe();
                showSubstituteRunnable(view).run();
            }
        });
    }
}
private RunnableW showSubstituteRunnable(View view){
    return new RunnableW(){
        @Override
        public void runSafe(){
            FrameLayout frame = getView().findViewById(R.id.frame_substitute);
            frame.addView(view);
            flipperLayout.showView(R.id.frame_substitute);
        }
    };
}
public void showRecycler(){
    if(getView() != null){
        showRecyclerRunnable().run();
    } else {
        observe(new ObserverEvent<Event.Is, Object>(this, ON_OPEN){
            @Override
            public void onComplete(Event.Is event, Object object){
                unsubscribe();
                showRecyclerRunnable().run();
            }
        });
    }
}
private RunnableW showRecyclerRunnable(){
    return new RunnableW(){
        @Override
        public void runSafe(){
            flipperLayout.showView(R.id.recycler);
        }
    };
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

public TYPE getData(){
    Integer index = getPosition();
    if(index == null){
        return null;
    }
    return (TYPE)recycler.getRowManager().getDataManager().get(index);
}
public void setData(TYPE data){
    Integer position;
    if(data == null){
        position = null;
    } else {
        position = recycler.getRowManager().getDataManager().indexOf(data);
    }
    setPosition(position);
}

public static class State<TYPE> extends DialogNavigable.State{
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
    public Param<TYPE> obtainParam(){
        return (Param)super.obtainParam();
    }
    @Override
    protected Method newMethod(){
        return new Method();
    }
    @Override
    public Method obtainMethod(){
        return (Method)super.obtainMethod();
    }

}

public static class Param<TYPE> extends DialogNavigable.Param{
    public Integer rowSize = ROW_SIZE_DEFAULT;
    public boolean confirmOnShortClickEnabled = false;
    public Integer recyclerDecorationDrawableResourceId = null;
    public TYPE initialValue = null;
    public String checkboxPreferenceKey = null;
    public String helper = null;
    public String substituteText = null;
    public Integer substituteViewHeight = null;
    public boolean hideActionButton = false;
    public String checkBoxText = null;

    @Override
    public State<TYPE> getState(){
        return super.getState();
    }
    @Override
    public Method obtainMethod(){
        return super.obtainMethod();
    }

    public TYPE getInitialValue(){
        return initialValue;
    }
    public Param<TYPE> setInitialValue(TYPE initialValue){
        this.initialValue = initialValue;
        return this;
    }

    public String getCheckboxPreferenceKey(){
        return checkboxPreferenceKey;
    }
    public Param setCheckboxPreferenceKey(String flag){
        this.checkboxPreferenceKey = flag;
        return this;
    }

    public boolean isChecked(){
        SharedPreferences sp = Application.sharedPreferences();
        return Compare.isTrue(sp.getBoolean(checkboxPreferenceKey));
    }
    public Param setChecked(boolean flag){
        SharedPreferences sp = Application.sharedPreferences();
        sp.put(checkboxPreferenceKey, flag);
        return this;
    }

    public String getHelper(){
        return helper;
    }
    public Param setHelper(int resourceId){
        return setHelper(AppContext.getResources().getString(resourceId));
    }
    public Param setHelper(String helper){
        this.helper = helper;
        return this;
    }

    public String getCheckBoxText(){
        return checkBoxText;
    }
    public Param setCheckBoxText(int resourceId){
        return setCheckBoxText(AppContext.getResources().getString(resourceId));
    }
    public Param setCheckBoxText(String text){
        this.checkBoxText = text;
        return this;
    }

    public String getSubstituteText(){
        return substituteText;
    }
    public Param<TYPE> setSubstituteText(int substituteTextResourceId, Integer substituteViewHeight_dp){
        return setSubstituteText(AppContext.getResources().getString(substituteTextResourceId), substituteViewHeight_dp);
    }
    public Param<TYPE> setSubstituteText(String substituteText, Integer substituteViewHeight_dp){
        this.substituteText = substituteText;
        this.substituteViewHeight = substituteViewHeight_dp;
        return this;
    }

    public boolean isConfirmOnShortClickEnabled(){
        return confirmOnShortClickEnabled;
    }
    public Param<TYPE> enableConfirmOnShortClick(boolean flag){
        this.confirmOnShortClickEnabled = flag;
        return this;
    }

    public boolean isHideActionButton(){
        return hideActionButton;
    }
    public Param<TYPE> setHideActionButton(boolean flag){
        this.hideActionButton = flag;
        return this;
    }

    public int getRowSize(){
        return rowSize;
    }
    public Param<TYPE> setRowSize(Integer rowSize){
        this.rowSize = rowSize;
        return this;
    }

    public Integer getRecyclerDecorationDrawableResourceId(){
        return recyclerDecorationDrawableResourceId;
    }
    public Param<TYPE> setRecyclerDecorationDrawableResourceId(Integer resourceId){
        this.recyclerDecorationDrawableResourceId = resourceId;
        return this;
    }
    public Param<TYPE> setRecyclerDefaultDecorationDrawable(){
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
    public Param<TYPE> setRowManager(RecyclerListRowManager<TYPE> rowManager){
        getState().obtainRecyclerListState().obtainParam().setRowManager(rowManager);
        return this;
    }

    public <RSP extends RecyclerListSwipper> RSP getSwipper(){
        if(!hasState()){
            return null;
        }
        if(!getState().hasRecyclerListState()){
            return null;
        }
        if(!getState().getRecyclerListState().hasParam()){
            return null;
        }
        return getState().getRecyclerListState().getParam().getItemTouchSwipper();
    }
    public Param<TYPE> setSwipper(RecyclerListSwipper swipper){
        getState().obtainRecyclerListState().obtainParam().setItemTouchSwipper(swipper);
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
    public Param<TYPE> setAnimator(RecyclerListItemAnimator animator){
        getState().obtainRecyclerListState().obtainParam().setItemAnimator(animator);
        return this;
    }

}

public static class Method extends DialogBase.Method{
    @Override
    public DialogModalRecycler getOwner(){
        return (DialogModalRecycler)super.getOwner();
    }

}

}


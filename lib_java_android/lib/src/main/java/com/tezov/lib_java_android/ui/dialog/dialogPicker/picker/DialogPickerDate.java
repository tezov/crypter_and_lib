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
import com.tezov.lib_java.util.UtilsString;
import java.util.List;
import java.util.LinkedList;
import java.util.Set;
import com.tezov.lib_java_android.database.sqlLite.filter.dbFilterOrder;
import com.tezov.lib_java_android.database.sqlLite.filter.chunk.ChunkCommand;
import androidx.fragment.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;

import androidx.annotation.Nullable;

import com.tezov.lib_java_android.R;
import com.tezov.lib_java_android.application.AppContext;
import com.tezov.lib_java_android.application.AppResources.IdentifierType;
import com.tezov.lib_java.toolbox.Clock;
import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java.debug.DebugString;
import com.tezov.lib_java_android.ui.dialog.dialogPicker.picker.base.DialogPickerBase;

import org.threeten.bp.LocalDate;

import java.util.ArrayList;
import java.util.Arrays;

public class DialogPickerDate extends DialogPickerBase<LocalDate>{
protected DatePicker picker;

@Override
public void onPrepare(boolean hasBeenReconstructed){
    super.onPrepare(hasBeenReconstructed);
    picker = getContentView().findViewById(R.id.picker);
    Param.Visibility visibility = obtainParam().getVisibility();
    if(visibility != null){
        ArrayList<String> viewName = new ArrayList<>(Arrays.asList("day", "month", "year"));
        for(int i = 0; i < viewName.size(); i++){
            try{
                int identifier = AppContext.getResources().getIdentifier(IdentifierType.id, viewName.get(i), "android");
                View spinner = picker.findViewById(identifier);
                if(!visibility.get(i)){
                    spinner.setVisibility(View.GONE);
                }
            } catch(java.lang.Throwable e){

DebugException.start().log(e).end();

            }
        }
    }
    if(hasBeenReconstructed){
        setValue(getState().savedInstance);
        getState().savedInstance = null;
    }
}

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
public Param getParam(){
    return (Param)super.getParam();
}

@Override
public Param obtainParam(){
    return (Param)super.obtainParam();
}

@Override
public Class<LocalDate> getEntryType(){
    return LocalDate.class;
}

@Nullable
@Override
public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){

    return super.onCreateView(inflater, container, savedInstanceState);
}

@Override
public <T> boolean setValue(Class<T> type, T object){
    if(type == LocalDate.class){
        setValue((LocalDate)object);
        return true;
    } else {
DebugException.start().unknown("type", type.getName()).end();
        return false;
    }
}

@Override
public void onSaveInstanceState(Bundle savedInstanceState){
    super.onSaveInstanceState(savedInstanceState);
    getState().savedInstance = getValue();
}

@Override
public boolean setValue(LocalDate date){
    picker.init(date.getYear(), date.getMonthValue() - 1, date.getDayOfMonth(), null);
    return true;
}

@Override
public LocalDate getValue(){
    if(obtainParam().getVisibility() == null){
        return LocalDate.of(2000, 1, 1);
    } else {
        return LocalDate.of(obtainParam().isYearVisible() ? picker.getYear() : 2000, obtainParam().isMonthVisible() ? picker.getMonth() + 1 : 1,
                obtainParam().isDayVisible() ? picker.getDayOfMonth() : 1);
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

public static class State extends DialogPickerBase.State{
    private LocalDate savedInstance = null;

    @Override
    protected Param newParam(){
        return new Param();
    }

    @Override
    public Param getParam(){
        return (Param)super.getParam();
    }

    @Override
    public Param obtainParam(){
        return (Param)super.obtainParam();
    }

}

public static class Param extends DialogPickerBase.Param{
    private Visibility visibility = null;

    @Override
    public int getLayoutID(){
        return R.layout.dialog_picker_date_wheel;
    }

    public Visibility getVisibility(){
        return visibility;
    }

    public boolean isDayVisible(){
        return visibility.day;
    }

    public boolean isMonthVisible(){
        return visibility.month;
    }

    public boolean isYearVisible(){
        return visibility.year;
    }

    public Param setVisibilities(boolean day, boolean month, boolean year){
        visibility = new Visibility(day, month, year);
        return this;
    }

    @Override
    public <O> O getInitialValue(){
        LocalDate date = super.getInitialValue();
        if(date != null){
            return (O)date;
        } else {
            return (O)Clock.Date.now();
        }
    }

    public Param setToNow(){
        setInitialValue(Clock.Date.now());
        return this;
    }

    @Override
    public DebugString toDebugString(){
        DebugString data = super.toDebugString();
        if(visibility == null){
            data.append("visibility", null);
        } else {
            data.append("visibility", "day:" + isDayVisible() + "month:" + isMonthVisible() + "year:" + isYearVisible());
        }
        return data;
    }

    public static class Visibility{
        boolean day;
        boolean month;
        boolean year;

        Visibility(boolean day, boolean month, boolean year){
            this.day = day;
            this.month = month;
            this.year = year;
        }

        public boolean get(int i){
            if(i == 0){
                return day;
            }
            if(i == 1){
                return month;
            }
            if(i == 2){
                return year;
            }

DebugException.start().explode(new IndexOutOfBoundsException()).end();

            return false;
        }

    }

}


}
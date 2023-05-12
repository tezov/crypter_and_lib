/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.crypter.dialog;

import com.tezov.lib_java.debug.DebugLog;
import com.tezov.lib_java.debug.DebugTrack;
import com.tezov.lib_java.debug.DebugException;
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
import static com.tezov.crypter.application.AppConfig.ENABLE_BUTTONS_AFTER_INTENT_SENT_DELAY_ms;

import com.tezov.lib_java_android.application.AppContext;
import com.tezov.lib_java_android.toolbox.PostToHandler;
import com.tezov.lib_java.type.runnable.RunnableW;
import com.tezov.lib_java_android.ui.misc.StateView;

import androidx.fragment.app.Fragment;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import com.tezov.crypter.R;
import com.tezov.lib_java_android.application.Application;
import com.tezov.lib_java_android.application.SharedPreferences;
import com.tezov.lib_java.toolbox.Compare;
import com.tezov.lib_java_android.wrapperAnonymous.ViewOnClickListenerW;
import com.tezov.lib_java.type.defEnum.Event;
import com.tezov.lib_java_android.ui.component.plain.ButtonIconMaterial;
import com.tezov.lib_java_android.ui.component.plain.CheckBox;
import com.tezov.lib_java_android.ui.dialog.DialogNavigable;
import com.tezov.lib_java_android.ui.dialog.modal.DialogModalRequest;

public class DialogMenuFormat extends DialogNavigable{

ButtonIconMaterial btnAction = null;
ButtonIconMaterial btnFileText = null;
ButtonIconMaterial btnFileGif = null;
ButtonIconMaterial btnText = null;

CheckBox chkRememberChoice = null;
@Override
public State getState(){
    return (State)super.getState();
}
@Override
public Param getParam(){
    return super.getParam();
}

@Override
public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
    super.onCreateView(inflater, container, savedInstanceState);
    View view = inflater.inflate(R.layout.dialog_menu_format, container, false);
    btnAction = view.findViewById(R.id.btn_close);
    btnAction.setOnClickListener(new ViewOnClickListenerW(){
        @Override
        public void onClicked(View v){
            if(disableButtons()){
                post(Event.ON_CANCEL, null);
                close();
            }
        }
    });
    btnFileText = view.findViewById(R.id.btn_file_text);
    btnFileText.setOnClickListener(new ViewOnClickListenerW(){
        @Override
        public void onClicked(View v){
            if(disableButtons()){
                SharedPreferences sp = Application.sharedPreferences();
                if(chkRememberChoice.isChecked()){
                    sp.put(getParam().defaultFormat, Format.FILE_TEXT.name());
                }
                post(Event.ON_CLICK_SHORT, Format.FILE_TEXT);
            }
        }
    });
    btnFileGif = view.findViewById(R.id.btn_file_gif);
    btnFileGif.setOnClickListener(new ViewOnClickListenerW(){
        @Override
        public void onClicked(View v){
            if(disableButtons()){
                SharedPreferences sp = Application.sharedPreferences();
                if(chkRememberChoice.isChecked()){
                    sp.put(getParam().defaultFormat, Format.FILE_GIF.name());
                }
                post(Event.ON_CLICK_SHORT, Format.FILE_GIF);
            }
        }
    });
    btnText = view.findViewById(R.id.btn_text);
    btnText.setOnClickListener(new ViewOnClickListenerW(){
        @Override
        public void onClicked(View v){
            if(disableButtons()){
                SharedPreferences sp = Application.sharedPreferences();
                if(chkRememberChoice.isChecked()){
                    sp.put(getParam().defaultFormat, Format.TEXT.name());
                }
                post(Event.ON_CLICK_SHORT, Format.TEXT);
            }
        }
    });
    chkRememberChoice = view.findViewById(R.id.chk_remember);
    Param param = getParam();
    if(param.checkboxPreferenceKey != null){
        chkRememberChoice.setChecked(param.isChecked());
    }
    chkRememberChoice.setOnClickListener(new ViewOnClickListenerW(){
        @Override
        public void onClicked(View v){
            if(param.checkboxPreferenceKey != null){
                param.setChecked(chkRememberChoice.isChecked());
            }
        }
    });
    SharedPreferences sp = Application.sharedPreferences();
    String formatString = sp.getString(getParam().defaultFormat);
    if(formatString != null){
        Drawable icon = AppContext.getResources().getDrawable(R.drawable.ic_arrow_double_right_24dp);
        Format format = Format.valueOf(formatString);
        switch(format){
            case FILE_TEXT:
                btnFileText.setIcon(icon);
                break;
            case FILE_GIF:
                btnFileGif.setIcon(icon);
                break;
            case TEXT:
                btnText.setIcon(icon);
                break;
        }
    }
    return view;
}

final public void restoreButtons(){
    restoreButtons(false);
}
final public void restoreButtons(boolean withDelay){
    PostToHandler.of(getView(), withDelay ? ENABLE_BUTTONS_AFTER_INTENT_SENT_DELAY_ms : 0, new RunnableW(){
        @Override
        public void runSafe(){
            getState().stateView.restore();
        }
    });
}

final public boolean disableButtons(){
    StateView stateView = getState().stateView;
    if(stateView.lock()){
        PostToHandler.of(getView(), new RunnableW(){
            @Override
            public void runSafe(){
                stateView.clear()
                    .enableNot(btnAction)
                    .enableNot(btnFileText)
                    .enableNot(btnFileGif)
                    .enableNot(btnText)
                    .clickableNot(chkRememberChoice);
                onDisabledButtons(stateView);
                stateView.unlock();
            }
        });
        return true;
    } else {
        return false;
    }
}
protected void onDisabledButtons(StateView stateView){

}

final public void enableButtons(){
    enableButtons(false);
}
final public void enableButtons(boolean withDelay){
    PostToHandler.of(getView(), withDelay ? ENABLE_BUTTONS_AFTER_INTENT_SENT_DELAY_ms : 0, new RunnableW(){
        @Override
        public void runSafe(){
            btnAction.setEnabled(true);
            btnFileText.setEnabled(true);
            btnFileGif.setEnabled(true);
            btnText.setEnabled(true);
            chkRememberChoice.setClickable(true);
            onEnabledButtons();
        }
    });
}
protected void onEnabledButtons(){

}

public enum Format{
    FILE_TEXT, FILE_GIF, TEXT
}
public static class State extends DialogModalRequest.State{
    protected StateView stateView;
    public State(){
        stateView = new StateView();
    }
    @Override
    protected Param newParam(){
        return new Param();
    }
    @Override
    public Param obtainParam(){
        return (Param)super.obtainParam();
    }
}
public static class Param extends DialogModalRequest.Param{
    public String defaultFormat = null;
    public String checkboxPreferenceKey = null;

    public String getDefaultFormat(){
        return defaultFormat;
    }
    public Param setDefaultFormat(String keyRememberFormat){
        this.defaultFormat = keyRememberFormat;
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
}


}

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
import com.tezov.lib_java.async.notifier.task.TaskValue;
import com.tezov.lib_java_android.toolbox.PostToHandler;
import com.tezov.lib_java.type.runnable.RunnableW;
import com.tezov.lib_java_android.ui.dialog.modal.DialogModalRequest;
import com.tezov.lib_java_android.ui.misc.StateView;
import com.tezov.lib_java_android.ui.navigation.Navigate;

import androidx.fragment.app.Fragment;

import static com.tezov.crypter.application.AppConfig.ENABLE_BUTTONS_AFTER_INTENT_SENT_DELAY_ms;
import static com.tezov.crypter.data.table.Descriptions.KEY;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import com.tezov.crypter.R;
import com.tezov.crypter.application.Application;
import com.tezov.crypter.data.table.db.dbKeyTable;
import com.tezov.lib_java_android.wrapperAnonymous.ViewOnClickListenerW;
import com.tezov.lib_java.type.defEnum.Event;
import com.tezov.lib_java_android.ui.component.plain.ButtonIconMaterial;
import com.tezov.lib_java_android.ui.dialog.DialogNavigable;

public class DialogMenuAction extends DialogNavigable{
public static TaskValue<DialogMenuAction>.Observable open(){
    DialogMenuAction.State state = new DialogMenuAction.State();
    return Navigate.To(DialogMenuAction.class, state);
}

ButtonIconMaterial btnAction = null;
ButtonIconMaterial btnImport = null;
ButtonIconMaterial btnCreate = null;
ButtonIconMaterial btnImportKeyAll = null;
ButtonIconMaterial btnExportKeyAll = null;

@Override
public State getState(){
    return (State)super.getState();
}

@Override
public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
    super.onCreateView(inflater, container, savedInstanceState);
    View view = inflater.inflate(R.layout.dialog_menu_action, container, false);
    btnAction = view.findViewById(R.id.btn_close);
    btnAction.setOnClickListener(new ViewOnClickListenerW(){
        @Override
        public void onClicked(View v){
            if(disableButtons()){
                close();
            }
        }
    });

    btnImport = view.findViewById(R.id.btn_import_key_shared);
    btnImport.setOnClickListener(new ViewOnClickListenerW(){
        @Override
        public void onClicked(View v){
            if(disableButtons()){
                post(Event.ON_CLICK_SHORT, R.id.btn_import_key_shared);
            }
        }
    });
    btnCreate = view.findViewById(R.id.btn_create_key);
    btnCreate.setOnClickListener(new ViewOnClickListenerW(){
        @Override
        public void onClicked(View v){
            if(disableButtons()){
                post(Event.ON_CLICK_SHORT, R.id.btn_create_key);
            }
        }
    });
    btnImportKeyAll = view.findViewById(R.id.btn_import_key_all);
    btnImportKeyAll.setOnClickListener(new ViewOnClickListenerW(){
        @Override
        public void onClicked(View v){
            if(disableButtons()){
                post(Event.ON_CLICK_SHORT, R.id.btn_import_key_all);
            }
        }
    });
    btnExportKeyAll = view.findViewById(R.id.btn_export_key_all);
    dbKeyTable.Ref ref = Application.tableHolder().handle().getMainRef(KEY);
    int keys = ref.size();
    if(keys > 0){
        btnExportKeyAll.setOnClickListener(new ViewOnClickListenerW(){
            @Override
            public void onClicked(View v){
                if(disableButtons()){
                    post(Event.ON_CLICK_SHORT, R.id.btn_export_key_all);
                }
            }
        });
    } else {
        btnExportKeyAll.setEnabled(false);
        btnExportKeyAll.setClickable(false);
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
                        .enableNot(btnImport)
                        .enableNot(btnCreate)
                        .enableNot(btnImportKeyAll)
                        .enableNot(btnExportKeyAll);
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
            btnImport.setEnabled(true);
            btnCreate.setEnabled(true);
            btnImportKeyAll.setEnabled(true);
            btnImportKeyAll.setEnabled(true);
            if(btnExportKeyAll.isClickable()){
                btnExportKeyAll.setEnabled(true);
            }
            onEnabledButtons();
        }
    });
}
protected void onEnabledButtons(){

}

public static class State extends DialogModalRequest.State{
    protected StateView stateView;
    public State(){
        stateView = new StateView();
    }
}

}

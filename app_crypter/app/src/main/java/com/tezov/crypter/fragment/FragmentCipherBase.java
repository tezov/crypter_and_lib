/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.crypter.fragment;

import com.tezov.lib_java.type.primitive.IntTo;
import com.tezov.lib_java.toolbox.CompareType;
import com.tezov.lib_java.debug.DebugLog;
import java.util.Set;
import com.tezov.lib_java_android.database.sqlLite.filter.chunk.ChunkCommand;
import java.util.List;
import com.tezov.lib_java_android.database.sqlLite.filter.dbFilterOrder;
import androidx.fragment.app.Fragment;
import com.tezov.lib_java.type.unit.UnitByte;
import com.tezov.lib_java.util.UtilsString;
import com.tezov.lib_java.toolbox.Clock;
import java.util.LinkedList;
import com.tezov.lib_java.debug.DebugTrack;
import com.tezov.lib_java.type.primitive.ObjectTo;
import static com.tezov.crypter.application.AppConfig.ENABLE_BUTTONS_AFTER_INTENT_SENT_DELAY_ms;
import static com.tezov.crypter.application.SharePreferenceKey.SP_ALIAS_LOAD_BOOLEAN;
import static com.tezov.crypter.application.SharePreferenceKey.SP_CIPHER_REMEMBER_ALIAS_CHECKBOX_BOOL;
import static com.tezov.crypter.application.SharePreferenceKey.SP_CIPHER_REMEMBER_ALIAS_UID_BYTES;
import static com.tezov.lib_java_android.database.sqlLite.filter.dbSign.LIKE;
import static com.tezov.lib_java.type.defEnum.Event.ON_REMOVE;
import static com.tezov.lib_java_android.ui.navigation.NavigatorManager.NavigatorKey.FRAGMENT;

import android.graphics.Rect;
import android.view.View;

import com.tezov.crypter.R;
import com.tezov.crypter.activity.ActivityMain;
import com.tezov.crypter.application.Application;
import com.tezov.crypter.data.dbItem.dbKey;
import com.tezov.crypter.data.item.ItemKey;
import com.tezov.crypter.data.item.UtilsKey;
import com.tezov.crypter.data.table.description.DescriptionKey;
import com.tezov.crypter.dialog.DialogCreateKey;
import com.tezov.crypter.dialog.DialogEditKey;
import com.tezov.crypter.dialog.DialogExportKey;
import com.tezov.crypter.dialog.DialogExportKeyAll;
import com.tezov.crypter.dialog.DialogImportKey;
import com.tezov.crypter.dialog.DialogImportKeyAll;
import com.tezov.crypter.dialog.DialogMenuAction;
import com.tezov.crypter.dialog.DialogOpenKeystore;
import com.tezov.crypter.misc.FormItemKeyAdapter;
import com.tezov.crypter.navigation.NavigationArguments;
import com.tezov.crypter.navigation.NavigationHelper;
import com.tezov.crypter.recycler.keystore.KeyDataManager;
import com.tezov.crypter.recycler.keystore.KeyRowManager;
import com.tezov.crypter.recycler.keystore.KeySwipper;
import com.tezov.crypter.user.UserAuth;
import com.tezov.lib_java.application.AppRandomNumber;
import com.tezov.lib_java_android.application.AppContext;
import com.tezov.lib_java_android.application.AppKeyboard;
import com.tezov.lib_java_android.application.SharedPreferences;
import com.tezov.lib_java.async.notifier.observer.event.ObserverEvent;
import com.tezov.lib_java.async.notifier.observer.state.ObserverStateE;
import com.tezov.lib_java.async.notifier.observer.value.ObserverValue;
import com.tezov.lib_java.async.notifier.observer.value.ObserverValueE;
import com.tezov.lib_java.async.notifier.task.TaskState;
import com.tezov.lib_java_android.authentification.defAuthMethod;
import com.tezov.lib_java.generator.uid.UidBase;
import com.tezov.lib_java.generator.uid.defUid;
import com.tezov.lib_java.toolbox.Compare;
import com.tezov.lib_java_android.toolbox.PostToHandler;
import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java_android.wrapperAnonymous.ViewOnClickListenerW;
import com.tezov.lib_java.type.defEnum.Event;
import com.tezov.lib_java.type.runnable.RunnableGroup;
import com.tezov.lib_java.type.runnable.RunnableW;
import com.tezov.lib_java_android.ui.component.plain.ButtonIconMaterial;
import com.tezov.lib_java_android.ui.component.plain.FocusCemetery;
import com.tezov.lib_java_android.ui.dialog.modal.DialogModalRecycler;
import com.tezov.lib_java_android.ui.form.component.plain.FormEditText;
import com.tezov.lib_java.data.validator.ValidatorNotEmpty;
import com.tezov.lib_java_android.ui.fragment.FragmentNavigable;
import com.tezov.lib_java_android.ui.misc.StateView;
import com.tezov.lib_java_android.ui.navigation.Navigate;
import com.tezov.lib_java_android.ui.navigation.NavigatorManager;
import com.tezov.lib_java_android.ui.navigation.defNavigable;
import com.tezov.lib_java_android.util.UtilsView;

import fragment.FragmentBase_bt;

public abstract class FragmentCipherBase extends FragmentBase_bt{
public final static int NOTIFY_SIGNED_OUT = AppRandomNumber.nextInt();
public final static int NOTIFY_SWITCH_TO_PASSWORD = AppRandomNumber.nextInt();

protected boolean isAlias = false;
protected StateView stateView = null;

protected ButtonIconMaterial btnSelectAlias = null;
protected FormEditText frmPassword = null;
protected FormEditText frmAlias = null;

public void bindView(View view){
    stateView = new StateView();

    btnSelectAlias = view.findViewById(R.id.btn_select_alias);
    btnSelectAlias.setOnClickListener(new ViewOnClickListenerW(){
        @Override
        public void onClicked(View v){
            if(disableButtons()){
                selectAlias();
            }
        }
    });
    FormItemKeyAdapter keyAdapter = new FormItemKeyAdapter();
    keyAdapter.setValidator(new ValidatorNotEmpty<String>());
    frmAlias = view.findViewById(R.id.frm_alias);
    frmAlias.link(keyAdapter);
    frmAlias.addOnClickListener(new ViewOnClickListenerW(){
        @Override
        public void onClicked(View v){
            if(disableButtons()){
                switchToPassword(true);
            }
        }
    });
    frmAlias.setText(null);
    frmPassword = view.findViewById(R.id.frm_password);
    frmPassword.setValidator(new ValidatorNotEmpty<>());
    frmPassword.link(new FormEditText.EntryString(){
        @Override
        public boolean setValue(String value){
            boolean changed = !Compare.equals(this.value, value);
            if(changed){
                this.value = value;
            }
            return changed;
        }
        @Override
        public <T> void onSetValue(Class<T> type){
            onPasswordChanged(stateView);
        }
    });
}

public void onViewBound(){
    Application.State state = Application.getState();
    if(state.isAlias()){
        defUid uidAlias = state.getAliasUid();
        ItemKey itemKey = UtilsKey.get(uidAlias);
        if(itemKey != null){
            switchToAlias(new dbKey(itemKey));
        }
    }
    else if(state.isPassword()){
        switchToPassword(false);
        frmPassword.setValue(Application.getState().getPassword());
    } else {
        loadMemorizedAlias();
    }
    state.clear();
}
private boolean loadMemorizedAlias(){
    SharedPreferences sp = Application.sharedPreferences();
    if(Compare.isTrue(sp.getBoolean(SP_ALIAS_LOAD_BOOLEAN))){
        defUid uidAlias = UidBase.fromBytes(sp.getBytes(SP_CIPHER_REMEMBER_ALIAS_UID_BYTES));
        if(uidAlias != null){
            ItemKey itemKey = UtilsKey.get(uidAlias);
            if(itemKey != null){
                switchToAlias(new dbKey(itemKey));
                return true;
            } else {
                sp.remove(SP_CIPHER_REMEMBER_ALIAS_UID_BYTES);
            }
        }
    }
    return false;
}
public void onSaveApplicationState(){
    Application.State state = Application.getState();
    if(isAlias){
        dbKey dataKey = getDataKey(false);
        if(dataKey != null){
            state.setAlias(dataKey.getUid()).switchedToAlias();
        } else {
            state.clear();
        }
    } else {
        char[] Password = frmPassword.getChars();
        if(Password != null){
            state.setPassword(Password).switchedToPassword();
        } else {
            state.clear();
        }
    }
}

@Override
public void onPrepare(boolean hasBeenReconstructed){
    super.onPrepare(hasBeenReconstructed);
    AppKeyboard.observeVisibilityChange(new ObserverValue<Boolean>(this){
        Rect originalPadding;
        Rect shrinkPadding;
        int actionBarSize;
        ObserverValue<Boolean> init(){
            View fragmentView = getView();
            originalPadding = UtilsView.getPadding(fragmentView);
            shrinkPadding = new Rect(originalPadding.left, 0, originalPadding.right, 0);
            actionBarSize = AppContext.getResources().resolveDimension(R.attr.actionBarSize).intValue();
            return this;
        }
        @Override
        public void onComplete(Boolean visible){
            ActivityMain activity = (ActivityMain)getActivity();
            View fragmentView = getView();
            if(!visible){
                activity.getToolbarBottom().setVisible(true);
                activity.setBannerVisible(true);
                UtilsView.setPadding(fragmentView, originalPadding);

            } else {
                shrinkPadding.bottom = AppKeyboard.getHeight() + originalPadding.bottom;
                UtilsView.setPadding(fragmentView, shrinkPadding);
                activity.setBannerVisible(false);
                activity.getToolbarBottom().setVisible(false);
            }
        }
    }.init());
    Navigate.observe(new ObserverEvent<NavigatorManager.NavigatorKey.Is, NavigatorManager.Event>(this, FRAGMENT){
        @Override
        public void onComplete(NavigatorManager.NavigatorKey.Is is, NavigatorManager.Event event){
            if(event == NavigatorManager.Event.ON_NAVIGATE_TO){
                FragmentNavigable fr = Navigate.getCurrentFragmentRef();
                if(fr instanceof FragmentCipherBase){
                    onSaveApplicationState();
                }
            }
        }
    });
}

@Override
public boolean requestViewUpdate(Integer what, com.tezov.lib_java_android.ui.navigation.NavigationArguments arg){
    if(what == NOTIFY_SIGNED_OUT){
        if(isAlias){
            SharedPreferences sp = Application.sharedPreferences();
            if(!Compare.isTrue(sp.getBoolean(SP_ALIAS_LOAD_BOOLEAN))){
                switchToPassword(false);
            }
        }
        return true;
    }
    else if(what == NOTIFY_SWITCH_TO_PASSWORD){
        if(isAlias){
            switchToPassword(false);
        }
        return true;
    } else {
        return super.requestViewUpdate(what, arg);
    }
}
@Override
public boolean onNewNavigationArguments(com.tezov.lib_java_android.ui.navigation.NavigationArguments arg){
    NavigationArguments arguments = (NavigationArguments)arg;
    if(arguments.isTargetDialog()){
        Class<? extends defNavigable> targetDialog = arguments.getTarget();
        if(targetDialog == DialogImportKey.class){
            openDialog_ImportKey(arguments);
            return true;
        } else if(targetDialog == DialogExportKey.class){
            openDialog_ExportKey(arguments);
            return true;
        }
    }
    return super.onNewNavigationArguments(arg);
}
@Override
public void onDestroy(){
    AppKeyboard.unObserveVisibilityChange(this);
    super.onDestroy();
}

final public void restoreButtons(){
    restoreButtons(false);
}
final public void restoreButtons(boolean withDelay){
    PostToHandler.of(getView(), withDelay ? ENABLE_BUTTONS_AFTER_INTENT_SENT_DELAY_ms : 0, new RunnableW(){
        @Override
        public void runSafe(){
            stateView.restore();
        }
    });
}

final public boolean disableButtons(){
    if(stateView.lock()){
        PostToHandler.of(getView(), new RunnableW(){
            @Override
            public void runSafe(){
                stateView.clear().enableNot(frmPassword).clickableNot(frmAlias).enableNot(btnSelectAlias);
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
            frmPassword.setEnabled(true);
            frmAlias.setClickable(true);
            btnSelectAlias.setEnabled(true);
            onEnabledButtons();
        }
    });
}
protected void onEnabledButtons(){

}

protected void selectAlias(){
    RunnableGroup gr = new RunnableGroup(this).name("selectAlias");
    int LABEL_OPEN_DIALOG_SELECT_ALIAS = gr.label();
    if(!UserAuth.isKeystoreOpened()){
        gr.add(new RunnableGroup.Action(){
            @Override
            public void runSafe(){
                Application.userAuth().signIn(null).observe(new ObserverValueE<defAuthMethod.State.Is>(this){
                    @Override
                    public void onComplete(defAuthMethod.State.Is state){
                        if(Application.userAuth().isAuthenticated()){
                            skipUntilLabel(LABEL_OPEN_DIALOG_SELECT_ALIAS);
                        } else {
                            next();
                        }
                    }
                    @Override
                    public void onException(defAuthMethod.State.Is state, Throwable e){
                        next();
                    }
                });
            }
        }.name("try to open keystore"));
        gr.add(new RunnableGroup.Action(){
            @Override
            public void runSafe(){
                openDialog_OpenKeystore(true).observe(new ObserverStateE(this){
                    @Override
                    public void onComplete(){
                        next();
                    }
                    @Override
                    public void onException(Throwable e){
                        putException(e);
                        done();
                    }
                });
            }
        }.name("open dialog KeystoreOpen"));
    }
    gr.add(new RunnableGroup.Action(LABEL_OPEN_DIALOG_SELECT_ALIAS){
        @Override
        public void runSafe(){
            openDialog_SelectAlias().observe(new ObserverStateE(this){
                @Override
                public void onComplete(){
                    next();
                }
                @Override
                public void onException(Throwable e){
                    putException(e);
                    done();
                }
                @Override
                public void onCancel(){
                    putException(new Throwable("canceled"));
                    done();
                }
            });
        }
    }.name("open dialog SelectAlias"));
    gr.setOnDone(new RunnableGroup.Action(){
        @Override
        public void runSafe(){
            if(getException() != null){
                onSwitchedToCanceled(stateView);
            }
        }
    });
    gr.start();
}
protected boolean switchToAlias(dbKey keyData){
    frmAlias.setValue(ItemKey.class, keyData.getItem());
    if(!isAlias){
        isAlias = true;
        frmPassword.setValue(String.class, null);
        getView().findViewById(R.id.frm_alias_layout).setVisibility(View.VISIBLE);
        getView().findViewById(R.id.frm_password_layout).setVisibility(View.INVISIBLE);
        onSwitchedToAlias(stateView);
        return true;
    } else {
        return false;
    }
}
protected boolean switchToPassword(boolean showKeyBoard){
    boolean done = false;
    if(isAlias){
        isAlias = false;
        frmAlias.setValue(ItemKey.class, null);
        getView().findViewById(R.id.frm_password_layout).setVisibility(View.VISIBLE);
        getView().findViewById(R.id.frm_alias_layout).setVisibility(View.INVISIBLE);
        onSwitchedToPassword(stateView);
        done = true;
    }
    if(showKeyBoard){
        PostToHandler.of(getView(), new RunnableW(){
            @Override
            public void runSafe(){
                AppKeyboard.show(frmPassword);
            }
        });
    }
    return done;
}

protected void onPasswordChanged(StateView stateView){
    restoreButtons();
}
protected void onSwitchedToPassword(StateView stateView){
    onPasswordChanged(stateView);
}
protected void onSwitchedToAlias(StateView stateView){
    onPasswordChanged(stateView);
}
protected void onSwitchedToCanceled(StateView stateView){
    restoreButtons();
}

protected boolean canStart(){
    FocusCemetery.request(getView());
    return (!isAlias && frmPassword.isValid()) || (isAlias && frmAlias.isValid());
}
protected dbKey getDataKey(boolean loadKeyRing){
    if(!isAlias || !frmAlias.isValid()){
        return null;
    } else {
        FormItemKeyAdapter keyAdapter = (FormItemKeyAdapter)frmAlias.getEntry();
        ItemKey itemKey = UtilsKey.get(keyAdapter.getValue().getUid());
        if(itemKey == null){
            return null;
        } else {
            dbKey dataKey = new dbKey(itemKey);
            if(loadKeyRing){
                dataKey.loadKeyRing(true);
            }
            return dataKey;
        }
    }
}

protected TaskState.Observable openDialog_OpenKeystore(boolean waitNavigateConfirmedToClose){
    TaskState task = new TaskState();
    DialogOpenKeystore.State state = new DialogOpenKeystore.State();
    DialogOpenKeystore.Param param = state.obtainParam();
    param.setTitle(UserAuth.hasKeystore() ? R.string.lbl_open_keystore_title : R.string.lbl_create_keystore_title).setCancelButtonText(R.string.btn_cancel).setConfirmButtonText(R.string.btn_confirm);
    Navigate.To(DialogOpenKeystore.class, state).observe(new ObserverValueE<DialogOpenKeystore>(this){
        @Override
        public void onComplete(DialogOpenKeystore dialog){
            observeDialog_OpenKeystore(task, dialog, waitNavigateConfirmedToClose);
        }
        @Override
        public void onException(DialogOpenKeystore dialog, Throwable e){
            task.notifyException(e);
        }
    });
    return task.getObservable();
}
private void observeDialog_OpenKeystore(TaskState task, DialogOpenKeystore dialog_OpenKeystore, boolean waitNavigateConfirmedToClose){
    dialog_OpenKeystore.observe(new ObserverEvent<Event.Is, Object>(this, Event.ON_CONFIRM){
        @Override
        public void onComplete(Event.Is event, Object o){
            PostToHandler.of(getView(), new RunnableW(){
                @Override
                public void runSafe(){
                    frmAlias.showError(false);
                    task.notifyComplete();
                    NavigationHelper.close(dialog_OpenKeystore, waitNavigateConfirmedToClose);
                }
            });
        }
    });
    dialog_OpenKeystore.observe(new ObserverEvent<Event.Is, Object>(this, Event.ON_CANCEL){
        @Override
        public void onComplete(Event.Is event, Object o){
            task.notifyException("open keystore canceled");
        }
    });
}

private TaskState.Observable openDialog_SelectAlias(){
    TaskState task = new TaskState();
    KeyDataManager dataManager = new KeyDataManager();
    dataManager.resetViewIterator();
    dataManager.setDefaultFilter(new KeyDataManager.Filter(DescriptionKey.Field.ALIAS, LIKE));
    KeyRowManager rowManager = new KeyRowManager(dataManager);
    DialogModalRecycler.State<dbKey> state = new DialogModalRecycler.State<>();
    DialogModalRecycler.Param<dbKey> param = state.obtainParam();
    param.enableConfirmOnShortClick(true);
    param.setTitle(R.string.lbl_select_key_title);
    param.setCheckBoxText(R.string.chk_remember_my_choice);
    param.setCheckboxPreferenceKey(SP_CIPHER_REMEMBER_ALIAS_CHECKBOX_BOOL);
    param.setRowManager(rowManager);
    param.setSwipper(new KeySwipper());
    param.setRecyclerDefaultDecorationDrawable();
    param.setSubstituteText(R.string.lbl_no_keys, 100);
    dbKey keyData = getDataKey(false);
    if(keyData != null){
        param.setInitialValue(keyData);
    }
    Navigate.To(DialogModalRecycler.class, state).observe(new ObserverValueE<DialogModalRecycler>(this){
        @Override
        public void onComplete(DialogModalRecycler dialog){
            observeDialog_SelectAlias(task, dialog);
        }
        @Override
        public void onException(DialogModalRecycler dialog, Throwable e){
            task.notifyException(e);
        }
    });
    return task.getObservable();
}
private void observeDialog_SelectAlias(TaskState task, DialogModalRecycler<dbKey> dialog_SelectAlias){
    dialog_SelectAlias.observe(new ObserverEvent<Event.Is, Object>(this, Event.ON_CLICK_SHORT){
        @Override
        public void onComplete(Event.Is event, Object o){
            if(o instanceof dbKey){
                dbKey keyData = (dbKey)o;
                SharedPreferences sp = Application.sharedPreferences();
                if(Compare.isTrue(sp.getBoolean(SP_CIPHER_REMEMBER_ALIAS_CHECKBOX_BOOL))){
                    sp.put(SP_CIPHER_REMEMBER_ALIAS_UID_BYTES, keyData.getUid());
                }
                if(switchToAlias(keyData)){
                    task.notifyComplete();
                } else {
                    task.cancel();
                    task.notifyCanceled();
                }
            }
        }
    });
    dialog_SelectAlias.observe(new ObserverEvent<Event.Is, Object>(this, Event.ON_CANCEL){
        @Override
        public void onComplete(Event.Is event, Object o){
            task.cancel();
            task.notifyCanceled();
        }
    });
    dialog_SelectAlias.observe(new ObserverEvent<Event.Is, Object>(this, Event.ON_ACTION){
        @Override
        public void onComplete(Event.Is event, Object o){
            openDialog_MenuAction(task, dialog_SelectAlias);
        }
    });
    KeyDataManager dataManager = (KeyDataManager)((DialogModalRecycler)dialog_SelectAlias).getParam().getRowManager().getDataManager();
    dataManager.observe(new ObserverEvent<Event.Is, dbKey>(this, ON_REMOVE){
        @Override
        public void onComplete(Event.Is event, dbKey dataKeyRemoved){
            if(isAlias){
                SharedPreferences sp = Application.sharedPreferences();
                defUid uidAlias = UidBase.fromBytes(sp.getBytes(SP_CIPHER_REMEMBER_ALIAS_UID_BYTES));
                if(Compare.equalsAndNotNull(uidAlias, dataKeyRemoved.getUid())){
                    sp.remove(SP_CIPHER_REMEMBER_ALIAS_UID_BYTES);
                }
                String currentAlias = frmAlias.getValue();
                if(Compare.equals(currentAlias, dataKeyRemoved.getItem().getAlias())){
                    switchToPassword(true);
                }
            }
        }
    });
    dataManager.observe(new ObserverEvent<Event.Is, dbKey>(this, com.tezov.crypter.application.Event.ON_CLICK_EDIT){
        @Override
        public void onComplete(Event.Is event, dbKey data){
            openDialog_EditKey(data);
        }
    });
    dataManager.observe(new ObserverEvent<Event.Is, dbKey>(this, com.tezov.crypter.application.Event.ON_CLICK_SHARE){
        @Override
        public void onComplete(Event.Is event, dbKey data){
            openDialog_ExportKey(data);
        }
    });
}
private void openDialog_EditKey(dbKey data){
    DialogEditKey.State state = new DialogEditKey.State();
    DialogEditKey.Param param = state.obtainParam();
    param.setDataKey(data);
    param.setTitle(R.string.lbl_edit_key_title).setCancelButtonText(R.string.btn_cancel).setConfirmButtonText(R.string.btn_confirm);
    Navigate.To(DialogEditKey.class, state);
}
private void openDialog_ExportKey(dbKey data){
    DialogExportKey.State state = new DialogExportKey.State();
    DialogExportKey.Param param = state.obtainParam();
    param.setDataKey(data);
    param.setTitle(R.string.lbl_share_key_title)
            .setCancelButtonText(R.string.btn_cancel).setConfirmButtonText(R.string.btn_confirm);
    Navigate.To(DialogExportKey.class, state);
}

private void openDialog_MenuAction(TaskState task, DialogModalRecycler<dbKey> dialog_SelectAlias){
    DialogMenuAction.open().observe(new ObserverValueE<DialogMenuAction>(this){
        @Override
        public void onComplete(DialogMenuAction dialog){
            dialog.observe(new ObserverEvent<Event.Is, Object>(this, Event.ON_CLICK_SHORT){
                @Override
                public void onComplete(Event.Is event, Object o){
                    int id = (int)o;
                    boolean waitNavigateConfirmed = true;
                    if(id == R.id.btn_create_key){
                        openDialog_CreateKey(task, dialog_SelectAlias);
                    } else if(id == R.id.btn_import_key_shared){
                        openDialog_ImportKey(task, dialog_SelectAlias);
                    } else if(id == R.id.btn_import_key_all){
                        openDialog_ImportKeyAll(task, dialog_SelectAlias);
                    } else if(id == R.id.btn_export_key_all){
                        openDialog_ExportKeyAll(task, dialog_SelectAlias);
                    } else {
DebugException.start().log("unknown button id").end();
                        task.cancel();
                        task.notifyCanceled();
                        waitNavigateConfirmed = false;
                    }
                    NavigationHelper.close(dialog, waitNavigateConfirmed);
                }
            });
        }
        @Override
        public void onException(DialogMenuAction dialog, Throwable e){

        }
    });
}

private void openDialog_ImportKey(TaskState task, DialogModalRecycler<dbKey> dialog_SelectAlias){
    DialogImportKey.State state = new DialogImportKey.State();
    DialogImportKey.Param param = state.obtainParam();
    param.setTitle(R.string.lbl_import_key_title)
        .setCancelButtonText(R.string.btn_cancel).setConfirmButtonText(R.string.btn_confirm);
    Navigate.To(DialogImportKey.class, state).observe(new ObserverValueE<DialogImportKey>(this){
        @Override
        public void onComplete(DialogImportKey dialog){
            observeDialog_ImportKey(task, dialog_SelectAlias, dialog);
        }
        @Override
        public void onException(DialogImportKey dialog, Throwable e){

        }
    });
}
private void observeDialog_ImportKey(TaskState task, DialogModalRecycler<dbKey> dialog_SelectAlias, DialogImportKey dialog_ImportKey){
    dialog_ImportKey.observe(new ObserverEvent<Event.Is, Object>(this, Event.ON_CONFIRM){
        @Override
        public void onComplete(Event.Is event, Object keyData){
            if(keyData instanceof dbKey){
                switchToAlias((dbKey)keyData);
                dialog_SelectAlias.close();
                task.notifyComplete();
            }
        }
    });
}

private void openDialog_ImportKeyAll(TaskState task, DialogModalRecycler<dbKey> dialog_SelectAlias){
    DialogImportKeyAll.State state = new DialogImportKeyAll.State();
    DialogImportKeyAll.Param param = state.obtainParam();
    param.setTitleIcon(R.drawable.ic_import_24dp).setTitle(R.string.lbl_import_keys_title).setCancelButtonText(R.string.btn_cancel).setConfirmButtonText(R.string.btn_confirm);
    Navigate.To(DialogImportKeyAll.class, state).observe(new ObserverValueE<DialogImportKeyAll>(this){
        @Override
        public void onComplete(DialogImportKeyAll dialog){
            observeDialog_ImportKeyAll(task, dialog_SelectAlias, dialog);
        }
        @Override
        public void onException(DialogImportKeyAll dialog, Throwable e){

        }
    });
}
private void observeDialog_ImportKeyAll(TaskState task, DialogModalRecycler<dbKey> dialog_SelectAlias, DialogImportKeyAll dialog_ImportKey){
    dialog_ImportKey.observe(new ObserverEvent<Event.Is, Object>(this, Event.ON_CLOSE){
        @Override
        public void onComplete(Event.Is event, Object o){
            dialog_SelectAlias.close();
// TODO better fix bug layout recycler not updated
// with the imported keys instead of closing all to hide the bug ;)
            task.cancel();
            task.notifyCanceled();
        }
    });
}

private void openDialog_ExportKeyAll(TaskState task, DialogModalRecycler<dbKey> dialog_SelectAlias){
    DialogExportKeyAll.State state = new DialogExportKeyAll.State();
    DialogExportKeyAll.Param param = state.obtainParam();
    param.setTitleIcon(R.drawable.ic_export_24dp).setCancelButtonText(R.string.btn_cancel).setConfirmButtonText(R.string.btn_confirm).setTitle(R.string.lbl_export_keys_title);
    Navigate.To(DialogExportKeyAll.class, state).observe(new ObserverValueE<DialogExportKeyAll>(this){
        @Override
        public void onComplete(DialogExportKeyAll dialog){
            observeDialog_ExportKeyAll(task, dialog_SelectAlias, dialog);
        }
        @Override
        public void onException(DialogExportKeyAll dialog, Throwable e){

        }
    });

}
private void observeDialog_ExportKeyAll(TaskState task, DialogModalRecycler<dbKey> dialog_SelectAlias, DialogExportKeyAll dialog_ExportKey){

}

private void openDialog_CreateKey(TaskState task, DialogModalRecycler<dbKey> dialog_SelectAlias){
    DialogCreateKey.State state = new DialogCreateKey.State();
    DialogCreateKey.Param param = state.obtainParam();
    param.setTitle(R.string.lbl_create_key_title).setCancelButtonText(R.string.btn_cancel).setConfirmButtonText(R.string.btn_confirm);
    Navigate.To(DialogCreateKey.class, state).observe(new ObserverValueE<DialogCreateKey>(this){
        @Override
        public void onComplete(DialogCreateKey dialog){
            observeDialog_CreateKey(task, dialog_SelectAlias, dialog);
        }
        @Override
        public void onException(DialogCreateKey dialog, Throwable e){

        }
    });

}
private void observeDialog_CreateKey(TaskState task, DialogModalRecycler<dbKey> dialog_SelectAlias, DialogCreateKey dialog_CreateKey){
    dialog_CreateKey.observe(new ObserverEvent<Event.Is, Object>(this, Event.ON_CONFIRM){
        @Override
        public void onComplete(Event.Is event, Object o){
            if(o instanceof dbKey){
                dbKey keyData = (dbKey)o;
                SharedPreferences sp = Application.sharedPreferences();
                if(Compare.isTrue(sp.getBoolean(SP_CIPHER_REMEMBER_ALIAS_CHECKBOX_BOOL))){
                    sp.put(SP_CIPHER_REMEMBER_ALIAS_UID_BYTES, keyData.getUid());
                }
                dialog_SelectAlias.close();
                if(switchToAlias(keyData)){
                    task.notifyComplete();
                } else {
                    task.cancel();
                    task.notifyCanceled();
                }
            }
        }
    });
}

private void openDialog_ImportKey(NavigationArguments arguments){
    RunnableGroup gr = new RunnableGroup(this).name("selectAlias");
    int LABEL_OPEN_DIALOG_IMPORT_KEY = gr.label();
    int KEY_DIALOG_IMPORT_KEY = gr.key();
    if(!UserAuth.isKeystoreOpened()){
        gr.add(new RunnableGroup.Action(){
            @Override
            public void runSafe(){
                Application.userAuth().signIn(null).observe(new ObserverValueE<defAuthMethod.State.Is>(this){
                    @Override
                    public void onComplete(defAuthMethod.State.Is state){
                        if(Application.userAuth().isAuthenticated()){
                            skipUntilLabel(LABEL_OPEN_DIALOG_IMPORT_KEY);
                        } else {
                            next();
                        }
                    }
                    @Override
                    public void onException(defAuthMethod.State.Is state, Throwable e){
                        next();
                    }
                });
            }
        }.name("try to open keystore"));
        gr.add(new RunnableGroup.Action(){
            @Override
            public void runSafe(){
                openDialog_OpenKeystore(true).observe(new ObserverStateE(this){
                    @Override
                    public void onComplete(){
                        next();
                    }
                    @Override
                    public void onException(Throwable e){
                        putException(e);
                        done();
                    }
                });
            }
        }.name("open dialog DialogOpenKeystore"));
    }
    gr.add(new RunnableGroup.Action(LABEL_OPEN_DIALOG_IMPORT_KEY){
        @Override
        public void runSafe(){
            DialogImportKey.State state = new DialogImportKey.State();
            DialogImportKey.Param param = state.obtainParam();
            param.setTitle(R.string.lbl_import_key_title).setCancelButtonText(R.string.btn_cancel).setConfirmButtonText(R.string.btn_confirm);
            Navigate.To(DialogImportKey.class, state, arguments).observe(new ObserverValueE<DialogImportKey>(this){
                @Override
                public void onComplete(DialogImportKey dialog){
                    put(KEY_DIALOG_IMPORT_KEY, dialog);
                    next();
                }
                @Override
                public void onException(DialogImportKey dialog, Throwable e){
                    putException(e);
                    done();
                }
            });
        }
    }.name("open dialog ImportKey"));
    gr.add(new RunnableGroup.Action(){
        @Override
        public void runSafe(){
            DialogImportKey dialog_ImportKey = get(KEY_DIALOG_IMPORT_KEY);
            dialog_ImportKey.observe(new ObserverEvent<Event.Is, Object>(this, Event.ON_CONFIRM){
                @Override
                public void onComplete(Event.Is event, Object keyData){
                    if(keyData instanceof dbKey){
                        switchToAlias((dbKey)keyData);
                        next();
                    }
                }
            });
            dialog_ImportKey.observe(new ObserverEvent<Event.Is, Object>(this, Event.ON_CANCEL){
                @Override
                public void onComplete(Event.Is event, Object o){
                    done();
                }
            });
        }
    }.name("observe dialog ImportKey"));
    gr.setOnDone(new RunnableGroup.Action(){
        @Override
        public void runSafe(){
            Throwable e = getException();
            if(e != null){
DebugException.start().log(e).end();
            }
        }
    });
    gr.start();
}
private void openDialog_ExportKey(NavigationArguments arguments){
    RunnableGroup gr = new RunnableGroup(this).name("selectAlias");
    int LABEL_OPEN_DIALOG_IMPORT_KEY = gr.label();
    int KEY_DIALOG_SELECT_ALIAS = gr.key();
    int KEY_DIALOG_IMPORT_KEY = gr.key();
    if(!UserAuth.isKeystoreOpened()){
        gr.add(new RunnableGroup.Action(){
            @Override
            public void runSafe(){
                Application.userAuth().signIn(null).observe(new ObserverValueE<defAuthMethod.State.Is>(this){
                    @Override
                    public void onComplete(defAuthMethod.State.Is state){
                        if(Application.userAuth().isAuthenticated()){
                            skipUntilLabel(LABEL_OPEN_DIALOG_IMPORT_KEY);
                        } else {
                            next();
                        }
                    }
                    @Override
                    public void onException(defAuthMethod.State.Is state, Throwable e){
                        next();
                    }
                });
            }
        }.name("try to open keystore"));
        gr.add(new RunnableGroup.Action(){
            @Override
            public void runSafe(){
                openDialog_OpenKeystore(true).observe(new ObserverStateE(this){
                    @Override
                    public void onComplete(){
                        next();
                    }
                    @Override
                    public void onException(Throwable e){
                        putException(e);
                        done();
                    }
                });
            }
        }.name("open dialog DialogOpenKeystore"));
    }
    gr.add(new RunnableGroup.Action(LABEL_OPEN_DIALOG_IMPORT_KEY){
        @Override
        public void runSafe(){
            KeyDataManager dataManager = new KeyDataManager();
            dataManager.resetViewIterator();
            dataManager.setDefaultFilter(new KeyDataManager.Filter(DescriptionKey.Field.ALIAS, LIKE));
            KeyRowManager rowManager = new KeyRowManager(dataManager);
            DialogModalRecycler.State<dbKey> state = new DialogModalRecycler.State<>();
            DialogModalRecycler.Param<dbKey> param = state.obtainParam();
            param.enableConfirmOnShortClick(true);
            param.setTitle(R.string.lbl_select_key_to_share_title);
            param.setRowManager(rowManager);
            param.setRecyclerDefaultDecorationDrawable();
            param.setSubstituteText(R.string.lbl_no_keys, 100);
            param.setHideActionButton(true);
            dbKey keyData = getDataKey(false);
            if(keyData != null){
                param.setInitialValue(keyData);
            }
            Navigate.To(DialogModalRecycler.class, state).observe(new ObserverValueE<DialogModalRecycler>(this){
                @Override
                public void onComplete(DialogModalRecycler dialog){
                    put(KEY_DIALOG_SELECT_ALIAS, dialog);
                    next();
                }
                @Override
                public void onException(DialogModalRecycler dialog, Throwable e){
                    putException(e);
                    done();
                }
            });

        }
    }.name("open dialog SelectAlias"));
    gr.add(new RunnableGroup.Action(){
        @Override
        public void runSafe(){
            DialogModalRecycler dialog_SelectAlias = get(KEY_DIALOG_SELECT_ALIAS);
            dialog_SelectAlias.observe(new ObserverEvent<Event.Is, Object>(this, Event.ON_CLICK_SHORT){
                @Override
                public void onComplete(Event.Is event, Object keyData){
                    putValue((dbKey)keyData);
                    next();
                }
            });
            dialog_SelectAlias.observe(new ObserverEvent<Event.Is, Object>(this, Event.ON_CANCEL){
                @Override
                public void onComplete(Event.Is event, Object o){
                    done();
                }
            });
        }
    }.name("observe dialog SelectAlias"));
    gr.add(new RunnableGroup.Action(){
        @Override
        public void runSafe(){
            DialogExportKey.State state = new DialogExportKey.State();
            DialogExportKey.Param param = state.obtainParam();
            param.setTitle(R.string.lbl_share_key_title).setCancelButtonText(R.string.btn_cancel).setConfirmButtonText(R.string.btn_confirm);
            param.setDataKey(getValue());
            Navigate.To(DialogExportKey.class, state, arguments).observe(new ObserverValueE<>(this){
                @Override
                public void onComplete(DialogExportKey dialog){
                    put(KEY_DIALOG_IMPORT_KEY, dialog);
                    next();
                }
                @Override
                public void onException(DialogExportKey dialog, Throwable e){
                    putException(e);
                    done();
                }
            });
        }
    }.name("open dialog ExportKey"));
    gr.add(new RunnableGroup.Action(){
        @Override
        public void runSafe(){
            DialogExportKey dialog_ExportKey = get(KEY_DIALOG_IMPORT_KEY);
            dialog_ExportKey.observe(new ObserverEvent<Event.Is, Object>(this, Event.ON_CLOSE){
                @Override
                public void onComplete(Event.Is event, Object o){
                    done();
                }
            });
        }
    }.name("observe dialog ExportKey"));
    gr.setOnDone(new RunnableGroup.Action(){
        @Override
        public void runSafe(){
            Throwable e = getException();
            if(e != null){
DebugException.start().log(e).end();
            }
        }
    });
    gr.start();
}

}

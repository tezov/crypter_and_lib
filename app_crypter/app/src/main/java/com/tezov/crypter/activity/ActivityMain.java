/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.crypter.activity;

import com.tezov.lib_java.type.primitive.IntTo;
import com.tezov.lib_java.toolbox.CompareType;
import com.tezov.lib_java.debug.DebugLog;
import java.util.Set;

import com.tezov.lib_java_android.ads.adMax.AdMaxBanner;
import com.tezov.lib_java_android.ads.adMax.AdMaxInstance;
import com.tezov.lib_java_android.ads.adMax.AdMaxInterstitialCyclicLoadwState;
import com.tezov.lib_java_android.database.sqlLite.filter.chunk.ChunkCommand;
import java.util.List;
import com.tezov.lib_java_android.database.sqlLite.filter.dbFilterOrder;
import androidx.fragment.app.Fragment;
import com.tezov.lib_java.type.unit.UnitByte;
import com.tezov.lib_java.util.UtilsString;
import java.util.LinkedList;
import com.tezov.lib_java.debug.DebugTrack;
import com.tezov.lib_java.type.primitive.ObjectTo;
import static com.tezov.crypter.application.AppConfig.ADMOB_INTERSTITIAL_DELAY_CYCLIC_ms;
import static com.tezov.crypter.application.AppConfig.ADMOB_INTERSTITIAL_DELAY_START_ms;
import static com.tezov.crypter.application.AppConfigKey.ADMOB_FAIL_MAX_COUNT_ALLOWED;
import static com.tezov.crypter.application.AppConfigKey.ADMOB_FAIL_MAX_TIME_ALLOWED_days;
import static com.tezov.crypter.application.AppConfigKey.ADMOB_FAIL_UNTRUST_MAX_TIME_ALLOWED_mn;
import static com.tezov.crypter.application.AppConfigKey.ADMOB_FAIL_UNTRUST_MIN_COUNT_RETABLISH;
import static com.tezov.crypter.application.Application.SKU_NO_ADS;
import static com.tezov.crypter.application.SharePreferenceKey.SP_ADMOB_FAIL_COUNT_INT;
import static com.tezov.crypter.application.SharePreferenceKey.SP_ADMOB_LAST_SUCCEED_TIME_LONG;
import static com.tezov.crypter.application.SharePreferenceKey.SP_ADMOB_UNTRUSTED_BOOL;
import static com.tezov.crypter.application.SharePreferenceKey.SP_ADMOB_UNTRUSTED_RETABLISH_COUNT_INT;
import static com.tezov.crypter.application.SharePreferenceKey.SP_NAVIGATION_LAST_DESTINATION_STRING;
import static com.tezov.crypter.data.table.Descriptions.HISTORY;
import static com.tezov.crypter.navigation.NavigationHelper.DestinationKey.CIPHER_FILE;
import static com.tezov.crypter.navigation.NavigationHelper.DestinationKey.CIPHER_TEXT;
import static com.tezov.crypter.navigation.NavigationHelper.DestinationKey.INFO;
import static com.tezov.crypter.navigation.NavigationHelper.DestinationKey.PREFERENCE;
import static com.tezov.lib_java_android.application.Application.sharedPreferences;
import static com.tezov.lib_java_android.application.SharePreferenceKey.SP_APP_PREVIOUS_LAUNCH_BOOL;
import static com.tezov.lib_java_android.ui.navigation.NavigatorManager.NavigatorKey.FRAGMENT;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.Nullable;

import com.tezov.crypter.R;
import com.tezov.crypter.activity.activityFilter.ActivityFilterDispatcher;
import com.tezov.crypter.application.AppConfig;
import com.tezov.crypter.application.Application;
import com.tezov.crypter.application.ReceiverAlarmKeystore;
import com.tezov.crypter.data.item.ItemHistory;
import com.tezov.crypter.data.table.db.dbHistoryTable;
import com.tezov.crypter.dialog.DialogAdmobFailure;
import com.tezov.crypter.dialog.DialogClearHistoryFile;
import com.tezov.crypter.dialog.DialogDeleteKeystore;
import com.tezov.crypter.dialog.DialogSuggestBuyNoAds;
import com.tezov.crypter.fragment.FragmentCipherBase;
import com.tezov.crypter.fragment.FragmentCipherFile;
import com.tezov.crypter.navigation.NavigationHelper;
import com.tezov.crypter.navigation.ToolbarContent;
import com.tezov.crypter.user.UserAuth;
import com.tezov.lib_java.async.Handler;
import com.tezov.lib_java.async.notifier.observer.event.ObserverEvent;
import com.tezov.lib_java.async.notifier.observer.state.ObserverStateE;
import com.tezov.lib_java.async.notifier.observer.value.ObserverValue;
import com.tezov.lib_java.async.notifier.observer.value.ObserverValueE;
import com.tezov.lib_java.async.notifier.task.TaskState;
import com.tezov.lib_java.async.notifier.task.TaskValue;
import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java.toolbox.Clock;
import com.tezov.lib_java.toolbox.Compare;
import com.tezov.lib_java.type.defEnum.Event;
import com.tezov.lib_java.type.runnable.RunnableW;
import com.tezov.lib_java_android.application.AppContext;
import com.tezov.lib_java_android.application.AppDisplay;
import com.tezov.lib_java_android.application.ConnectivityManager;
import com.tezov.lib_java_android.application.SharedPreferences;
import com.tezov.lib_java_android.toolbox.PostToHandler;
import com.tezov.lib_java_android.ui.activity.ActivityToolbar;
import com.tezov.lib_java_android.ui.fragment.FragmentNavigable;
import com.tezov.lib_java_android.ui.layout.FrameFlipperLayout;
import com.tezov.lib_java_android.ui.navigation.Navigate;
import com.tezov.lib_java_android.ui.navigation.NavigatorManager;
import com.tezov.lib_java_android.ui.toolbar.Toolbar;
import com.tezov.lib_java_android.ui.toolbar.ToolbarBottom;
import com.tezov.lib_java_android.wrapperAnonymous.ViewOnClickListenerW;

import java.util.concurrent.TimeUnit;

public abstract class ActivityMain extends ActivityToolbar{
private final static long UPDATE_TOOLBAR_MENU_DELAY_ms = 100;
private ToolbarContent toolbarContent = null;
private ConnectivityManager.QueryHelper queryConnection = null;

@Override
protected State newState(){
    return new State();
}
@Override
public State getState(){
    return super.getState();
}
@Override
public State obtainState(){
    return super.obtainState();
}

@Override
protected int getLayoutId(){
    return R.layout.activity_main;
}

public ToolbarContent getToolbarContent(){
    return toolbarContent;
}

@Override
public boolean listenKeyboard(){
    return true;
}

@Override
protected void onNewIntent(Intent sourceIntent){
    super.onNewIntent(sourceIntent);
    ActivityFilterDispatcher.onNewIntent(this);
}

@Override
protected void onCreate(@Nullable Bundle savedInstanceState){
    super.onCreate(savedInstanceState);
    AppDisplay.setOrientationPortrait(true);
    toolbarContent = new ToolbarContent(this);
    queryConnection = new ConnectivityManager.QueryHelper(Handler.MAIN()){
        @Override
        public void onConnected(ConnectivityManager.State state){
            getState().resumeAdmob();
        }
        @Override
        public void onDisConnected(ConnectivityManager.State state){
            getState().pauseAdmob();
        }
    }.addGoogleSocketTest();
    ConnectivityManager connectivity = Application.connectivityManager();
    connectivity.addQuery(queryConnection);
}

private void showAdMob(){
    if(queryConnection != null){
        if(Application.isOwnedNoAds()){
            queryConnection.enable(false);
            obtainState().destroyAdmob();
        } else {
            obtainState().createAdmob();
            queryConnection.enable(true);
            if(!ConnectivityManager.isConnected()){
                queryConnection.onDisConnected(null);
            }
        }
    }
}
private TaskValue<Boolean>.Observable mustShowSuggestBuy(){
    if(Application.isOwnedNoAds()){
        return TaskValue.Complete(false);
    }
    TaskValue<Boolean> task = new TaskValue<>();
    DialogSuggestBuyNoAds.isOwned(SKU_NO_ADS).observe(new ObserverValueE<Boolean>(this){
        @Override
        public void onComplete(Boolean isOwned){
            task.notifyComplete(!isOwned && DialogSuggestBuyNoAds.isTrialTimeInterstitialOver() && DialogSuggestBuyNoAds.canShow());
        }
        @Override
        public void onException(Boolean isOwned, Throwable e){

//DebugException.start().log(e).end();

            task.notifyComplete(false);
        }
    });
    return task.getObservable();
}
private void showSuggestBuy(){
    DialogSuggestBuyNoAds.open(false).observe(new ObserverValueE<Boolean>(this){
        @Override
        public void onComplete(Boolean bought){
            showAdMob();
        }
        @Override
        public void onException(Boolean isOwned, Throwable e){

DebugException.start().log(e).end();

            onComplete(false);
        }
    });
}

@Override
public void onPrepare(boolean hasBeenReconstructed){
    super.onPrepare(hasBeenReconstructed);
    boolean adMobPending = false;
    if(!hasBeenReconstructed){
        NavigatorManager.DestinationKey.Is source = Navigate.getSource(this);
        if(source == null){
            if(!ActivityFilterDispatcher.onPrepare(this)){
                NavigationHelper.DestinationKey.Is destination = null;
                SharedPreferences sp = sharedPreferences();
                String destinationString = sp.getString(SP_NAVIGATION_LAST_DESTINATION_STRING);
                if(destinationString != null){
                    destination = NavigationHelper.DestinationKey.find(destinationString);
                }
                if(destination == null){
                    destination = INFO;
                }
                Navigate.observe(new ObserverEvent<NavigatorManager.NavigatorKey.Is, NavigatorManager.Event>(this, FRAGMENT){
                    int id;
                    @Override
                    public void onComplete(NavigatorManager.NavigatorKey.Is navigator, NavigatorManager.Event event){
                        if(event == NavigatorManager.Event.NAVIGATE_TO_CONFIRMED){
                            unsubscribe();
                            getToolbarBottom().setChecked(id);
                        }
                    }
                    ObserverEvent<NavigatorManager.NavigatorKey.Is, NavigatorManager.Event> init(int id){
                        this.id = id;
                        return this;
                    }

                }.init(destination.getId()));
                Navigate.To(destination);
            }
            adMobPending = true;
            mustShowSuggestBuy().observe(new ObserverValue<Boolean>(this){
                @Override
                public void onComplete(Boolean mustShow){
                    if(mustShow){
                        showSuggestBuy();
                    } else {
                        showAdMob();
                    }
                }
            });
        }
    }
    if(!adMobPending){
        showAdMob();
    }
}

@Override
public void onOpen(boolean hasBeenReconstructed, boolean hasBeenRestarted){
    super.onOpen(hasBeenReconstructed, hasBeenRestarted);
    if(hasBeenRestarted && !Application.isOwnedNoAds()){
        queryConnection.enable(true);
    }
}

@Override
protected void onPause(){
    super.onPause();
    queryConnection.enable(false);
}

@Override
protected boolean onCreateMenu(){
    Toolbar toolbar = getToolbar();
    toolbar.setVisibility(View.VISIBLE);
    toolbar.inflateMenu(R.menu.toolbar);
    ToolbarBottom toolbarBottom = getToolbarBottom();
    toolbarBottom.setVisibility(View.VISIBLE);
    toolbarBottom.inflateMenu(R.menu.toolbar_bottom);
    toolbarBottom.getBehavior().enableScrollDrag(false);
    return true;
}

@Override
public boolean onMenuOpened(int featureId, Menu menu){
    updateToolbarMenuItemVisibility();
    return true;
}
@Override
public boolean onMenuItemSelected(Type uiType, Object object){
    if(uiType == Type.TOOLBAR){
        MenuItem menuItem = (MenuItem)object;
        if(menuItem.getItemId() == R.id.mn_setting){
            Navigate.To(PREFERENCE);
        } else if(menuItem.getItemId() == R.id.mn_keystore_close){
            if(UserAuth.isKeystoreCanReOpen()){
                Application.userAuth().signOut();
                FragmentNavigable fr = Navigate.getCurrentFragmentRef();
                if(fr instanceof FragmentCipherBase){
                    fr.requestViewUpdate(FragmentCipherBase.NOTIFY_SIGNED_OUT, null);
                }
            }
        } else if(menuItem.getItemId() == R.id.mn_keystore_delete){
            if(UserAuth.hasKeystore()){
                if(!UserAuth.isKeystoreOpened()){
                    Navigate.To(DialogDeleteKeystore.class, DialogDeleteKeystore.newStateDefault()).observe(new ObserverValueE<DialogDeleteKeystore>(this){
                        @Override
                        public void onComplete(DialogDeleteKeystore dialog){
                            dialog.observe(new ObserverEvent<>(this, Event.ON_CONFIRM){
                                @Override
                                public void onComplete(Event.Is event, Object o){
                                    UserAuth.deleteKeystore();
                                    FragmentNavigable fr = Navigate.getCurrentFragmentRef();
                                    if(fr instanceof FragmentCipherFile){
                                        fr.requestViewUpdate(FragmentCipherFile.NOTIFY_RECYCLER_RESET, null);
                                    }
                                    if(fr instanceof FragmentCipherBase){
                                        fr.requestViewUpdate(FragmentCipherBase.NOTIFY_SWITCH_TO_PASSWORD, null);
                                    }
                                }
                            });
                        }
                        @Override
                        public void onException(DialogDeleteKeystore dialog, Throwable e){
DebugException.start().log(e).end();
                        }
                    });
                }
            }
        } else if(menuItem.getItemId() == R.id.mn_history_file_delete){
            Navigate.To(DialogClearHistoryFile.class, DialogClearHistoryFile.newStateDefault()).observe(new ObserverValueE<DialogClearHistoryFile>(this){
                @Override
                public void onComplete(DialogClearHistoryFile dialog){
                    dialog.observe(new ObserverEvent<>(this, Event.ON_CONFIRM){
                        @Override
                        public void onComplete(Event.Is event, Object o){
                            Handler.SECONDARY().post(this, new RunnableW(){
                                @Override
                                public void runSafe(){
                                    dbHistoryTable.Ref table = Application.tableHolder().handle().getMainRef(HISTORY);
                                    table.remove(ItemHistory.Type.FILE);
                                }
                            });
                        }
                    });
                }
                @Override
                public void onException(DialogClearHistoryFile dialog, Throwable e){
DebugException.start().log(e).end();
                }
            });
        }
        return true;
    }
    if(uiType == Type.TOOLBAR_BOTTOM){
        SharedPreferences sp = sharedPreferences();
        MenuItem menuItem = (MenuItem)object;
        if(menuItem.getItemId() == R.id.mn_cipher_file){
            sp.put(SP_NAVIGATION_LAST_DESTINATION_STRING, CIPHER_FILE.name());
            Navigate.To(CIPHER_FILE);
        } else if(menuItem.getItemId() == R.id.mn_cipher_text){
            sp.put(SP_NAVIGATION_LAST_DESTINATION_STRING, CIPHER_TEXT.name());
            Navigate.To(CIPHER_TEXT);
        } else if(menuItem.getItemId() == R.id.mn_info){
            sp.put(SP_NAVIGATION_LAST_DESTINATION_STRING, INFO.name());
            Navigate.To(INFO);
        }
        return true;
    }
    return false;
}

public void updateToolbarMenuItemVisibility(){
    updateToolbarMenuItemVisibility(0);
}
public void updateToolbarMenuItemVisibilityDelayed(){
    updateToolbarMenuItemVisibility(UPDATE_TOOLBAR_MENU_DELAY_ms);
}
private void updateToolbarMenuItemVisibility(long delay){
    Toolbar toolbar = getToolbar();
    PostToHandler.of(toolbar, delay, new RunnableW(){
        @Override
        public void runSafe(){
            boolean isKeyStoreOpen = UserAuth.isKeystoreOpened() || UserAuth.isKeystoreCanReOpen();
            Menu menu = toolbar.getMenu();
            MenuItem itemKeystoreClose = menu.findItem(R.id.mn_keystore_close);
            itemKeystoreClose.setVisible(isKeyStoreOpen);
            MenuItem itemKeystoreDelete = menu.findItem(R.id.mn_keystore_delete);
            itemKeystoreDelete.setVisible(!isKeyStoreOpen && UserAuth.hasKeystore());
            MenuItem itemHistoryFileDelete = menu.findItem(R.id.mn_history_file_delete);
            dbHistoryTable.Ref table = Application.tableHolder().handle().getMainRef(HISTORY);
            itemHistoryFileDelete.setVisible(table.size(ItemHistory.Type.FILE) > 0);
        }
    });
}

@Override
protected void onDestroy(){
    ReceiverAlarmKeystore.cancel(this);
    if(queryConnection != null){
        queryConnection.remove();
        queryConnection = null;
    }
    super.onDestroy();
}

@Override
public void removedFromStack(){
    if(hasState()){
        getState().destroyAdmob();
    }
    super.removedFromStack();
}

public TaskState.Observable showInterstitial(){
    if(hasState()){
        return getState().showInterstitial();
    } else {
        return TaskState.Exception("not available");
    }
}
public void setBannerVisible(boolean flag){
    if(hasState()){
        getState().setBannerVisible(flag);
    }
}
public static class State extends ActivityToolbar.State{
    private AdMaxBanner banner = null;
    private AdMaxInterstitialCyclicLoadwState interstitial = null;
    public ActivityMain getActivity(){
        return getOwner();
    }
    @Override
    public void attach(Object owner){
        super.attach(owner);
        attach((ActivityMain)owner);
    }
    private void attach(ActivityMain activity){

    }
    private TaskState.Observable showInterstitial(){
        if((interstitial != null) && interstitial.isReadyToBeShown()){
            return interstitial.show();
        } else {
            return TaskState.Exception("not available");
        }
    }
    private void createAdmob(){
        AdMaxInstance admob = Application.adMob();
        if(admob == null){
            return;
        }
        admob.post(new RunnableW(){
            @Override
            public void runSafe(){
                if(DialogSuggestBuyNoAds.isTrialTimeBannerOver() && (banner == null)){
                    FrameFlipperLayout adView = getActivity().findViewById(R.id.ad_view_banner_container);
                    adView.setResizeAllViewAtMaxSize(true);
                    View bannerDefaultView = adView.findViewById(R.id.ad_view_banner_default);
                    bannerDefaultView.setOnClickListener(new ViewOnClickListenerW(){
                        @Override
                        public void onClicked(View v){
                            if(ConnectivityManager.isConnected()){
                                DialogSuggestBuyNoAds.open(true).observe(new ObserverValueE<Boolean>(this){
                                    @Override
                                    public void onComplete(Boolean isOwned){
                                        if(isOwned){
                                            getActivity().queryConnection.enable(false);
                                            destroyAdmob();
                                        }
                                    }
                                    @Override
                                    public void onException(Boolean isOwned, Throwable e){
//                                        DebugException.pop().produce(e).log().pop();
                                    }
                                });
                            }
                        }
                    });
                    PostToHandler.of(adView, new RunnableW(){
                        @Override
                        public void runSafe(){
                            adView.setVisibility(View.VISIBLE);
                        }
                    });
                    banner = new AdMaxBanner(AppContext.getResources().getString(R.string.ad_banner_id));
                }
                if(DialogSuggestBuyNoAds.isTrialTimeInterstitialOver() && (interstitial == null)){
                    interstitial = new AdMaxInterstitialCyclicLoadwState(AppContext.getResources().getString(R.string.ad_interstitial_id), ADMOB_INTERSTITIAL_DELAY_CYCLIC_ms).mute(true);
                }
            }
        });
    }
    private void destroyAdmob(){
        AdMaxInstance admob = Application.adMob();
        if(admob != null){
            admob.clearPendings();
        }
        if(interstitial != null){
            interstitial.destroy();
            interstitial = null;
        }
        if(banner != null){
            banner.destroy();
            banner = null;
            FrameFlipperLayout view = getActivity().findViewById(R.id.ad_view_banner_container);
            PostToHandler.of(view, new RunnableW(){
                @Override
                public void runSafe(){
                    view.setVisibility(View.GONE);
                }
            });
        }
    }
    private void resumeAdmob(){
        AdMaxInstance admob = Application.adMob();
        if(admob == null){
            return;
        }
        admob.post(new RunnableW(){
            @Override
            public void runSafe(){
                if(interstitial != null){
                    if(!interstitial.isStarted()){
                        interstitial.start(ADMOB_INTERSTITIAL_DELAY_START_ms);
                    } else {
                        interstitial.resume();
                    }
                }
                if(banner != null){
                    SharedPreferences sp = Application.sharedPreferences();
                    sp.put(SP_ADMOB_LAST_SUCCEED_TIME_LONG, Clock.MilliSecond.now());
                    if(!Compare.equalsOrNull(sp.getInt(SP_ADMOB_FAIL_COUNT_INT), 0)){
                        sp.put(SP_ADMOB_FAIL_COUNT_INT, 0);
                    }
                    if(Compare.isTrue(sp.getBoolean(SP_ADMOB_UNTRUSTED_BOOL))){
                        int trustRetablishmentCount = sp.getInt(SP_ADMOB_UNTRUSTED_RETABLISH_COUNT_INT) + 1;
                        if(trustRetablishmentCount > AppConfig.getInt(ADMOB_FAIL_UNTRUST_MIN_COUNT_RETABLISH.getId())){
                            sp.put(SP_ADMOB_UNTRUSTED_BOOL, false);
                        } else {
                            sp.put(SP_ADMOB_UNTRUSTED_RETABLISH_COUNT_INT, trustRetablishmentCount);
                        }
                    }
                    banner.setContainer(getActivity().findViewById(R.id.ad_view_banner_container)).resume().observe(new ObserverStateE(this){
                        @Override
                        public void onComplete(){

                        }
                        @Override
                        public void onCancel(){
                        }
                        @Override
                        public void onException(Throwable e){
                        }
                    });
                }
            }
        });
    }
    private void pauseAdmob(){
        AdMaxInstance admob = Application.adMob();
        if(admob != null){
            admob.clearPendings();
        }
        if(interstitial != null){
            interstitial.pause();
        }
        if(banner != null){
            long now = Clock.MilliSecond.now();
            SharedPreferences sp = Application.sharedPreferences();
            Long lastSuccess = sp.getLong(SP_ADMOB_LAST_SUCCEED_TIME_LONG);
            if(lastSuccess == null){
                sp.put(SP_ADMOB_LAST_SUCCEED_TIME_LONG, now);
                lastSuccess = now;
            }
            if(Compare.isTrue(sp.getBoolean(SP_ADMOB_UNTRUSTED_BOOL))){
                lastSuccess += TimeUnit.MILLISECONDS.convert(AppConfig.getLong(ADMOB_FAIL_UNTRUST_MAX_TIME_ALLOWED_mn.getId()), TimeUnit.MINUTES);
            } else {
                lastSuccess += TimeUnit.MILLISECONDS.convert(AppConfig.getLong(ADMOB_FAIL_MAX_TIME_ALLOWED_days.getId()), TimeUnit.DAYS);
            }
            long offsetLastOpening = now - sp.getLong(SP_APP_PREVIOUS_LAUNCH_BOOL);
            lastSuccess += offsetLastOpening;
            if(lastSuccess < now){
                if(Compare.isTrue(sp.getBoolean(SP_ADMOB_UNTRUSTED_BOOL))){
                    sp.put(SP_ADMOB_UNTRUSTED_RETABLISH_COUNT_INT, 0);
                    DialogAdmobFailure.open();
                } else {
                    Integer countFailed = sp.getInt(SP_ADMOB_FAIL_COUNT_INT);
                    if(countFailed == null){
                        countFailed = 1;
                    } else {
                        countFailed++;
                    }
                    sp.put(SP_ADMOB_FAIL_COUNT_INT, countFailed);
                    if(countFailed > AppConfig.getInt(ADMOB_FAIL_MAX_COUNT_ALLOWED.getId())){
                        sp.put(SP_ADMOB_UNTRUSTED_BOOL, true);
                        sp.put(SP_ADMOB_UNTRUSTED_RETABLISH_COUNT_INT, 0);
                        DialogAdmobFailure.open();
                    }
                }
            }
            banner.pause().observe(new ObserverStateE(this){
                @Override
                public void onComplete(){

                }
                @Override
                public void onException(Throwable e){

DebugException.start().log(e).end();

                }
            });
        }
    }
    private void setBannerVisible(boolean flag){
        if(banner != null){
            getActivity().findViewById(R.id.ad_view_banner_container).setVisibility(flag ? View.VISIBLE : View.GONE);
        }
    }

}


}

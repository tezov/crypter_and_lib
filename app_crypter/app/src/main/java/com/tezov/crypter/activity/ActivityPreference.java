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
import com.tezov.lib_java_android.database.sqlLite.filter.chunk.ChunkCommand;
import com.tezov.lib_java_android.database.sqlLite.filter.dbFilterOrder;
import androidx.fragment.app.Fragment;
import com.tezov.lib_java.type.unit.UnitByte;
import com.tezov.lib_java.util.UtilsString;
import com.tezov.lib_java.toolbox.Clock;
import java.util.LinkedList;
import com.tezov.lib_java.debug.DebugTrack;
import com.tezov.lib_java.type.primitive.ObjectTo;
import com.tezov.lib_java_android.application.AppContext;
import com.tezov.lib_java.toolbox.Compare;
import com.tezov.lib_java.debug.DebugException;

import com.tezov.crypter.activity.activityFilter.ActivityFilterDispatcher;
import com.tezov.lib_java_android.ui.toolbar.Toolbar;
import com.tezov.lib_java_android.ui.toolbar.ToolbarBottom;

import static com.tezov.crypter.application.SharePreferenceKey.SP_DESTINATION_DIRECTORY_STRING;
import static com.tezov.crypter.application.SharePreferenceKey.SP_ENCRYPT_KEY_LENGTH_INT;
import static com.tezov.crypter.application.SharePreferenceKey.SP_ENCRYPT_KEY_TRANSFORMATION_INT;
import static com.tezov.crypter.application.SharePreferenceKey.SP_ENCRYPT_SHARE_SUBJECT_PREFIX_STRING;
import static com.tezov.crypter.application.SharePreferenceKey.SP_KEYSTORE_AUTO_CLOSE_DELAY_INT;
import static com.tezov.crypter.application.SharePreferenceKey.SP_KEYSTORE_KEEP_OPEN_BOOLEAN;
import static com.tezov.crypter.application.SharePreferenceKey.SP_KEYSTORE_KEEP_OPEN_DELAY_INT;
import static com.tezov.lib_java_android.application.SharePreferenceKey.FILE_NAME_SHARE_PREFERENCE;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.method.DigitsKeyListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.preference.Preference;

import com.tezov.crypter.R;
import com.tezov.crypter.application.AppInfo;
import com.tezov.crypter.application.Application;
import com.tezov.crypter.dialog.DialogStrictModeConfirm;
import com.tezov.crypter.dialog.DialogSuggestAppRating;
import com.tezov.crypter.dialog.DialogSuggestBuyNoAds;
import com.tezov.crypter.navigation.ToolbarContent;
import com.tezov.crypter.navigation.ToolbarHeaderBuilder;
import com.tezov.lib_java_android.application.AppDisplay;
import com.tezov.lib_java_android.application.SharedPreferences;
import com.tezov.lib_java_android.application.VersionSDK;
import com.tezov.lib_java.async.notifier.observer.event.ObserverEvent;
import com.tezov.lib_java.async.notifier.observer.state.ObserverStateE;
import com.tezov.lib_java.async.notifier.observer.value.ObserverValueE;
import com.tezov.lib_java.cipher.key.KeySim;
import com.tezov.lib_java_android.file.StorageTree;
import com.tezov.lib_java_android.playStore.PlayStore;
import com.tezov.lib_java_android.toolbox.PostToHandler;
import com.tezov.lib_java_android.wrapperAnonymous.PreferenceOnClickListenerW;
import com.tezov.lib_java.type.defEnum.Event;
import com.tezov.lib_java.type.runnable.RunnableW;
import com.tezov.lib_java_android.ui.component.plain.EditTextWithIcon;
import com.tezov.lib_java_android.ui.component.preference.EditTextPreference;
import com.tezov.lib_java_android.ui.component.preference.SwitchPreference;
import com.tezov.lib_java_android.ui.navigation.Navigate;
import com.tezov.lib_java_android.util.UtilsTextWatcher;

import java.util.List;

public class ActivityPreference extends com.tezov.lib_java_android.ui.activity.ActivityPreference{
private ToolbarContent toolbarContent = null;

@Override
protected int getLayoutId(){
    return R.layout.activity_preference;
}
@Override
protected int getPreferenceContainerId(){
    return R.id.container_fragment;
}
public ToolbarContent getToolbarContent(){
    return toolbarContent;
}

@Override
protected void onNewIntent(Intent sourceIntent){
    super.onNewIntent(sourceIntent);
    ActivityFilterDispatcher.onNewIntent(this);
}
@Override
protected void onCreate(Bundle savedInstanceState){
    super.onCreate(savedInstanceState);
    AppDisplay.setOrientationPortrait(true);
    toolbarContent = new ToolbarContent(this);
    AppInfo.privacyPolicySetOnClickListener(this.getViewRoot(), null);
    AppInfo.contactSetOnClickListener(this.getViewRoot());
}

@Override
public void onOpen(boolean hasBeenReconstructed, boolean hasBeenRestarted){
    super.onOpen(hasBeenReconstructed, hasBeenRestarted);
    setToolbarTittle(R.string.activity_preference_policy_title);
}
protected <DATA> void setToolbarTittle(DATA data){
    ToolbarContent toolbarContent = getToolbarContent();
    if(data == null){
        toolbarContent.setToolBarView(null);
    } else {
        ToolbarHeaderBuilder header = new ToolbarHeaderBuilder().setData(data);
        toolbarContent.setToolBarView(header.build(getToolbar()));
    }
}
@Override
protected boolean onCreateMenu(){
    Toolbar toolbar = getToolbar();
    toolbar.setVisibility(View.VISIBLE);
    ToolbarBottom toolbarBottom = getToolbarBottom();
    toolbarBottom.setVisibility(View.GONE);
    return true;
}

@Override
protected FragmentPreference createFragmentPreference(){
    return new FragmentPreference(R.xml.preference);
}

public static class FragmentPreference extends com.tezov.lib_java_android.ui.activity.ActivityPreference.FragmentSharePreference{
    public FragmentPreference(){
    }
    FragmentPreference(int xmlId){
        super(xmlId);
    }
    @Override
    public String getSharedPreferencesName(){
        return FILE_NAME_SHARE_PREFERENCE;
    }
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey){
        super.onCreatePreferences(savedInstanceState, rootKey);
        EditTextPreference preferenceAutoClose = (EditTextPreference)findPreference(R.id.pref_keystore_auto_close_delay);
        preferenceAutoClose.setOnBindEditTextListener(new EditTextPreference.OnBindEditTextListener(){
            @Override
            public void onBindEditText(EditTextWithIcon editText){
                editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                editText.setKeyListener(DigitsKeyListener.getInstance("0123456789"));
                editText.addTextChangedListener(new UtilsTextWatcher.IntRange(0, 30));
            }
        });
        EditTextPreference preferenceKeepOpen = (EditTextPreference)findPreference(R.id.pref_keystore_keep_open_delay);
        preferenceKeepOpen.setOnBindEditTextListener(new EditTextPreference.OnBindEditTextListener(){
            @Override
            public void onBindEditText(EditTextWithIcon editText){
                editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                editText.setKeyListener(DigitsKeyListener.getInstance("0123456789"));
                editText.addTextChangedListener(new UtilsTextWatcher.IntRange(0, 60));
            }
        });
    }
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        View view = super.onCreateView(inflater, container, savedInstanceState);
        Preference preferenceAppVersion = findPreference(R.id.pref_app_version);
        preferenceAppVersion.setSummary(AppContext.getResources().getString(R.string.application_version) + "/" + VersionSDK.getVersion());
        findPreference(R.id.pref_app_share).setOnPreferenceClickListener(new PreferenceOnClickListenerW(){
            @Override
            public boolean onClicked(Preference preference){
                setOnClickListenerEnabled(preference, false);
                shareApp();
                return true;
            }
        });
        Preference preferenceAppRating = findPreference(R.id.pref_app_rating);
        if(!DialogSuggestAppRating.isAlreadyDone()){
            preferenceAppRating.setVisible(true);
            preferenceAppRating.setOnPreferenceClickListener(new PreferenceOnClickListenerW(){
                @Override
                public boolean onClicked(Preference preference){
                    setOnClickListenerEnabled(preference, false);
                    openDialogSuggestReview();
                    return true;
                }
            });
        }
        findPreference(R.id.pref_encrypt_strict_mode).setOnPreferenceClickListener(new PreferenceOnClickListenerW(){
            @Override
            public boolean onClicked(Preference preference){
                setOnClickListenerEnabled(preference, false);
                SwitchPreference switchPreference = (SwitchPreference)findPreference(R.id.pref_encrypt_strict_mode);
                if(switchPreference.isChecked()){
                    openDialogStrictModeConfirm();
                } else {
                    setOnClickListenerEnabled(preference, true);
                }
                return true;
            }
        });
        updatePref_DestinationFolder(sp, findPreference(R.id.pref_destination_directory));
        updatePref_KeystoreAutoCloseDelay(sp, findPreference(R.id.pref_keystore_auto_close_delay));
        updatePref_KeystoreKeepOpenDelay(sp, findPreference(R.id.pref_keystore_keep_open_delay));
        updatePref_EncryptKeyLength(sp, findPreference(R.id.pref_encrypt_key_length));
        updatePref_EncryptKeyTransformation(sp, findPreference(R.id.pref_encrypt_key_transformation));
        updatePref_EncryptShareSubjectPrefix(sp, findPreference(R.id.pref_encrypt_share_subject_prefix_subject));
        return view;
    }
    @Override
    public void onResume(){
        super.onResume();
        updatePrefSkuNoAds(Application.isOwnedNoAds());
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sp, String key, String keyDecoded){
        super.onSharedPreferenceChanged(sp, key, keyDecoded);
        if(SP_KEYSTORE_AUTO_CLOSE_DELAY_INT.equals(keyDecoded)){
            updatePref_KeystoreAutoCloseDelay(sp, findPreference(key));
        }
        else if(SP_KEYSTORE_KEEP_OPEN_BOOLEAN.equals(keyDecoded)){
            updatePref_KeystoreKeepOpenDelay(sp, findPreference(R.id.pref_keystore_keep_open_delay));
        }
        else if(SP_KEYSTORE_KEEP_OPEN_DELAY_INT.equals(keyDecoded)){
            updatePref_KeystoreKeepOpenDelay(sp, findPreference(key));
        }
        else if(SP_ENCRYPT_KEY_LENGTH_INT.equals(keyDecoded)){
            updatePref_EncryptKeyLength(sp, findPreference(key));
        } else if(SP_ENCRYPT_KEY_TRANSFORMATION_INT.equals(keyDecoded)){
            updatePref_EncryptKeyTransformation(sp, findPreference(key));
        } else if(SP_ENCRYPT_SHARE_SUBJECT_PREFIX_STRING.equals(keyDecoded)){
            updatePref_EncryptShareSubjectPrefix(sp, findPreference(key));
        } else if(SP_DESTINATION_DIRECTORY_STRING.equals(keyDecoded)){
            updatePref_DestinationFolder(sp, findPreference(key));
        }
    }
    private void updatePref_DestinationFolder(SharedPreferences sp, Preference pref){
        String value = sp.getString(SP_DESTINATION_DIRECTORY_STRING);
        if(value != null){
            StorageTree uriTree = StorageTree.fromLink(value);
            if(uriTree != null){
                if(uriTree.canWrite()){
                    value = uriTree.getDisplayPath();
                } else {
                    sp.remove(SP_DESTINATION_DIRECTORY_STRING);
                    value = null;
                }
            }
        }
        String summary;
        if(value == null){
            summary = AppContext.getResources().getString(R.string.pref_destination_directory_android_summary);
        } else {
            summary = AppContext.getResources().getString(R.string.pref_destination_directory_summary);
        }
        pref.setSummary(String.format(summary, value));
    }
    private void updatePref_KeystoreKeepOpenDelay(SharedPreferences sp, Preference pref){
        Boolean keepOpen = sp.getBoolean(SP_KEYSTORE_KEEP_OPEN_BOOLEAN);
        if(Compare.isFalseOrNull(keepOpen)){
            pref.setVisible(false);
        }
        else{
            pref.setVisible(true);
            Integer value = sp.getInt(SP_KEYSTORE_KEEP_OPEN_DELAY_INT);
            String summary;
            if(value != null){
                summary = AppContext.getResources().getString(R.string.pref_keystore_keep_open_delay_summary);
                summary = String.format(summary, value);
            }
            else {
                summary = AppContext.getResources().getString(R.string.pref_keystore_keep_open_delay_disabled_summary);
            }
            pref.setSummary(summary);
        }
    }
    private void updatePref_KeystoreAutoCloseDelay(SharedPreferences sp, Preference pref){
        Integer value = sp.getInt(SP_KEYSTORE_AUTO_CLOSE_DELAY_INT);
        String summary;
        if(value != null){
            summary = AppContext.getResources().getString(R.string.pref_keystore_auto_close_delay_summary);
            summary = String.format(summary, value);
        } else {
            summary = AppContext.getResources().getString(R.string.pref_keystore_auto_close_delay_disabled_summary);
        }
        pref.setSummary(summary);
    }
    private void updatePref_EncryptKeyLength(SharedPreferences sp, Preference pref){
        Integer value = sp.getInt(SP_ENCRYPT_KEY_LENGTH_INT);
        if(value == null){
            value = 2;
        }
        List<String> keyLength = AppContext.getResources().getStrings(R.array.encrypt_key_length);
        KeySim.Length length = KeySim.Length.findWithId(value);
        String summary = AppContext.getResources().getString(R.string.pref_encrypt_key_length_summary);
        pref.setSummary(String.format(summary, keyLength.get(length.getId())));
    }
    private void updatePref_EncryptKeyTransformation(SharedPreferences sp, Preference pref){
        Integer value = sp.getInt(SP_ENCRYPT_KEY_TRANSFORMATION_INT);
        if(value == null){
            value = 2;
        }
        List<String> keyTransformation = AppContext.getResources().getStrings(R.array.encrypt_key_transformation);
        KeySim.Transformation transformation = KeySim.Transformation.find(value);
        String summary = AppContext.getResources().getString(R.string.pref_encrypt_key_transformation_summary);
        pref.setSummary(String.format(summary, keyTransformation.get(transformation.getId())));
    }
    private void updatePref_EncryptShareSubjectPrefix(SharedPreferences sp, Preference pref){
        String value = sp.getString(SP_ENCRYPT_SHARE_SUBJECT_PREFIX_STRING);
        if(value == null){
            String summary = AppContext.getResources().getString(R.string.pref_encrypt_share_subject_prefix_summary_none);
            pref.setSummary(summary);
        } else {
            String summary = AppContext.getResources().getString(R.string.pref_encrypt_share_subject_prefix_summary);
            pref.setSummary(String.format(summary, value));
        }

    }
    private void updatePrefSkuNoAds(boolean isOwnedNoAds){
        com.tezov.lib_java_android.application.SharedPreferences sp = Application.sharedPreferences();
        String keySkuNoAds = sp.encodeKey(AppContext.getResources().getIdentifierName(R.id.pref_sku_no_ads));
        Preference prefSkuNoAds = findPreference(keySkuNoAds);
        if(isOwnedNoAds){
            prefSkuNoAds.setTitle(R.string.pref_sku_no_ads_owned_title);
            prefSkuNoAds.setSummary(R.string.pref_sku_no_ads_owned_summary);
            prefSkuNoAds.setIcon(R.drawable.ic_confirm_outline_24dp);
            prefSkuNoAds.setEnabled(false);
        } else {
            prefSkuNoAds.setTitle(R.string.pref_sku_no_ads_buy_full_version_title);
            prefSkuNoAds.setSummary(R.string.pref_sku_no_ads_buy_full_version_summary);
            prefSkuNoAds.setIcon(R.drawable.ic_buy_24dp);
            prefSkuNoAds.setEnabled(true);
            prefSkuNoAds.setOnPreferenceClickListener(new PreferenceOnClickListenerW(){
                @Override
                public boolean onClicked(Preference preference){
                    setOnClickListenerEnabled(preference, false);
                    openDialogSuggestBuyNoAds();
                    return true;
                }
            });
        }
    }

    private void openDialogStrictModeConfirm(){
        Navigate.To(DialogStrictModeConfirm.class, DialogStrictModeConfirm.newStateDefault()).observe(new ObserverValueE<DialogStrictModeConfirm>(this){
            @Override
            public void onComplete(DialogStrictModeConfirm dialog){
                dialog.observe(new ObserverEvent<>(this, Event.ON_CLOSE){
                    @Override
                    public void onComplete(Event.Is event, Object o){
                        setOnClickListenerEnabled(R.id.pref_encrypt_strict_mode, true);
                    }
                });
                dialog.observe(new ObserverEvent<>(this, Event.ON_CANCEL){
                    @Override
                    public void onComplete(Event.Is event, Object o){
                        SwitchPreference switchPreference = (SwitchPreference)findPreference(R.id.pref_encrypt_strict_mode);
                        switchPreference.setChecked(false);

                    }
                });
            }
            @Override
            public void onException(DialogStrictModeConfirm dialog, Throwable e){
DebugException.start().log(e).end();
                setOnClickListenerEnabled(R.id.pref_encrypt_strict_mode, true);
            }
        });
    }
    private void openDialogSuggestBuyNoAds(){
        DialogSuggestBuyNoAds.open(true).observe(new ObserverValueE<Boolean>(this){
            @Override
            public void onComplete(Boolean isOwned){
                if(isOwned){
                    PostToHandler.of(getView(), new RunnableW(){
                        @Override
                        public void runSafe(){
                            updatePrefSkuNoAds(true);
                        }
                    });
                }
                setOnClickListenerEnabled(R.id.pref_sku_no_ads, true);
            }
            @Override
            public void onException(Boolean isOwned, Throwable e){
                setOnClickListenerEnabled(R.id.pref_sku_no_ads, true);
//                DebugException.pop().produce(e).log().pop();
            }
        });
    }
    private void openDialogSuggestReview(){
        DialogSuggestAppRating.open().observe(new ObserverValueE<DialogSuggestAppRating>(this){
            @Override
            public void onComplete(DialogSuggestAppRating dialog){
                dialog.observe(new ObserverEvent<Event.Is, Object>(this, Event.ON_CLOSE){
                    @Override
                    public void onComplete(Event.Is is, Object object){
                        Preference preferenceAppRating = findPreference(R.id.pref_app_rating);
                        if(DialogSuggestAppRating.isAlreadyDone()){
                            preferenceAppRating.setVisible(false);
                        } else {
                            setOnClickListenerEnabled(preferenceAppRating, true);
                        }
                    }
                });
            }
            @Override
            public void onException(DialogSuggestAppRating dialog, Throwable e){
                setOnClickListenerEnabled(R.id.pref_app_rating, true);
            }
        });
    }
    private void shareApp(){
        PlayStore.shareLink().observe(new ObserverStateE(this){
            @Override
            public void onComplete(){
                setOnClickListenerEnabled(R.id.pref_app_share, true);
            }
            @Override
            public void onException(Throwable e){
                setOnClickListenerEnabled(R.id.pref_app_share, true);
            }
        });
    }

}

}

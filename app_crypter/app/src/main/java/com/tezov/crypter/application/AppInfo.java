/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.crypter.application;

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
import com.tezov.lib_java_android.application.AppContext;

import androidx.fragment.app.Fragment;
import static com.tezov.crypter.application.SharePreferenceKey.SP_ENCRYPT_SHARE_SUBJECT_PREFIX_STRING;
import static com.tezov.crypter.navigation.NavigationHelper.DestinationKey.PRIVACY_POLICY;

import android.content.Intent;
import android.os.Build;
import android.view.View;
import android.widget.TextView;

import com.tezov.crypter.R;
import com.tezov.lib_java_android.application.SharedPreferences;
import com.tezov.lib_java.async.notifier.task.TaskState;
import com.tezov.lib_java_android.file.UriW;
import com.tezov.lib_java_android.playStore.PlayStore;
import com.tezov.lib_java_android.type.android.wrapper.BundleW;
import com.tezov.lib_java_android.wrapperAnonymous.ViewOnClickListenerW;
import com.tezov.lib_java_android.ui.navigation.Navigate;
import com.tezov.lib_java_android.ui.navigation.NavigationOption;
import com.tezov.lib_java_android.util.UtilsIntent;

public final class AppInfo extends com.tezov.lib_java_android.application.AppInfo{
public static final String SIGNATURE_KEY_PREFIX = "key-id-";

public static void privacyPolicySetOnClickListener(View view, Boolean keepInStack){
    TextView lblPrivacyPolicy = view.findViewById(R.id.lbl_privacy_policy);
    lblPrivacyPolicy.setOnClickListener(new ViewOnClickListenerW(){
        @Override
        public void onClicked(View v){
            NavigationOption option = null;
            if(keepInStack != null){
                option = new NavigationOption().setKeepInStack_NavTo(keepInStack);
            }
            Navigate.To(PRIVACY_POLICY, option);
        }
    });
}
public static void contactSetOnClickListener(View view){
    TextView lblContact = view.findViewById(R.id.lbl_contact);
    lblContact.setOnClickListener(new ViewOnClickListenerW(){
        @Override
        public void onClicked(View v){
            String target = AppContext.getResources().getString(R.string.app_email);
            String subject = "Contact from " + AppContext.getResources().getString(R.string.app_name);
            subject += "_" + AppContext.getResources().getString(R.string.application_version) + "/" + Build.VERSION.SDK_INT;
            UtilsIntent.emailTo(target, subject, null);
        }
    });
}

public static TaskState.Observable share(Integer subjectResourceId, UriW uri){
    return share(AppContext.getResources().getString(subjectResourceId), uri);
}
public static TaskState.Observable share(String subject, UriW uri){
    if(uri == null){
        return TaskState.Exception("uri out is null");
    }
    BundleW bundle = BundleW.obtain();
    StringBuilder builder = new StringBuilder();
    SharedPreferences sp = Application.sharedPreferences();
    String shareSubject = sp.getString(SP_ENCRYPT_SHARE_SUBJECT_PREFIX_STRING);
    if(shareSubject != null){
        builder.append(shareSubject).append(" / ");
    }
    if(subject != null){
        builder.append(subject).append(" ");
    }
    builder.append(uri.getFullName());
    bundle.put(Intent.EXTRA_SUBJECT, builder.toString());
    bundle.put(Intent.EXTRA_TEXT, AppContext.getResources().getString(R.string.share_encrypt_link) + PlayStore.getPackageLink());
    return uri.send(bundle);
}
public static TaskState.Observable share(String subject, String text){
    return UtilsIntent.send(subject, text);
}
public static TaskState.Observable open(UriW uri){
    if(uri == null){
        return TaskState.Exception("uri out is null");
    } else {
        return uri.open();
    }
}

}

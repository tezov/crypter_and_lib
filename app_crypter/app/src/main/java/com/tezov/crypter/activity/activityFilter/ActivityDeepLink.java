/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.crypter.activity.activityFilter;

import com.tezov.lib_java.debug.DebugTrack;
import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java.type.primitive.ObjectTo;
import com.tezov.lib_java.type.primitive.IntTo;
import com.tezov.lib_java.type.unit.UnitByte;
import com.tezov.lib_java.toolbox.CompareType;
import com.tezov.lib_java.toolbox.Clock;
import com.tezov.lib_java.util.UtilsString;
import java.util.LinkedList;
import java.util.Set;
import com.tezov.lib_java_android.database.sqlLite.filter.dbFilterOrder;
import com.tezov.lib_java_android.database.sqlLite.filter.chunk.ChunkCommand;
import com.tezov.lib_java.debug.DebugLog;

import androidx.fragment.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.tezov.crypter.application.Environment;
import com.tezov.crypter.dialog.DialogExportKey;
import com.tezov.crypter.dialog.DialogImportKey;
import com.tezov.crypter.fragment.FragmentCipherText;

import java.util.List;

//@DebugLogEnable
public class ActivityDeepLink extends AppCompatActivity{

@Override
protected void onCreate(Bundle savedInstanceState){
    super.onCreate(savedInstanceState);
    onNewDeepLink();
}
private void onNewDeepLink(){
DebugLog.start().track(this).end();
    Intent intent = getIntent();
    Uri uri = intent.getData();
    String extension = null;
    if(uri != null){
        List<String> segments = uri.getPathSegments();
        if(!segments.isEmpty()){
            extension = segments.get(0);
        }
    }
    boolean isValid = false;
    if(extension != null){
        if(Environment.EXTENSION_CIPHER_TEXT.equals(extension)){
            intent.putExtra(ActivityFilterDispatcher.EXTRA_TARGET_FRAGMENT, FragmentCipherText.class.getSimpleName());
            isValid = true;
        }
        else if(Environment.EXTENSION_SHARE_PUBLIC_KEY.equals(extension)){
            intent.putExtra(ActivityFilterDispatcher.EXTRA_TARGET_DIALOG, DialogExportKey.class.getSimpleName());
            isValid = true;
        }
        else if(Environment.EXTENSION_SHARE_ENCRYPTED_KEY.equals(extension)){
            intent.putExtra(ActivityFilterDispatcher.EXTRA_TARGET_DIALOG, DialogImportKey.class.getSimpleName());
            isValid = true;
        }
    }
    if(isValid){
        intent.setFlags(0);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.putExtra(ActivityFilterDispatcher.EXTRA_REQUEST, ActivityFilterDispatcher.EXTRA_REQUEST_LINK);
    }
    ActivityFilterDispatcher.startActivityFrom(this, isValid);
}

}

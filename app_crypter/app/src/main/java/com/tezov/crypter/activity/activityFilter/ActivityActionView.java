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
import java.util.List;
import java.util.LinkedList;
import java.util.Set;
import com.tezov.lib_java_android.database.sqlLite.filter.dbFilterOrder;
import com.tezov.lib_java_android.database.sqlLite.filter.chunk.ChunkCommand;
import androidx.fragment.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.tezov.crypter.application.Environment;
import com.tezov.crypter.dialog.DialogExportKey;
import com.tezov.crypter.dialog.DialogImportKey;
import com.tezov.crypter.fragment.FragmentCipherFile;
import com.tezov.crypter.fragment.FragmentCipherText;
import com.tezov.lib_java_android.file.UriW;
import com.tezov.lib_java.debug.DebugLog;
import com.tezov.lib_java.type.collection.ListOrObject;
import com.tezov.lib_java_android.util.UtilsIntent;

//@DebugLogEnable
public class ActivityActionView extends AppCompatActivity{
@Override
protected void onCreate(Bundle savedInstanceState){
    super.onCreate(savedInstanceState);
    onNewAction();
}
private void onNewAction(){
DebugLog.start().track(this).end();
    Intent intent = getIntent();
    ListOrObject<UriW> uris = UtilsIntent.getUris(intent, false);
    if(uris.size() > 1){
        Log.d(DebugLog.TAG, "multiple uri ignored");
    }
    UriW uri = uris.get();
    String extension = uri.getExtension();
    boolean isValid = false;
    if(Environment.EXTENSION_CIPHER_FILE.equals(extension)){
        UtilsIntent.retainUriPermission(intent, uri);
        intent.putExtra(ActivityFilterDispatcher.EXTRA_TARGET_FRAGMENT, FragmentCipherFile.class.getSimpleName());
        isValid = true;
    } else if(Environment.EXTENSION_CIPHER_TEXT.equals(extension)){
        intent.putExtra(ActivityFilterDispatcher.EXTRA_TARGET_FRAGMENT, FragmentCipherText.class.getSimpleName());
        isValid = true;
    } else if(Environment.EXTENSION_SHARE_PUBLIC_KEY.equals(extension)){
        intent.putExtra(ActivityFilterDispatcher.EXTRA_TARGET_DIALOG, DialogExportKey.class.getSimpleName());
        isValid = true;
    } else if(Environment.EXTENSION_SHARE_ENCRYPTED_KEY.equals(extension)){
        intent.putExtra(ActivityFilterDispatcher.EXTRA_TARGET_DIALOG, DialogImportKey.class.getSimpleName());
        isValid = true;
    }
    if(isValid){
        intent.setFlags(0);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.putExtra(ActivityFilterDispatcher.EXTRA_REQUEST, ActivityFilterDispatcher.EXTRA_REQUEST_VIEW);
    }
    ActivityFilterDispatcher.startActivityFrom(this, isValid);
}
}

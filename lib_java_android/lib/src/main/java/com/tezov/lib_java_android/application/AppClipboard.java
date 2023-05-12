/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java_android.application;

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
import androidx.fragment.app.Fragment;

import com.tezov.lib_java_android.ui.view.status.StatusParam;

import android.content.ClipData;
import android.content.ClipboardManager;

import com.tezov.lib_java_android.R;

import static android.content.ClipDescription.MIMETYPE_TEXT_PLAIN;
import static android.content.Context.CLIPBOARD_SERVICE;

public class AppClipboard{

public static String getText(){
    return getText(false);
}
public static String getText(boolean showToast){
    CharSequence charSequence = getCharSequence(showToast);
    if(charSequence != null){
        return charSequence.toString();
    }
    else return null;
}
public static CharSequence getCharSequence(){
    return getCharSequence(true);
}
public static CharSequence getCharSequence(boolean showToast){
    ClipboardManager clipboard = AppContext.getSystemService(CLIPBOARD_SERVICE);
    if(clipboard.hasPrimaryClip()){
        ClipData clip = clipboard.getPrimaryClip();
        if(clip.getDescription().hasMimeType(MIMETYPE_TEXT_PLAIN)){
            CharSequence charSequence = clip.getItemAt(0).getText();
            if((charSequence != null) && (charSequence.length() > 0)){
                if(showToast){
                    showToast();
                }
                return charSequence;
            }
        }
    }
    return null;
}
public static void showToast(){
    AppInfo.toast(R.string.lbl_pasted_from_clipboard, StatusParam.DELAY_INFO_SHORT_ms, StatusParam.Color.INFO);

}

public static void setText(String name, CharSequence title){
    setText(name, title, true);
}
public static void setText(String name, CharSequence title, boolean showToast){
    ClipboardManager clipboard = AppContext.getSystemService(CLIPBOARD_SERVICE);
    ClipData clip = ClipData.newPlainText(name, title);
    clipboard.setPrimaryClip(clip);
    if(showToast){
        AppInfo.toast(R.string.lbl_copied_to_clipboard, StatusParam.DELAY_INFO_SHORT_ms, StatusParam.Color.INFO);
    }
}


}

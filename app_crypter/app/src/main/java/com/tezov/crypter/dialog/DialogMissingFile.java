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
import com.tezov.lib_java_android.application.AppContext;
import com.tezov.lib_java_android.application.AppResources;

import androidx.fragment.app.Fragment;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.core.graphics.drawable.DrawableCompat;
import androidx.core.widget.TextViewCompat;

import com.tezov.crypter.R;
import com.tezov.lib_java_android.ui.dialog.modal.DialogModalRequest;
import com.tezov.lib_java_android.ui.layout.ConstraintLayout;

public class DialogMissingFile extends DialogModalRequest{
public static State newStateDefault(String filePath){
    Drawable drawable = AppContext.getResources().getDrawable(R.drawable.ic_warning_24dp);
    DrawableCompat.setTint(drawable, AppContext.getResources().getColorARGB(R.color.Orange));
    State state = new State();
    Param param = state.obtainParam();
    param.setFilePath(filePath).setTitleIcon(drawable).setTitle(R.string.lbl_missing_file_title);
    return state;
}
@Override
protected void mergeFrameView(LayoutInflater inflater, ConstraintLayout frame, Bundle savedInstanceState){
    Param param = (Param)getParam();
    TextView textView = new TextView(frame.getContext());
    textView.setLayoutParams(new ViewGroup.LayoutParams(WRAP_CONTENT, WRAP_CONTENT));
    textView.setText(param.filePath);
    AppResources resources = AppContext.getResources();
    textView.setPaddingRelative(resources.getDimensionInt(R.dimen.dimPaddingElement_4), resources.getDimensionInt(R.dimen.dimPaddingElement_2),
            resources.getDimensionInt(R.dimen.dimPaddingElement_4), resources.getDimensionInt(R.dimen.dimPaddingElement_2));
    TextViewCompat.setTextAppearance(textView, R.style.TxtNorm);
    frame.addView(textView);
}

public static class State extends DialogModalRequest.State{
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
    public String filePath = null;
    public String getFilePath(){
        return filePath;
    }
    public Param setFilePath(int resourceId){
        return setFilePath(AppContext.getResources().getString(resourceId));
    }
    public Param setFilePath(String filePath){
        this.filePath = filePath;
        return this;
    }

}

}

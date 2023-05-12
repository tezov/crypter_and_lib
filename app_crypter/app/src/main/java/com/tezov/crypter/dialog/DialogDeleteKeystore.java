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

public class DialogDeleteKeystore extends DialogModalRequest{
public static State newStateDefault(){
    Drawable drawable = AppContext.getResources().getDrawable(R.drawable.ic_warning_24dp);
    DrawableCompat.setTint(drawable, AppContext.getResources().getColorARGB(R.color.Red));
    State state = new State();
    Param param = state.obtainParam();
    param.setTitleIcon(drawable).setTitle(R.string.lbl_keystore_delete_title).setCancelButtonText(R.string.btn_cancel).setConfirmButtonText(R.string.btn_confirm);
    return state;
}
@Override
protected void mergeFrameView(LayoutInflater inflater, ConstraintLayout frame, Bundle savedInstanceState){
    TextView textView = new TextView(frame.getContext());
    textView.setLayoutParams(new ViewGroup.LayoutParams(WRAP_CONTENT, WRAP_CONTENT));
    textView.setText(R.string.lbl_final_deletion_confirmation_description);
    AppResources resources = AppContext.getResources();
    textView.setPaddingRelative(resources.getDimensionInt(R.dimen.dimPaddingElement_4), resources.getDimensionInt(R.dimen.dimPaddingElement_2),
            resources.getDimensionInt(R.dimen.dimPaddingElement_4), resources.getDimensionInt(R.dimen.dimPaddingElement_2));
    TextViewCompat.setTextAppearance(textView, R.style.TxtNorm);
    frame.addView(textView);
}

}

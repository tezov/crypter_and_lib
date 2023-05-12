/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java_android.ui.dialog.dialogPicker.picker.base;

import com.tezov.lib_java.debug.DebugLog;
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
import androidx.fragment.app.Fragment;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.tezov.lib_java_android.toolbox.PostToHandler;
import com.tezov.lib_java.wrapperAnonymous.ComparatorW;
import com.tezov.lib_java.type.runnable.RunnableW;
import com.tezov.lib_java.util.UtilsList;
import com.tezov.lib_java_android.util.UtilsView;

import java.util.List;

public class DialogButtonViewOption extends DialogButtonView{
public DialogButtonViewOption(Context context){
    super(context);
}

public DialogButtonViewOption(Context context, @Nullable AttributeSet attrs){
    super(context, attrs);
}

public DialogButtonViewOption(Context context, @Nullable AttributeSet attrs, int defStyleAttr){
    super(context, attrs, defStyleAttr);
}

public DialogButtonViewOption(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes){
    super(context, attrs, defStyleAttr, defStyleRes);
}

@Override
final protected void addButtonContainer(Context context){
    LinearLayout layout = new LinearLayout(context);
    LinearLayout.LayoutParams layoutParam = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
    layout.setLayoutParams(layoutParam);
    layout.setOrientation(LinearLayout.HORIZONTAL);
    this.addView(layout);
}

@Override
protected boolean onParamsCreated(ViewGroup.LayoutParams params){
    params.width = LayoutParams.WRAP_CONTENT;
    return super.onParamsCreated(params);
}

@Override
final protected ViewGroup getContainer(){
    return (ViewGroup)this.getChildAt(0);
}

public void sortButtonById(){
    RunnableW runnable = new RunnableW(){
        @Override
        public void runSafe(){
            sortButton();
        }
    };
    if(!PostToHandler.of(this, runnable)){
        runnable.run();
    }
}

private void sortButton(){
    LinearLayout layout = (LinearLayout)getContainer();
    List<View> views = UtilsList.sort(UtilsView.childrenToList(layout), new ComparatorW<View>(){
        @Override
        public int compare(View v1, View v2){
            return Integer.compare(v1.getId(), v2.getId());
        }
    });
    layout.removeAllViews();
    for(View view: views){
        layout.addView(view);
    }
}

}

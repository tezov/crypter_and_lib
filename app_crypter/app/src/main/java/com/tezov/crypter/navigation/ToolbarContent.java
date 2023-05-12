/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.crypter.navigation;

import com.tezov.lib_java.debug.DebugLog;
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
import static com.tezov.lib_java.type.defEnum.Event.ON_CLICK_SHORT;

import android.view.View;

import com.tezov.lib_java.debug.DebugTrack;
import com.tezov.lib_java_android.wrapperAnonymous.ViewOnClickListenerW;
import com.tezov.lib_java.type.ref.WR;
import com.tezov.lib_java_android.ui.activity.ActivityToolbar;
import com.tezov.lib_java_android.ui.layout.LinearLayout;
import com.tezov.lib_java_android.ui.toolbar.Toolbar;
import com.tezov.lib_java_android.ui.toolbar.ToolbarCollapsible;

public class ToolbarContent{
private final WR<Toolbar> toolbarWR;
private WR<ToolbarCollapsible> toolbarCollapsibleWR;

public ToolbarContent(ActivityToolbar activity){
DebugTrack.start().create(this).end();
    this.toolbarWR = WR.newInstance(activity.getToolbar());
}

public void clear(){
    toolbarWR.get().setContentView(null);
    toolbarCollapsibleWR.get().setContentView(null);
}

public void setToolBarView(View view){
    if((view != null) && !view.hasOnClickListeners()){
        view.setOnClickListener(new ViewOnClickListenerW(){
            @Override
            public void onClicked(View v){
                toolbarWR.get().post(ON_CLICK_SHORT, v);
            }
        });
    }
    toolbarWR.get().setContentView(view);
}

private LinearLayout getAppBarLayout(){
    return (LinearLayout)toolbarCollapsibleWR.get().getContentView();
}
public int collapsibleViewCount(){
    return getAppBarLayout().getChildCount();
}
public boolean hasCollapsibleView(View view){
    return getAppBarLayout().hasView(view);
}
public View findCollapsibleViewById(int id){
    return getAppBarLayout().findViewById(id);
}
public void putCollapsibleView(View view){
    getAppBarLayout().putView(view);
}
public void removeCollapsibleView(int id){
    getAppBarLayout().removeViewWithId(id);
}

@Override
protected void finalize() throws Throwable{
DebugTrack.start().destroy(this).end();
    super.finalize();
}

}
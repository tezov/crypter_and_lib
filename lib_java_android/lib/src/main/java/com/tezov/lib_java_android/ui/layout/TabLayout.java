/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java_android.ui.layout;

import com.tezov.lib_java.debug.DebugLog;
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

import androidx.annotation.NonNull;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.annotation.Nullable;

import com.tezov.lib_java_android.R;
import com.tezov.lib_java_android.application.AppContext;
import com.tezov.lib_java.debug.DebugTrack;
import com.tezov.lib_java_android.ui.misc.AttributeReader;

import java.util.List;

public class TabLayout extends com.google.android.material.tabs.TabLayout{
final static private int[] ATTR_INDEX = R.styleable.TabLayout_lib;
private List<String> titles = null;
private List<Drawable> icons = null;
private int size = 0;

public TabLayout(android.content.Context context){
    super(context);
    init(context, null, 0);
}
public TabLayout(android.content.Context context, @Nullable AttributeSet attrs){
    super(context, attrs);
    init(context, attrs, 0);
}
public TabLayout(android.content.Context context, @Nullable AttributeSet attrs, int defStyleAttr){
    super(context, attrs, defStyleAttr);
    init(context, attrs, defStyleAttr);
}

private void init(android.content.Context context, AttributeSet attrs, int defStyleAttr){
DebugTrack.start().create(this).end();
    setFocusable(true);
    setClickable(true);
    if(attrs == null){
        return;
    }
    AttributeReader attributeReader = new AttributeReader().parse(context, ATTR_INDEX, attrs);
    Integer titleArrayID = attributeReader.getReference(R.styleable.TabLayout_lib_title_array);
    if(titleArrayID != null){
        titles = AppContext.getResources().getStrings(titleArrayID);
    }
    Integer iconArrayID = attributeReader.getReference(R.styleable.TabLayout_lib_icon_array);
    if(iconArrayID != null){
        icons = AppContext.getResources().getDrawables(iconArrayID);
    }
    if((titles != null) && (icons != null)){
        size = Math.max(titles.size(), icons.size());
    } else if(titles != null){
        size = titles.size();
    } else if(icons != null){
        size = icons.size();
    }
}

public int getSize(){
    return size;
}

public List<String> getTitles(){
    return titles;
}

public String getTitle(int index){
    if((titles == null) || index >= titles.size()){
        return null;
    } else {
        return titles.get(index);
    }
}

public List<Drawable> getIcons(){
    return icons;
}

public Drawable getIcon(int index){
    if((icons == null) || index >= icons.size()){
        return null;
    } else {
        return icons.get(index);
    }
}

@Override
public boolean onInterceptTouchEvent(MotionEvent ev){
    if(!isClickable()){
        return true;
    } else {
        return super.onInterceptTouchEvent(ev);
    }
}

@Override
protected void finalize() throws Throwable{
DebugTrack.start().destroy(this).end();
    super.finalize();
}

}

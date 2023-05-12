/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java_android.util;

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

import com.tezov.lib_java_android.application.AppContext;
import com.tezov.lib_java.application.AppRandomNumber;
import com.tezov.lib_java_android.application.Application;
import com.tezov.lib_java.debug.DebugString;
import com.tezov.lib_java_android.ui.dialog.DialogNavigable;
import com.tezov.lib_java_android.ui.navigation.defNavigable;

import androidx.coordinatorlayout.widget.CoordinatorLayout;

import android.graphics.Rect;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import com.tezov.lib_java_android.application.AppResources;
import com.tezov.lib_java.toolbox.Compare;
import com.tezov.lib_java.toolbox.Reflection;
import com.tezov.lib_java.debug.DebugLog;
import com.tezov.lib_java.wrapperAnonymous.ConsumerW;

import java.util.ArrayList;
import java.util.List;

import static com.tezov.lib_java_android.util.UtilsView.Direction.DOWN;
import static com.tezov.lib_java_android.util.UtilsView.Direction.UP;

public class UtilsView{
private final static String decal = "--|";

private UtilsView(){
}

protected static void toDebugLogViewTree(String prefix, View view){
    if(!(view instanceof ViewGroup)){
        return;
    }
    ViewGroup viewGroup = (ViewGroup)view;
    for(int end = viewGroup.getChildCount(), i = 0; i < end; i++){
        View v = viewGroup.getChildAt(i);
        int id = v.getId();
        String idString = (id != AppResources.NULL_ID) ? " id:" + getResourceEntryName(id) : "";
DebugLog.start().send(prefix + "<" + v.getClass().getSimpleName() + idString + ">").end();
        toDebugLogViewTree(prefix + decal, v);
DebugLog.start().send(prefix + "</" + v.getClass().getSimpleName() + ">").end();
    }

}
public static void toDebugLogViewTree(View view){
    if(view == null){
DebugLog.start().send("view is null").end();
        return;
    }
    int id = view.getId();
    String idString = (id != AppResources.NULL_ID) ? " id:" + getResourceEntryName(id) : "";
DebugLog.start().send("<" + view.getClass().getSimpleName() + idString + ">").end();
    toDebugLogViewTree(decal, view);
DebugLog.start().send("<" + view.getClass().getSimpleName() + ">").end();
}
public static void toDebugLogActivityTree(){
    toDebugLogViewTree(AppContext.getActivity().getViewRoot());
}

public static String getResourceEntryName(int id){
    try{
        return AppContext.getResources().get().getResourceEntryName(id);
    } catch(Throwable e){
        return Integer.toString(id);
    }
}
public static String getResourceEntryName(View view){
    if(view == null){
        return null;
    }
    int id = view.getId();
    if(id == AppResources.NULL_ID){
        return null;
    }
    return AppContext.getResources().get().getResourceEntryName(id);

}
public static <C extends Class<V>, V extends View> V findFirst(Integer id, C type, View view, Direction direction){
    if(direction == UP){
        return findFirstUP(id, type, view);
    }
    if(direction == DOWN){
        return findFirstDOWN(id, type, view);
    }
    return null;
}
public static <V extends View> V findFirst(Integer id, View view, Direction direction){
    if(direction == UP){
        return findFirstUP(id, null, view);
    }
    if(direction == DOWN){
        return findFirstDOWN(id, null, view);
    }
    return null;
}
public static <C extends Class<V>, V extends View> V findFirst(C type, View view, Direction direction){
    if(direction == UP){
        return findFirstUP(null, type, view);
    }
    if(direction == DOWN){
        return findFirstDOWN(null, type, view);
    }
    return null;
}
protected static <C extends Class<V>, V extends View> V findFirstDOWN(Integer id, C type, View view){
    boolean isTypeEqual = Reflection.isInstanceOf(view, type);
    boolean isIdEqual = Compare.equals(id, view.getId());
    if(((id == null) || isIdEqual) && ((type == null) || isTypeEqual)){
        return (V)view;
    }
    if(!(view instanceof ViewGroup)){
        return null;
    }
    ViewGroup viewGroup = (ViewGroup)view;
    int end = viewGroup.getChildCount();
    for(int i = 0; i < end; i++){
        V v = findFirstDOWN(id, type, viewGroup.getChildAt(i));
        if(v != null){
            return v;
        }
    }
    return null;
}
protected static <V extends View, C extends Class<V>> V findFirstUP(Integer id, C type, View view){
    while(true){
        if(view == null){
            return null;
        }
        boolean isTypeEqual = Reflection.isInstanceOf(view, type);
        boolean isIdEqual = Compare.equals(id, view.getId());
        if(((id == null) || isIdEqual) && ((type == null) || isTypeEqual)){
            return (V)view;
        }
        ViewParent viewParent = view.getParent();
        if(!(viewParent instanceof View)){
            return null;
        }
        view = (View)viewParent;
    }
}
public static <V extends View, C extends Class<V>> void foreach(C type, View view, Direction direction, ConsumerW<V> consumer){
    List<V> vs = new ArrayList<>();
    if(direction == UP){
        findViewsUP(type, view, consumer);
    }
    if(direction == DOWN){
        findViewsDOWN(type, view, consumer);
    }
}
protected static <C extends Class<V>, V extends View> void findViewsDOWN(C type, View view, ConsumerW<V> consumer){
    if(Reflection.isInstanceOf(view, type)){
        consumer.accept((V)view);
    }
    if(!(view instanceof ViewGroup)){
        return;
    }
    ViewGroup viewGroup = (ViewGroup)view;
    int end = viewGroup.getChildCount();
    for(int i = 0; i < end; i++){
        findViewsDOWN(type, viewGroup.getChildAt(i), consumer);
    }
}
protected static <C extends Class<V>, V extends View> void findViewsUP(C type, View view, ConsumerW<V> consumer){
    while(true){
        if(view == null){
            return;
        }
        if(Reflection.isInstanceOf(view, type)){
            consumer.accept((V)view);
        }
        ViewParent viewParent = view.getParent();
        if(!(viewParent instanceof View)){
            return;
        }
        view = (View)viewParent;
    }
}
public static <V extends View> List<V> childrenToList(ViewGroup viewGroup){
    List<V> views = new ArrayList<>();
    for(int end = viewGroup.getChildCount(), i = 0; i < end; i++){
        views.add((V)viewGroup.getChildAt(i));
    }
    return views;
}

public static ViewGroup findMasterRoot(){
    defNavigable ref = Application.navigationHelper().getLastRef(null, false);
    if(ref instanceof DialogNavigable){
        return (ViewGroup)((DialogNavigable)ref).getView();
    }
    else {
        return AppContext.getActivity().getViewRoot();
    }
}
public static ViewGroup findCoordinatorRoot(View view){
    do{
        if(view instanceof CoordinatorLayout){
            return (ViewGroup)view;
        }
        ViewParent parent = view.getParent();
        view = parent instanceof View ? (View)parent : null;
    } while(view != null);
    return findMasterRoot();
}

public static Rect getPadding(View view){
    Rect rect = new Rect();
    getPadding(view, rect);
    return rect;
}
public static void getPadding(View view, Rect rect){
     rect.left = view.getPaddingStart();
     rect.right = view.getPaddingEnd();
     rect.top = view.getPaddingTop();
     rect.bottom = view.getPaddingBottom();
}
public static void setPadding(View view, Rect rect){
    view.setPadding(rect.left, rect.top, rect.right, rect.bottom);
}

public static void generateAndSetId(ViewGroup parent, View viewToSetId){
    do{
        int id = AppRandomNumber.nextInt();
        if(parent.findViewById(id) == null){
            viewToSetId.setId(id);
            break;
        }
    } while(true);
}

public static void toDebugLogViewInfo(View view){
    DebugString data = new DebugString();
    data.appendHex("id", view.getId());
    data.append("alpha", view.getAlpha());
    data.append("size", view.getWidth()+"x"+view.getHeight());
    data.append("measure", view.getMeasuredWidth()+"x"+view.getMeasuredHeight());
    data.append("paddingH", view.getPaddingStart()+"/"+view.getPaddingEnd());
    data.append("paddingV", view.getPaddingTop()+"/"+view.getPaddingBottom());
    if(view.getLayoutParams() instanceof ViewGroup.MarginLayoutParams){
        ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams)view.getLayoutParams();
        data.append("marginH", p.leftMargin+"/"+p.rightMargin);
        data.append("marginV", p.topMargin+"/"+p.bottomMargin);
    }
    data.append("position", view.getX()+"/"+view.getY());
    data.append("translation", view.getTranslationX()+"/"+view.getTranslationY());
    data.append("rotation", view.getRotationX()+"/"+view.getRotationY());
    data.append("pivot", view.getPivotX()+"/"+view.getPivotY());
    data.append("elevation", view.getElevation());
DebugLog.start().send(data).end();
}

public enum Direction{UP, DOWN}

}

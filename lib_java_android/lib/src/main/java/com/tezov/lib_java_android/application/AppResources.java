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
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;

import androidx.core.content.ContextCompat;
import androidx.core.os.ConfigurationCompat;

import com.tezov.lib_java.toolbox.Nullify;
import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java.type.collection.ListOrObject;
import com.tezov.lib_java_android.type.primaire.Color;
import com.tezov.lib_java.wrapperAnonymous.FunctionW;
import com.tezov.lib_java_android.ui.activity.ActivityBase;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Locale;

public class AppResources extends com.tezov.lib_java.application.AppResources{
public static final int NOT_A_RESOURCE = 0;
public static final int NULL_ID = -1;

public AppResources(ApplicationSystem applicationSystem){

}
public android.content.res.Resources get(){
    ActivityBase activity = AppContext.getActivity();
    if(activity != null){
        return activity.getResources();
    } else {
        return AppContext.get().getResources();
    }
}

@Override
public Locale getLocale(){
    return ConfigurationCompat.getLocales(get().getConfiguration()).get(0);
}

public String getIdentifierPath(int resourceId){
    TypedValue value = new TypedValue();
    get().getValue(resourceId, value, true);
    return value.string.toString();
}
public String getIdentifierName(int resourceId){
    return get().getResourceEntryName(resourceId);
}
public String getTypeName(int resourceId){
    return get().getResourceTypeName(resourceId);
}

public int getIdentifier(IdentifierType identifierType, String resourceName){
    return get().getIdentifier(resourceName, identifierType.name(), AppContext.getPackageName());
}
public int getIdentifier(IdentifierType identifierType, String resourceName, String packageName){
    return get().getIdentifier(resourceName, identifierType.name(), packageName);
}
public int getLayoutId(String resourceName){
    return getIdentifier(IdentifierType.layout, resourceName);
}

@Override
public int getId(String resourceName){
    return getIdentifier(IdentifierType.id, resourceName);
}

@Override
public String getString(String resourceName){
    return getString(getIdentifier(IdentifierType.string, resourceName));
}
@Override
public List<String> getStrings(String resourceName){
    return getStrings(getIdentifier(IdentifierType.array, resourceName));
}
@Override
public int getColorARGB(String resourceName){
    return getColorARGB(getIdentifier(IdentifierType.color, resourceName));
}
public Color getColor(String resourceName){
    return getColor(getIdentifier(IdentifierType.color, resourceName));
}
public List<Color> getColors(String resourceName){
    return getColors(getIdentifier(IdentifierType.array, resourceName));
}
public List<ColorStateList> getColorsAsStateLists(String resourceName){
    return getColorsAsStateLists(getIdentifier(IdentifierType.array, resourceName));
}
@Override
public float getDimensionFloat(String resourceName){
    return getDimensionFloat(getIdentifier(IdentifierType.dimen, resourceName));
}
public Drawable getDrawable(String resourceName){
    return getDrawable(getIdentifier(IdentifierType.drawable, resourceName));
}
public List<Drawable> getDrawables(String resourceName){
    return getDrawables(getIdentifier(IdentifierType.array, resourceName));
}
public List<Integer> getResourceArrayId(String resourceName){
    return getResourceArrayId(getIdentifier(IdentifierType.array, resourceName));
}
@Override
public InputStream openRawResource(String resourceName) throws IOException{
    return openRawResource(getIdentifier(IdentifierType.raw, resourceName));
}

private <T> List<T> getArray(IdentifierType identifierType, int resourceId){
    if(resourceId == NOT_A_RESOURCE){
        return null;
    }
    TypedArray typedArray = get().obtainTypedArray(resourceId);
    int length = typedArray.length();
    if(length == 0){
        typedArray.recycle();
        return null;
    }
    FunctionW<Integer, T> function;
    switch(identifierType){
        case string:
            function = new FunctionW<Integer, T>(){
                @Override
                public T apply(Integer i){
                    return (T)typedArray.getString(i);
                }
            };
            break;
        case drawable:
            function = new FunctionW<Integer, T>(){
                @Override
                public T apply(Integer i){
                    return (T)typedArray.getDrawable(i);
                }
            };
            break;
        case color:
            function = new FunctionW<Integer, T>(){
                @Override
                public T apply(Integer i){
                    return (T)Color.fromARGB(typedArray.getColor(i, 0));
                }
            };
            break;
        case colorAsStateList:
            function = new FunctionW<Integer, T>(){
                @Override
                public T apply(Integer i){
                    Color color = Color.fromARGB(typedArray.getColor(i, 0));
                    return (T)ColorStateList.valueOf(color.getARGB());
                }
            };
            break;
        case resource_id:
            function = new FunctionW<Integer, T>(){
                @Override
                public T apply(Integer i){
                    Integer value = typedArray.getResourceId(i, NOT_A_RESOURCE);
                    if(value == NOT_A_RESOURCE){
                        value = typedArray.getInt(i, 0);
                    }
                    return (T)value;
                }
            };
            break;
        default:{

DebugException.start().unknown("type", identifierType.name()).end();


            function = null;
        }
    }
    ListOrObject<T> drawables = new ListOrObject<>();
    for(int i = 0; i < length; i++){
        T t = function.apply(i);
        if(t != null){
            drawables.add(t);
        }
    }
    typedArray.recycle();
    return Nullify.collection(drawables);
}
@Override
public String getString(int resourceId){
    return get().getString(resourceId);
}
@Override
public List<String> getStrings(int resourceId){
    return getArray(IdentifierType.string, resourceId);
}
@Override
public int getColorARGB(int resourceId){
    return ContextCompat.getColor(AppContext.getActivity(), resourceId);
}
public Color getColor(int resourceId){
    return Color.fromARGB(getColorARGB(resourceId));
}
public List<Color> getColors(int resourceId){
    return getArray(IdentifierType.color, resourceId);
}
public ColorStateList getColorStateList(int resourceId){
    return ContextCompat.getColorStateList(AppContext.getActivity(), resourceId);
}
public List<ColorStateList> getColorsAsStateLists(int resourceId){
    return getArray(IdentifierType.colorAsStateList, resourceId);
}
@Override
public float getDimensionFloat(int resourceId){
    return get().getDimension(resourceId);
}
public Drawable getDrawable(int resourceId){
    return ContextCompat.getDrawable(AppContext.getActivity(), resourceId);
}
public List<Drawable> getDrawables(int resource){
    return getArray(IdentifierType.drawable, resource);
}
public List<Integer> getResourceArrayId(int resourceId){
    return getArray(IdentifierType.resource_id, resourceId);
}
@Override
public InputStream openRawResource(int resourceId) throws IOException{
    return get().openRawResource(resourceId);
}

public TypedValue resolveAttribute(android.content.res.Resources.Theme theme, int resourceId, boolean resolveRefs){
    TypedValue tv = new TypedValue();
    theme.resolveAttribute(resourceId, tv, resolveRefs);
    return tv;
}
public TypedValue resolveAttribute(android.content.Context context, int resourceId, boolean resolveRefs){
    return resolveAttribute(context.getTheme(), resourceId, resolveRefs);
}
public Integer resolveAttributeId(android.content.res.Resources.Theme theme, int resourceId, boolean resolveRefs){
    int id = resolveAttribute(theme, resourceId, resolveRefs).resourceId;
    return id != 0 ? id : null;
}
public Integer resolveAttributeId(android.content.Context context, int resourceId, boolean resolveRefs){
    return resolveAttributeId(context.getTheme(), resourceId, resolveRefs);
}
public Integer resolveAttributeId(int resourceId, boolean resolveRefs){
    return resolveAttributeId(AppContext.getActivity(), resourceId, resolveRefs);
}
public Integer resolveAttributeId(int resourceId){
    return resolveAttributeId(resourceId, true);
}
public Integer resolveColorARGB(int resourceId){
    Integer id = resolveAttributeId(resourceId, true);
    if(id != null){
        return getColorARGB(id);
    } else {
        return null;
    }
}
public Float resolveDimension(int resourceId){
    Integer id = resolveAttributeId(resourceId, true);
    if(id != null){
        return getDimensionFloat(id);
    } else {
        return null;
    }
}

public Integer resourceIdFromStyle(int styleId, int attr){
    TypedArray ta = AppContext.get().obtainStyledAttributes(styleId, new int[]{attr});
    int id = ta.getResourceId(0, NULL_ID);
    ta.recycle();
    if(id != NULL_ID){
        return id;
    } else {
        return null;
    }
}
public Integer colorARGBFromStyle(int styleId, int attr){
    Integer id = resourceIdFromStyle(styleId, attr);
    if(id == null){
        return null;
    } else {
        return getColorARGB(id);
    }
}

public int getInteger(int resourceId){
    return get().getInteger(resourceId);
}
public boolean getBoolean(int resourceId){
    return get().getBoolean(resourceId);
}

public enum IdentifierType{
    id, layout, string, dimen, drawable, raw, resource_id, color, colorAsStateList, array
}

}


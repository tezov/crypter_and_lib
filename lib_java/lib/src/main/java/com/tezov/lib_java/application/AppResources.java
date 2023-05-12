/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java.application;

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
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public abstract class AppResources{

public abstract Locale getLocale();

public abstract int getId(String resourceName);

public abstract String getString(String resourceName);
public abstract List<String> getStrings(String name);
public abstract int getColorARGB(String resourceName);
public abstract float getDimensionFloat(String resourceName);
public int getDimensionInt(String resourceName){
    return (int)getDimensionFloat(resourceName);
}
public abstract InputStream openRawResource(String resourceName) throws IOException;

public abstract String getString(int resourceId);
public abstract List<String> getStrings(int resourceId);
public abstract int getColorARGB(int resourceId);
public abstract float getDimensionFloat(int resourceId);
public Integer getDimensionInt(int resourceId){
    return (int)getDimensionFloat(resourceId);
}
public abstract InputStream openRawResource(int resourceId) throws IOException;

}

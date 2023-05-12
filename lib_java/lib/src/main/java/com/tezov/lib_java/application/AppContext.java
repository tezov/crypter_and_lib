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
import com.tezov.lib_java.type.primitive.ObjectTo;
import com.tezov.lib_java.type.primitive.IntTo;
import com.tezov.lib_java.type.unit.UnitByte;
import com.tezov.lib_java.toolbox.CompareType;
import com.tezov.lib_java.toolbox.Clock;
import com.tezov.lib_java.util.UtilsString;
import java.util.List;
import java.util.LinkedList;
import java.util.Set;
import com.tezov.lib_java.debug.DebugException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AppContext{

private static String PACKAGE_NAME = null;
private static String APPLICATION_ID = null;
private static String APPLICATION_ID_SUFFIX = null;
private static String PACKAGE_OWNER = null;

private static AppResources resource = null;

public static void setPackageName(String packageName){
    PACKAGE_NAME = packageName;
    {
        Pattern pattern = Pattern.compile("^([^.]+\\.[^.]+\\.[^.]+)(?:\\..+)$");
        Matcher matcher = pattern.matcher(packageName);
        if(matcher.matches()){
            APPLICATION_ID = matcher.group(1);
        }
        else{
DebugException.start().log("package name doesn't follow convention").end();
        }
    }
    {
        String packageNameSuffix = packageName.replace(APPLICATION_ID, "");
        if(packageNameSuffix.startsWith(".")){
            APPLICATION_ID_SUFFIX = packageNameSuffix.substring(1);
        }
    }
    {
        Pattern pattern = Pattern.compile("^(?:[^.]+\\.)([^.]+)(?:\\..+)$");
        Matcher matcher = pattern.matcher(packageName);
        if(matcher.matches()){
            PACKAGE_OWNER = matcher.group(1);
        }
    }


}
public static String getPackageName(){
    return PACKAGE_NAME;
}
public static String getPackageOwner(){
    return PACKAGE_OWNER;
}
public static String getApplicationId(){
    return APPLICATION_ID;
}
public static String getApplicationIdSuffix(){
    return APPLICATION_ID_SUFFIX;
}

public static void setResource(AppResources resource){
    AppContext.resource = resource;
}
public static AppResources getResources(){
    return resource;
}

}

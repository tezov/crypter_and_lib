/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java_android.type.primaire;

import com.tezov.lib_java.debug.DebugLog;
import com.tezov.lib_java.type.primitive.ObjectTo;
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

import android.graphics.ColorSpace;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.tezov.lib_java.application.AppRandomNumber;
import com.tezov.lib_java_android.application.AppContext;
import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java.debug.DebugTrack;
import com.tezov.lib_java.type.primitive.IntTo;
import com.tezov.lib_java.type.primitive.string.StringHexTo;

import java.util.Arrays;

public class Color extends android.graphics.Color{
public final static String COLOR_PREFIX = "#";
public final static int COLOR_WITHOUT_ALPHA_LENGTH = 6;
public final static int COLOR_WITH_ALPHA_LENGTH = 8;
public final static String COLOR_ALPHA_OPAQUE = "FF";

private final int colorARGB;

protected Color(int colorARGB){
DebugTrack.start().create(this).end();
    this.colorARGB = colorARGB;
}

public static Color fromHex(String name){
    name = name.replace(COLOR_PREFIX, "");
    if(name.length() == COLOR_WITHOUT_ALPHA_LENGTH){
        name = COLOR_ALPHA_OPAQUE + name;
    }
    return new Color(StringHexTo.Int(name));
}
public static Color fromResource(int resource){
    return AppContext.getResources().getColor(resource);
}
public static Color fromARGB(int colorARGB){
    return new Color(colorARGB);
}
public static Color from(int a, int r, int g, int b){
    return fromARGB((a << 24) | (r << 16) | (g << 8) | b);
}
public static Color from(int r, int g, int b){
    return fromARGB((0xFF << 24) | (r << 16) | (g << 8) | b);
}
public static Color from(float a, float r, float g, float b){
    return from((int)(a * 255 + 0.5), (int)(r * 255 + 0.5), (int)(g * 255 + 0.5), (int)(b * 255 + 0.5));
}
public static Color from(float r, float g, float b){
    return from((int)(r * 255 + 0.5), (int)(g * 255 + 0.5), (int)(b * 255 + 0.5));
}

public static Color random(){
    return fromARGB(0xFF000000 | AppRandomNumber.nextInt(0xFFFFFF));
}
private static float toFloat(int value){
    return value / 255.0f;
}

public int getARGB(){
    return colorARGB;
}
public int getAlpha(){
    return (colorARGB & 0xFF000000) >> 24;
}
public int getRed(){
    return (colorARGB & 0x00FF0000) >> 16;
}
public int getGreen(){
    return (colorARGB & 0x0000FF00) >> 8;
}
public int getBlue(){
    return colorARGB & 0x000000FF;
}
@Override
public float red(){
    return getRed();
}
@Override
public float green(){
    return getRed();
}
@Override
public float blue(){
    return getBlue();
}
@Override
public float alpha(){
    return getAlpha();
}
@Override
public int toArgb(){
    return colorARGB;
}
@Override
public long pack(){
DebugException.start().notImplemented().end();
    return 0;
}
@NonNull
@Override
public Color convert(@NonNull ColorSpace colorSpace){
DebugException.start().notImplemented().end();
    return null;
}
@Override
public int getComponentCount(){
    return 4;
}
@NonNull
@Override
public float[] getComponents(){
    return new float[]{toFloat(getRed()), toFloat(getGreen()), toFloat(getBlue()), toFloat(getAlpha())};
}
@NonNull
@Override
public float[] getComponents(@Nullable float[] components){
    float[] componentsThis = getComponents();
    if(components == null){
        return Arrays.copyOf(componentsThis, componentsThis.length);
    }

    if(components.length < componentsThis.length){
        throw new IllegalArgumentException("The specified array's length must be at " + "least " + componentsThis.length);
    }
    System.arraycopy(componentsThis, 0, components, 0, componentsThis.length);
    return components;
}
@Override
public float getComponent(int component){
    return getComponents()[component];
}
@Override
public float luminance(){
DebugException.start().notImplemented().end();
    return 0f;
}

@Override
public boolean equals(Object o){
    if(!(o instanceof Color)){
        return false;
    } else {
        return colorARGB == ((Color)o).colorARGB;
    }
}

@NonNull
@Override
public String toString(){
    return IntTo.StringHex(colorARGB);
}

@Override
protected void finalize() throws Throwable{
DebugTrack.start().destroy(this).end();
    super.finalize();
}

}

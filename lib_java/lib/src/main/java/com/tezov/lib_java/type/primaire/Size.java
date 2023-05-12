/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java.type.primaire;

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
import com.tezov.lib_java.debug.DebugLog;
import com.tezov.lib_java.debug.DebugString;
import com.tezov.lib_java.debug.DebugTrack;

public class Size{
private int width;
private int height;

public Size(){
    this(0, 0);
}

public Size(int width, int height){
DebugTrack.start().create(this).end();
    this.width = width;
    this.height = height;
}

public static Size wrap(android.util.Size size){
    if(size == null){
        return null;
    } else {
        return new Size(size.getWidth(), size.getHeight());
    }
}

public int getWidth(){
    return width;
}

public Size setWidth(int width){
    this.width = width;
    return this;
}

public int getHeight(){
    return height;
}

public Size setHeight(int height){
    this.height = height;
    return this;
}

public Size swap(){
    int tmp = width;
    width = height;
    height = tmp;
    return this;
}

public float ratio(){
    if(width != 0){
        return ((float)height) / ((float)width);
    } else {
        return Float.NaN;
    }
}

public Size scaleTo(Scale s){
    width = (int)(width * s.getW());
    height = (int)(height * s.getH());
    return this;
}

public Size scaleFrom(Scale s){
    width = (int)(width / s.getW());
    height = (int)(height / s.getH());
    return this;
}

public DebugString toDebugString(){
    DebugString data = new DebugString();
    data.append("width", width);
    data.append("height", height);
    return data;
}

final public void toDebugLog(){
DebugLog.start().send(toDebugString()).end();
}

@Override
protected void finalize() throws Throwable{
DebugTrack.start().destroy(this).end();
    super.finalize();
}

}

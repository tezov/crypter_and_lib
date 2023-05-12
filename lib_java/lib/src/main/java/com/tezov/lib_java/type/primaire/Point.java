/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java.type.primaire;

import com.tezov.lib_java.type.primitive.ObjectTo;
import com.tezov.lib_java.type.primitive.IntTo;
import com.tezov.lib_java.type.unit.UnitByte;
import com.tezov.lib_java.toolbox.CompareType;
import com.tezov.lib_java.toolbox.Clock;
import com.tezov.lib_java.util.UtilsString;
import java.util.List;
import java.util.LinkedList;
import java.util.Set;
import android.graphics.RectF;

import com.tezov.lib_java.buffer.ByteBuffer;
import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java.debug.DebugLog;
import com.tezov.lib_java.debug.DebugString;
import com.tezov.lib_java.debug.DebugTrack;

import org.apache.commons.lang3.math.NumberUtils;

public class Point{
private static final String TOKEN_STRING_SPLIT = "/";
private float x;
private float y;

public Point(){
    this(0.0f, 0.0f);
}

public Point(float x, float y){
DebugTrack.start().create(this).end();
    this.x = x;
    this.y = y;
}

public static Point fromString(String s){
    if(s == null){
        return null;
    }
    String[] data = s.split(TOKEN_STRING_SPLIT);
    if(data.length != 2){

DebugException.start().logHidden("Point getRaw string wrong size").end();

        return null;
    }
    if(!NumberUtils.isCreatable(data[0])){

DebugException.start().logHidden("Point getRaw string x is not numeric value").end();

        return null;
    }
    if(!NumberUtils.isCreatable(data[1])){

DebugException.start().logHidden("Point getRaw string y is not numeric value").end();

        return null;
    }
    return new Point(NumberUtils.toFloat(data[0]), NumberUtils.toFloat(data[1]));
}

public static Point fromBytes(byte[] bytes){
    if(bytes == null){
        return null;
    }
    ByteBuffer bytesBuffer = ByteBuffer.wrap(bytes);
    return new Point(bytesBuffer.getFloat(), bytesBuffer.getFloat());
}

public static Point onCircle(Point center, float radius, float degrees){
    float x = (float)(center.getX() + radius * Math.sin(-degrees * Math.PI / 180 + Math.PI / 2));
    float y = (float)(center.getY() + radius * Math.cos(-degrees * Math.PI / 180 + Math.PI / 2));
    return new Point(x, y);
}

public String asString(){
    return x + TOKEN_STRING_SPLIT + y;
}

public byte[] toBytes(){
    ByteBuffer byteBuffer = ByteBuffer.obtain(ByteBuffer.FLOAT_SIZE(2));
    byteBuffer.put(x);
    byteBuffer.put(y);
    return byteBuffer.array();
}

public Point minus(Point point){
    return new Point(x - point.x, y - point.y);
}

public double distance(Point point){
    Point diff = minus(point);
    return Math.sqrt(diff.x * diff.x + diff.y * diff.y);
}

public boolean isInside(RectF mask){
    return (mask.left<x && x<mask.right) && (mask.top<y && y<mask.bottom);
}

public float getX(){
    return x;
}

public Point setX(float x){
    this.x = x;
    return this;
}

public float getY(){
    return y;
}

public Point setY(float y){
    this.y = y;
    return this;
}

public float ratio(){
    if(x != 0){
        return x / y;
    } else {
        return Float.NaN;
    }
}

public Point scaleTo(Scale s){
    x = x * s.getW();
    y = y * s.getH();
    return this;
}

public Point scaleFrom(Scale s){
    x = x / s.getW();
    y = y / s.getH();
    return this;
}

@Override
public boolean equals(Object obj){
    if(!(obj instanceof Point)){
        return false;
    }
    Point second = (Point)obj;
    return (this.x == second.x) && (this.y == second.y);
}

@Override
public String toString(){
    return "( x:" + x + " , y:" + y + " )";
}

public DebugString toDebugString(){
    DebugString data = new DebugString();
    data.append("x", x);
    data.append("y", y);
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

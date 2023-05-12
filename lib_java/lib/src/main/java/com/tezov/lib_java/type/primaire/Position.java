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
import com.tezov.lib_java.buffer.ByteBuffer;
import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java.debug.DebugLog;
import com.tezov.lib_java.debug.DebugString;
import com.tezov.lib_java.debug.DebugTrack;

import org.apache.commons.lang3.math.NumberUtils;

public class Position{
protected static final String TOKEN_STRING_SPLIT = "/";
int x;
int y;

public Position(){
    this(0, 0);
}

public Position(int x, int y){
DebugTrack.start().create(this).end();
    this.x = x;
    this.y = y;
}

public static Position fromString(String s){
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
    return new Position(NumberUtils.toInt(data[0]), NumberUtils.toInt(data[1]));
}

public static Point fromBytes(byte[] bytes){
    if(bytes == null){
        return null;
    }
    ByteBuffer bytesBuffer = ByteBuffer.wrap(bytes);
    return new Point(bytesBuffer.getInt(), bytesBuffer.getInt());
}

public static Position indexToPosition(int index, int xPerRow){
    return new Position(index % xPerRow, index / xPerRow);
}

public String asString(){
    return x + TOKEN_STRING_SPLIT + y;
}

public byte[] toBytes(){
    ByteBuffer byteBuffer = ByteBuffer.obtain(ByteBuffer.INT_SIZE(2));
    byteBuffer.put(x);
    byteBuffer.put(y);
    return byteBuffer.array();
}

public Position minus(Position point){
    return new Position(x - point.x, y - point.y);
}

public double distance(Position point){
    Position diff = minus(point);
    return Math.sqrt(diff.x * diff.x + diff.y * diff.y);
}

public int toIndex(int xPerRow){
    return xPerRow * y + x;
}

public int getX(){
    return x;
}

public int getY(){
    return y;
}

@Override
public boolean equals(Object obj){
    if(!(obj instanceof Position)){
        return false;
    }
    Position second = (Position)obj;
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

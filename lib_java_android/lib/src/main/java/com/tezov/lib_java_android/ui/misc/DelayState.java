/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java_android.ui.misc;

import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java.type.primitive.ObjectTo;
import com.tezov.lib_java.type.primitive.IntTo;
import com.tezov.lib_java.type.unit.UnitByte;
import com.tezov.lib_java.toolbox.CompareType;
import com.tezov.lib_java.util.UtilsString;
import java.util.List;
import java.util.LinkedList;
import java.util.Set;
import com.tezov.lib_java_android.database.sqlLite.filter.dbFilterOrder;
import com.tezov.lib_java_android.database.sqlLite.filter.chunk.ChunkCommand;
import androidx.fragment.app.Fragment;
import com.tezov.lib_java.buffer.ByteBuffer;
import com.tezov.lib_java.toolbox.Clock;
import com.tezov.lib_java.debug.DebugLog;
import com.tezov.lib_java.debug.DebugString;
import com.tezov.lib_java.debug.DebugTrack;
import com.tezov.lib_java.wrapperAnonymous.ConsumerW;

public class DelayState{
private long delayTarget = 0;
private Long timestampTarget = null;
private Long timestampPaused = null;

public DelayState(){
DebugTrack.start().create(this).end();
}
public long getTarget(){
    return delayTarget;
}
public DelayState setTarget(long delay){
    this.delayTarget = delay;
    return this;
}
private Long computeDelay(){
    long timeStampNow = Clock.MilliSecond.now();
    Long delay;
    if(timestampTarget != null){
        if(timestampPaused == null){
            if(timestampTarget >= timeStampNow){
                delay = timestampTarget - timeStampNow;
            } else {
                delay = null;
            }
        } else {
            delay = timestampTarget - timestampPaused;
            if(delay < 0){
                delay = null;
            }
        }
    } else {
        delay = delayTarget;
    }
    if(delay != null){
        timestampTarget = timeStampNow + delay;
    }
    return delay;
}

public DelayState start(ConsumerW<Long> consumer){
    Long delay = computeDelay();
    if(consumer != null){
        consumer.accept(delay);
    }
    return this;
}

public DelayState pause(){
    timestampPaused = Clock.MilliSecond.now();
    return this;
}

public DelayState reset(){
    timestampTarget = null;
    timestampPaused = null;
    computeDelay();
    return this;
}

protected int byteBufferLength(){
    return ByteBuffer.LONG_SIZE(3);
}

protected ByteBuffer toByteBuffer(){
    ByteBuffer bytesBuffer = ByteBuffer.obtain(byteBufferLength());
    bytesBuffer.put(delayTarget);
    bytesBuffer.put(timestampTarget);
    bytesBuffer.put(timestampPaused);
    return bytesBuffer;
}

public byte[] toBytes(){
    return toByteBuffer().array();
}

protected DelayState fromByteBuffer(ByteBuffer byteBuffer){
    delayTarget = byteBuffer.getLong();
    timestampTarget = byteBuffer.getLong();
    timestampPaused = byteBuffer.getLong();
    return this;
}

public DelayState fromBytes(byte[] bytes){
    fromByteBuffer(ByteBuffer.wrap(bytes));
    return this;
}

public DebugString toDebugString(){
    DebugString data = new DebugString();
    data.append("delayStart_scd", Clock.MilliSecondTo.Second.toString(delayTarget));
    data.append("timestampNow", Clock.Time.now());
    data.append("timestampNext", timestampTarget != null ? Clock.MilliSecondTo.Time.toString(timestampTarget) : null);
    data.append("timestampPaused", timestampPaused != null ? Clock.MilliSecondTo.Time.toString(timestampPaused) : null);
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

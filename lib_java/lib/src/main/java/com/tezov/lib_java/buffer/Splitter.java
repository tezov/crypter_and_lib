/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java.buffer;

import com.tezov.lib_java.debug.DebugLog;
import com.tezov.lib_java.type.primitive.ObjectTo;
import com.tezov.lib_java.type.primitive.IntTo;
import com.tezov.lib_java.type.unit.UnitByte;
import com.tezov.lib_java.toolbox.CompareType;
import com.tezov.lib_java.toolbox.Clock;
import com.tezov.lib_java.util.UtilsString;
import java.util.LinkedList;
import java.util.Set;
import com.tezov.lib_java.cipher.dataAdapter.bytes.DataBytesAdapter;
import com.tezov.lib_java.cipher.dataAdapter.bytes.DataBytesToStringAdapter;
import com.tezov.lib_java.cipher.dataAdapter.bytes.DataStringToBytesAdapter;
import com.tezov.lib_java.cipher.dataAdapter.string.DataStringAdapter;
import com.tezov.lib_java.cipher.definition.defDataAdapterDecoder;
import com.tezov.lib_java.cipher.definition.defDataAdapterEncoder;
import com.tezov.lib_java.debug.DebugTrack;
import com.tezov.lib_java.type.primaire.Entry;
import com.tezov.lib_java.wrapperAnonymous.ComparatorW;
import com.tezov.lib_java.util.UtilsList;

import com.tezov.lib_java.async.notifier.observer.state.ObserverState;
import com.tezov.lib_java.async.notifier.task.TaskState;
import com.tezov.lib_java.file.UtilsStream;
import com.tezov.lib_java.toolbox.Compare;
import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java.debug.DebugObject;
import com.tezov.lib_java.type.collection.ListEntry;
import com.tezov.lib_java.util.UtilsBytes;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.tezov.lib_java.buffer.ByteBufferOutput.BUFFER_INITIAL_SIZE;

public class Splitter{
private final static DataStringAdapter.Format FORMAT = DataStringAdapter.Format.BASE58;
private final static float MIN_LENGTH_PART_DEVIATION = 0.85f;
private static final int ID_LENGTH = 4;

public static List<String> splitToString(byte[] bytes, int lengthPart){
    return split(bytes, lengthPart, DataBytesToStringAdapter.forEncoder(FORMAT));
}
public static List<byte[]> split(byte[] bytes, int lengthPart){
    return split(bytes, lengthPart, new DataBytesAdapter());
}
public static <IN, OUT> List<OUT> split(IN data, int lengthPart, defDataAdapterEncoder<IN, OUT> dataAdapter){
    int lengthPartFull = lengthPart;
    byte[] bytes = dataAdapter.fromIn(data);
    byte[] id = UtilsBytes.random(ID_LENGTH);
    Integer splitParts = splitPartsCount(id, bytes, lengthPartFull);
    if(splitParts == null){
        return null;
    }
    lengthPartFull = (bytes.length / splitParts);
    int partsFull;
    int lengthPartNotFull;
    float lengthDeviation;
    while(true){
        partsFull = bytes.length / lengthPartFull;
        lengthPartNotFull = bytes.length % lengthPartFull;
        lengthDeviation = ((float)lengthPartNotFull)/lengthPartFull;
        if((lengthPartNotFull <= 0)||(lengthDeviation >= MIN_LENGTH_PART_DEVIATION)){
            break;
        }
        int correction = (lengthPartFull - lengthPartNotFull) / (partsFull+1);
        lengthPartFull -= correction;
    }
    int parts = partsFull + (lengthPartNotFull > 0 ? 1 : 0);
    List<OUT> datas = new ArrayList<>();
    for(int i = 0; i < partsFull; i++){
        OUT out = dataAdapter.toOut(splitPartPack(id, parts, i, bytes, i * lengthPartFull, lengthPartFull));
        datas.add(out);
    }
    if(lengthPartNotFull > 0){
        OUT out = dataAdapter.toOut(splitPartPack(id, parts, (parts - 1), bytes, partsFull * lengthPartFull, lengthPartNotFull));
        datas.add(out);
    }
    return datas;

}

private static int splitLengthHeader(byte[] id, int parts, int lengthPart){
    ByteBufferBuilder buffer = ByteBufferBuilder.obtain();
    buffer.put(id);
    buffer.put(parts);
    buffer.put(parts);
    buffer.put(lengthPart);
    return buffer.arrayPacked().length;
}
private static Integer splitPartsCount(byte[] id, byte[] bytes, int lengthPart){
    int partsPrevious = -1;
    int parts = 0;
    int length = bytes.length;
    while(parts != partsPrevious){
        partsPrevious = parts;
        int partsFull = length / lengthPart;
        int rest = length % lengthPart;
        parts = partsFull + (rest > 0 ? 1 : 0);
        int lengthHeader = splitLengthHeader(id, parts, lengthPart);
        if(lengthPart <= lengthHeader){
DebugException.start().log("lengthPart " + lengthPart + " is too small. lengthHeader is " + lengthHeader).end();
            return null;
        }
        length = bytes.length + parts * lengthHeader;
    }
    return parts;
}
public static byte[] splitPartPack(byte[] id, int parts, int part, byte[] bytes, int offset, int length){
    ByteBufferBuilder buffer = ByteBufferBuilder.obtain();
    buffer.put(id);
    buffer.put(parts);
    buffer.put(part);
    buffer.put(Arrays.copyOfRange(bytes, offset, offset + length));
    return buffer.arrayPacked();
}

public static byte[] joinString(List<String> datas){
    return join(datas, DataStringToBytesAdapter.forDecoder(FORMAT));
}
public static byte[] join(List<byte[]> datas){
    return join(datas, new DataBytesAdapter());
}
public static <IN, OUT> OUT join(List<IN> datas, defDataAdapterDecoder<IN, OUT> dataAdapter){
    byte[] id;
    byte[] idPrevious = null;
    ByteArrayOutputStream out = new ByteArrayOutputStream(BUFFER_INITIAL_SIZE);
    int size = datas.size();
    try{
        for(int i = 0; i < size; i++){
            byte[] data = dataAdapter.fromIn(datas.get(i));
            ByteBuffer buffer = ByteBuffer.wrapPacked(data);
            id = buffer.getBytes();
            if((idPrevious != null) && !Compare.equals(idPrevious, id)){
DebugException.start().log("wrong id " + DebugObject.toString(id) + " expected " + DebugObject.toString(idPrevious)).end();
                return null;
            }
            idPrevious = id;
            Integer parts = buffer.getInt();
            if(!Compare.equals(parts, size)){
DebugException.start().log("wrong parts " + parts + " expected " + size).end();
                return null;
            }
            int part = buffer.getInt();
            if(!Compare.equals(part, i)){
DebugException.start().log("wrong part " + part + " expected " + i).end();
                return null;
            }
            out.write(buffer.getBytes());
        }
        byte[] join = out.toByteArray();
        UtilsStream.close(out);
        return dataAdapter.toOut(join);
    } catch(Throwable e){
        UtilsStream.close(out);
DebugException.start().log(e).end();
        return null;
    }
}

public static Joiner<byte[], byte[]> newJoinerBytes(){
    return new Joiner<>(new DataBytesAdapter());
}
public static Joiner<String, byte[]> newJoinerStringToBytes(){
    return new Joiner<>(DataStringToBytesAdapter.forDecoder(FORMAT));
}
public static class Joiner<IN, OUT>{
    private final defDataAdapterDecoder<IN, OUT> dataAdapter;
    private ListEntry<Integer, IN> datas;
    private byte[] id;
    private Integer parts ;
    private TaskState task;
    public Joiner(defDataAdapterDecoder<IN, OUT> dataAdapter){
DebugTrack.start().create(this).end();
        this.dataAdapter = dataAdapter;
        this.datas = new ListEntry<>();
        reset(true);
    }
    public void observeProgress(ObserverState observer){
        task.observe(observer);
    }
    public void cancel(){
        if(task != null){
            task.cancel();
            task.notifyCanceled();
            task = null;
        }
    }
    public void reset(boolean clearTask){
        datas = new ListEntry<>();
        id = null;
        parts = null;
        if((task!=null) && clearTask){
            task.cancel();
            task.notifyCanceled();
            task = null;
        }
        if(task == null){
            task = new TaskState();
        }
    }
    public boolean update(IN data){
        if((task == null) || isComplete()){
            return true;
        }
        if(data == null){
            return false;
        }
        byte[] bytes = dataAdapter.fromIn(data);
        if((bytes == null) || (bytes.length <= 0)){
            return false;
        }
        ByteBuffer buffer = ByteBuffer.wrapPacked(bytes);
        byte[] id = buffer.getBytes();
        if(id == null){
            return false;
        }
        if(this.id == null){
            this.id = id;
        }
        if(!Compare.equals(id, id)){
            return false;
        }
        Integer parts = buffer.getInt();
        if(parts == null){
            return false;
        }
        if(this.parts == null){
            this.parts = parts;
        }
        Integer part = buffer.getInt();
        if((part != null) && !datas.hasKey(part)){
            datas.put(part, data);
        }
        boolean isDone = isComplete();
        TaskState tmp = task;
        if(isDone){
            datas = (ListEntry<Integer, IN>)UtilsList.sort(datas, new ComparatorW<Entry<Integer, IN>>(){
                @Override
                public int compare(Entry<Integer, IN> e1, Entry<Integer, IN> e2){
                    return Integer.compare(e1.key, e2.key);
                }
            }, ListEntry::new);
            task = null;
        }
        tmp.notifyComplete();
        return isDone;
    }
    public Integer getPartMax(){
        return parts;
    }
    public int getPartObtained(){
        return datas.size();
    }
    public OUT getData(){
        return join(datas.getValues(), dataAdapter);
    }
    public boolean isComplete(){
        return Compare.equals(datas.size(),parts);
    }

    @Override
    protected void finalize() throws Throwable{
DebugTrack.start().destroy(this).end();
        super.finalize();
    }

}

}

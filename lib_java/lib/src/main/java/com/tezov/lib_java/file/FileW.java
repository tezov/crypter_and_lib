/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java.file;

import com.tezov.lib_java.debug.DebugLog;
import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java.type.primitive.ObjectTo;
import com.tezov.lib_java.type.primitive.IntTo;
import com.tezov.lib_java.type.unit.UnitByte;
import com.tezov.lib_java.toolbox.CompareType;
import com.tezov.lib_java.toolbox.Clock;
import com.tezov.lib_java.util.UtilsString;
import java.util.LinkedList;
import java.util.Set;
import com.tezov.lib_java.buffer.ByteBuffer;
import com.tezov.lib_java.debug.DebugTrack;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.GatheringByteChannel;
import java.nio.channels.ScatteringByteChannel;
import java.util.ArrayList;
import java.util.List;

public class FileW extends java.io.File{
public FileW(String pathname){
    super(pathname);
    init();
}

public FileW(String parent, String child){
    this(new FileW(parent), child);
}

public FileW(FileW parent, String child){
    super(parent, child);
    init();
}

private void init(){
DebugTrack.start().create(this).end();
}
@Override
public FileW getParentFile(){
    String parent = super.getParent();
    if(parent == null){
        return null;
    } else {
        return new FileW(parent);
    }
}
public boolean createDirectory(){
    if(!exists()){
        return mkdirs();
    } else {
        return true;
    }
}

public String getExtension(){
    return UtilsFile.getExtension(getName());
}
public String getMineType(){
    return UtilsFile.getMimeTypeForFullName(getName());
}

private int computeBufferLength(List<ByteBuffer> byteBuffers){
    int length = 0;
    for(ByteBuffer byteBuffer: byteBuffers){
        length += byteBuffer.remaining();
    }
    return length;
}

public void write(ByteBuffer byteBuffer) throws IOException{
    write(new ByteBuffer[]{byteBuffer});
}
public void write(ByteBuffer[] byteBuffers) throws IOException{
    GatheringByteChannel output = null;
    try{
        output = new FileOutputStream(this).getChannel();
        java.nio.ByteBuffer[] buffers = new java.nio.ByteBuffer[byteBuffers.length];
        for(int end = byteBuffers.length, i = 0; i < end; i++){
            buffers[i] = byteBuffers[i].buffer();
        }
        output.write(buffers);
        output.close();
    }
    catch(IOException e){
        if(output != null){
            output.close();
        }
        throw e;
    }
}

public ByteBuffer read() throws IOException{
    List<ByteBuffer> byteBuffers = read(null, true);
    if(byteBuffers == null){
        return null;
    } else {
        return byteBuffers.get(0);
    }
}
public List<ByteBuffer> read(List<ByteBuffer> byteBuffers, boolean readAll) throws IOException{
    ScatteringByteChannel input = null;
    try{
        input = new FileInputStream(this).getChannel();
        if(byteBuffers == null){
            if(!readAll){
                input.close();
                return null;
            }
            byteBuffers = new ArrayList<>();
        }
        int bufferLength = computeBufferLength(byteBuffers);
        int fileLength = (int)length();
        java.nio.ByteBuffer[] buffers;
        if(readAll && (fileLength > bufferLength)){
            buffers = new java.nio.ByteBuffer[byteBuffers.size() + 1];
            byteBuffers.add(ByteBuffer.obtain(fileLength - bufferLength));
        } else {
            buffers = new java.nio.ByteBuffer[byteBuffers.size()];
        }
        for(int i = 0; i < byteBuffers.size(); i++){
            buffers[i] = byteBuffers.get(i).buffer();
        }
        input.read(buffers);
        for(ByteBuffer byteBuffer: byteBuffers){
            byteBuffer.rewind();
        }
        input.close();
        return byteBuffers;
    } catch(IOException e){
        if(input != null){
            input.close();
        }
        throw e;
    }
}

@Override
protected void finalize() throws Throwable{
DebugTrack.start().destroy(this).end();
    super.finalize();
}

}

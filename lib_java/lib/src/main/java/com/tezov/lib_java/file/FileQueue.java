/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java.file;

import com.tezov.lib_java.debug.DebugLog;
import com.tezov.lib_java.type.primitive.ObjectTo;
import com.tezov.lib_java.type.primitive.IntTo;
import com.tezov.lib_java.type.unit.UnitByte;
import com.tezov.lib_java.toolbox.CompareType;
import com.tezov.lib_java.toolbox.Clock;
import com.tezov.lib_java.util.UtilsString;
import java.util.LinkedList;
import java.util.Set;
import com.tezov.lib_java.async.Handler;
import com.tezov.lib_java.async.notifier.task.TaskState;
import com.tezov.lib_java.async.notifier.task.TaskValue;
import com.tezov.lib_java.buffer.ByteBuffer;
import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java.debug.DebugTrack;
import com.tezov.lib_java.type.runnable.RunnableQueue;
import com.tezov.lib_java.type.runnable.RunnableW;

import java.util.List;

public class FileQueue{
private final RunnableQueue<RunnableW> queue;

public FileQueue(){
DebugTrack.start().create(this).end();
    queue = new RunnableQueue<>(this, Handler.SECONDARY());
}

private void addRunnable(RunnableW runnable){
    queue.add(runnable);
    if(!queue.isBusy()){
        nextRunnable();
    }
}
private void nextRunnable(){
    if(!queue.isEmpty()){
        queue.next();
    } else {
        queue.done();
    }
}

public TaskState.Observable write(FileW file, ByteBuffer byteBuffer){
    return write(file, new ByteBuffer[]{byteBuffer});
}
public TaskState.Observable write(FileW file, ByteBuffer[] byteBuffers){
    TaskState task = new TaskState();
    addRunnable(new RunnableW(){
        @Override
        public void runSafe() throws Throwable{
            file.write(byteBuffers);
            task.notifyComplete();
        }
        @Override
        public void onException(Throwable e){
            task.notifyException(e);
        }
        @Override
        public void afterRun(){
            nextRunnable();
        }
    });
    return task.getObservable();
}

public TaskValue<ByteBuffer>.Observable read(FileW file){
    TaskValue<ByteBuffer> task = new TaskValue<>();
    addRunnable(new RunnableW(){
        @Override
        public void runSafe() throws Throwable{
            List<ByteBuffer> results = file.read(null, true);
            if(results == null){
                task.notifyComplete(null);
            } else {
                task.notifyComplete(results.get(0));
            }

        }
        @Override
        public void onException(Throwable e){
            task.notifyException(null, e);
        }
        @Override
        public void afterRun(){
            nextRunnable();
        }
    });
    return task.getObservable();
}
public TaskValue<List<ByteBuffer>>.Observable read(FileW file, List<ByteBuffer> byteBuffers, boolean readAll){
    TaskValue<List<ByteBuffer>> task = new TaskValue<>();
    addRunnable(new RunnableW(){
        @Override
        public void runSafe() throws Throwable{
            List<ByteBuffer> results = file.read(byteBuffers, readAll);
            task.notifyComplete(results);
        }
        @Override
        public void onException(Throwable e){
            task.notifyException(null, e);
        }
        @Override
        public void afterRun(){
            nextRunnable();
        }
    });
    return task.getObservable();
}

public TaskState.Observable write(File file, ByteBuffer byteBuffer, String fileExtension){
    return write(file, new ByteBuffer[]{byteBuffer}, fileExtension);
}
public TaskState.Observable write(File file, ByteBuffer byteBuffer){
    return write(file, new ByteBuffer[]{byteBuffer});
}
public TaskState.Observable write(File file, ByteBuffer[] byteBuffers, String fileExtension){
    if(!file.isBuilt()){
        file.setExtension(fileExtension);
    }
    return write(file, byteBuffers);
}
public TaskState.Observable write(File file, ByteBuffer[] byteBuffers){
    return write(file.getFile(), byteBuffers);
}

public TaskValue<ByteBuffer>.Observable read(File file,String fileExtension){
    if(!file.isBuilt()){
        file.setExtension(fileExtension);
    } else if(!fileExtension.equals(file.getExtension())){
DebugException.start().explode("extension did not match and file already built").end();
    }
    return read(file);
}
public TaskValue<ByteBuffer>.Observable read(File file){
    return read(file.getFile());
}
public TaskValue<List<ByteBuffer>>.Observable read(File file, List<ByteBuffer> byteBuffers, String fileExtension){
    if(!file.isBuilt()){
        file.setExtension(fileExtension);
    }
    return read(file, byteBuffers);
}
public TaskValue<List<ByteBuffer>>.Observable read(File file, List<ByteBuffer> byteBuffers){
    return read(file.getFile(), byteBuffers, true);
}

@Override
protected void finalize() throws Throwable{
DebugTrack.start().destroy(this).end();
    super.finalize();
}

}

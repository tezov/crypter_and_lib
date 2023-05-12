/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java.socket;

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
import com.tezov.lib_java.async.Handler;
import com.tezov.lib_java.async.notifier.task.TaskState;
import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java.type.runnable.RunnableW;
import com.tezov.lib_java.wrapperAnonymous.UncaughtExceptionHandlerW;

import java.io.IOException;
import java.net.*;

import static com.tezov.lib_java.socket.TcpServer.NULL_PORT;

public class UdpPacket extends UdpListener.DatagramPacket{
public final static String BROADCAST_ADDRESS_DEFAULT = "255.255.255.255";
private final static int MAX_BIND_RETRY = 10;
protected Handler thread = null;
protected DatagramSocket socket = null;
private boolean isBusy = false;
private Integer portLocal = null;

public UdpPacket(){
    super(new DatagramPacket(new byte[0], 0));
}

public UdpPacket(InetAddress address, int port){
    this(new InetSocketAddress(address, port));
}
public UdpPacket(String ip, int port){
    this(new InetSocketAddress(ip, port));
}
public UdpPacket(InetSocketAddress address){
    super(new DatagramPacket(new byte[0], 0, address));
}

private UdpPacket me(){
    return this;
}

public Integer getPortLocal(){
    if(socket != null){
        int port = socket.getLocalPort();
        return port == NULL_PORT ? null : port;
    } else {
        return portLocal;
    }
}
public UdpPacket setPortLocal(int port){
    this.portLocal = port;
    return this;
}

public Handler getThread(){
    return thread;
}
public void post(RunnableW r){
    thread.post(this, r);
}

public boolean isBound(){
    return socket != null && socket.isBound();
}

public TaskState.Observable bind(){
    if(isBound()){
        return TaskState.Exception("already bound");
    }
    if(isBusy){
        return TaskState.Exception("busy");
    }
    isBusy = true;
    TaskState finalTask = new TaskState();
    if(thread != null){
        thread.quit();
    }
    thread = Handler.newHandler(this);
    thread.setUncaughtExceptionHandler(new UncaughtExceptionHandlerW(){
        public void uncaughtException(Thread t, Throwable e){
            onRunningException(e);
            unbind(true, false);
        }
    });
    thread.post(this, new RunnableW(){
        TaskState task = finalTask;
        @Override
        public void runSafe() throws Throwable{
            socket = new DatagramSocket(null);
            int count = 0;
            do{
                try{
                    Integer port = portLocal;
                    if(port == null){
                        port = UtilsSocket.randomPortDynamic();
                    }
                    SocketAddress inetSocketAddress = new InetSocketAddress((InetAddress)null, port);
                    socket.bind(inetSocketAddress);
                    break;
                } catch(IOException e){
                    count++;
                }
            } while(count < MAX_BIND_RETRY);
            if(!socket.isBound()){
                throw new Throwable("Failed to bind");
            }
            onBound(me());
            TaskState tmp = task;
            task = null;
            isBusy = false;
            tmp.notifyComplete();
        }
        @Override
        public void onException(Throwable e){
            TaskState tmp = task;
            task = null;
            tmp.notifyException(e);
            unbind(true, false);
        }
    });
    return finalTask.getObservable();
}
public void send(){
    try{
        socket.send(packet);
    } catch(IOException e){
        onRunningException(e);
    }
}

public void unbind(){
    unbind(false, true);
}
public void unbind(boolean force){
    unbind(force, true);
}
private void unbind(boolean force, boolean callOnUnBound){
    if(!isBusy || force){
        isBusy = true;
        if(socket != null){
            if(!socket.isClosed()){
                try{
                    socket.close();
                } catch(Throwable e){
DebugException.start().log(e).end();
                }
            }
            socket = null;
        }
        if(thread != null){
            thread.quit();
            thread = null;

        }
        if(callOnUnBound){
            onUnbound();
        }
        isBusy = false;
    }
}

protected void onBound(UdpPacket packet){

}
protected void onRunningException(Throwable e){

DebugException.start().log(e).end();

}
protected void onUnbound(){

}

}

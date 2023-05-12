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
import com.tezov.lib_java.type.runnable.RunnableTimeOut;
import com.tezov.lib_java.type.runnable.RunnableW;
import com.tezov.lib_java.wrapperAnonymous.UncaughtExceptionHandlerW;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.concurrent.TimeUnit;

public class TcpClient extends TcpServer.SocketClient{
private final static int MAX_BIND_RETRY = 10;
private boolean isBusy = false;

private InetSocketAddress addressRemote;
private Integer portLocal = null;

public TcpClient(){
    this(null);
}
public TcpClient(String addressRemote, int portRemote){
    this(new InetSocketAddress(addressRemote, portRemote));
}
public TcpClient(InetSocketAddress addressRemote){
    this.addressRemote = addressRemote;
}
private TcpClient me(){
    return this;
}

public Integer getPortRemote(){
    Integer port = super.getPortRemote();
    if(port != null){
        return port;
    } else if(addressRemote != null){
        return addressRemote.getPort();
    } else {
        return null;
    }
}
public String getAddressRemote(){
    String address = super.getAddressRemote();
    if(address != null){
        return address;
    } else if(addressRemote != null){
        return addressRemote.getHostName();
    } else {
        return null;
    }
}
public TcpClient setAddressRemote(InetSocketAddress addressRemote){
    this.addressRemote = addressRemote;
    return this;
}
public TcpClient setAddressRemote(String ip, int port){
    setAddressRemote(new InetSocketAddress(ip, port));
    return this;
}

@Override
public Integer getPortLocal(){
    Integer port = super.getPortLocal();
    if(port == null){
        port = portLocal;
    }
    return port;
}
public TcpClient setPortLocal(int port){
    this.portLocal = port;
    return this;
}

public boolean isConnected(){
    return (socket != null) && socket.isConnected();
}
public boolean isBusy(){
    return isBusy;
}

public TaskState.Observable connect(){
    return connect(null, null);
}
public TaskState.Observable connect(Long timeout_ms){
    return connect(timeout_ms, TimeUnit.MILLISECONDS);
}
public TaskState.Observable connect(Long timeout, TimeUnit unit){
    if(isConnected()){
        return TaskState.Exception("already connected");
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
        @Override
        public void uncaughtException(Thread t, Throwable e){
            onRunningException(e);
            disconnect(true, false);
        }
    });
    thread.post(this, new RunnableW(){
        TaskState task = finalTask;
        RunnableTimeOut runnableTimeout = null;
        void startTimeOut(){
            if(timeout != null){
                runnableTimeout = new RunnableTimeOut(this, timeout, unit){
                    @Override
                    public void onTimeOut(){
                        finalTask.notifyException(new Throwable("timeout"));
                        disconnect(false, false);
                        isBusy = false;
                    }
                };
                runnableTimeout.start();
            }
        }
        @Override
        public void runSafe() throws Throwable{
            if(socket != null){
DebugException.start().log("not null").end();
                socket.close();
            }
            socket = new Socket();
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
            startTimeOut();
            socket.connect(addressRemote);
            if(runnableTimeout != null){
                runnableTimeout.completed();
            }
            if((runnableTimeout == null) || !runnableTimeout.isTimeout()){
                onConnected(me());
                TaskState tmp = task;
                task = null;
                isBusy = false;
                tmp.notifyComplete();
            }
        }
        @Override
        public void onException(Throwable e){
            TaskState tmp = task;
            task = null;
            tmp.notifyException(e);
            disconnect(true, false);
        }
    });
    return finalTask.getObservable();
}

public void disconnect(){
    disconnect(false, true);
}
public void disconnect(boolean force){
    disconnect(force, true);
}
private void disconnect(boolean force, boolean callOnDisconnect){
    if(!isBusy || force){
        isBusy = true;
        close();
        if(callOnDisconnect){
            onDisconnected();
        }
        isBusy = false;
    }
}

protected void onConnected(TcpClient socket){
    socket.disconnect();
}
protected void onRunningException(Throwable e){

DebugException.start().log(e).end();

}
protected void onDisconnected(){

}

}

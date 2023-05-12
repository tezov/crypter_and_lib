/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java.socket;

import com.tezov.lib_java.debug.DebugLog;
import com.tezov.lib_java.type.primitive.ObjectTo;
import com.tezov.lib_java.type.primitive.IntTo;
import com.tezov.lib_java.type.unit.UnitByte;
import com.tezov.lib_java.toolbox.CompareType;
import com.tezov.lib_java.toolbox.Clock;
import com.tezov.lib_java.util.UtilsString;
import java.util.List;
import java.util.LinkedList;
import java.util.Set;
import static com.tezov.lib_java.socket.TcpServer.NULL_PORT;

import com.tezov.lib_java.application.AppConfig;
import com.tezov.lib_java.application.AppConfigKey;
import com.tezov.lib_java.async.Handler;
import com.tezov.lib_java.async.notifier.task.TaskState;
import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java.debug.DebugTrack;
import com.tezov.lib_java.type.ref.WR;
import com.tezov.lib_java.type.runnable.RunnableW;
import com.tezov.lib_java.util.UtilsBytes;
import com.tezov.lib_java.wrapperAnonymous.UncaughtExceptionHandlerW;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class UdpListener{
public final static int BUFFER_LENGTH_DEFAULT = AppConfig.getInt(AppConfigKey.UDP_LISTENER_PACKET_BUFFER_LENGTH_o.getId());
private boolean isBusy = false;
private Handler thread = null;
private DatagramSocket socket = null;
private Integer portLocal;
private InetAddress addressLocal = null;
private boolean isBroadcastEnabled = false;
private int bufferLength;

public UdpListener(){
    this(null, BUFFER_LENGTH_DEFAULT);
}
public UdpListener(Integer portLocal, int bufferLength){
DebugTrack.start().create(this).end();
    this.portLocal = portLocal;
    this.bufferLength = bufferLength;
}

public Integer getPortLocal(){
    if(socket != null){
        int port = socket.getLocalPort();
        return port == NULL_PORT ? null : port;
    } else {
        return portLocal;
    }
}
public UdpListener setPortLocal(int portLocal){
    this.portLocal = portLocal;
    return this;
}
public boolean isBroadcastEnabled(){
    return isBroadcastEnabled;
}
public UdpListener setBroadcastEnabled(boolean flag){
    this.isBroadcastEnabled = flag;
    return this;
}
public String getAddressLocal(){
    if((socket != null) && (socket.getInetAddress() != null)){
        return socket.getInetAddress().getHostAddress();
    } else if(addressLocal != null){
        return addressLocal.getHostAddress();
    } else {
        return null;
    }
}
public UdpListener setAddressLocal(String addressLocal){
    try{
        return setAddressLocal(InetAddress.getByName(addressLocal));
    } catch(UnknownHostException e){

DebugException.start().log(e).end();

        return setAddressLocal((InetAddress)null);
    }
}
public UdpListener setAddressLocal(InetAddress address){
    this.addressLocal = address;
    return this;
}
public int getBufferLength(){
    return bufferLength;
}
public UdpListener setBufferLength(int length){
    this.bufferLength = length;
    return this;
}

public boolean isStarted(){
    return (socket != null) && !socket.isClosed();
}
public boolean isBusy(){
    return isBusy;
}

public TaskState.Observable start(){
    if(isStarted()){
        return TaskState.Exception("already started");
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
            stop(true);
        }
    });
    thread.post(this, new RunnableW(){
        TaskState task = finalTask;
        @Override
        public void runSafe() throws Throwable{
            InetAddress inetAddress = null;
            if(!isBroadcastEnabled){
                if(addressLocal == null){
                    throw new Throwable("not valid inetAddress found");
                }
                inetAddress = addressLocal;
            }
            Integer port = portLocal;
            if(port == null){
                port = UtilsSocket.randomPortDynamic();
            }
            socket = new DatagramSocket(null);
            socket.bind(new InetSocketAddress(inetAddress, port));
            socket.setBroadcast(isBroadcastEnabled);
            onStarted();
            TaskState tmp = task;
            task = null;
            isBusy = false;
            tmp.notifyComplete();
            while(isStarted()){
                java.net.DatagramPacket packet = new java.net.DatagramPacket(UtilsBytes.obtain(bufferLength), bufferLength);
                socket.receive(packet);
                onPacket(new DatagramPacket(packet));
            }
            onStopped();
            stop(false);
        }
        @Override
        public void onException(Throwable e){
            if(task != null){
                TaskState tmp = task;
                task = null;
                tmp.notifyException(e);
                stop(true);
            } else {
                if((e instanceof SocketException) && "Socket closed".equals(e.getMessage())){
                    onStopped();
                    stop(false);
                } else {
                    thread.throwUncaughtException(e);
                }
            }
        }
    });
    return finalTask.getObservable();
}

public void stop(){
    stop(false);
}
public void stop(boolean force){
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
        isBusy = false;
    }
}

protected void onStarted(){

}
protected void onPacket(DatagramPacket packet){
    socket.close();
}
protected void onRunningException(Throwable e){
DebugException.start().log(e).end();
}
protected void onStopped(){

}

@Override
protected void finalize() throws Throwable{
DebugTrack.start().destroy(this).end();
    super.finalize();
}
public static class DatagramPacket{
    protected java.net.DatagramPacket packet;
    public DatagramPacket(java.net.DatagramPacket packet){
DebugTrack.start().create(this).end();
        this.packet = packet;
    }

    public String getAddressRemote(){
        if((packet != null) && (packet.getAddress() != null)){
            return packet.getAddress().getHostAddress();
        } else {
            return null;
        }
    }
    public DatagramPacket setAddressRemote(String address) throws UnknownHostException{
        return setAddressRemote(InetAddress.getByName(address));
    }
    public DatagramPacket setAddressRemote(InetAddress address){
        packet.setAddress(address);
        return this;
    }

    Integer getPortRemote(){
        if(packet != null){
            int port = packet.getPort();
            return port == NULL_PORT ? null : port;
        } else {
            return null;
        }
    }
    public DatagramPacket setPortRemote(int port){
        packet.setPort(port);
        return this;
    }

    public void write(byte[] b){
        write(b, 0, b.length);
    }
    public void write(byte[] b, int off){
        write(b, off, b.length - off);
    }
    public void write(byte[] b, int off, int len){
        byte[] packetBytes = UtilsBytes.obtain(len);
        System.arraycopy(b, off, packetBytes, 0, len);
        packet.setData(packetBytes);
    }

    public byte[] read(){
        int packetAvailable = packet.getLength() - packet.getOffset();
        byte[] b = UtilsBytes.obtain(packetAvailable);
        read(b, 0, b.length);
        return b;
    }
    public byte[] read(int len){
        int packetAvailable = packet.getLength() - packet.getOffset();
        if(len > packetAvailable){
            len = packetAvailable;
        }
        byte[] b = UtilsBytes.obtain(len);
        read(b, 0, b.length);
        return b;
    }
    public int read(byte[] b, int off){
        return read(b, off, b.length - off);
    }
    public int read(byte[] b, int off, int len){
        int packetAvailable = packet.getLength() - packet.getOffset();
        if(len > packetAvailable){
            len = packetAvailable;
        }
        System.arraycopy(packet.getData(), packet.getOffset(), b, off, len);
        return len;
    }

    @Override
    protected void finalize() throws Throwable{
DebugTrack.start().destroy(this).end();
        super.finalize();
    }

}

}

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
import com.tezov.lib_java.type.unit.UnitByte;
import com.tezov.lib_java.toolbox.CompareType;
import com.tezov.lib_java.toolbox.Clock;
import com.tezov.lib_java.util.UtilsString;
import java.util.List;
import java.util.LinkedList;
import java.util.Set;
import com.tezov.lib_java.async.Handler;
import com.tezov.lib_java.async.notifier.task.TaskState;
import com.tezov.lib_java.file.UtilsStream;
import com.tezov.lib_java.toolbox.Compare;
import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java.debug.DebugTrack;
import com.tezov.lib_java.type.primitive.BytesTo;
import com.tezov.lib_java.type.primitive.FloatTo;
import com.tezov.lib_java.type.primitive.IntTo;
import com.tezov.lib_java.type.primitive.LongTo;
import com.tezov.lib_java.type.primitive.string.StringCharTo;
import com.tezov.lib_java.type.ref.WR;
import com.tezov.lib_java.type.runnable.RunnableW;
import com.tezov.lib_java.wrapperAnonymous.UncaughtExceptionHandlerW;
import com.tezov.lib_java.util.UtilsBytes;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.*;

public class TcpServer{
public final static int NULL_PORT = -1;
private boolean isBusy = false;
private Handler thread = null;
private ServerSocket socket = null;
private Integer portLocal;
private InetAddress addressLocal = null;

public TcpServer(){
    this(null);
}
public TcpServer(Integer portLocal){
DebugTrack.start().create(this).end();
    this.portLocal = portLocal;
}

private TcpServer me(){
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
public TcpServer setPortLocal(Integer portLocal){
    this.portLocal = portLocal;
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

public TcpServer setAddressLocal(String addressLocal){
    try{
        return setAddressLocal(InetAddress.getByName(addressLocal));
    } catch(UnknownHostException e){

DebugException.start().log(e).end();

        return setAddressLocal((InetAddress)null);
    }
}
public TcpServer setAddressLocal(InetAddress address){
    this.addressLocal = address;
    return this;
}

public boolean isStarted(){
    return socket != null && !socket.isClosed();
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
            InetAddress inetAddress = addressLocal;
            if(inetAddress == null){
                throw new Throwable("not valid inetAddress found");
            }
            Integer port = portLocal;
            if(port == null){
                port = UtilsSocket.randomPortDynamic();
            }
            socket = new ServerSocket();
            SocketAddress inetSocketAddress = new InetSocketAddress(inetAddress, port);
            socket.bind(inetSocketAddress);
            onStarted();
            TaskState tmp = task;
            task = null;
            isBusy = false;
            tmp.notifyComplete();
            while(isStarted()){
                Socket socket = me().socket.accept();
                onNewClient(new SocketClient(socket));
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
protected void onNewClient(SocketClient socket){
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

public static class SocketClient{
    protected Socket socket;
    protected Handler thread = null;
    protected InputStream in = null;
    protected OutputStream out = null;
    public SocketClient(){
        this(null);
    }
    public SocketClient(Socket socket){
DebugTrack.start().create(this).end();
        this.socket = socket;
    }
    private SocketClient me(){
        return this;
    }

    public SocketClient setSocket(Socket socket){
        this.socket = socket;
        return this;
    }

    public String getAddressRemote(){
        if((socket != null) && (socket.getInetAddress() != null)){
            return socket.getInetAddress().getHostAddress();
        } else {
            return null;
        }
    }
    public Integer getPortRemote(){
        if(socket != null){
            int port = socket.getPort();
            return port == NULL_PORT ? null : port;
        } else {
            return null;
        }
    }

    public Integer getPortLocal(){
        if(socket != null){
            return socket.getLocalPort();

        }
        else return NULL_PORT;
    }
    public InetAddress getAddressLocal(){
        if(socket != null){
            return socket.getLocalAddress();

        }
        else return null;
    }

    public InputStream getInputStream(){
        if(in == null){
            try{
                in = socket.getInputStream();
            } catch(IOException e){

DebugException.start().log(e).end();

            }
        }
        return in;
    }
    public OutputStream getOutputStream(){
        if(out == null){
            try{
                out = socket.getOutputStream();
            } catch(IOException e){

DebugException.start().log(e).end();

            }
        }
        return out;
    }

    public Handler getThread(){
        if(thread == null){
            thread = Handler.newHandler(this);
        }
        return thread;
    }
    public void setUncaughtExceptionHandler(UncaughtExceptionHandlerW uncaughtExceptionHandler){
        getThread().setUncaughtExceptionHandler(uncaughtExceptionHandler);
    }
    public void post(RunnableW r){
        getThread().post(this, r);
    }

    public boolean writeNext(UtilsStream.InputStreamW source){
        try{
            Integer size = source.available();
            if(size == null){
                throw new Throwable("size is null");
            }
            if(!writeNext(size)){
                throw new Throwable("failed to write");
            }
            source.getStreamLinker().transfer(getOutputStream());
            source.close();
            return true;
        } catch(Throwable e){
            UtilsStream.close(source);
            return false;
        }
    }
    public boolean readNextStream(UtilsStream.OutputStreamW destination){
        try{
            Integer size = readNextInt();
            if(size == null){
                throw new Throwable("size is null");
            }
            destination.getStreamLinker().transfer(getInputStream(), size);
            destination.close();
            return true;
        } catch(Throwable e){
            UtilsStream.close(destination);
            return false;
        }
    }

    public boolean writeNull(){
        return writeFlag(false);
    }
    private boolean writeNotNull(){
        return writeFlag(true);
    }
    public boolean writeNext(Boolean b){
        if(b == null){
            return writeNull();
        } else {
            if(!writeNotNull()){
                return false;
            }
            return writeFlag(b);
        }
    }
    public boolean writeNext(Integer i){
        if(i == null){
            return writeNull();
        } else {
            if(!writeNotNull()){
                return false;
            }
            return write(IntTo.Bytes(i));
        }
    }
    public boolean writeNext(Long l){
        if(l == null){
            return writeNull();
        } else {
            if(!writeNotNull()){
                return false;
            }
            return write(LongTo.Bytes(l));
        }
    }
    public boolean writeNext(Float f){
        if(f == null){
            return writeNull();
        } else {
            if(!writeNotNull()){
                return false;
            }
            return write(FloatTo.Bytes(f));
        }
    }
    public boolean writeNext(String s){
        return writeNext(StringCharTo.Bytes(s));
    }
    public boolean writeNext(byte[] b){
        try{
            OutputStream out = getOutputStream();
            if(b == null){
                out.write(IntTo.Bytes(0), 0, IntTo.BYTES);
            } else {
                out.write(IntTo.Bytes(b.length), 0, IntTo.BYTES);
                out.write(b, 0, b.length);
            }
            return true;
        } catch(Throwable e){

DebugException.start().log(e).end();

            return false;
        }
    }
    private boolean write(byte[] b){
        try{
            OutputStream out = getOutputStream();
            out.write(b, 0, b.length);
            return true;
        } catch(Throwable e){

DebugException.start().log(e).end();

            return false;
        }
    }
    private boolean writeFlag(boolean flag){
        try{
            OutputStream out = getOutputStream();
            out.write(flag ? 1 : 0);
            return true;
        } catch(Throwable e){

DebugException.start().log(e).end();

            return false;
        }
    }
    public boolean flush(){
        try{
            out.flush();
            return true;
        } catch(IOException e){

DebugException.start().log(e).end();

            return false;
        }
    }

    public void readNextNull(){
        readFlag();
    }
    private boolean isNextNull(){
        return Compare.isFalseOrNull(readFlag());
    }
    public Boolean readNextBoolean(){
        if(isNextNull()){
            return null;
        } else {
            return readFlag();
        }
    }
    public Integer readNextInt(){
        if(isNextNull()){
            return null;
        } else {
            return BytesTo.Int(read(IntTo.BYTES));
        }
    }
    public Long readNextLong(){
        if(isNextNull()){
            return null;
        } else {
            return BytesTo.Long(read(LongTo.BYTES));
        }
    }
    public Float readNextFloat(){
        if(isNextNull()){
            return null;
        } else {
            return BytesTo.Float(read(FloatTo.BYTES));
        }
    }
    public String readNextString(){
        return BytesTo.StringChar(readNextBytes(null));
    }
    public String readNextString(Integer maxLength){
        return BytesTo.StringChar(readNextBytes(maxLength));
    }
    public byte[] readNextBytes(){
        return readNextBytes(null);
    }
    public byte[] readNextBytes(Integer maxLength){
        try{
            int length = readLength();
            if(length == 0){
                return null;
            } else if((maxLength != null) && (length > maxLength)){
                throw new Throwable("length(" + length + ") > maxLength(" + maxLength + ")");
            } else {
                byte[] b = UtilsBytes.obtain(length);
                int readLength = getInputStream().read(b, 0, length);
                if(readLength != length){
                    throw new Throwable("length(" + length + ") and readLength(" + readLength + ") mismatches");
                }
                return b;
            }
        } catch(Throwable e){

DebugException.start().log(e).end();

            return null;
        }
    }
    private int readLength() throws Throwable{
        byte[] lengthBytes = UtilsBytes.obtain(IntTo.BYTES);
        if(getInputStream().read(lengthBytes, 0, IntTo.BYTES) != IntTo.BYTES){
            throw new Throwable("invalid lengthBytes read");
        }
        int length = BytesTo.Int(lengthBytes);
        if(length < 0){
            throw new Throwable("length negative");
        }
        return length;
    }
    private byte[] read(Integer len){
        try{
            byte[] b = UtilsBytes.obtain(len);
            int readLength = getInputStream().read(b, 0, len);
            if(readLength != len){
                throw new Throwable("length and readLength mismatches");
            }
            return b;
        } catch(Throwable e){

DebugException.start().log(e).end();

            return null;
        }
    }
    public Boolean readFlag(){
        try{
            return getInputStream().read() == 1;
        } catch(Throwable e){

DebugException.start().log(e).end();

            return null;
        }
    }

    public void close(){
        if(socket != null){
            try{
                socket.close();
            } catch(IOException e){
DebugException.start().log(e).end();
            }
            socket = null;
            in = null;
            out = null;
        }
        if(thread != null){
            thread.quit();
            thread = null;
        }
    }
    @Override
    protected void finalize() throws Throwable{
DebugTrack.start().destroy(this).end();
        close();
        super.finalize();
    }

}

}

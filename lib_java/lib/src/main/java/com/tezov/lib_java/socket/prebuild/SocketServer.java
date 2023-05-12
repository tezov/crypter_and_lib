/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java.socket.prebuild;

import com.tezov.lib_java.async.notifier.observer.state.ObserverState;
import com.tezov.lib_java.debug.DebugLog;
import com.tezov.lib_java.type.primitive.ObjectTo;
import com.tezov.lib_java.type.primitive.IntTo;
import com.tezov.lib_java.type.unit.UnitByte;
import com.tezov.lib_java.toolbox.CompareType;
import com.tezov.lib_java.toolbox.Clock;
import java.util.List;
import java.util.LinkedList;
import java.util.Set;
import com.tezov.lib_java.application.AppConfigKey;
import com.tezov.lib_java.socket.prebuild.datagram.*;
import com.tezov.lib_java.application.AppConfig;
import com.tezov.lib_java.async.notifier.observer.state.ObserverStateE;
import com.tezov.lib_java.async.notifier.task.TaskState;
import com.tezov.lib_java.socket.TcpServer;
import com.tezov.lib_java.toolbox.Compare;
import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java.debug.DebugTrack;
import com.tezov.lib_java.type.runnable.RunnableGroup;
import com.tezov.lib_java.type.runnable.RunnableW;
import com.tezov.lib_java.wrapperAnonymous.UncaughtExceptionHandlerW;
import com.tezov.lib_java.util.UtilsString;

import java.net.InetAddress;

import static com.tezov.lib_java.socket.prebuild.datagram.Datagram.DATAGRAM_MAX_LENGTH;

public class SocketServer{
private final static int SERVER_ID_LENGTH = 8;
private final static long TIMEOUT_HANDSHAKE_ms = AppConfig.getLong(AppConfigKey.SOCKET_TIMEOUT_HANDSHAKE_ms.getId());
private final String id;
private TaskState task = null;
private TcpServer server = null;

public SocketServer(){
    this(UtilsString.randomHex(SERVER_ID_LENGTH));
}
public SocketServer(String id){
DebugTrack.start().create(this).end();
    this.id = id;
}
private SocketServer me(){
    return this;
}
public String getId(){
    return id;
}

public String getAddressLocal(){
    if(server != null){
        return server.getAddressLocal();
    } else {
        return null;
    }
}
public Integer getPortLocal(){
    if(server != null){
        return server.getPortLocal();
    } else {
        return null;
    }
}

public boolean isBusy(){
    return task != null;
}
public boolean isStarted(){
    return (server != null) && server.isStarted();
}

public TaskState.Observable start(InetAddress addressLocal, Integer portLocal){
    if(isStarted()){
        return TaskState.Exception("i'm already started");
    }
    if(isBusy()){
        return TaskState.Exception("i'm busy");
    }
    task = new TaskState();
    RunnableGroup gr = new RunnableGroup(this).name("start server");
    gr.add(new RunnableGroup.Action(){
        @Override
        public void runSafe(){
            if(server != null){
                server.stop(true);
            }
            server = new TcpServer(){
                @Override
                protected void onNewClient(SocketClient socket){
                    me().onNewClient(socket);
                }
                @Override
                protected void onRunningException(Throwable e){
                    if(isBusy()){
                        putException(new Throwable(e));
                        done();
                    } else {
                        me().onRunningException(e);
                    }
                }
            };
            server.setAddressLocal(addressLocal).setPortLocal(portLocal).start().observe(new ObserverStateE(this){
                @Override
                public void onComplete(){
                    next();
                }
                @Override
                public void onException(Throwable e){
                    putException(e);
                    done();
                }
            });
        }
    }.name("start server"));
    gr.setOnDone(new RunnableGroup.Action(){
        @Override
        public void runSafe(){
            Throwable e = getException();
            if(e != null){
                stop(true).observe(new ObserverState(this){
                    @Override
                    public void onComplete(){
                        TaskState tmp = task;
                        task = null;
                        tmp.notifyException(e);
                    }
                });
            } else {
                TaskState tmp = task;
                task = null;
                tmp.notifyComplete();
            }
        }
    });
    gr.start();
    return task.getObservable();
}

public TaskState.Observable stop(){
    return stop(false);
}
private TaskState.Observable stop(boolean force){
    if((task != null) && !force){
        TaskState stopTask = new TaskState();
        this.task.observe(new ObserverStateE(this){
            @Override
            public void onComplete(){
                stop().observe(new ObserverState(this){
                    @Override
                    public void onComplete(){
                        stopTask.notifyComplete();
                    }
                });
            }
            @Override
            public void onException(Throwable e){
                stopTask.notifyComplete();
            }
        });
        return stopTask.getObservable();
    }
    else{
        if(server != null){
            server.stop(true);
            server = null;
        }
        return TaskState.Complete();
    }
}

protected void onNewClient(TcpServer.SocketClient socket){
    RunnableGroup gr = new RunnableGroup(this).name("onNewClient");
    int KEY_REQUEST = gr.key();
    int KEY_ANSWER = gr.key();
    gr.add(new RunnableGroup.Action(){
        @Override
        public void runSafe(){
            socket.setUncaughtExceptionHandler(new UncaughtExceptionHandlerW(){
                @Override
                public void uncaughtException(Thread t, Throwable e){
                    socket.close();
                    putException(new Throwable(e));
                    done();
                }
            });
            next();
        }
    });
    gr.add(new RunnableGroup.Action(){
        @Override
        public void runSafe(){
            if(!isStarted() || isBusy()){
                putException("server is not started");
                done();
                return;
            }
            socket.post(new RunnableW(){
                @Override
                public void runSafe(){
                    DatagramRequest request = me().buildRequest(socket);
                    request.setOwnerId(id, server.getAddressLocal(), server.getPortLocal());
                    put(KEY_REQUEST, request);
                    if(!socket.writeNext(request.toBytes()) || !socket.flush()){
                        putException("failed to write");
                        done();
                    } else {
                        next();
                    }
                }
            });
        }
    }.name("send request message"));
    gr.add(new RunnableGroup.ActionTimeout(){
        @Override
        public void onTimeOut(){
            socket.close();
            putException("timeout");
            done();
        }
        @Override
        public void runSafe(){
            if(!isStarted() || isBusy()){
                putException("server is not started");
                done();
                return;
            }
            socket.post(new RunnableW(){
                @Override
                public void runSafe(){
                    startTimeout(TIMEOUT_HANDSHAKE_ms);
                    DatagramAnswer answer = Datagram.from(socket.readNextBytes(DATAGRAM_MAX_LENGTH));
                    if(!isTimeout()){
                        completed();
                        put(KEY_ANSWER, answer);
                        putValue((answer != null) && Compare.equals(id, answer.getWho()) && me().acceptAnswer(socket, get(KEY_REQUEST), answer));
                        next();
                    }
                }
            });
        }
    }.name("wait answer message"));
    gr.add(new RunnableGroup.Action(){
        @Override
        public void runSafe(){
            if(!isStarted() || isBusy()){
                putException("server is not started");
                done();
                return;
            }
            socket.post(new RunnableW(){
                @Override
                public void runSafe(){
                    DatagramAnswer answer = me().buildConfirmation(socket, get(KEY_REQUEST), get(KEY_ANSWER), getValue());
                    answer.setOwnerId(id, server.getAddressLocal(), server.getPortLocal());
                    answer.from((Datagram)get(KEY_REQUEST));
                    if(!socket.writeNext(answer.toBytes()) || !socket.flush()){
                        putException("failed to write");
                    }
                    done();
                }
            });
        }
    }.name("send confirmation"));
    gr.setOnDone(new RunnableGroup.Action(){
        @Override
        public void runSafe(){
            socket.setUncaughtExceptionHandler(null);
            Throwable e = getException();
            if(e == null){
                if(Compare.isTrue(getValue())){
                    onClientAccepted(socket);
                } else {
                    onClientRefused(socket);
                }
            }
        }
    });
    gr.start();
}
protected void onClientAccepted(TcpServer.SocketClient socket){

}
protected void onClientRefused(TcpServer.SocketClient socket){

}
protected DatagramRequest buildRequest(TcpServer.SocketClient socket){
    return new DatagramRequest().init();
}
protected boolean acceptAnswer(TcpServer.SocketClient socket, DatagramRequest request, DatagramAnswer answer){
    return Compare.equals(request.getId(), answer.getWhat()) && answer.isAccepted();
}
protected DatagramAnswer buildConfirmation(TcpServer.SocketClient socket, DatagramRequest request, DatagramMessage answer, boolean accepted){
    DatagramAnswer confirmation = new DatagramAnswer().init();
    confirmation.setAccepted(accepted);
    return confirmation;
}
protected void onRunningException(Throwable e){
    stop();
}

@Override
protected void finalize() throws Throwable{
DebugTrack.start().destroy(this).end();
    super.finalize();
}

}

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
import com.tezov.lib_java.application.AppConfig;
import com.tezov.lib_java.application.AppConfigKey;
import com.tezov.lib_java.toolbox.Compare;

import com.tezov.lib_java.socket.prebuild.datagram.*;
import com.tezov.lib_java.async.notifier.observer.state.ObserverStateE;
import com.tezov.lib_java.async.notifier.task.TaskState;
import com.tezov.lib_java.socket.TcpClient;
import com.tezov.lib_java.socket.TcpServer;
import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java.debug.DebugTrack;
import com.tezov.lib_java.type.runnable.RunnableGroup;
import com.tezov.lib_java.type.runnable.RunnableW;
import com.tezov.lib_java.wrapperAnonymous.UncaughtExceptionHandlerW;
import com.tezov.lib_java.util.UtilsString;

import static com.tezov.lib_java.socket.prebuild.datagram.Datagram.DATAGRAM_MAX_LENGTH;

public class SocketClient{
private final static int CLIENT_ID_LENGTH = 8;
private final static long TIMEOUT_HANDSHAKE_ms = AppConfig.getLong(AppConfigKey.SOCKET_TIMEOUT_HANDSHAKE_ms.getId());
private final String id;
private TaskState task = null;
private TcpClient client = null;

public SocketClient(){
    this(UtilsString.randomHex(CLIENT_ID_LENGTH));
}
public SocketClient(String serverId){
DebugTrack.start().create(this).end();
    this.id = serverId;
}
private SocketClient me(){
    return this;
}
public String getId(){
    return id;
}

public boolean isBusy(){
    return task != null;
}
public boolean isConnected(){
    return (client != null) && client.isConnected();
}
public TaskState.Observable connect(String addressRemote, int portRemote){
    if((client != null) && (client.isConnected())){
        return TaskState.Exception("already connected");
    }
    if(addressRemote == null){
        return TaskState.Exception("Address is null");
    }
    if(isBusy()){
        return TaskState.Exception("busy");
    }
    task = new TaskState();
    RunnableGroup gr = new RunnableGroup(this).name("start client");
    int KEY_REQUEST = gr.key();
    gr.add(new RunnableGroup.Action(){
        @Override
        public void runSafe(){
            if(client != null){
                client.disconnect(true);
            }
            client = new TcpClient(){
                @Override
                protected void onRunningException(Throwable e){
                    if(isBusy()){
                        putException(new Throwable(e));
                        done();
                    } else {
                        me().onRunningException(e);
                    }
                }
                @Override
                protected void onDisconnected(){
                    if(!isBusy()){
                        me().onDisconnected();
                    }
                }
            };
            client.setAddressRemote(addressRemote, portRemote).connect(TIMEOUT_HANDSHAKE_ms).observe(new ObserverStateE(this){
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
    }.name("connect client"));
    gr.add(new RunnableGroup.Action(){
        @Override
        public void runSafe(){
            client.setUncaughtExceptionHandler(new UncaughtExceptionHandlerW(){
                @Override
                public void uncaughtException(Thread t, Throwable e){
                    client.close();
                    putException(new Throwable(e));
                    done();
                }
            });
            next();
        }
    });
    gr.add(new RunnableGroup.ActionTimeout(){
        @Override
        public void onTimeOut(){
            putException("timeout");
            client.disconnect();
            done();
        }
        @Override
        public void runSafe(){
            client.post(new RunnableW(){
                @Override
                public void runSafe(){
                    startTimeout(TIMEOUT_HANDSHAKE_ms);
                    DatagramRequest request = Datagram.from(client.readNextBytes(DATAGRAM_MAX_LENGTH));
                    if(!isTimeout()){
                        completed();
                        if((request != null) && me().acceptRequest(request)){
                            put(KEY_REQUEST, request);
                            next();
                        } else {
                            putException("connection refused by sender");
                            client.disconnect();
                            done();
                        }
                    }
                }
            });
        }
    }.name("wait request message"));
    gr.add(new RunnableGroup.Action(){
        @Override
        public void runSafe(){
            client.post(new RunnableW(){
                @Override
                public void runSafe(){
                    DatagramAnswer answer = me().buildAnswer(get(KEY_REQUEST));
                    answer.setOwnerId(id, client.getAddressLocal(), client.getPortLocal());
                    answer.from((Datagram)get(KEY_REQUEST));
                    if(!client.writeNext(answer.toBytes()) || !client.flush()){
                        putException("failed to write");
                        done();
                    } else {
                        next();
                    }
                }
            });
        }
    }.name("send answer message"));
    gr.add(new RunnableGroup.ActionTimeout(){
        @Override
        public void onTimeOut(){
            putException("timeout");
            client.disconnect();
            done();
        }
        @Override
        public void runSafe(){
            client.post(new RunnableW(){
                @Override
                public void runSafe(){
                    startTimeout(TIMEOUT_HANDSHAKE_ms);
                    DatagramAnswer confirmation = Datagram.from(client.readNextBytes(DATAGRAM_MAX_LENGTH));
                    if(!isTimeout()){
                        completed();
                        if((confirmation!=null) && me().acceptConfirmation(get(KEY_REQUEST), confirmation)){
                            next();
                        } else {
                            putException("connection refused by receiver");
                            client.disconnect();
                            done();
                        }
                    }
                }
            });
        }
    }.name("wait confirmation"));
    gr.setOnDone(new RunnableGroup.Action(){
        @Override
        public void runSafe(){
            client.setUncaughtExceptionHandler(null);
            Throwable e = getException();
            if(e != null){
                disconnect(true).observe(new ObserverState(this){
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
public TaskState.Observable disconnect(){
    return disconnect(false);
}
private TaskState.Observable disconnect(boolean force){
    if((task != null) && !force){
        TaskState disconnectTask = new TaskState();
        task.observe(new ObserverStateE(this){
            @Override
            public void onComplete(){
                disconnect().observe(new ObserverState(this){
                    @Override
                    public void onComplete(){
                        disconnectTask.notifyComplete();
                    }
                });
            }
            @Override
            public void onException(Throwable e){
                disconnectTask.notifyComplete();
            }
        });
        return disconnectTask.getObservable();
    }
    else{
        if(client != null){
            client.setUncaughtExceptionHandler(null);
            client.disconnect();
            client = null;
        }
        return TaskState.Complete();
    }
}

protected boolean acceptRequest(DatagramRequest request){
    return true;
}
protected DatagramAnswer buildAnswer(DatagramMessage request){
    DatagramAnswer answer = new DatagramAnswer().init();
    answer.setAccepted(true);
    return answer;
}
protected boolean acceptConfirmation(DatagramMessage request, DatagramAnswer confirmation){
    return Compare.equals(request.getOwnerId(), confirmation.getWho()) && Compare.equals(request.getId(), confirmation.getWhat()) && confirmation.isAccepted();
}
protected void onRunningException(Throwable e){
    disconnect();
}
protected void onDisconnected(){
    disconnect();
}

public TcpServer.SocketClient getSocket(){
    return client;
}

@Override
protected void finalize() throws Throwable{
DebugTrack.start().destroy(this).end();
    super.finalize();
}

}

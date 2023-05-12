/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java.socket.prebuild;

import com.tezov.lib_java.async.Handler;
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
import com.tezov.lib_java.application.AppRandomNumber;
import com.tezov.lib_java.async.notifier.observer.state.ObserverState;
import com.tezov.lib_java.async.notifier.observer.state.ObserverStateE;
import com.tezov.lib_java.async.notifier.task.TaskState;
import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java.debug.DebugLog;
import com.tezov.lib_java.debug.DebugTrack;
import com.tezov.lib_java.socket.UdpListener;
import com.tezov.lib_java.socket.UdpPacket;
import com.tezov.lib_java.socket.prebuild.datagram.Datagram;
import com.tezov.lib_java.socket.prebuild.datagram.DatagramAnswer;
import com.tezov.lib_java.socket.prebuild.datagram.DatagramBeacon;
import com.tezov.lib_java.socket.prebuild.datagram.DatagramRequest;
import com.tezov.lib_java.type.collection.ListEntry;
import com.tezov.lib_java.type.runnable.RunnableGroup;
import com.tezov.lib_java.type.runnable.RunnableTimeOut;
import com.tezov.lib_java.type.runnable.RunnableW;
import com.tezov.lib_java.util.UtilsString;
import com.tezov.lib_java.wrapperAnonymous.SupplierW;

import java.net.InetAddress;

public class SocketMessenger{
private final static long BEACON_DELAY_DEFAULT_ms = AppConfig.getLong(AppConfigKey.SOCKET_MESSENGER_BEACON_DELAY_ms.getId());
private final static long NO_ACK_DELAY_RETRY_ms = AppConfig.getLong(AppConfigKey.SOCKET_MESSENGER_NO_ACK_DELAY_RETRY_ms.getId());
private final static int NO_ACK_DELAY_RETRY_RANDOM_ms = 500;
private final static int NO_ACK_MAX_RETRY = 3;
private final static int SERVER_ID_LENGTH = 8;
private final String id;
private final ListEntry<String, PendingDatagram> progressing = new ListEntry<>();
private TaskState task = null;
private UdpListener listener = null;
private UdpPacket packet = null;
private Integer portRemote = null;
private InetAddress addressRemote = null;
private final long beaconDelay_ms = BEACON_DELAY_DEFAULT_ms;
private RunnableTimeOut beaconRunnable = null;
private SupplierW<InetAddress> broadcastAddressSupplier = null;

public SocketMessenger(){
    this(UtilsString.randomHex(SERVER_ID_LENGTH));
}
public SocketMessenger(String id){
DebugTrack.start().create(this).end();
    this.id = id;
}

private void addPendingDatagram(PendingDatagram datagram){
    synchronized(progressing){
        progressing.put(datagram.getId(), datagram);
    }
}
private void removePendingDatagram(String id){
    synchronized(progressing){
        if(id == null){
DebugException.start().log("id is null").end();
        } else {
            PendingDatagram removedPendingDatagram = progressing.removeKey(id);
            if(removedPendingDatagram == null){
DebugException.start().log("remove pending datagram failed").end();
            }
        }
    }
}
private void clearPendingDatagram(){
    synchronized(progressing){
        progressing.clear();
    }
}
private PendingDatagram getPendingDatagram(String id){
    synchronized(progressing){
        if(id == null){
            return null;
        } else {
            return progressing.getValue(id);
        }
    }
}

private SocketMessenger me(){
    return this;
}
public String getId(){
    return id;
}

public String getAddressLocal(){
    if(listener != null){
        return listener.getAddressLocal();
    } else {
        return null;
    }
}
public Integer getPortLocal(){
    if(listener != null){
        return listener.getPortLocal();
    } else {
        return null;
    }
}

public Integer getPortRemote(){
    return portRemote;
}
public SocketMessenger setPortRemote(Integer portRemote){
    this.portRemote = portRemote;
    return this;
}

public InetAddress getAddressRemote(){
    return addressRemote;
}
public SocketMessenger setAddressRemote(InetAddress addressRemote){
    this.addressRemote = addressRemote;
    return this;
}

public SocketMessenger setBroadcastAddressSupplier(SupplierW<InetAddress> broadcastAddressSupplier){
    this.broadcastAddressSupplier = broadcastAddressSupplier;
    return this;
}

public boolean isBusy(){
    return task != null;
}
public boolean isStarted(){
    return (listener != null) && listener.isStarted();
}

public TaskState.Observable start(InetAddress localAddress, Integer port, boolean broadcastEnabled){
    if(isStarted()){
        return TaskState.Exception("already started");
    }
    if(task != null){
        return TaskState.Exception("busy");
    }
    task = new TaskState();
    RunnableGroup gr = new RunnableGroup(this).name("start server");
    gr.add(new RunnableGroup.Action(){
        @Override
        public void runSafe(){
            if(listener != null){
                listener.stop(true);
            }
            listener = new UdpListener(){
                @Override
                protected void onPacket(DatagramPacket packet){
                    Datagram datagram = Datagram.from(packet.read());
                    me().onNewPacket(datagram);
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
            listener.setBroadcastEnabled(true);
            listener.setBroadcastEnabled(broadcastEnabled)
                    .setAddressLocal(localAddress)
                    .setPortLocal(port)
                    .start().observe(new ObserverStateE(this){
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
    gr.add(new RunnableGroup.Action(){
        @Override
        public void runSafe(){
            if(packet != null){
                packet.unbind(true);
            }
            packet = new UdpPacket(){
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
            InetAddress inetAddress;
            if(!broadcastEnabled){
                inetAddress = addressRemote;
            }
            else {
                if(broadcastAddressSupplier == null){
                    putException("broadcastAddressSupplier is null");
                    done();
                    return;
                }
                inetAddress = broadcastAddressSupplier.get();
            }
            if(inetAddress == null){
                putException("not valid inetAddress found");
                done();
                return;
            }
            packet.setAddressRemote(inetAddress);
            packet.bind().observe(new ObserverStateE(this){
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
            next();
        }
    }.name("bind datagram"));
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
            }
            else {
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
        task.observe(new ObserverStateE(this){
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
        stopBeacon();
        if(packet != null){
            packet.unbind(true);
            packet = null;
        }
        if(listener != null){
            listener.stop(true);
            listener = null;
        }
        clearPendingDatagram();
        return TaskState.Complete();
    }
}

public boolean startBeacon(InetAddress remoteAddress, int remotePort){
    if(isStarted()){
        if(beaconRunnable == null){
            beaconRunnable = new RunnableTimeOut(this, beaconDelay_ms){
                @Override
                public void onTimeOut(){
                    if(isStarted()){
                        DatagramBeacon beacon = buildBeaconRequest();
                        send(remoteAddress, remotePort, beacon);
                        start();
                    }
                }
            }.start();
        }
        return true;
    }
    return false;
}
public void stopBeacon(){
    if(beaconRunnable != null){
        beaconRunnable.cancel();
        beaconRunnable = null;
    }
}

public void sendRequest(DatagramRequest datagram){
    sendRequest((InetAddress)null, null, datagram);
}
public void sendRequest(Integer remotePort, DatagramRequest datagram){
    sendRequest((InetAddress)null, remotePort, datagram);
}
public boolean sendRequest(String remoteAddress, Integer remotePort, DatagramRequest datagram){
    try{
        if(remoteAddress == null){
            return sendRequest((InetAddress)null, remotePort, datagram);
        } else {
            return sendRequest(InetAddress.getByName(remoteAddress), remotePort, datagram);
        }
    } catch(Throwable e){
        return false;
    }
}
public boolean sendRequest(InetAddress remoteAddress, Integer remotePort, DatagramRequest datagram){
    if(remotePort == null){
        remotePort = portRemote;
    }
    if(remotePort == null){
        remotePort = listener.getPortLocal();
    }
    if(remotePort != null){
        PendingDatagram pendingDatagram = new PendingDatagram(remoteAddress, remotePort, datagram);
        addPendingDatagram(pendingDatagram);
        pendingDatagram.start();
        return true;
    } else {
        return false;
    }
}

public void sendAnswer(DatagramAnswer datagram){
    sendAnswer((InetAddress)null, null, datagram);
}
public void sendAnswer(Integer remotePort, DatagramAnswer datagram){
    sendAnswer((InetAddress)null, remotePort, datagram);
}
public boolean sendAnswer(String remoteAddress, Integer remotePort, DatagramAnswer datagram){
    try{
        if(remoteAddress == null){
            return sendAnswer((InetAddress)null, remotePort, datagram);
        } else {
            return sendAnswer(InetAddress.getByName(remoteAddress), remotePort, datagram);
        }
    } catch(Throwable e){
        return false;
    }
}
public boolean sendAnswer(InetAddress remoteAddress, Integer remotePort, DatagramAnswer datagram){
    if(remotePort == null){
        remotePort = portRemote;
    }
    if(remotePort == null){
        remotePort = listener.getPortLocal();
    }
    if(remotePort != null){
        send(remoteAddress, remotePort, datagram);
        return true;
    } else {
        return false;
    }
}

public void send(Datagram datagram){
    send((InetAddress)null, null, datagram);
}
public void send(Integer remotePort, Datagram datagram){
    send((InetAddress)null, remotePort, datagram);
}
public boolean send(String remoteAddress, Integer remotePort, Datagram datagram){
    try{
        if(remoteAddress == null){
            send((InetAddress)null, remotePort, datagram);
        } else {
            send(InetAddress.getByName(remoteAddress), remotePort, datagram);
        }
        return true;
    } catch(Throwable e){
        return false;
    }
}
public void send(InetAddress remoteAddress, Integer remotePort, Datagram datagram){
    packet.post(new RunnableW(){
        @Override
        public void runSafe(){
            datagram.setOwnerId(id, listener.getAddressLocal(), listener.getPortLocal());
            if(remoteAddress != null){
                packet.setAddressRemote(remoteAddress);
            }
            packet.setPortRemote(remotePort);
            packetWrite(datagram);
            packet.send();
        }
    });
}

private void packetWrite(Datagram datagram){
    packet.write(datagram.toBytes());
}
private void onNewPacket(Datagram datagram){
    if((datagram == null) || (id.equals(datagram.getOwnerId()))){
        return;
    }
    if(datagram instanceof DatagramRequest){
        DatagramRequest request = ((DatagramRequest)datagram);
        if(acceptRequest(request)){
            DatagramAnswer answer = me().onReceivedRequest(request);
            if(answer != null){
                answer.from(request);
                send(request.getOwnerAddress(), request.getOwnerPort(), answer);
            }
        }
    } else if(datagram instanceof DatagramAnswer){
        DatagramAnswer answer = ((DatagramAnswer)datagram);
        PendingDatagram pendingDatagram = getPendingDatagram(answer.getWhat());
        DatagramRequest request = pendingDatagram != null ? pendingDatagram.request : null;
        if(((answer.getWho() == null) || id.equals(answer.getWho())) && acceptAnswer(answer, request)){
            if(pendingDatagram != null){
                pendingDatagram.completed();
            }
            onReceivedAnswer(answer, request);
        }
    } else if(datagram instanceof DatagramBeacon){
        DatagramBeacon beacon = ((DatagramBeacon)datagram);
        if(beacon.isBack()){
            if(id.equals(beacon.getWho()) && acceptBeaconAnswer(beacon)){
                onReceivedBeaconAnswer(beacon);
            }
        } else {
            if(acceptBeaconRequest(beacon)){
                DatagramBeacon beaconBack = buildBeaconAnswer();
                beaconBack.setBack(true);
                beaconBack.from(beacon);
                send(beacon.getOwnerAddress(), beacon.getOwnerPort(), beaconBack);
            }
        }
    } else {
        onReceived(datagram);
    }
}
protected void onReceived(Datagram datagram){

}
protected boolean acceptBeaconRequest(DatagramBeacon beacon){
    return false;
}
protected DatagramBeacon buildBeaconRequest(){
    return new DatagramBeacon().init();
}
protected boolean acceptBeaconAnswer(DatagramBeacon beacon){
    return false;
}
protected DatagramBeacon buildBeaconAnswer(){
    return new DatagramBeacon().init();
}
protected void onReceivedBeaconAnswer(DatagramBeacon beacon){

}
protected boolean acceptRequest(DatagramRequest request){
    return true;
}
protected DatagramAnswer onReceivedRequest(DatagramRequest request){
    return new DatagramAnswer().init().setAccepted(true);
}
protected void onAnswerToRequestFailed(Throwable e, int remotePort, DatagramRequest request){
DebugException.start().log(e).end();
}
protected boolean acceptAnswer(DatagramAnswer answer, DatagramRequest request){
    return true;
}
protected void onReceivedAnswer(DatagramAnswer answer, DatagramRequest request){

}
protected void onRunningException(Throwable e){
    stop();
}
@Override
protected void finalize() throws Throwable{
DebugTrack.start().destroy(this).end();
    super.finalize();
}

private class PendingDatagram extends RunnableTimeOut{
    private final int remotePort;
    private final InetAddress remoteAddress;
    private final DatagramRequest request;
    private int count = 0;
    public PendingDatagram(InetAddress remoteAddress, int remotePort, DatagramRequest request){
        super(me(), 0);
        this.request = request;
        request.setOwnerId(id, listener.getAddressLocal(), listener.getPortLocal());
        this.remotePort = remotePort;
        this.remoteAddress = remoteAddress;
    }
    public String getId(){
        return request.getId();
    }
    @Override
    public <R extends RunnableTimeOut> R start(){
        setDelay(NO_ACK_DELAY_RETRY_ms + AppRandomNumber.nextInt(NO_ACK_DELAY_RETRY_RANDOM_ms));
        return super.start();
    }
    @Override
    public void onStart(){
        if(!isStarted()){
            onFailed(new Throwable("not started"));
        } else {
            packet.post(new RunnableW(){
                @Override
                public void runSafe(){
                    if(remoteAddress != null){
                        packet.setAddressRemote(remoteAddress);
                    }
                    packet.setPortRemote(remotePort);
                    packetWrite(request);
                    packet.send();
                }
            });
        }
    }
    @Override
    public void onComplete(){
        removePendingDatagram(getId());
    }
    @Override
    public void onTimeOut(){
        count++;
        if(count < NO_ACK_MAX_RETRY){
            start();
        } else {
            onFailed(new Throwable("timeout"));
        }
    }
    private void onFailed(Throwable e){
        removePendingDatagram(getId());
        me().onAnswerToRequestFailed(e, remotePort, request);
    }

}

}

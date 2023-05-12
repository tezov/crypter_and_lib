/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java_android.application;

import com.tezov.lib_java.type.primitive.ObjectTo;
import com.tezov.lib_java.type.primitive.IntTo;
import com.tezov.lib_java.type.unit.UnitByte;
import com.tezov.lib_java.toolbox.CompareType;
import com.tezov.lib_java.toolbox.Clock;
import java.util.LinkedList;
import java.util.Set;
import com.tezov.lib_java_android.database.sqlLite.filter.dbFilterOrder;
import com.tezov.lib_java_android.database.sqlLite.filter.chunk.ChunkCommand;
import androidx.fragment.app.Fragment;
import com.tezov.lib_java.wrapperAnonymous.ConsumerW;

import static com.tezov.lib_java_android.application.ConnectivityHotSpotManager.HOTSPOT_USB_ADDRESS_START_BY;
import static com.tezov.lib_java_android.application.ConnectivityHotSpotManager.HOTSPOT_WIFI_ADDRESS_START_BY;
import static com.tezov.lib_java_android.application.ConnectivityManager.State.AIR_PLANE;
import static com.tezov.lib_java_android.application.ConnectivityManager.State.DISCONNECTED;
import static com.tezov.lib_java_android.application.ConnectivityManager.State.TYPE_DATA;
import static com.tezov.lib_java_android.application.ConnectivityManager.State.TYPE_UNKNOWN;
import static com.tezov.lib_java_android.application.ConnectivityManager.State.TYPE_VPN;
import static com.tezov.lib_java_android.application.ConnectivityManager.State.TYPE_WIFI;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.os.Build;
import android.provider.Settings;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.tezov.lib_java.async.Handler;
import com.tezov.lib_java.async.notifier.observer.value.ObserverValue;
import com.tezov.lib_java.async.notifier.task.TaskValue;
import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java.debug.DebugLog;
import com.tezov.lib_java.debug.DebugTrack;
import com.tezov.lib_java.type.Delayed;
import com.tezov.lib_java.wrapperAnonymous.FunctionW;
import com.tezov.lib_java.wrapperAnonymous.PredicateW;
import com.tezov.lib_java.type.collection.ListEntry;
import com.tezov.lib_java.type.collection.ListOrObject;
import com.tezov.lib_java.type.primaire.Entry;
import com.tezov.lib_java.type.primitive.ByteTo;
import com.tezov.lib_java.type.runnable.RunnableGroup;
import com.tezov.lib_java.type.runnable.RunnableW;
import com.tezov.lib_java.util.UtilsArray;
import com.tezov.lib_java.util.UtilsString;

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ConnectivityManager{
final private static long CONNECTIVITY_SOCKET_TEST_DELAY_START_ms = AppConfig.getLong(AppConfigKey.CONNECTIVITY_SOCKET_TEST_DELAY_START_ms.getId());
final private static int CONNECTIVITY_SOCKET_TIMEOUT_ms = AppConfig.getInt(AppConfigKey.CONNECTIVITY_SOCKET_TIMEOUT_ms.getId());
final private static long CONNECTIVITY_VALID_CHANGE_DELAY_MIN_ms = AppConfig.getLong(AppConfigKey.CONNECTIVITY_VALID_CHANGE_DELAY_MIN_ms.getId());
final private static long CONNECTIVITY_SOCKET_TEST_DELAY_MIN_RETRY_ms = AppConfig.getLong(AppConfigKey.CONNECTIVITY_SOCKET_TEST_DELAY_MIN_RETRY_ms.getId());
final private static long CONNECTIVITY_SOCKET_TEST_DELAY_MAX_RETRY_ms = AppConfig.getLong(AppConfigKey.CONNECTIVITY_SOCKET_TEST_DELAY_MAX_RETRY_ms.getId());
final private static int CONNECTIVITY_SOCKET_TEST_DELAY_STEP_RETRY = AppConfig.getInt(AppConfigKey.CONNECTIVITY_SOCKET_TEST_DELAY_STEP_RETRY.getId());

private NetworkCallback br = null;
private Delayed<State> state = null;
private List<Query> queries = null;
public ConnectivityManager(){
DebugTrack.start().create(this).end();
}
private static Class<ConnectivityManager> myClass(){
    return ConnectivityManager.class;
}

private static boolean computeAirplaneMode(){
    return Settings.System.getInt(AppContext.getContentResolver(), Settings.Global.AIRPLANE_MODE_ON, 0) != 0;
}
public static boolean isConnected(){
    State computedState;
    if(VersionSDK.isSupEqualTo28_P()){
        computedState = NetworkCallback_after28_P.computeState();
    } else {
        computedState = NetworkCallback_before28_P.computeState();
    }
    return isConnected(computedState);
}
public static boolean isConnected(State state){
    return (state == TYPE_WIFI) || (state == TYPE_DATA) || (state == TYPE_VPN) || (state == TYPE_UNKNOWN);
}
public static boolean isDisconnected(){
    State computedState;
    if(VersionSDK.isSupEqualTo28_P()){
        computedState = NetworkCallback_after28_P.computeState();
    } else {
        computedState = NetworkCallback_before28_P.computeState();
    }
    return isDisconnected(computedState);
}
public static boolean isDisconnected(State state){
    return (state == DISCONNECTED) || (state == AIR_PLANE);
}

public static TaskValue<Boolean>.Observable socketTest(ListEntry<String, Integer> socketTestList){
    if(socketTestList == null){
        return TaskValue.Complete(true);
    }
    TaskValue<Boolean> task = new TaskValue<>();
    RunnableGroup gr = new RunnableGroup(myClass()).name("Socket Test");
    for(Entry<String, Integer> e: socketTestList){
        gr.add(new RunnableGroup.Action(){
            @Override
            public void runSafe(){
                socketTest(getHandler(), e.key, e.value).observe(new ObserverValue<Boolean>(this){
                    @Override
                    public void onComplete(Boolean success){
                        if(success){
                            next();
                        } else {
                            putException(new SocketException());
                            done();
                        }
                    }
                });
            }
        });
    }
    gr.setOnDone(new RunnableGroup.Action(){
        @Override
        public void runSafe(){
            task.notifyComplete(getException() == null);
        }
    });
    gr.start(CONNECTIVITY_SOCKET_TEST_DELAY_START_ms);
    return task.getObservable();
}
public static TaskValue<Boolean>.Observable socketTest(Handler handler, String address, int port){
    TaskValue<Boolean> task = new TaskValue<>();
    Handler.SECONDARY().post(myClass(), new RunnableW(){
        Socket socket = null;
        @Override
        public void beforeRun(){
            socket = new Socket();
        }
        @Override
        public void runSafe() throws Throwable{
            InetSocketAddress socketAddress = new InetSocketAddress(address, port);
            socket.connect(socketAddress, CONNECTIVITY_SOCKET_TIMEOUT_ms);
            socket.close();
            complete(true);
        }
        @Override
        public void onException(Throwable e){
            try{
                if((socket!=null) && !socket.isClosed()){
                    socket.close();
                }
            } catch(java.lang.Throwable ec){
DebugException.start().log(ec).end();
            }
            complete(false);
        }
        void complete(boolean success){
            handler.post(this, new RunnableW(){
                @Override
                public void runSafe(){
                    task.notifyComplete(success);
                }
            });
        }
    });
    return task.getObservable();
}
public static InetAddress getBroadcastIPv4Address(){
    InetAddress inetAddress = getIPv4Address();
    if(inetAddress != null){
        return getBroadcast(inetAddress);
    } else {
        return null;
    }
}
public static InetAddress getBroadcast(InetAddress inetAddress){
    try{
        NetworkInterface networkInterface = getNetworkInterface(inetAddress);
        List<InterfaceAddress> addresses = networkInterface.getInterfaceAddresses();
        InetAddress broadCastAddress = null;
        for(InterfaceAddress interfaceAddress: addresses){
            broadCastAddress = interfaceAddress.getBroadcast();
        }
        return broadCastAddress;
    } catch(Throwable e){

DebugException.start().log(e).end();

    }
    return null;
}
public static NetworkInterface getNetworkInterface(InetAddress inetAddressToFind){
    return forEachInterface(new PredicateW<InetAddress>(){
        @Override
        public boolean test(InetAddress inetAddress){
            String address = inetAddress.getHostAddress();
            return (address != null) && address.equals(inetAddressToFind.getHostAddress());
        }
    });
}
public static String getNetworkInterfaceString(InetAddress inetAddressToFind){
    NetworkInterface networkInterface = getNetworkInterface(inetAddressToFind);
    return networkInterface != null ? networkInterface.getName() : null;
}
public static String getMacAddress(NetworkInterface networkInterface){
    try{
        byte[] mac = networkInterface.getHardwareAddress();
        if(mac != null){
            return UtilsString.join(":", UtilsArray.getIterator(mac), new FunctionW<Byte, String>(){
                @Override
                public String apply(Byte b){
                    return ByteTo.StringHex(b);
                }
            }).toString();
        } else {
            return null;
        }
    } catch(SocketException e){

DebugException.start().log(e).end();

        return null;
    }

}
public static String getMacAddress(InetAddress inetAddress){
    NetworkInterface networkInterface = getNetworkInterface(inetAddress);
    if(networkInterface != null){
        return getMacAddress(networkInterface);
    } else {
        return null;
    }
}
public static Inet4Address getIPv4Address(){
    if(ConnectivityHotSpotManager.isEnabled()){
        return (Inet4Address)forEachAddress(new PredicateW<InetAddress>(){
            @Override
            public boolean test(InetAddress inetAddress){
                String address = inetAddress.getHostAddress();
                return (address!=null) && (inetAddress instanceof Inet4Address) && address.startsWith(HOTSPOT_WIFI_ADDRESS_START_BY);
            }
        });
    }
    else if(isConnected()){
        return (Inet4Address)forEachAddress(new PredicateW<InetAddress>(){
            @Override
            public boolean test(InetAddress inetAddress){
                return inetAddress instanceof Inet4Address;
            }
        });
    } else {
        return null;
    }
}
public static String getIPv4AddressString(){
    InetAddress inetAddress = getIPv4Address();
    return inetAddress != null ? inetAddress.getHostAddress() : null;
}
public static Inet6Address getIPv6Address(){
    if(isConnected()){
        return (Inet6Address)forEachAddress(new PredicateW<InetAddress>(){
            @Override
            public boolean test(InetAddress inetAddress){
                return inetAddress instanceof Inet6Address;
            }
        });
    } else {
        return null;
    }
}
public static String getIPv6AddressString(){
    Inet6Address inetAddress = getIPv6Address();
    return inetAddress != null ? iPv6ToString(inetAddress) : null;
}
public static NetworkInterface forEachInterface(PredicateW<InetAddress> predicateAddress){
    try{
        List<NetworkInterface> networkInterfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
        for(NetworkInterface networkInterface: networkInterfaces){
            List<InetAddress> inetAddresses = Collections.list(networkInterface.getInetAddresses());
            for(InetAddress inetAddress: inetAddresses){
                if(!inetAddress.isLoopbackAddress()){
                    String address = inetAddress.getHostAddress();
                    if((address != null) && predicateAddress.test(inetAddress)){
                        return networkInterface;
                    }
                }
            }
        }
        return null;
    } catch(Throwable e){

DebugException.start().log(e).end();

        return null;
    }
}
public static InetAddress forEachAddress(PredicateW<InetAddress> predicateAddress){
    try{
        List<NetworkInterface> networkInterfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
        for(NetworkInterface networkInterface: networkInterfaces){
            List<InetAddress> inetAddresses = Collections.list(networkInterface.getInetAddresses());
            for(InetAddress inetAddress: inetAddresses){
                if(!inetAddress.isLoopbackAddress()){
                    if((inetAddress.getHostAddress() != null) && predicateAddress.test(inetAddress)){
                        return inetAddress;
                    }
                }
            }
        }
        return null;
    } catch(Throwable e){
DebugException.start().log(e).end();
        return null;
    }
}
public static String iPv6ToString(Inet6Address inetAddress){
    String address = inetAddress.getHostAddress();
    if(address != null){
        int delim = address.indexOf('%'); // drop ip6 zone suffix
        return delim < 0 ? address.toUpperCase() : address.substring(0, delim).toUpperCase();
    }
    else{
        return null;
    }

}

public boolean isAirplaneMode(){
    return getState() == AIR_PLANE;
}
private ConnectivityManager me(){
    return this;
}
public void unregisterReceiver(boolean force){
    synchronized(me()){
        if((queries == null) || (queries.size() <= 0) || force){
            if(br != null){
                br.unregister();
                br = null;
                state.setOnChangedRunnable(null);
                state = null;
                if(queries != null){
                    queries.clear();
                    queries = null;
                }
            }
        }
    }
}
private void registerReceiver(){
    synchronized(me()){
        if(br == null){
            state = new Delayed<>(computeAirplaneMode() ? AIR_PLANE : DISCONNECTED);
            state.setDelayTimeToValidate(CONNECTIVITY_VALID_CHANGE_DELAY_MIN_ms);
            state.setOnChangedRunnable(new ConsumerW<State>(){
                @Override
                public void accept(State lastValid){
DebugLog.start().send(me(), "ConnectivityManager changed to " + state.getLastValid().name()).end();
                    runQuery(lastValid);
                }
            });
            if(VersionSDK.isSupEqualTo28_P()){
                br = new NetworkCallback_after28_P(this);
            } else {
                br = new NetworkCallback_before28_P(this);
            }
            br.register();
        }
    }
}
public State getState(){
    synchronized(me()){
        if(state == null){
            return null;
        } else {
            return state.getLastValid();
        }
    }
}
private void runQuery(State state){
    if(queries != null){
        Handler.PRIMARY().post(this, new RunnableW(){
            @Override
            public void runSafe(){
                if(queries != null){
                    for(Query q: queries){
                        if(q.isEnabled()){
                            q.runActionIfContains(state);
                        }
                    }
                    unregisterReceiver(false);
                }
            }
        });
    }
}
public void addQuery(Query query){
    synchronized(me()){
        registerReceiver();
        if(queries == null){
            queries = new ArrayList<>();
        }
        query.link(this);
        queries.add(query);
    }
}
private void removeQuery(Query query){
    synchronized(me()){
        if(queries != null){
            queries.remove(query);
            if(queries.size() <= 0){
                queries = null;
            }
            unregisterReceiver(false);
        }
    }
}
@Override
protected void finalize() throws Throwable{
DebugTrack.start().destroy(this).end();
    unregisterReceiver(true);
    super.finalize();
}
public enum State{
    DISCONNECTED, AIR_PLANE, TYPE_UNKNOWN, TYPE_WIFI, TYPE_DATA, TYPE_VPN
}

private static abstract class NetworkCallback{
    ConnectivityManager connectivityManager;
    public NetworkCallback(ConnectivityManager connectivityManager){
DebugTrack.start().create(this).end();
        this.connectivityManager = connectivityManager;
    }
    abstract void register();

    abstract void unregister();
    void onChanged(State computedState){
        if(isConnected(computedState)){
            connectivityManager.state.update(computedState);
        } else {
            connectivityManager.state.force(computedState);
        }
    }
    @Override
    protected void finalize() throws Throwable{
DebugTrack.start().destroy(this).end();
        super.finalize();
    }

}

@RequiresApi(api = Build.VERSION_CODES.P)
@SuppressLint("MissingPermission")
private static class NetworkCallback_after28_P extends NetworkCallback{
    final static int NOTIFY_DELAY_ms = 100;
    android.net.ConnectivityManager.NetworkCallback callback = null;
    NetworkCallback_after28_P(ConnectivityManager connectivityManager){
        super(connectivityManager);
    }
    static State computeState(){
        android.net.ConnectivityManager cm = AppContext.getSystemService(android.content.Context.CONNECTIVITY_SERVICE);
        Network network = cm.getActiveNetwork();
        if(network == null){
            return DISCONNECTED;
        }
        NetworkCapabilities nc = cm.getNetworkCapabilities(network);
        if(nc != null){
            if(nc.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)){
                return TYPE_WIFI;
            }
            if(nc.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)){
                return TYPE_DATA;
            }
            if(nc.hasTransport(NetworkCapabilities.TRANSPORT_VPN)){
                return TYPE_VPN;
            }
            return TYPE_UNKNOWN;
        }
        if(computeAirplaneMode()){
            return AIR_PLANE;
        } else {
            return DISCONNECTED;
        }
    }
    @Override
    void register(){
        callback = new android.net.ConnectivityManager.NetworkCallback(){
            @Override
            public void onAvailable(@NonNull Network network){
                onChanged();
            }
            @Override
            public void onLost(@NonNull Network network){
                onChanged();
            }
            @Override
            public void onUnavailable(){
                onChanged();
            }
        };
        NetworkRequest networkRequest = new NetworkRequest.Builder().addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
                .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                .addTransportType(NetworkCapabilities.TRANSPORT_VPN)
                .addTransportType(NetworkCapabilities.TRANSPORT_ETHERNET)
                .build();
        android.net.ConnectivityManager cm = AppContext.getSystemService(android.content.Context.CONNECTIVITY_SERVICE);
        cm.registerNetworkCallback(networkRequest, callback);
    }
    void onChanged(){
        Handler.PRIMARY().post(this, NOTIFY_DELAY_ms, new RunnableW(){
            @Override
            public void runSafe(){
                onChanged(computeState());
            }
        });
    }
    @Override
    void unregister(){
        android.net.ConnectivityManager cm = AppContext.getSystemService(android.content.Context.CONNECTIVITY_SERVICE);
        cm.unregisterNetworkCallback(callback);
        callback = null;
    }

}
@SuppressLint("MissingPermission")
@SuppressWarnings("deprecation")
private static class NetworkCallback_before28_P extends NetworkCallback{
    final static int NOTIFY_DELAY_ms = 100;
    BroadcastReceiver callback = null;
    NetworkCallback_before28_P(ConnectivityManager connectivityManager){
        super(connectivityManager);
    }
    static State computeState(){
        android.net.ConnectivityManager cm = AppContext.getSystemService(android.content.Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if(ni != null){
            if(!ni.isAvailable() || !ni.isConnected()){
                return DISCONNECTED;
            }
            if(ni.getType() == android.net.ConnectivityManager.TYPE_WIFI){
                return TYPE_WIFI;
            }
            if(ni.getType() == android.net.ConnectivityManager.TYPE_MOBILE){
                return TYPE_DATA;
            }
            if(ni.getType() == android.net.ConnectivityManager.TYPE_VPN){
                return TYPE_VPN;
            }
DebugException.start().unknown("type", ni.getType()).end();
            return TYPE_UNKNOWN;
        }
        if(computeAirplaneMode()){
            return AIR_PLANE;
        } else {
            return DISCONNECTED;
        }
    }
    @Override
    void register(){
        callback = new BroadcastReceiver(){
            @Override
            public void onReceive(android.content.Context context, Intent intent){
                onChanged();
            }
        };
        IntentFilter filter = new IntentFilter();
        filter.addAction(android.net.ConnectivityManager.CONNECTIVITY_ACTION);
        AppContext.registerReceiver(callback, filter);
    }
    void onChanged(){
        Handler.PRIMARY().post(this, NOTIFY_DELAY_ms, new RunnableW(){
            @Override
            public void runSafe(){
                onChanged(computeState());
            }
        });
    }
    @Override
    void unregister(){
        AppContext.get().unregisterReceiver(callback);
        callback = null;
    }

}

public static abstract class Query{
    private final Handler handler;
    private final List<State> states;
    protected ConnectivityManager connectivityManager = null;
    private boolean isEnabled = false;

    private ListEntry<String, Integer> sockets = null;
    private TaskValue<Boolean>.Observable socketObservable = null;
    private long socketRetryDelay = CONNECTIVITY_SOCKET_TEST_DELAY_MIN_RETRY_ms;
    private RunnableW socketRetry = null;

    public Query(){
        this(null);
    }
    public Query(Handler handler){
DebugTrack.start().create(this).end();
        states = new ListOrObject<State>();
        this.handler = handler;
    }
    private Query me(){
        return this;
    }
    private void link(ConnectivityManager connectivityManager){
        this.connectivityManager = connectivityManager;
    }
    public <Q extends Query> Q addGoogleSocketTest(){
        addSocketTest("8.8.8.8", 53);
        addSocketTest("www.google.com", 80);
        return (Q)this;
    }
    public Query addSocketTest(String address, int port){
        if(sockets == null){
            sockets = new ListEntry<String, Integer>();
        }
        for(Entry<String, Integer> e: sockets){
            if((e.key.equals(address)) && (e.value == port)){
                return this;
            }
        }
        sockets.add(address, port);
        return this;
    }
    public List<State> getStates(){
        return states;
    }
    public <Q extends Query> Q addState(State state){
        states.add(state);
        return (Q)this;
    }
    public <Q extends Query> Q addState(List<State> states){
        this.states.addAll(states);
        return (Q)this;
    }
    public <Q extends Query> Q addConnectedState(){
        addState(TYPE_DATA);
        addState(TYPE_WIFI);
        addState(TYPE_VPN);
        addState(TYPE_UNKNOWN);
        return (Q)this;
    }
    public <Q extends Query> Q addDisConnectedState(){
        addState(DISCONNECTED);
        addState(AIR_PLANE);
        return (Q)this;
    }
    public <Q extends Query> Q allState(){
        addConnectedState();
        addDisConnectedState();
        return (Q)this;
    }

    public <Q extends Query> Q enable(boolean flag){
        if(connectivityManager != null){
            boolean wasEnabled = isEnabled;
            State state = connectivityManager.getState();
            boolean connectivityIsConnected = ConnectivityManager.isConnected(state);
            if(flag && !wasEnabled && connectivityIsConnected){
                runActionIfContains(state);
            }
            isEnabled = flag;
            if(!flag && wasEnabled && connectivityIsConnected){
                runActionIfContains(DISCONNECTED);
            }
        }
        return (Q)this;
    }
    public boolean isEnabled(){
        return isEnabled;
    }

    private TaskValue<Boolean>.Observable socketTest(){
        if(socketRetry != null){
            socketRetry.cancel(this, Handler.PRIMARY());
            socketRetry = null;
            socketRetryDelay = CONNECTIVITY_SOCKET_TEST_DELAY_MIN_RETRY_ms;
        }
        socketObservable = ConnectivityManager.socketTest(sockets);
        socketObservable.observe(new ObserverValue<Boolean>(this){
            @Override
            public void onComplete(Boolean success){
                socketObservable = null;
                if(success){
                    socketRetryDelay = CONNECTIVITY_SOCKET_TEST_DELAY_MIN_RETRY_ms;
                } else {
                    socketRetry = new RunnableW(){
                        @Override
                        public void runSafe(){
                            socketRetry = null;
                            if(ConnectivityManager.isConnected(connectivityManager.getState())){
                                if(socketRetryDelay < CONNECTIVITY_SOCKET_TEST_DELAY_MAX_RETRY_ms){
                                    socketRetryDelay += (CONNECTIVITY_SOCKET_TEST_DELAY_MAX_RETRY_ms + CONNECTIVITY_SOCKET_TEST_DELAY_MIN_RETRY_ms) / CONNECTIVITY_SOCKET_TEST_DELAY_STEP_RETRY;
                                }
                                runActionIfContains(connectivityManager.getState());
                            } else {
                                socketRetryDelay = CONNECTIVITY_SOCKET_TEST_DELAY_MIN_RETRY_ms;
                            }
                        }
                    };
                    Handler.PRIMARY().post(me(), socketRetryDelay, socketRetry);
                }
            }
        });
        return socketObservable;
    }
    private void runActionIfContains(State state){
        if(states.contains(state)){
            checkSocketAndRunAction(state);
        }
    }
    private void checkSocketAndRunAction(State state){
        if(sockets == null){
            runAction(state);
            return;
        }
        if(isDisconnected(state)){
            runAction(state);
            return;
        }
        if(socketObservable == null){
            socketTest().observe(new ObserverValue<Boolean>(this){
                @Override
                public void onComplete(Boolean success){
                    if(success){
                        runAction(state);
                    }
                }
            });
        }
    }
    private void runAction(State state){
        if(handler != null){
            handler.post(this, new RunnableW(){
                @Override
                public void runSafe(){
                    me().action(state);
                }
            });
        } else {
            action(state);
        }
    }
    protected abstract void action(State state);

    public void remove(){
        if(connectivityManager != null){
            connectivityManager.removeQuery(this);
            connectivityManager = null;
        }
    }

    @Override
    protected void finalize() throws Throwable{
DebugTrack.start().destroy(this).end();
        super.finalize();
    }

}
public abstract static class QueryHelper extends Query{
    private State lastState = computeAirplaneMode() ? AIR_PLANE : DISCONNECTED;
    public QueryHelper(){
        this(null);
    }
    public QueryHelper(Handler handler){
        super(handler);
        addConnectedState();
        addDisConnectedState();
    }

    public boolean isConnected(){
        return ConnectivityManager.isConnected(connectivityManager.getState());
    }
    public boolean isDisconnected(){
        return ConnectivityManager.isDisconnected(connectivityManager.getState());
    }

    @Override
    final protected void action(State state){
        if(lastState != state){
            lastState = state;
            if(ConnectivityManager.isConnected(state)){
                onConnected(state);
            } else if(ConnectivityManager.isDisconnected(state)){
                onDisConnected(state);
            } else {
DebugException.start().unknown("state", state).end();
            }

        }
    }
    public abstract void onConnected(State state);

    public abstract void onDisConnected(State state);

}

}

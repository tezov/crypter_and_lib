/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java_android.application;

import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java.type.primitive.ObjectTo;
import com.tezov.lib_java.type.primitive.IntTo;
import com.tezov.lib_java.type.unit.UnitByte;
import com.tezov.lib_java.toolbox.CompareType;
import com.tezov.lib_java.toolbox.Clock;
import com.tezov.lib_java.util.UtilsString;
import java.util.LinkedList;
import java.util.Set;
import com.tezov.lib_java_android.database.sqlLite.filter.dbFilterOrder;
import com.tezov.lib_java_android.database.sqlLite.filter.chunk.ChunkCommand;
import androidx.fragment.app.Fragment;

import com.tezov.lib_java.wrapperAnonymous.ConsumerW;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;

import com.tezov.lib_java.async.Handler;
import com.tezov.lib_java.debug.DebugLog;
import com.tezov.lib_java.debug.DebugTrack;
import com.tezov.lib_java.type.Delayed;
import com.tezov.lib_java.type.runnable.RunnableW;
import com.tezov.lib_java.wrapperAnonymous.PredicateW;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

import static android.content.Context.WIFI_SERVICE;

public class ConnectivityHotSpotManager{
public final static String HOTSPOT_WIFI_ADDRESS_START_BY = "192.168.43.";
public final static String HOTSPOT_USB_ADDRESS_START_BY = "192.168.42.";
final private static long CONNECTIVITY_HOTSPOT_VALID_CHANGE_DELAY_MIN_ms = AppConfig.getLong(AppConfigKey.CONNECTIVITY_HOTSPOT_VALID_CHANGE_DELAY_MIN_ms.getId());
private final static int WIFI_AP_STATE_ENABLED = 13;
private final static String WIFI_AP_STATE_CHANGED_ACTION = "android.net.wifi.WIFI_AP_STATE_CHANGED";

private BroadcastReceiver br = null;
private Delayed<Boolean> state = null;
private List<Query> queries = null;

public ConnectivityHotSpotManager(){
DebugTrack.start().create(this).end();
}
private static Class<ConnectivityHotSpotManager> myClass(){
    return ConnectivityHotSpotManager.class;
}

public static boolean isEnabled(){
    try{
        WifiManager wifiManager = AppContext.getSystemService(WIFI_SERVICE);
        java.lang.reflect.Method method = wifiManager.getClass().getDeclaredMethod("isWifiApEnabled");
        method.setAccessible(true);
        return (boolean)method.invoke(wifiManager);
    } catch(Throwable e){
        return false;
    }
}

public static InetAddress getBroadcastIPv4Address(){
    InetAddress inetAddress = getIPv4Address();
    if(inetAddress != null){
        return ConnectivityManager.getBroadcast(inetAddress);
    } else {
        return null;
    }
}
public static Inet4Address getIPv4Address(){
    if(isEnabled()){
        return (Inet4Address)ConnectivityManager.forEachAddress(new PredicateW<InetAddress>(){
            @Override
            public boolean test(InetAddress inetAddress){
                if(inetAddress instanceof Inet4Address){
                    String address = inetAddress.getHostAddress();
                    if(address != null){
                        return address.startsWith(HOTSPOT_WIFI_ADDRESS_START_BY);
                    }
                }
                return false;
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

private ConnectivityHotSpotManager me(){
    return this;
}

private void runQuery(Boolean state){
    if(queries != null){
        Handler.PRIMARY().post(this, new RunnableW(){
            @Override
            public void runSafe(){
                if(queries != null){
                    for(Query q: queries){
                        if(q.isEnabled()){
                            q.runAction(state);
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

public boolean isActive(){
    synchronized(this){
        if(state == null){
            return false;
        } else {
            return state.getLastValid();
        }
    }
}

public void unregisterReceiver(boolean force){
    synchronized(me()){
        if((queries == null) || (queries.size() <= 0) || force){
            if(br != null){
                AppContext.get().unregisterReceiver(br);
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
            state = new Delayed<>(isEnabled());
            state.setDelayTimeToValidate(CONNECTIVITY_HOTSPOT_VALID_CHANGE_DELAY_MIN_ms);
            state.setOnChangedRunnable(new ConsumerW<Boolean>(){
                @Override
                public void accept(Boolean lastValid){
DebugLog.start().send(me(), "ConnectivityHotSpotManager changed to " + state.getLastValid()).end();
                    runQuery(lastValid);
                }
            });
            br = new BroadcastReceiver(){
                @Override
                public void onReceive(android.content.Context context, Intent intent){
                    int wifi_state = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, 0);
                    if(wifi_state == WIFI_AP_STATE_ENABLED){
                        state.update(true);
                    } else {
                        state.force(false);
                    }
                    runQuery(state.getLastValid());
                }
            };
            IntentFilter filter = new IntentFilter();
            filter.addAction(WIFI_AP_STATE_CHANGED_ACTION);
            AppContext.registerReceiver(br, filter);
        }
    }
}

@Override
protected void finalize() throws Throwable{
DebugTrack.start().destroy(this).end();
    unregisterReceiver(true);
    super.finalize();
}

public static abstract class Query{
    private final Handler handler;
    private boolean lastState = false;
    private ConnectivityHotSpotManager connectivityHotSpotManager = null;
    private boolean isEnabled = false;

    public Query(){
        this(null);
    }
    public Query(Handler handler){
DebugTrack.start().create(this).end();
        this.handler = handler;
    }
    private Query me(){
        return this;
    }
    private void link(ConnectivityHotSpotManager connectivityManager){
        this.connectivityHotSpotManager = connectivityManager;
    }

    public <Q extends Query> Q enable(boolean flag){
        if(connectivityHotSpotManager != null){
            boolean connectivityIsActive = connectivityHotSpotManager.isActive();
            if(flag && connectivityIsActive){
                runAction(true);
            }
            this.isEnabled = flag;
            if(!flag && connectivityIsActive){
                runAction(false);
            }
        }
        return (Q)this;
    }
    public boolean isEnabled(){
        return isEnabled;
    }

    private void runAction(boolean state){
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

    private void action(boolean state){
        if(lastState != state){
            lastState = state;
            if(state){
                onActive();
            } else {
                onInactive();
            }
        }
    }
    public abstract void onActive();

    public abstract void onInactive();

    public void remove(){
        if(connectivityHotSpotManager != null){
            connectivityHotSpotManager.removeQuery(this);
            connectivityHotSpotManager = null;
        }
    }

    @Override
    protected void finalize() throws Throwable{
DebugTrack.start().destroy(this).end();
        super.finalize();
    }

}

}

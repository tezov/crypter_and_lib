/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java_android.application;

import com.tezov.lib_java.debug.DebugLog;
import com.tezov.lib_java.debug.DebugTrack;
import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java.type.primitive.ObjectTo;
import com.tezov.lib_java.type.primitive.IntTo;
import com.tezov.lib_java.type.unit.UnitByte;
import com.tezov.lib_java.toolbox.CompareType;
import com.tezov.lib_java.toolbox.Clock;
import com.tezov.lib_java.util.UtilsString;
import java.util.List;
import java.util.LinkedList;
import java.util.Set;
import com.tezov.lib_java_android.database.sqlLite.filter.dbFilterOrder;
import com.tezov.lib_java_android.database.sqlLite.filter.chunk.ChunkCommand;
import androidx.fragment.app.Fragment;
public enum AppConfigKey{
DEBUG_LOG_ON_DEVICE(-2),
DEBUG_LOG_CONFIG_ENCRYPTED(-1),
GUID(1),
FINGER_PRINT_SHA1_PLAYSTORE(2),
FINGER_PRINT_SHA1_DEV(3),
RS_SCRIPT_DELAY_DESTROY_second(4),
POOL_ENABLE(5),
NOTIFICATION_MIN_UPDATE_DELAY_ms(6),
CONNECTIVITY_VALID_CHANGE_DELAY_MIN_ms(7),
CONNECTIVITY_HOTSPOT_VALID_CHANGE_DELAY_MIN_ms(8),
CONNECTIVITY_SOCKET_TEST_DELAY_START_ms(9),
CONNECTIVITY_SOCKET_TEST_DELAY_MIN_RETRY_ms( 10),
CONNECTIVITY_SOCKET_TEST_DELAY_MAX_RETRY_ms(11),
CONNECTIVITY_SOCKET_TEST_DELAY_STEP_RETRY(12),
CONNECTIVITY_SOCKET_TIMEOUT_ms(13),
AUTH_TIMEOUT_DELAY_ms(14),
FB_TABLE_PAUSE_TIMEOUT_DELAY_ms(15),
FB_DIRECTORY_TEMP(16),
DB_FILE_DIRECTORY_TRASH(17);
int id;
AppConfigKey(int id){
    if(id > 0){
        this.id = id + com.tezov.lib_java.application.AppConfigKey.getLastIndex();
    }
    else{
        this.id = id + com.tezov.lib_java.application.AppConfigKey.getFirstIndex();
    }
}
public static int getFirstIndex(){
    return -2 + com.tezov.lib_java.application.AppConfigKey.getFirstIndex();
}
public static int getLastIndex(){
    return 17 + com.tezov.lib_java.application.AppConfigKey.getLastIndex();
}
public static AppConfigKey find(int id){
    for(AppConfigKey k: values()){
        if(k.id == id){
            return k;
        }
    }
    return null;
}
public static String getName(int id){
    AppConfigKey key = find(id);
    if(key != null){
        return key.name();
    } else {
        return null;
    }
}
public static AppConfigKey find(String name){
    for(AppConfigKey k: values()){
        if(k.name().equals(name)){
            return k;
        }
    }
    return null;
}
public static Integer getId(String name){
    AppConfigKey key = find(name);
    if(key != null){
        return key.id;
    } else {
        return null;
    }
}
public int getId(){
    return id;
}

public static class Adapter extends com.tezov.lib_java.application.AppConfigKey.Adapter{
    @Override
    public Integer toIndex(String name){
        AppConfigKey key = find(name);
        if(key != null){
            return key.id;
        } else {
            return super.toIndex(name);
        }
    }
    @Override
    public String fromIndex(Integer index){
        AppConfigKey key = find(index);
        if(key != null){
            return key.name();
        } else {
            return super.fromIndex(index);
        }
    }
}
}

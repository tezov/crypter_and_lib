/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java_android.file;

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
import com.tezov.lib_java_android.application.AppContext;
import com.tezov.lib_java_android.application.VersionSDK;
import com.tezov.lib_java.file.Directory;
import com.tezov.lib_java.file.StoragePackage;

public class EnvironnementAndroid implements StoragePackage.Environnement{

private static android.content.Context context(){
    return AppContext.get();
}

@Override
public String getPrivateDataPath(){
    String basePath;
    if(VersionSDK.isSupEqualTo24_NOUGAT()){
        basePath = context().getFilesDir().getParent();
    } else {
        basePath = context().getDataDir().getPath();
    }
    return basePath + Directory.PATH_SEPARATOR;
}
@Override
public String getPrivateDataCachePath(){
    return context().getCacheDir().getPath() + Directory.PATH_SEPARATOR;
}
@Override
public String getPrivateDataFilePath(){
    return context().getFilesDir().getPath() + Directory.PATH_SEPARATOR;
}
@Override
public String getPrivateDataBasePath(){
    String basePath;
    if(VersionSDK.isSupEqualTo24_NOUGAT()){
        basePath = context().getFilesDir().getParent();
    } else {
        basePath = context().getDataDir().getPath();
    }
    return basePath + Directory.PATH_SEPARATOR + StoragePackage.Path.DATABASE.path();
}
@Override
public String getPrivateSharePreferencePath(){
    String basePath;
    if(VersionSDK.isSupEqualTo24_NOUGAT()){
        basePath = context().getFilesDir().getParent();
    } else {
        basePath = context().getDataDir().getPath();
    }
    return basePath + Directory.PATH_SEPARATOR + StoragePackage.Path.SHARED_PREFERENCES.path();
}
@Override
public String getPublicDataPath(){
    return context().getExternalFilesDir(null).getParent() + Directory.PATH_SEPARATOR;
}

}

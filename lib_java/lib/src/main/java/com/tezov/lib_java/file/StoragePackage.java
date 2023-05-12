/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java.file;

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
import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java.type.defEnum.EnumBase;

public class StoragePackage{
protected StoragePackage(){
}
public interface Environnement{
    String getPrivateDataPath();
    String getPrivateDataCachePath();
    String getPrivateDataFilePath();
    String getPrivateDataBasePath();
    String getPrivateSharePreferencePath();
    String getPublicDataPath();

}
private static Environnement environnement = null;
public static Environnement getEnvironnement(){
    return environnement;
}
public static void setEnvironnement(Environnement environnement){
    StoragePackage.environnement = environnement;
}
public enum Type{
    PRIVATE_DATA, PRIVATE_DATA_CACHE, PRIVATE_DATA_FILE, PRIVATE_DATABASE, PRIVATE_SHARE_PREFERENCE, PUBLIC_DATA;
    public Directory newDirectory(Path.Is path){
        if(path != null){
            return new Directory(this, path);
        } else {
            return new Directory(this);
        }
    }
    public File newFile(Path.Is path){
        if(path != null){
            return new File(this, path);
        } else {
            return new File(this);
        }
    }
    public FileW newFileW(){
        return new FileW(getPath());
    }
    public String getPath(){
        switch(this){
            case PRIVATE_DATA:
                return environnement.getPrivateDataPath();
            case PRIVATE_DATA_CACHE:
                return environnement.getPrivateDataCachePath();
            case PRIVATE_DATA_FILE:
                return environnement.getPrivateDataFilePath();
            case PRIVATE_DATABASE:
                return environnement.getPrivateDataBasePath();
            case PRIVATE_SHARE_PREFERENCE:
                return environnement.getPrivateSharePreferencePath();
            case PUBLIC_DATA:
                return environnement.getPublicDataPath();
            default:
DebugException.start().unknown("storage", this).end();
                return null;
        }
    }
}
public interface Path{
    Is TEMP = new Is("TEMP", "tmp");
    Is TRASH = new Is("TRASH", "trash");
    Is DATABASE = new Is("DATABASE", "databases");
    Is DATABASE_DOCUMENT = new Is("DOCUMENT", DATABASE.path + "document");
    Is SHARED_PREFERENCES = new Is("SHARED_PREFERENCES", "shared_prefs");
    static Is find(String s){
        return Is.findTypeOf(Is.class, s);
    }
    class Is extends EnumBase.Is{
        private final String path;
        public Is(String value, String path){
            super(value);
            this.path = path + Directory.PATH_SEPARATOR;
        }
        public Is(String value, Is parent, String path){
            this(value, parent.path + path);
        }
        public String path(){
            return path;
        }

    }
}

}

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
import java.util.LinkedList;
import java.util.Set;
import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java.wrapperAnonymous.ComparatorW;
import com.tezov.lib_java.wrapperAnonymous.PredicateW;
import com.tezov.lib_java.util.UtilsList;
import com.tezov.lib_java.util.UtilsString;

import java.util.Arrays;
import java.util.List;

public class StorageFile{
public final static int URI_OBTAIN_MAX_RETRY = 5;
public final static int URI_RANDOM_HEX_LENGTH = 6;

public static java.io.File obtain(String directoryPath, String fileFullName){
    return obtain(new java.io.File(directoryPath), fileFullName);
}
public static java.io.File obtain(java.io.File directory, String fileFullName){
    try{
        if(!directory.exists() && !directory.mkdirs()){
            throw new Throwable("directory can't be created");
        }
        if(!directory.canWrite()){
            throw new Throwable("directory can't be written");
        }
        java.io.File mediaFile = new java.io.File(directory, fileFullName);
        if(mediaFile.exists() || !mediaFile.createNewFile()){
            throw new Throwable("file can't be created");
        }
        return new java.io.File(directory, fileFullName);
    } catch(Throwable e){
        return null;
    }
}
private static List<String> selectFileLike(java.io.File directory, String fileFullName, boolean excludeFileFullName){
    if(!directory.exists()){
        return null;
    } else {
        UtilsFile.FileName fileFullNameExploded = new UtilsFile.FileName(fileFullName);
        PredicateW<String> predicate = newPredicateSelectFileLike(fileFullNameExploded, excludeFileFullName);
        String[] files = directory.list((dir, fullName)->predicate.test(fullName));
        if((files != null) && (files.length > 0)){
            return Arrays.asList(files);
        } else {
            return null;
        }
    }
}
public static PredicateW<String> newPredicateSelectFileLike(UtilsFile.FileName fileFullNameExploded, boolean excludeFileFullName){
    String fileFullName = fileFullNameExploded.getFullName();
    String fileFullNameWithoutNumber = fileFullNameExploded.getFullNameWithoutNumber();
    Integer fileFullNameNumber = fileFullNameExploded.getNumber();
    return new PredicateW<>(){
        @Override
        public boolean test(String fullName){
            UtilsFile.FileName fullNameExploded = new UtilsFile.FileName(fullName);
            if(fileFullNameWithoutNumber.equals(fullNameExploded.getFullNameWithoutNumber())){
                if(!excludeFileFullName || !fullName.equals(fileFullName)){
                    if(fileFullNameNumber == null){
                        return true;
                    } else {
                        Integer fullNameNumber = fullNameExploded.getNumber();
                        return (fullNameNumber != null) && (fullNameNumber >= fileFullNameNumber);
                    }
                }
            }
            return false;
        }
    };
}
public static java.io.File obtainUnique(String directoryPath, String fileFullName){
    return obtainUnique(new java.io.File(directoryPath), fileFullName);
}
public static java.io.File obtainUnique(java.io.File directory, String fileFullName){
    List<String> fileNames = selectFileLike(directory, fileFullName, false);
    List<String> fileNamesSorted = null;
    if(fileNames != null){
        fileNamesSorted = UtilsList.sort(fileNames, new ComparatorW<String>(){
            @Override
            public int compare(String s1, String s2){
                Integer n1 = UtilsFile.FileName.getNumber(s1);
                Integer n2 = UtilsFile.FileName.getNumber(s2);
                if((n1 != null) && (n2 != null)){
                    return Integer.compare(n1, n2);
                } else if(n1 != null){
                    return 1;
                } else {
                    return -1;
                }
            }
        });
    }
    UtilsFile.FileName fileName = new UtilsFile.FileName(fileFullName);
    if(fileNamesSorted != null){
        Integer biggerNumber = UtilsFile.FileName.getNumber(fileNamesSorted.get(fileNamesSorted.size() - 1));
        if((biggerNumber == null) || (biggerNumber < 0)){
            biggerNumber = 0;
        }
        fileName.setNumber(biggerNumber + 1);
    }
    try{
        if(!directory.exists() && !directory.mkdirs()){
            throw new Throwable("directory can't be created");
        }
        int attempt = 0;
        do{
            java.io.File mediaFile = new java.io.File(directory, fileName.getFullName());
            if(mediaFile.createNewFile()){
                return new java.io.File(directory, fileName.getFullName());
            }
            fileName.incNumber();
            attempt++;
        } while(attempt < URI_OBTAIN_MAX_RETRY);
        fileName.appendToName("_" + UtilsString.randomBase49(URI_RANDOM_HEX_LENGTH)).setNumber(null);
        java.io.File mediaFile = new java.io.File(directory, fileName.getFullName());
        if(mediaFile.createNewFile()){
            return new java.io.File(directory, fileName.getFullName());
        } else {
            return null;
        }
    } catch(Throwable e){
DebugException.start().log(e).end();
        return null;
    }
}
public static java.io.File obtainClosest(String directoryPath, String fileFullName){
    return obtainClosest(new java.io.File(directoryPath), fileFullName);
}
public static java.io.File obtainClosest(java.io.File directory, String fileFullName){
    java.io.File file = findFile(directory, fileFullName);
    if(file != null){
        return file;
    }
    file = obtain(directory, fileFullName);
    if(file != null){
        return file;
    }
    List<String> fileNames = selectFileLike(directory, fileFullName, true);
    List<String> fileNamesSorted = null;
    if(fileNames != null){
        fileNamesSorted = UtilsList.sort(fileNames, new ComparatorW<String>(){
            @Override
            public int compare(String s1, String s2){
                Integer n1 = UtilsFile.FileName.getNumber(s1);
                Integer n2 = UtilsFile.FileName.getNumber(s2);
                if((n1 != null) && (n2 != null)){
                    return Integer.compare(n1, n2);
                } else if(n1 != null){
                    return 1;
                } else {
                    return -1;
                }
            }
        });
    }
    try{
        if(!directory.exists() && !directory.mkdirs()){
            throw new Throwable("directory can't be created");
        }
        Integer biggerNumber = null;
        if(fileNamesSorted != null){
            for(String s: fileNamesSorted){
                java.io.File mediaFile = new java.io.File(directory, s);
                if(mediaFile.canWrite()){
                    return new java.io.File(directory, s);
                }
            }
            biggerNumber = UtilsFile.FileName.getNumber(fileNamesSorted.get(fileNamesSorted.size() - 1));
            if((biggerNumber == null) || (biggerNumber < 0)){
                biggerNumber = 0;
            }
        }
        int attempt = 0;
        UtilsFile.FileName fileName = new UtilsFile.FileName(fileFullName);
        if(biggerNumber == null){
            fileName.setNumber(null);
        } else {
            fileName.setNumber(biggerNumber + 1);
        }
        try{
            do{
                java.io.File mediaFile = new java.io.File(directory, fileName.getFullName());
                if(mediaFile.createNewFile()){
                    return new java.io.File(directory, fileName.getFullName());
                }
                fileName.incNumber();
                attempt++;
            } while(attempt < URI_OBTAIN_MAX_RETRY);
        } catch(Throwable e){
        }
        fileName.appendToName("_" + UtilsString.randomBase49(URI_RANDOM_HEX_LENGTH)).setNumber(null);
        java.io.File mediaFile = new java.io.File(directory, fileName.getFullName());
        if(mediaFile.createNewFile()){
            return new java.io.File(directory, fileName.getFullName());
        } else {
            return null;
        }
    } catch(Throwable e){
        return null;
    }

}
public static java.io.File findFile(String directoryPath, String fileFullName){
    return findFile(new java.io.File(directoryPath), fileFullName);
}
public static java.io.File findFile(java.io.File directory, String fileFullName){
    if(!directory.exists()){
        return null;
    }
    java.io.File mediaFile = new java.io.File(directory, fileFullName);
    if(!mediaFile.exists()){
        return null;
    }
    if(!mediaFile.canWrite()){
        return null;
    } else {
        return mediaFile;
    }
}

public static File obtainFile(Directory directory, String fileFullName){
    return obtainFile(directory.getPath(), fileFullName);
}
public static File obtainFile(String directoryPath, String fileFullName){
    java.io.File file = obtain(directoryPath, fileFullName);
    try{
        if(file != null){
            return new File(file);
        } else {
            return null;
        }
    } catch(Throwable e){
        file.delete();
        return null;
    }
}
public static File obtainUniqueFile(Directory directory, String fileFullName){
    return obtainUniqueFile(directory.getPath(), fileFullName);
}
public static File obtainUniqueFile(String directoryPath, String fileFullName){
    java.io.File file = obtainUnique(directoryPath, fileFullName);
    try{
        if(file != null){
            return new File(file);
        } else {
            return null;
        }
    } catch(Throwable e){
        file.delete();
        return null;
    }
}
public static File obtainClosestFile(Directory directory, String fileFullName){
    return obtainClosestFile(directory.getPath(), fileFullName);
}
public static File obtainClosestFile(String directoryPath, String fileFullName){
    java.io.File file = obtainClosest(directoryPath, fileFullName);
    try{
        if(file != null){
            return new File(file);
        } else {
            return null;
        }
    } catch(Throwable e){
        file.delete();
        return null;
    }
}


}

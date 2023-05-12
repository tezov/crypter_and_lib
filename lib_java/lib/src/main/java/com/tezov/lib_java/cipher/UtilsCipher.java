/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java.cipher;

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
import java.util.Enumeration;

import com.tezov.lib_java.debug.DebugLog;

import org.bouncycastle.jce.ECNamedCurveTable;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.security.Provider;
import java.security.Security;
import java.util.Set;

public class UtilsCipher{

public static void toDebugLogAlgorithmRSA(){
    toDebugLogAlgorithm("RSA");
}
public static void toDebugLogAlgorithmAES(){
    toDebugLogAlgorithm("AES");
}
public static void toDebugLogAlgorithmDES(){
    toDebugLogAlgorithm("DES");
}
public static void toDebugLogAlgorithm(){
    toDebugLogAlgorithm(null);
}
public static void toDebugLogAlgorithm(String startWidth){
    for(Provider provider: Security.getProviders()){
DebugLog.start().send("*************** " + provider.getName()).end();
        Set<Provider.Service> services = provider.getServices();
        for(Provider.Service service: services){
            if((startWidth == null) || service.getAlgorithm().startsWith(startWidth)){
DebugLog.start().send(service.getAlgorithm()).end();
            }
        }
    }
}

public static void toDebugLogCurveName(){
    toDebugLogCurveName(null);
}
public static void toDebugLogCurveName(String startWidth){
    Enumeration a = ECNamedCurveTable.getNames();
    while(a.hasMoreElements()){
        String name = (String)a.nextElement();
        if((startWidth == null) || name.startsWith(startWidth)){
DebugLog.start().send(name).end();
        }
    }
}

}

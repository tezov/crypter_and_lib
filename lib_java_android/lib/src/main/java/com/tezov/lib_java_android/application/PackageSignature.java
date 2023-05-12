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
import androidx.annotation.RequiresApi;

import java.util.Collections;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.content.pm.SigningInfo;
import android.os.Build;

import com.tezov.lib_java_android.BuildConfig;
import com.tezov.lib_java.toolbox.Nullify;
import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java.cipher.UtilsMessageDigest;
import com.tezov.lib_java.cipher.UtilsMessageDigest.Mode;

import java.util.ArrayList;
import java.util.List;

public class PackageSignature{

private static String signatureDigest(Mode mode, android.content.pm.Signature signature){
    return UtilsMessageDigest.digestToStringHex(mode, signature.toByteArray());
}
private static List<String> signatureDigest(Mode mode, android.content.pm.Signature[] sigList){
    List<String> signaturesList = new ArrayList<>();
    for(android.content.pm.Signature signature: sigList){
        if(signature != null){
            signaturesList.add(signatureDigest(mode, signature));
        }
    }
    return signaturesList;
}
public static List<String> select(Mode mode){
    return select(mode, AppContext.getPackageName());
}

public static List<String> select(Mode mode, String packageName){
    if(VersionSDK.isSupEqualTo28_P()){
        return select_after28_P(mode, packageName);
    } else {
        return select_before28_R(mode, packageName);
    }
}
@RequiresApi(api = Build.VERSION_CODES.P)
private static List<String> select_after28_P(Mode mode, String packageName){
    try{
        PackageManager pm = AppContext.getPackageManager();
        PackageInfo packageInfo = pm.getPackageInfo(packageName, PackageManager.GET_SIGNING_CERTIFICATES);
        if(packageInfo == null){
            return null;
        }
        SigningInfo signingInfo = packageInfo.signingInfo;
        if(signingInfo == null){
            return null;
        }
        Signature[] signatures;
        if(packageInfo.signingInfo.hasMultipleSigners()){
            signatures = signingInfo.getApkContentsSigners();
        } else {
            signatures = signingInfo.getSigningCertificateHistory();
        }
        if((signatures.length == 0) || (signatures[0] == null)){
            return null;
        }
        return Nullify.collection(signatureDigest(mode, signatures));

    } catch(PackageManager.NameNotFoundException e){
        return null;
    }
}
@SuppressWarnings("deprecation")
private static List<String> select_before28_R(Mode mode, String packageName){
    try{
        PackageManager pm = AppContext.getPackageManager();
        PackageInfo packageInfo = pm.getPackageInfo(packageName, PackageManager.GET_SIGNATURES);
        if(packageInfo == null){
            return null;
        }
        if((packageInfo.signatures == null) || (packageInfo.signatures.length == 0) || (packageInfo.signatures[0] == null)){
            return null;
        }
        return Nullify.collection(signatureDigest(mode, packageInfo.signatures));
    } catch(PackageManager.NameNotFoundException e){
        return null;
    }
}

public static String get(Mode mode){
    return get(mode, AppContext.getPackageName());
}
public static String get(Mode mode, String packageName){
    List<String> signatures = select(mode, packageName);
    if(signatures == null){
        return null;
    }

    if(signatures.size() > 1){
DebugException.start().log("more than 1 signature(" + signatures.size() + ")").end();
    }

    return signatures.get(0);
}

public static String getFingerPrint(){
    String fp;
    if(BuildConfig.DEBUG_ONLY){
        fp = com.tezov.lib_java.application.AppConfig.getString(AppConfigKey.FINGER_PRINT_SHA1_DEV.getId());
    }
    else{
        fp = AppConfig.getString(AppConfigKey.FINGER_PRINT_SHA1_PLAYSTORE.getId());
    }
    if(fp != null){
        fp = fp.replace(":", "");
    }
    else{
        fp = get(Mode.SHA1);
    }
    return fp;
}

public static boolean matchesFingerPrint(){
    return matchesOne(Mode.SHA1, Collections.singletonList(getFingerPrint()), AppContext.getPackageName());
}
public static boolean matchesFingerPrint(String packageNameToCheck){
    return matchesOne(Mode.SHA1, getFingerPrint(), packageNameToCheck);
}
public static boolean matchesOne(Mode mode, String packageNameApproved, String packageNameToCheck){
    if(packageNameApproved == null){
        return false;
    } else {
        return matchesOne(mode, select(mode, packageNameApproved), packageNameToCheck);
    }
}
private static boolean matchesOne(Mode mode, List<String> packageNameApprovedSignatures, String packageNameToCheck){
    if((packageNameApprovedSignatures == null) || (packageNameToCheck == null)){
        return false;
    }
    List<String> packageNameToCheckSignatures = select(mode, packageNameToCheck);
    if(packageNameToCheckSignatures == null){
        return false;
    }
    for(String signatureToCheck: packageNameToCheckSignatures){
        if(packageNameApprovedSignatures.contains(signatureToCheck)){
            return true;
        }
    }
    return false;
}

public static boolean matchesAll(Mode mode, String packageNameApproved, String packageNameToCheck){
    if(packageNameApproved == null){
        return false;
    } else {
        return matchesAll(mode, select(mode, packageNameApproved), packageNameToCheck);
    }
}
public static boolean matchesAll(Mode mode, List<String> packageNameApprovedSignatures, String packageNameToCheck){
    if((packageNameApprovedSignatures == null) || (packageNameToCheck == null)){
        return false;
    }
    List<String> packageNameToCheckSignatures = select(mode, packageNameToCheck);
    if(packageNameToCheckSignatures == null){
        return false;
    }
    if(packageNameApprovedSignatures.size() != packageNameToCheckSignatures.size()){
        return false;
    }
    for(String signatureToCheck: packageNameToCheckSignatures){
        if(!packageNameApprovedSignatures.contains(signatureToCheck)){
            return false;
        }
    }
    return true;
}

}

/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java.cipher;

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
import java.security.MessageDigest;
import java.security.NoSuchProviderException;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;

import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.security.Security;

import javax.crypto.Cipher;
import javax.crypto.KeyAgreement;
import javax.crypto.Mac;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKeyFactory;

public class SecureProvider{
public final static String ANDROID = null;
public final static String BOUNCY_CASTLE = "BC";
public static final String DEFAULT_SECURE_RANDOM_ALGO = "SHA1PRNG";

private static String provider = ANDROID;
public static void init(String provider){
    if(BOUNCY_CASTLE.equals(provider)){
        Security.removeProvider(BOUNCY_CASTLE);
        Security.insertProviderAt(new BouncyCastleProvider(), 1);
    }
    SecureProvider.provider = provider;
}

public static SecureRandom randomGenerator() throws NoSuchAlgorithmException{
    return randomGenerator(DEFAULT_SECURE_RANDOM_ALGO);
}
public static SecureRandom randomGenerator(String algo) throws NoSuchAlgorithmException{
    return SecureRandom.getInstance(algo);
}
public static SecretKeyFactory keyFactory(String algo) throws NoSuchAlgorithmException, NoSuchProviderException{
    if(provider != null){
        return SecretKeyFactory.getInstance(algo, provider);
    }
    else {
        return SecretKeyFactory.getInstance(algo);
    }
}
public static KeyFactory keyPairFactory(String algo) throws NoSuchAlgorithmException, NoSuchProviderException{
    if(provider != null){
        return KeyFactory.getInstance(algo, provider);
    }
    else {
        return KeyFactory.getInstance(algo);
    }
}
public static KeyPairGenerator keyPairGenerator(String algo) throws NoSuchAlgorithmException, NoSuchProviderException{
    if(provider != null){
        return KeyPairGenerator.getInstance(algo, provider);
    }
    else {
        return KeyPairGenerator.getInstance(algo);
    }
}
public static KeyAgreement keyAgreement(String algo) throws NoSuchAlgorithmException, NoSuchProviderException{
    if(provider != null){
        return KeyAgreement.getInstance(algo, provider);
    }
    else {
        return KeyAgreement.getInstance(algo);
    }
}
public static Cipher cipher(String algo) throws NoSuchPaddingException, NoSuchAlgorithmException, NoSuchProviderException{
    if(provider != null){
        return Cipher.getInstance(algo, provider);
    }
    else {
        return Cipher.getInstance(algo);
    }
}
public static Mac mac(String algo) throws NoSuchAlgorithmException, NoSuchProviderException{
    if(provider != null){
        return Mac.getInstance(algo, provider);
    }
    else {
        return Mac.getInstance(algo);
    }
}
public static MessageDigest messageDigest(String algo) throws NoSuchAlgorithmException, NoSuchProviderException{
    if(provider != null){
        return MessageDigest.getInstance(algo, provider);
    }
    else {
        return MessageDigest.getInstance(algo);
    }
}

}

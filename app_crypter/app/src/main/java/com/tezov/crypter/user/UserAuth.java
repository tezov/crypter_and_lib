/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.crypter.user;

import com.tezov.lib_java.debug.DebugLog;
import com.tezov.lib_java.debug.DebugTrack;
import com.tezov.lib_java.type.primitive.ObjectTo;
import com.tezov.lib_java.type.primitive.IntTo;
import com.tezov.lib_java.type.unit.UnitByte;
import com.tezov.lib_java.toolbox.CompareType;
import com.tezov.lib_java.util.UtilsString;
import java.util.List;
import java.util.LinkedList;
import java.util.Set;
import com.tezov.lib_java_android.database.sqlLite.filter.dbFilterOrder;
import com.tezov.lib_java_android.database.sqlLite.filter.chunk.ChunkCommand;
import androidx.fragment.app.Fragment;
import static com.tezov.crypter.application.SharePreferenceKey.SP_ALIAS_FORGET_BOOLEAN;
import static com.tezov.crypter.application.SharePreferenceKey.SP_CIPHER_REMEMBER_ALIAS_UID_BYTES;
import static com.tezov.crypter.application.SharePreferenceKey.SP_KEYSTORE_AUTO_CLOSE_DELAY_INT;
import static com.tezov.crypter.application.SharePreferenceKey.SP_KEYSTORE_KEEP_OPEN_BOOLEAN;
import static com.tezov.crypter.application.SharePreferenceKey.SP_KEYSTORE_KEEP_OPEN_DELAY_INT;
import static com.tezov.crypter.application.SharePreferenceKey.SP_KEYSTORE_SPEC_STRING;
import static com.tezov.crypter.application.SharePreferenceKey.SP_KEYSTORE_TIMESTAMP_LONG;
import static com.tezov.crypter.application.SharePreferenceKey.SP_KEYSTORE_TRIAL_BYTE;
import static com.tezov.crypter.application.SharePreferenceKey.SP_SESSION_DATA_BYTE;
import static com.tezov.crypter.application.SharePreferenceKey.SP_SESSION_TIMESTAMP_LONG;
import static com.tezov.crypter.application.SharePreferenceKey.SP_SESSION_UID_BYTE;

import com.tezov.crypter.application.AppInfo;
import com.tezov.crypter.application.Application;
import com.tezov.lib_java_android.application.SharedPreferences;
import com.tezov.lib_java.async.notifier.task.TaskValue;
import com.tezov.lib_java_android.authentification.defAuthMethod;
import com.tezov.lib_java.cipher.dataInput.EncoderBytes;
import com.tezov.lib_java.cipher.dataInput.EncoderBytesCipher;
import com.tezov.lib_java.cipher.dataOuput.DecoderBytes;
import com.tezov.lib_java.cipher.dataOuput.DecoderBytesCipher;
import com.tezov.lib_java.cipher.definition.defDecoderBytes;
import com.tezov.lib_java.cipher.definition.defEncoderBytes;
import com.tezov.lib_java.cipher.key.KeyMutual;
import com.tezov.lib_java.cipher.key.KeySim;
import com.tezov.lib_java.cipher.misc.PasswordCipher;
import com.tezov.lib_java.generator.uid.UUID;
import com.tezov.lib_java.toolbox.Clock;
import com.tezov.lib_java.toolbox.Compare;
import com.tezov.lib_java.debug.DebugException;

import java.util.concurrent.TimeUnit;

public class UserAuth extends com.tezov.lib_java_android.authentification.UserAuth{

public UserAuth(){
    setAuthMethod(new AuthMethod());
}
public static boolean hasKeystore(){
    return Application.sharedPreferences().getString(SP_KEYSTORE_SPEC_STRING) != null;
}
public static boolean isKeystoreOpened(){
    return Application.tableHolderCipher().isOpen();
}
public static boolean isKeystoreCanReOpen(){
    SharedPreferences sp = Application.sharedPreferences();
    if(!sp.hasKey(SP_SESSION_UID_BYTE)){
        return false;
    }
    Long timestampSession = sp.getLong(SP_SESSION_TIMESTAMP_LONG);
    if(timestampSession == null){
        return false;
    }
    long now = Clock.MilliSecond.now();
    boolean canReopen = Compare.isTrue(sp.getBoolean(SP_KEYSTORE_KEEP_OPEN_BOOLEAN));
    if(!canReopen){
        Integer delayMinutes = sp.getInt(SP_KEYSTORE_AUTO_CLOSE_DELAY_INT);
        if(delayMinutes != null){
            long delay_ms = TimeUnit.MILLISECONDS.convert(delayMinutes, TimeUnit.MINUTES);
            long timestampAutoCloseDelay = timestampSession + delay_ms;
            canReopen = (now < timestampAutoCloseDelay);
        }
    } else {
        Integer delayJours = sp.getInt(SP_KEYSTORE_KEEP_OPEN_DELAY_INT);
        if(delayJours != null){
            long delay_ms = TimeUnit.MILLISECONDS.convert(delayJours, TimeUnit.DAYS);
            long timestampKeepOpenDelay = timestampSession + delay_ms;
            if(now >= timestampKeepOpenDelay){
                Integer delayMinutes = sp.getInt(SP_KEYSTORE_AUTO_CLOSE_DELAY_INT);
                if(delayMinutes != null){
                    delay_ms = TimeUnit.MILLISECONDS.convert(delayMinutes, TimeUnit.MINUTES);
                    long timestampAutoCloseDelay = timestampSession + delay_ms;
                    canReopen = (now < timestampAutoCloseDelay);
                }
            }
        }
    }
    return canReopen;
}
public static void updateSessionTimestamp(){
    SharedPreferences sp = Application.sharedPreferences();
    sp.put(SP_SESSION_TIMESTAMP_LONG, Clock.MilliSecond.now());
}
public static void deleteKeystore(){
    SharedPreferences sp = Application.sharedPreferences();
    sp.remove(SP_KEYSTORE_SPEC_STRING);
    sp.remove(SP_KEYSTORE_TIMESTAMP_LONG);
    sp.remove(SP_KEYSTORE_TRIAL_BYTE);
    sp.remove(SP_SESSION_TIMESTAMP_LONG);
    sp.remove(SP_SESSION_UID_BYTE);
    sp.remove(SP_SESSION_DATA_BYTE);
    sp.remove(SP_CIPHER_REMEMBER_ALIAS_UID_BYTES);
    Application.tableHolder().delete();
    Application.tableHolderCipher().delete();
}
public static boolean checkCredential(PasswordCipher password){
    SharedPreferences sp = Application.sharedPreferences();
    String keystoreSpec = sp.getString(SP_KEYSTORE_SPEC_STRING);
    if(keystoreSpec != null){
        KeyMutual userKey = KeyMutual.fromSpec(password, keystoreSpec, false);
        return checkCredential(userKey);
    } else {
        return false;
    }
}
private static boolean checkCredential(KeyMutual userKey){
    SharedPreferences sp = Application.sharedPreferences();
    byte[] trialEncoded = sp.getBytes(SP_KEYSTORE_TRIAL_BYTE);
    defDecoderBytes decoder = DecoderBytes.newDecoder(userKey);
    byte[] trialDecoded = decoder.decode(trialEncoded);
    return Compare.equalsAndNotNull(trialDecoded, userKey.getId());
}
@Override
public AuthMethod getAuthMethod(){
    return super.getAuthMethod();
}
@Override
protected TaskValue<defAuthMethod.State.Is>.Observable afterSignIn(defAuthMethod.State.Is state, Throwable e){
    TaskValue<defAuthMethod.State.Is> task = new TaskValue<>();
    if(state == defAuthMethod.State.SIGN_IN_CONFIRMED){
        setUser();
        UserAuth.updateSessionTimestamp();
        e = Application.tableHolderCipher().open();
        if(e == null){
            task.notifyComplete(defAuthMethod.State.SIGN_IN_CONFIRMED);
        } else {
            task.notifyException(defAuthMethod.State.SIGN_IN_FAILED, (Throwable)null);
        }
    } else {
        task.notifyException(state, (Throwable)null);
    }
    return task.getObservable();
}
@Override
protected TaskValue<defAuthMethod.State.Is>.Observable afterSignUp(defAuthMethod.State.Is state, Throwable e){
DebugException.start().notImplemented().end();
    return null;
}
@Override
protected TaskValue<defAuthMethod.State.Is>.Observable afterSignOut(){
    setUser(null);
    Throwable e = Application.tableHolderCipher().close();
    SharedPreferences sp = Application.sharedPreferences();
    sp.remove(SP_SESSION_UID_BYTE);
    sp.remove(SP_SESSION_DATA_BYTE);
    sp.remove(SP_SESSION_TIMESTAMP_LONG);
    if(Compare.isTrue(sp.getBoolean(SP_ALIAS_FORGET_BOOLEAN))){
        sp.remove(SP_CIPHER_REMEMBER_ALIAS_UID_BYTES);
    }
    if(e != null){
DebugException.start().log(e).end();
    }
    return TaskValue.Complete(defAuthMethod.State.SIGN_OUT_CONFIRMED);
}
private void setUser(){
    SharedPreferences sp = Application.sharedPreferences();
    setUser(new User.Builder(getAuthMethod().getUserKey()).setTimestamp(sp.getLong(SP_KEYSTORE_TIMESTAMP_LONG)).build());
}
public static class AuthMethod implements defAuthMethod{
    private KeyMutual userKey = null;
    public KeyMutual getUserKey(){
        return userKey;
    }
    @Override
    public TaskValue<State.Is>.Observable signOut(){
        if(userKey != null){
            userKey.destroyNoThrow();
            userKey = null;
        }
        return TaskValue.Complete(State.SIGN_OUT_CONFIRMED);
    }
    @Override
    public TaskValue<State.Is>.Observable signInWithToken(PasswordCipher token, long timeout, TimeUnit timeUnit){
        TaskValue<State.Is> task = new TaskValue<>();
        KeyMutual userKey;
        if(token == null){
            userKey = userKeyRetrieve();
        } else {
            SharedPreferences sp = Application.sharedPreferences();
            String keystoreSpec = sp.getString(SP_KEYSTORE_SPEC_STRING);
            if(keystoreSpec == null){
                userKey = new KeyMutual().generate(token, KeySim.Transformation.AES_GCM_NO_PAD, KeySim.Length.L256);
                sp.put(SP_KEYSTORE_SPEC_STRING, userKey.specToStringBase64());
                sp.put(SP_KEYSTORE_TIMESTAMP_LONG, Clock.MilliSecond.now());
                defEncoderBytes encoder = EncoderBytes.newEncoder(userKey);
                sp.put(SP_KEYSTORE_TRIAL_BYTE, encoder.encode(userKey.getId()));
            } else {
                userKey = KeyMutual.fromSpec(token, keystoreSpec, false);
            }
        }
        signIn(task, userKey);
        return task.getObservable();
    }
    public KeyMutual userKeyRetrieve(){
        boolean museRetrieve = UserAuth.isKeystoreCanReOpen();
        if(!museRetrieve){
            return null;
        }
        SharedPreferences sp = Application.sharedPreferences();
        byte[] sessionUidBytes = sp.getBytes(SP_SESSION_UID_BYTE);
        if(sessionUidBytes == null){
            return null;
        }
        byte[] sessionDataBytes = sp.getBytes(SP_SESSION_DATA_BYTE);
        if(sessionDataBytes == null){
            return null;
        }
        KeyMutual sessionKey = AppInfo.getKeyMutual(PasswordCipher.fromClear(UUID.fromBytes(sessionUidBytes).toHexString().toCharArray()));
        defDecoderBytes decoder = new DecoderBytesCipher(sessionKey);
        byte[] sessionDataBytesDecoded = decoder.decode(sessionDataBytes);
        if(sessionDataBytesDecoded == null){
            return null;
        }
        return KeyMutual.fromKey(sessionDataBytesDecoded);
    }
    public void signIn(TaskValue<State.Is> task, KeyMutual userKey){
        if(userKey == null){
            task.notifyException(State.SIGN_IN_FAILED, (Throwable)null);
            return;
        }
        if(!checkCredential(userKey)){
            task.notifyException(State.SIGN_IN_FAILED_WRONG_CREDENTIAL, (Throwable)null);
        } else {
            SharedPreferences sp = Application.sharedPreferences();
            UUID uid = Application.getState().sessionUid();
            KeyMutual sessionKey = AppInfo.getKeyMutual(PasswordCipher.fromClear(uid.toHexString().toCharArray()));
            defEncoderBytes encoder = new EncoderBytesCipher(sessionKey);
            byte[] userKeyEncrypted = encoder.encode(userKey.keyToBytes());
            sp.put(SP_SESSION_DATA_BYTE, userKeyEncrypted);
            sp.put(SP_SESSION_UID_BYTE, uid.toBytes());
            this.userKey = userKey;
            userKey.erasePrivateData();
            task.notifyComplete(State.SIGN_IN_CONFIRMED);
        }
    }
    @Override
    public TaskValue<State.Is>.Observable signUpWithToken(PasswordCipher token, long timeout, TimeUnit timeUnit){
DebugException.start().notImplemented().end();
        return null;
    }
    @Override
    public TaskValue<State.Is>.Observable signInWithEmailAndPassword(String email, PasswordCipher password, long timeout, TimeUnit timeUnit){
DebugException.start().notImplemented().end();
        return null;
    }
    @Override
    public TaskValue<State.Is>.Observable signUpWithEmailAndPassword(String email, PasswordCipher password, long timeout, TimeUnit timeUnit){
DebugException.start().notImplemented().end();
        return null;
    }
    @Override
    public TaskValue<State.Is>.Observable passwordRecoveryWithEmail(String email, long timeout, TimeUnit timeUnit){
DebugException.start().notImplemented().end();
        return null;
    }

}

}

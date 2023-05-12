/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java_android.authentification;

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

import com.tezov.lib_java_android.application.AppConfigKey;
import com.tezov.lib_java_android.application.AppConfig;
import com.tezov.lib_java.cipher.misc.PasswordCipher;
import com.tezov.lib_java.async.notifier.task.TaskValue;
import com.tezov.lib_java.type.defEnum.EnumBase;

import java.util.concurrent.TimeUnit;

public interface defAuthMethod{
long AUTH_TIMEOUT_DELAY_ms = AppConfig.getLong(AppConfigKey.AUTH_TIMEOUT_DELAY_ms.getId());

TaskValue<State.Is>.Observable signOut();

default TaskValue<State.Is>.Observable signInWithToken(PasswordCipher token){
    return signInWithToken(token, AUTH_TIMEOUT_DELAY_ms);
}
default TaskValue<State.Is>.Observable signInWithToken(PasswordCipher token, long timeout_ms){
    return signInWithToken(token, timeout_ms, TimeUnit.MILLISECONDS);
}
TaskValue<State.Is>.Observable signInWithToken(PasswordCipher token, long timeout, TimeUnit timeUnit);

default TaskValue<State.Is>.Observable signUpWithToken(PasswordCipher token){
    return signUpWithToken(token, AUTH_TIMEOUT_DELAY_ms);
}
default TaskValue<State.Is>.Observable signUpWithToken(PasswordCipher token, long timeout_ms){
    return signUpWithToken(token, timeout_ms, TimeUnit.MILLISECONDS);
}
TaskValue<State.Is>.Observable signUpWithToken(PasswordCipher token, long timeout, TimeUnit timeUnit);

default TaskValue<State.Is>.Observable signInWithEmailAndPassword(String email, PasswordCipher password){
    return signInWithEmailAndPassword(email, password, AUTH_TIMEOUT_DELAY_ms);
}
default TaskValue<State.Is>.Observable signInWithEmailAndPassword(String email, PasswordCipher password, long timeout_ms){
    return signInWithEmailAndPassword(email, password, timeout_ms, TimeUnit.MILLISECONDS);
}
TaskValue<State.Is>.Observable signInWithEmailAndPassword(String email, PasswordCipher password, long timeout, TimeUnit timeUnit);

default TaskValue<State.Is>.Observable signUpWithEmailAndPassword(String email, PasswordCipher password){
    return signUpWithEmailAndPassword(email, password, AUTH_TIMEOUT_DELAY_ms);
}
default TaskValue<State.Is>.Observable signUpWithEmailAndPassword(String email, PasswordCipher password, long timeout_ms){
    return signUpWithEmailAndPassword(email, password, timeout_ms, TimeUnit.MILLISECONDS);
}
TaskValue<State.Is>.Observable signUpWithEmailAndPassword(String email, PasswordCipher password, long timeout, TimeUnit timeUnit);

default TaskValue<State.Is>.Observable passwordRecoveryWithEmail(String email){
    return passwordRecoveryWithEmail(email, AUTH_TIMEOUT_DELAY_ms);
}
default TaskValue<State.Is>.Observable passwordRecoveryWithEmail(String email, long timeout_ms){
    return passwordRecoveryWithEmail(email, timeout_ms, TimeUnit.MILLISECONDS);
}
TaskValue<State.Is>.Observable passwordRecoveryWithEmail(String email, long timeout, TimeUnit timeUnit);

interface State{
    Is ONLINE = new Is("ONLINE");
    Is OFFLINE = new Is("OFFLINE");
    Is SIGN_OUT_CONFIRMED = new Is("SIGN_OUT_CONFIRMED");
    Is SIGN_OUT_FAILED = new Is("SIGN_OUT_FAILED");
    Is SIGN_IN_CONFIRMED = new Is("SIGN_IN_CONFIRMED");
    Is SIGN_IN_FAILED = new Is("SIGN_IN_FAILED");
    Is SIGN_IN_FAILED_WRONG_CREDENTIAL = new Is("SIGN_IN_FAILED_WRONG_CREDENTIAL");
    Is SIGN_IN_FAILED_EMAIL_UNKNOWN = new Is("SIGN_IN_FAILED_EMAIL_UNKNOWN");
    Is SIGN_UP_CONFIRMED = new Is("SIGN_UP_CONFIRMED");
    Is SIGN_UP_FAILED_EMAIL_COLLISION = new Is("SIGN_UP_FAILED_EMAIL_COLLISION");
    Is RECOVERY_EMAIL_SENT = new Is("RECOVERY_EMAIL_SENT");
    Is RECOVERY_FAILED_EMAIL_UNKNOWN = new Is("RECOVERY_FAILED_EMAIL_UNKNOWN");
    Is NETWORK_ERROR = new Is("NETWORK_ERROR");
    Is UNKNOWN_ERROR = new Is("UNKNOWN_ERROR");
    Is TOO_MANY_REQUEST = new Is("TOO_MANY_REQUEST");
    Is TIME_OUT = new Is("TIME_OUT");

    class Is extends EnumBase.Is{
        public Is(String value){
            super(value);
        }

    }

}

}

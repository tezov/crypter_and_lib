/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java_android.database.firebase;

import com.tezov.lib_java.debug.DebugLog;
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

import static com.tezov.lib_java_android.authentification.defAuthMethod.State.OFFLINE;
import static com.tezov.lib_java_android.authentification.defAuthMethod.State.ONLINE;
import static com.tezov.lib_java_android.authentification.defAuthMethod.State.SIGN_IN_CONFIRMED;
import static com.tezov.lib_java_android.authentification.defAuthMethod.State.SIGN_OUT_CONFIRMED;
import static com.tezov.lib_java_android.authentification.defAuthMethod.State.SIGN_UP_CONFIRMED;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.tezov.lib_java_android.application.AppConfigKey;
import com.tezov.lib_java_android.application.AppConfig;
import com.tezov.lib_java.async.notifier.Notifier;
import com.tezov.lib_java.async.notifier.Subscription;
import com.tezov.lib_java.async.notifier.observable.ObservableValue;
import com.tezov.lib_java.async.notifier.observer.value.ObserverValue;
import com.tezov.lib_java.async.notifier.observer.value.ObserverValueE;
import com.tezov.lib_java.async.notifier.task.TaskValue;
import com.tezov.lib_java_android.authentification.defAuthMethod;
import com.tezov.lib_java.cipher.misc.PasswordCipher;
import com.tezov.lib_java_android.database.firebase.holder.fbContext;
import com.tezov.lib_java_android.toolbox.SingletonHolder;
import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java.debug.DebugTrack;
import com.tezov.lib_java_android.wrapperAnonymous.FireBaseValueEventListenerW;
import com.tezov.lib_java_android.wrapperAnonymous.FirebaseAuthStateListenerW;
import com.tezov.lib_java_android.wrapperAnonymous.TaskOnCompleteListenerW;
import com.tezov.lib_java.type.runnable.RunnableGroup;
import com.tezov.lib_java.type.runnable.RunnableTimeOut;
import com.tezov.lib_java.util.UtilsNull;

import java.util.concurrent.TimeUnit;

final public class fbAuthHelper implements defAuthMethod{
private final static long FB_AUTH_TIMEOUT_DELAY_ms = AppConfig.getLong(AppConfigKey.AUTH_TIMEOUT_DELAY_ms.getId());
private FirebaseAuth auth = null;
private Notifier<Void> notifier;
private Object user;
private boolean isOnline;

private fbAuthHelper(){
    try{
DebugTrack.start().create(this).end();
        notifier = new Notifier<>(new ObservableValue<State>(), true);
        user = UtilsNull.NULL_OBJECT;
        isOnline = false;
    } catch(java.lang.Throwable e){

DebugException.start().log(e).end();

    }
}
public static Class myClass(){
    return fbAuthHelper.class;
}
public static fbAuthHelper singleton(){
    return SingletonHolder.get(fbAuthHelper.class);
}
public static void singletonRelease(){
    SingletonHolder.release(fbAuthHelper.class);
}
public void init(fbContext context){
    if(auth == null){
        auth = context.getFireBaseAuthReference();
        DatabaseReference fbBaseConnected = context.getFireBaseInfoReference();
        fbBaseConnected.addValueEventListener(new FireBaseValueEventListenerW(){
            @Override
            public void onChange(@NonNull DataSnapshot dataSnapshot){
                isOnline = dataSnapshot.getValue(Boolean.class);
                if(isOnline){
                    post(ONLINE);
                } else {
                    post(OFFLINE);
                }
            }
            @Override
            public void onCancel(@NonNull DatabaseError databaseError){
DebugException.start().log(databaseError.toException()).end();
            }
        });
        updateUser();
        auth.addAuthStateListener(new FirebaseAuthStateListenerW(){
            @Override
            public void onChanged(@NonNull FirebaseAuth firebaseAuth){
                updateUser();
            }
        });
    }
}
public Subscription observe(ObserverValue<State.Is> observer){
    return notifier.register(observer);
}
public void unObserve(Object owner){
    notifier.unregister(owner);
}
public void unObserveAll(){
    notifier.unregisterAll();
}

private void post(State.Is state){
    ObservableValue<State.Is>.Access access = notifier.obtainAccess(this, null);
    access.setValue(state);
}

public boolean isOnline(){
    return isOnline;
}

private void updateUser(){
    Object userOld = user;
    user = auth.getCurrentUser();
    if(user != userOld){
        if(user != null){
            post(SIGN_IN_CONFIRMED);
        } else {
            post(SIGN_OUT_CONFIRMED);
        }
    }
}

public boolean isLogged(){
    updateUser();
    return (user != null);
}

private boolean isLogged(String email){
    updateUser();
    if(!isLogged()){
        return false;
    } else {
        return (email.equals(getUser().getEmail()));
    }
}

private FirebaseUser getUser(){
    return (FirebaseUser)user;
}

public String getUserEmail(){
    return getUser().getEmail();
}
public String getUserUID(){
    return getUser().getUid();
}

public boolean isAnonymous(){
    return (user != null) && getUser().isAnonymous();
}

@Override
public TaskValue<State.Is>.Observable signOut(){
    TaskValue<State.Is> task = new TaskValue();
    if(!isLogged()){
        task.notifyComplete(SIGN_OUT_CONFIRMED);
    } else {
        observe(new ObserverValue<fbAuthHelper.State.Is>(this){
            @Override
            public void onComplete(State.Is event){
                if(event == SIGN_OUT_CONFIRMED){
                    unsubscribe();
                    task.notifyComplete(SIGN_OUT_CONFIRMED);
                }
            }
        });
        auth.signOut();
    }
    return task.getObservable();
}

public TaskValue<State.Is>.Observable signInAnonymously(){
    return signInAnonymously(FB_AUTH_TIMEOUT_DELAY_ms, TimeUnit.MILLISECONDS);
}
public TaskValue<State.Is>.Observable signInAnonymously(long timeout){
    return signInAnonymously(timeout, TimeUnit.MILLISECONDS);
}
public TaskValue<State.Is>.Observable signInAnonymously(long timeout, TimeUnit timeUnit){
    TaskValue<State.Is> register = new TaskValue<>();
    RunnableGroup gr = new RunnableGroup(this).name("Sign In Anonymously");
    if(isLogged()){
        gr.add(new RunnableGroup.Action(){
            @Override
            public void runSafe(){
                signOut().observe(new ObserverValueE<State.Is>(this){
                    @Override
                    public void onComplete(State.Is state){
                        next();
                    }
                });
            }
        }.name("Sign Out"));
    }
    gr.add(new RunnableGroup.Action(){
        @Override
        public void runSafe(){
            auth.signInAnonymously().addOnCompleteListener(new OnCompleteListener<AuthResult>(this, register, timeout, timeUnit){
                @Override
                public void onComplete(){
                    updateUser();
                    putValue(SIGN_IN_CONFIRMED);
                    next();
                }
                @Override
                public void onException(Throwable e){
                    putException(e);
                    next();
                }
            }.create());
        }
    }.name("Sign In"));
    gr.setOnDone(new RunnableGroup.Action(){
        @Override
        public void runSafe(){
            Throwable e = getException();
            if(e != null){
                if(e instanceof FirebaseTooManyRequestsException){
                    register.notifyException(State.TOO_MANY_REQUEST, e);
                } else if(e instanceof FirebaseNetworkException){
                    register.notifyException(State.NETWORK_ERROR, e);
                } else if((e instanceof FirebaseException) && (e.getMessage() != null) && (e.getMessage().contains("Unable to resolve"))){
                    register.notifyException(State.NETWORK_ERROR, e);
                } else if(e instanceof FireBaseTimeoutException){
                    register.notifyException(State.TIME_OUT, e);
                } else {

DebugException.start().log(e).end();

                    register.notifyException(State.UNKNOWN_ERROR, e);
                }
            } else {
                register.notifyComplete(getValue());
            }
        }
    });
    gr.start();
    return register.getObservable();

}
@Override
public TaskValue<defAuthMethod.State.Is>.Observable signInWithToken(PasswordCipher token, long timeout, TimeUnit timeUnit){
DebugException.start().notImplemented().end();
    return null;
}
@Override
public TaskValue<defAuthMethod.State.Is>.Observable signUpWithToken(PasswordCipher token, long timeout, TimeUnit timeUnit){
DebugException.start().notImplemented().end();
    return null;
}
@Override
public TaskValue<State.Is>.Observable signInWithEmailAndPassword(String email, PasswordCipher password, long timeout, TimeUnit timeUnit){
    TaskValue<State.Is> register = new TaskValue<>();
    RunnableGroup gr = new RunnableGroup(this).name("Sign In With Email and Password");
    if(isLogged()){
        gr.add(new RunnableGroup.Action(){
            @Override
            public void runSafe(){
                signOut().observe(new ObserverValueE<State.Is>(this){
                    @Override
                    public void onComplete(State.Is state){
                        next();
                    }
                });
            }
        }.name("Sign Out"));
    }
    gr.add(new RunnableGroup.Action(){
        @Override
        public void runSafe(){
            auth.signInWithEmailAndPassword(email, new String(password.get())).addOnCompleteListener(new OnCompleteListener<AuthResult>(this, register, timeout, timeUnit){
                @Override
                public void onComplete(){
                    updateUser();
                    putValue(SIGN_IN_CONFIRMED);
                    next();
                }
                @Override
                public void onException(Throwable e){
                    putException(e);
                    next();
                }
            }.create());
        }
    }.name("Sign In"));
    gr.setOnDone(new RunnableGroup.Action(){
        @Override
        public void runSafe(){
            Throwable e = getException();
            if(e != null){
                if(e instanceof FirebaseAuthInvalidUserException){
                    register.notifyException(State.SIGN_IN_FAILED_EMAIL_UNKNOWN, e);
                } else if(e instanceof FirebaseAuthInvalidCredentialsException){
                    register.notifyException(State.SIGN_IN_FAILED_WRONG_CREDENTIAL, e);
                } else if(e instanceof FirebaseTooManyRequestsException){
                    register.notifyException(State.TOO_MANY_REQUEST, e);
                } else if(e instanceof FirebaseNetworkException){
                    register.notifyException(State.NETWORK_ERROR, e);
                } else if((e instanceof FirebaseException) && (e.getMessage() != null) && (e.getMessage().contains("Unable to resolve"))){
                    register.notifyException(State.NETWORK_ERROR, e);
                } else if(e instanceof FireBaseTimeoutException){
                    register.notifyException(State.TIME_OUT, e);
                } else {

DebugException.start().log(e).end();

                    register.notifyException(State.UNKNOWN_ERROR, e);
                }
            } else {
                register.notifyComplete(getValue());
            }
        }
    });
    gr.start();
    return register.getObservable();
}
@Override
public TaskValue<State.Is>.Observable signUpWithEmailAndPassword(String email, PasswordCipher password, long timeout, TimeUnit timeUnit){
    RunnableGroup gr = new RunnableGroup(this).name("Sign Up");
    if(isLogged()){
        gr.add(new RunnableGroup.Action(){
            @Override
            public void runSafe(){
                signOut().observe(new ObserverValue<fbAuthHelper.State.Is>(this){
                    @Override
                    public void onComplete(fbAuthHelper.State.Is state){
                        next();
                    }
                });
            }
        }.name("Sign Out"));
    }
    TaskValue<State.Is> register = new TaskValue<>();
    gr.add(new RunnableGroup.Action(){
        @Override
        public void runSafe(){
            auth.createUserWithEmailAndPassword(email, new String(password.get())).addOnCompleteListener(new OnCompleteListener<AuthResult>(this, register, timeout, timeUnit){
                @Override
                public void onComplete(){
                    updateUser();
                    putValue(SIGN_UP_CONFIRMED);
                    next();
                }

                @Override
                public void onException(java.lang.Throwable e){
                    putException(e);
                    next();
                }
            }.create());
        }
    }.name("Sign Up"));
    gr.setOnDone(new RunnableGroup.Action(){
        @Override
        public void runSafe(){
            Throwable e = getException();
            if(e != null){
                if(e instanceof FirebaseAuthUserCollisionException){
                    register.notifyException(State.SIGN_UP_FAILED_EMAIL_COLLISION, e);
                } else if(e instanceof FirebaseTooManyRequestsException){
                    register.notifyException(State.TOO_MANY_REQUEST, e);
                } else if(e instanceof FirebaseNetworkException){
                    register.notifyException(State.NETWORK_ERROR, e);
                } else if(e instanceof FireBaseTimeoutException){
                    register.notifyException(State.TIME_OUT, e);
                } else {

DebugException.start().log(DebugTrack.getFullSimpleName(e)).end();

                    register.notifyException(State.UNKNOWN_ERROR, e);
                }
            } else {
                register.notifyComplete(getValue());
            }
        }
    });
    gr.start();
    return register.getObservable();
}
@Override
public TaskValue<State.Is>.Observable passwordRecoveryWithEmail(String email, long timeout, TimeUnit timeUnit){
    TaskValue<State.Is> register = new TaskValue<>();
    auth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>(this, register, timeout, timeUnit){
        @Override
        public void onComplete(){
            register.notifyComplete(State.RECOVERY_EMAIL_SENT);
        }

        @Override
        public void onException(java.lang.Throwable e){
            if(e instanceof FirebaseAuthInvalidUserException){
                register.notifyException(State.RECOVERY_FAILED_EMAIL_UNKNOWN, e);
            } else if(e instanceof FirebaseTooManyRequestsException){
                register.notifyException(State.TOO_MANY_REQUEST, e);
            } else if(e instanceof FirebaseNetworkException){
                register.notifyException(State.NETWORK_ERROR, e);
            } else if(e instanceof FireBaseTimeoutException){
                register.notifyException(State.TIME_OUT, e);
            } else {
DebugException.start().log(DebugTrack.getFullSimpleName(e)).end();
                register.notifyException(State.UNKNOWN_ERROR, e);
            }
        }
    }.create());
    return register.getObservable();
}

public TaskValue<State.Is>.Observable updateEmail(String email){
    return updateEmail(email, FB_AUTH_TIMEOUT_DELAY_ms, TimeUnit.MILLISECONDS);
}
public TaskValue<State.Is>.Observable updateEmail(String email, long timeout){
    return updateEmail(email, timeout, TimeUnit.MILLISECONDS);
}
public TaskValue<State.Is>.Observable updateEmail(String email, long timeout, TimeUnit timeUnit){
    TaskValue<State.Is> register = new TaskValue<>();
    getUser().updateEmail(email).addOnCompleteListener(new OnCompleteListener<Void>(this, register, timeout, timeUnit){
        @Override
        public void onComplete(){
            updateUser();
            register.notifyComplete(State.UPDATE_EMAIL_DONE);
        }

        @Override
        public void onException(java.lang.Throwable e){
            if(e instanceof FirebaseTooManyRequestsException){
                register.notifyException(State.TOO_MANY_REQUEST, e);
            } else if(e instanceof FirebaseNetworkException){
                register.notifyException(State.NETWORK_ERROR, e);
            } else if(e instanceof FireBaseTimeoutException){
                register.notifyException(State.TIME_OUT, e);
            } else {

DebugException.start().log(DebugTrack.getFullSimpleName(e)).end();


                register.notifyException(State.UNKNOWN_ERROR, e);
            }
        }
    }.create());
    return register.getObservable();
}

public TaskValue<State.Is>.Observable updatePassword(String email){
    return updatePassword(email, FB_AUTH_TIMEOUT_DELAY_ms, TimeUnit.MILLISECONDS);
}
public TaskValue<State.Is>.Observable updatePassword(String email, long timeout){
    return updatePassword(email, timeout, TimeUnit.MILLISECONDS);
}
public TaskValue<State.Is>.Observable updatePassword(String password, long timeout, TimeUnit timeUnit){
    TaskValue<State.Is> register = new TaskValue<>();
    getUser().updatePassword(password).addOnCompleteListener(new OnCompleteListener<Void>(this, register, timeout, timeUnit){
        @Override
        public void onComplete(){
            updateUser();
            register.notifyComplete(State.UPDATE_PASSWORD_DONE);
        }

        @Override
        public void onException(java.lang.Throwable e){
            if(e instanceof FirebaseTooManyRequestsException){
                register.notifyException(State.TOO_MANY_REQUEST, e);
            } else if(e instanceof FirebaseNetworkException){
                register.notifyException(State.NETWORK_ERROR, e);
            } else if(e instanceof FireBaseTimeoutException){
                register.notifyException(State.TIME_OUT, e);
            } else {

DebugException.start().log(DebugTrack.getFullSimpleName(e)).end();


                register.notifyException(State.UNKNOWN_ERROR, e);
            }
        }
    }.create());
    return register.getObservable();
}

public TaskValue<State.Is>.Observable deleteUser(){
    return deleteUser(FB_AUTH_TIMEOUT_DELAY_ms, TimeUnit.MILLISECONDS);
}
public TaskValue<State.Is>.Observable deleteUser(long timeout){
    return deleteUser(timeout, TimeUnit.MILLISECONDS);
}
public TaskValue<State.Is>.Observable deleteUser(long timeout, TimeUnit timeUnit){
    TaskValue<State.Is> register = new TaskValue<>();
    getUser().delete().addOnCompleteListener(new OnCompleteListener<Void>(this, register, timeout, timeUnit){
        @Override
        public void onComplete(){
            updateUser();
            register.notifyComplete(State.DELETE_USER_DONE);
        }
        @Override
        public void onException(java.lang.Throwable e){
            if(e instanceof FirebaseTooManyRequestsException){
                register.notifyException(State.TOO_MANY_REQUEST, e);
            } else if(e instanceof FirebaseNetworkException){
                register.notifyException(State.NETWORK_ERROR, e);
            } else if(e instanceof FireBaseTimeoutException){
                register.notifyException(State.TIME_OUT, e);
            } else {

DebugException.start().log(DebugTrack.getFullSimpleName(e)).end();


                register.notifyException(State.UNKNOWN_ERROR, e);
            }
        }
    }.create());
    return register.getObservable();
}

@Override
protected void finalize() throws Throwable{
    auth.signOut();
DebugTrack.start().destroy(this).end();
    super.finalize();
}

public interface State extends defAuthMethod.State{
    Is UPDATE_EMAIL_DONE = new Is("UPDATE_EMAIL_DONE");
    Is UPDATE_PASSWORD_DONE = new Is("UPDATE_PASSWORD_DONE");
    Is DELETE_USER_DONE = new Is("DELETE_USER_DONE");

}

private static class FireBaseTimeoutException extends Throwable{}

private abstract static class OnCompleteListener<T> extends RunnableTimeOut{
    TaskValue<State.Is> register;

    OnCompleteListener(Object owner, TaskValue<State.Is> register, long delay, TimeUnit timeUnit){
        super(owner, delay, timeUnit);
        this.register = register;
    }

    OnCompleteListener me(){
        return this;
    }

    @Override
    final public void onTimeOut(){
        me().onException(new FireBaseTimeoutException());
        register.cancel();
    }

    final TaskOnCompleteListenerW<T> create(){
        start();
        return new TaskOnCompleteListenerW<T>(){
            @Override
            public void onComplete(@NonNull Task<T> task){
                if(!register.isCanceled()){
                    if(task.getException() == null){
                        completed();
                    } else {
                        cancel();
                        me().onException(task.getException());
                    }
                }
            }
        };
    }

}

}

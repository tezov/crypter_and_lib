/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java_android.authentification;

import com.tezov.lib_java.debug.DebugLog;
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
import com.tezov.lib_java.cipher.misc.PasswordCipher;
import com.tezov.lib_java.async.notifier.Notifier;
import com.tezov.lib_java.async.notifier.observable.ObservableEvent;
import com.tezov.lib_java.async.notifier.observer.event.ObserverEvent;
import com.tezov.lib_java.async.notifier.observer.value.ObserverValueE;
import com.tezov.lib_java.async.notifier.task.TaskValue;
import com.tezov.lib_java.debug.DebugTrack;
import com.tezov.lib_java.type.runnable.RunnableGroup;

import static com.tezov.lib_java_android.authentification.defAuthMethod.State;

public abstract class UserAuth{
private final Notifier<Event> notifier;
private defAuthMethod authMethod = null;
private User user = null;

public UserAuth(){
DebugTrack.start().create(this).end();
    notifier = new Notifier<>(new ObservableEvent<>(), true);
}
private UserAuth me(){
    return this;
}

protected void post(Event event, String value){
    ObservableEvent<Event, String>.Access access = notifier.obtainAccess(this, event);
    access.setValue(value);
}

public Notifier.Subscription observe(ObserverEvent<Event, String> observer){
    return notifier.register(observer);
}

public void unObserve(Object owner){
    notifier.unregister(owner);
}

public void unObserveAll(){
    notifier.unregisterAll();
}

public <AM extends defAuthMethod> AM getAuthMethod(){
    return (AM)authMethod;
}

protected void setAuthMethod(defAuthMethod authMethod){
    this.authMethod = authMethod;
}

public boolean isAuthenticated(){
    return user != null;
}

public <U extends User> U getUser(){
    return (U)user;
}

protected void setUser(User user){
    this.user = user;
}

public boolean hasUser(){
    return user != null;
}

public TaskValue<State.Is>.Observable signIn(PasswordCipher token){
    TaskValue<State.Is> task = new TaskValue<>();
    RunnableGroup gr = new RunnableGroup(this).name("signIn");
    gr.add(new RunnableGroup.Action(){
        @Override
        public void runSafe(){
            beforeSignIn(null).observe(new ObserverValueE<State.Is>(this){
                @Override
                public void onComplete(State.Is state){
                    next();
                }
                @Override
                public void onException(State.Is state, Throwable e){
                    putValue(state);
                    putException(e);
                    done();
                }
            });
        }
    }.name("beforeSignIn"));
    gr.add(new RunnableGroup.Action(){
        @Override
        public void runSafe(){
            authMethod.signInWithToken(token).observe(new ObserverValueE<State.Is>(this){
                @Override
                public void onComplete(State.Is state){
                    putValue(state);
                    next();
                }
                @Override
                public void onException(State.Is state, Throwable e){
                    putValue(state);
                    putException(e);
                    done();
                }
            });
        }
    }.name("signInWithToken"));
    gr.add(new RunnableGroup.Action(){
        @Override
        public void runSafe(){
            afterSignIn(getValue(), getException()).observe(new ObserverValueE<State.Is>(this){
                @Override
                public void onComplete(State.Is state){
                    next();
                }
                @Override
                public void onException(State.Is state, Throwable e){
                    putValue(state);
                    putException(e);
                    done();
                }
            });
        }
    }.name("afterSignIn"));
    gr.setOnDone(new RunnableGroup.Action(){
        @Override
        public void runSafe(){
            Throwable e = getException();
            if(e == null){
                task.notifyComplete(getValue());
                me().post(Event.SIGN_IN, null);
            } else {
                task.notifyException(getValue(), e);
            }
        }
    });
    gr.start();
    return task.getObservable();
}
public TaskValue<State.Is>.Observable signUp(String token){
    TaskValue<State.Is> task = new TaskValue<>();
    RunnableGroup gr = new RunnableGroup(this).name("signUp");
    gr.add(new RunnableGroup.Action(){
        @Override
        public void runSafe(){
            beforeSignUp(token).observe(new ObserverValueE<State.Is>(this){
                @Override
                public void onComplete(State.Is state){
                    next();
                }

                @Override
                public void onException(State.Is state, Throwable e){
                    putValue(state);
                    putException(e);
                    done();
                }
            });
        }
    }.name("beforeSignUp"));
    gr.add(new RunnableGroup.Action(){
        @Override
        public void runSafe(){
            authMethod.signUpWithToken(null).observe(new ObserverValueE<State.Is>(this){
                @Override
                public void onComplete(State.Is state){
                    putValue(state);
                    next();
                }

                @Override
                public void onException(State.Is state, Throwable e){
                    putValue(state);
                    putException(e);
                    done();

                }
            });
        }
    }.name("signUpWithToken"));
    gr.add(new RunnableGroup.Action(){
        @Override
        public void runSafe(){
            afterSignUp(getValue(), getException()).observe(new ObserverValueE<State.Is>(this){
                @Override
                public void onComplete(State.Is state){
                    next();
                }

                @Override
                public void onException(State.Is state, Throwable e){
                    putValue(state);
                    putException(e);
                    done();
                }
            });
        }
    }.name("afterSignUp"));
    gr.setOnDone(new RunnableGroup.Action(){
        @Override
        public void runSafe(){
            Throwable e = getException();
            if(e == null){
                task.notifyComplete(getValue());
                me().post(Event.SIGN_IN, null);
            } else {
                task.notifyException(getValue(), e);
            }
        }
    });
    gr.start();
    return task.getObservable();
}

public TaskValue<State.Is>.Observable signIn(String email, PasswordCipher password){
    TaskValue<State.Is> task = new TaskValue<>();
    RunnableGroup gr = new RunnableGroup(this).name("signIn");
    gr.add(new RunnableGroup.Action(){
        @Override
        public void runSafe(){
            beforeSignIn(email).observe(new ObserverValueE<State.Is>(this){
                @Override
                public void onComplete(State.Is state){
                    next();
                }
                @Override
                public void onException(State.Is state, Throwable e){
                    putValue(state);
                    putException(e);
                    done();
                }
            });
        }
    }.name("beforeSignIn"));
    gr.add(new RunnableGroup.Action(){
        @Override
        public void runSafe(){
            authMethod.signInWithEmailAndPassword(email, password).observe(new ObserverValueE<State.Is>(this){
                @Override
                public void onComplete(State.Is state){
                    putValue(state);
                    next();
                }
                @Override
                public void onException(State.Is state, Throwable e){
                    putValue(state);
                    putException(e);
                    done();
                }
            });
        }
    }.name("signInWithEmailAndPassword"));
    gr.add(new RunnableGroup.Action(){
        @Override
        public void runSafe(){
            afterSignIn(getValue(), getException()).observe(new ObserverValueE<State.Is>(this){
                @Override
                public void onComplete(State.Is state){
                    next();
                }
                @Override
                public void onException(State.Is state, Throwable e){
                    putValue(state);
                    putException(e);
                    done();
                }
            });
        }
    }.name("afterSignIn"));
    gr.setOnDone(new RunnableGroup.Action(){
        @Override
        public void runSafe(){
            Throwable e = getException();
            if(e == null){
                task.notifyComplete(getValue());
                me().post(Event.SIGN_IN, null);
            } else {
                task.notifyException(getValue(), e);
            }
        }
    });
    gr.start();
    return task.getObservable();
}
public TaskValue<State.Is>.Observable signUp(String email, PasswordCipher password){
    TaskValue<State.Is> task = new TaskValue<>();
    RunnableGroup gr = new RunnableGroup(this).name("signUp");
    gr.add(new RunnableGroup.Action(){
        @Override
        public void runSafe(){
            beforeSignUp(email).observe(new ObserverValueE<State.Is>(this){
                @Override
                public void onComplete(State.Is state){
                    next();
                }

                @Override
                public void onException(State.Is state, Throwable e){
                    putValue(state);
                    putException(e);
                    done();
                }
            });
        }
    }.name("beforeSignUp"));
    gr.add(new RunnableGroup.Action(){
        @Override
        public void runSafe(){
            authMethod.signUpWithEmailAndPassword(email, password).observe(new ObserverValueE<State.Is>(this){
                @Override
                public void onComplete(State.Is state){
                    putValue(state);
                    next();
                }

                @Override
                public void onException(State.Is state, Throwable e){
                    putValue(state);
                    putException(e);
                    done();

                }
            });
        }
    }.name("createUserWithEmailAndPassword"));
    gr.add(new RunnableGroup.Action(){
        @Override
        public void runSafe(){
            afterSignUp(getValue(), getException()).observe(new ObserverValueE<State.Is>(this){
                @Override
                public void onComplete(State.Is state){
                    next();
                }

                @Override
                public void onException(State.Is state, Throwable e){
                    putValue(state);
                    putException(e);
                    done();
                }
            });
        }
    }.name("afterSignUp"));
    gr.setOnDone(new RunnableGroup.Action(){
        @Override
        public void runSafe(){
            Throwable e = getException();
            if(e == null){
                task.notifyComplete(getValue());
                me().post(Event.SIGN_IN, null);
            } else {
                task.notifyException(getValue(), e);
            }
        }
    });
    gr.start();
    return task.getObservable();
}

public TaskValue<State.Is>.Observable signOut(){
    TaskValue<State.Is> task = new TaskValue<>();
    RunnableGroup gr = new RunnableGroup(this).name("signOut");
    gr.add(new RunnableGroup.Action(){
        @Override
        public void runSafe(){
            beforeSignOut().observe(new ObserverValueE<State.Is>(this){
                @Override
                public void onComplete(State.Is state){
                    next();
                }

                @Override
                public void onException(State.Is state, Throwable e){
                    putValue(state);
                    putException(e);
                    done();
                }
            });
        }
    }.name("beforeSignOut"));
    gr.add(new RunnableGroup.Action(){
        @Override
        public void runSafe(){
            authMethod.signOut().observe(new ObserverValueE<State.Is>(this){
                @Override
                public void onComplete(State.Is state){
                    putValue(state);
                    next();
                }
                @Override
                public void onException(State.Is state, Throwable e){
                    putValue(state);
                    putException(e);
                    done();

                }
            });
        }
    }.name("signOut"));
    gr.add(new RunnableGroup.Action(){
        @Override
        public void runSafe(){
            afterSignOut().observe(new ObserverValueE<State.Is>(this){
                @Override
                public void onComplete(State.Is state){
                    next();
                }

                @Override
                public void onException(State.Is state, Throwable e){
                    putValue(state);
                    putException(e);
                    done();
                }
            });
        }
    }.name("afterSignOut"));
    gr.setOnDone(new RunnableGroup.Action(){
        @Override
        public void runSafe(){
            Throwable e = getException();
            if(e == null){
                task.notifyComplete(getValue());
                me().post(Event.SIGN_OUT, null);
            } else {
                task.notifyException(getValue(), e);
            }
        }
    });
    gr.start();
    return task.getObservable();
}

protected TaskValue<State.Is>.Observable beforeSignIn(String data){
    TaskValue<State.Is> task = new TaskValue<>();
    task.notifyComplete(null);
    return task.getObservable();
}
protected abstract TaskValue<State.Is>.Observable afterSignIn(State.Is state, Throwable e);

protected TaskValue<State.Is>.Observable beforeSignUp(String data){
    TaskValue<State.Is> task = new TaskValue<>();
    task.notifyComplete(null);
    return task.getObservable();
}
protected abstract TaskValue<State.Is>.Observable afterSignUp(State.Is state, Throwable e);

protected TaskValue<State.Is>.Observable beforeSignOut(){
    TaskValue<State.Is> task = new TaskValue<>();
    task.notifyComplete(null);
    return task.getObservable();
}
protected abstract TaskValue<State.Is>.Observable afterSignOut();

public TaskValue<State.Is>.Observable passwordRecovery(String email){
    return authMethod.passwordRecoveryWithEmail(email);
}

@Override
protected void finalize() throws Throwable{
DebugTrack.start().destroy(this).end();
    super.finalize();
}


public enum Event{
    SIGN_IN, SIGN_IN_UPDATE, SIGN_OUT, SIGN_OUT_UPDATE
}

}

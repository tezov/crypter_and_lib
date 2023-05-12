/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java_android.playStore;

import com.tezov.lib_java.debug.DebugLog;
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

import com.tezov.lib_java_android.application.AppContext;
import com.tezov.lib_java_android.wrapperAnonymous.BillingPurchaseResponseListenerW;

import androidx.annotation.NonNull;

import com.android.billingclient.api.AcknowledgePurchaseParams;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClient.BillingResponseCode;
import com.android.billingclient.api.BillingClient.SkuType;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.Purchase.PurchaseState;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.tezov.lib_java.async.notifier.observer.state.ObserverStateE;
import com.tezov.lib_java.async.notifier.observer.value.ObserverValueE;
import com.tezov.lib_java.async.notifier.task.TaskState;
import com.tezov.lib_java.async.notifier.task.TaskValue;
import com.tezov.lib_java.toolbox.Nullify;
import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java.debug.DebugTrack;
import com.tezov.lib_java.type.runnable.RunnableGroup;
import com.tezov.lib_java_android.wrapperAnonymous.BillingAcknowledgePurchaseListenerW;
import com.tezov.lib_java_android.wrapperAnonymous.BillingPurchaseUpdatedListenerW;
import com.tezov.lib_java_android.wrapperAnonymous.BillingSkuDetailsListenerW;

import java.util.ArrayList;
import java.util.List;

public class PlayStoreBilling{
public final static String SKU_TEST_FAKE = "android.test.fake";
public final static String SKU_TEST_PURCHASE = "android.test.purchase";
public final static String SKU_TEST_PURCHASED = "android.test.purchased";
public final static String SKU_TEST_CANCELED = "android.test.canceled";
public final static String SKU_TEST_REFUNDED = "android.test.refunded";
public final static String SKU_TEST_UNAVAILABLE = "android.test.item_unavailable";

private BillingClient billingClient = null;
private TaskValue<Purchase> purchaseFlowTask = null;
private SkuDetails purchaseSku = null;

public PlayStoreBilling(){
DebugTrack.start().create(this).end();
}
private static Throwable newExceptionFrom(BillingResult result){
    int code = result.getResponseCode();
    if(code == BillingResponseCode.USER_CANCELED){
        return new UserCancelException(result);
    } else if(code == BillingResponseCode.ITEM_UNAVAILABLE){
        return new ItemUnavailableException(result);
    } else if(code == BillingResponseCode.ITEM_ALREADY_OWNED){
        return new ItemOwnedException(result);
    } else if(code == BillingResponseCode.ITEM_NOT_OWNED){
        return new ItemNotOwnedException(result);
    } else if((code == BillingResponseCode.BILLING_UNAVAILABLE) || ((code == BillingResponseCode.SERVICE_UNAVAILABLE))){
        return new BillingUnavailableException(result);
    } else if(code == BillingResponseCode.SERVICE_TIMEOUT){
        return new BillingTimeoutException(result);
    } else if(code == BillingResponseCode.SERVICE_DISCONNECTED){
        return new BillingDisconnectedException();
    } else if(code == BillingResponseCode.FEATURE_NOT_SUPPORTED){
        return new BillingNotSupportedException(result);
    } else {
        return new BillingErrorException(result);
    }
}
private static Throwable newExceptionFrom(Purchase purchase){
    if(purchase == null){
        return new PurchaseNullException();
    } else {
        int state = purchase.getPurchaseState();
        if(state == PurchaseState.PURCHASED){
            return null;
        } else if(state == PurchaseState.PENDING){
            return new PurchasePendingStateException(purchase);
        } else {
            return new PurchaseUnspecifiedStateException(purchase);
        }
    }
}
public TaskState.Observable connect(){
    if(billingClient != null){
        return TaskState.Complete();
    }
    TaskState task = new TaskState();
    BillingClient.Builder billingClientBuilder = BillingClient.newBuilder(AppContext.get());
    billingClientBuilder.setListener(new BillingPurchaseUpdatedListenerW(){
        @Override
        public void onPurchasesUpdated(@NonNull BillingResult result, List<Purchase> purchases){
            purchaseFlowProgress(result, purchases);
        }
    });
    billingClientBuilder.enablePendingPurchases();
    billingClient = billingClientBuilder.build();
    billingClient.startConnection(new BillingClientStateListener(){
        @Override
        public void onBillingSetupFinished(@NonNull BillingResult result){
            if(result.getResponseCode() == BillingResponseCode.OK){
                task.notifyComplete();
            } else {
                task.notifyException(newExceptionFrom(result));
            }
        }
        @Override
        public void onBillingServiceDisconnected(){
            onDisconnected();
        }
    });
    return task.getObservable();
}
public TaskState.Observable disconnect(){
    if(purchaseFlowTask != null){
        return TaskState.Exception(new BusyException());
    }
    if(billingClient != null){
        billingClient.endConnection();
        billingClient = null;
    }
    return TaskState.Complete();
}
protected void onDisconnected(){
    if(purchaseFlowTask != null){
        purchaseFlowNotifyCanceled();
    }
    billingClient = null;
}
private TaskValue<List<SkuDetails>>.Observable querySku(String sku){
    List<String> skus = new ArrayList<>();
    skus.add(sku);
    return querySku(skus);
}
private TaskValue<List<SkuDetails>>.Observable querySku(List<String> skus){
    if(billingClient == null){
        return TaskValue.Exception(null, new BillingDisconnectedException());
    }
    TaskValue<List<SkuDetails>> task = new TaskValue<>();
    SkuDetailsParams.Builder params = SkuDetailsParams.newBuilder();
    params.setSkusList(skus).setType(SkuType.INAPP);
    billingClient.querySkuDetailsAsync(params.build(), new BillingSkuDetailsListenerW(){
        @Override
        public void onSkuDetailsResponse(@NonNull BillingResult result, List<SkuDetails> skuDetailsList){
            if(result.getResponseCode() == BillingResponseCode.OK){
                task.notifyComplete(skuDetailsList);
            } else {
                task.notifyException(null, newExceptionFrom(result));
            }
        }
    });
    return task.getObservable();
}
private TaskValue<Purchase>.Observable purchaseFlow(SkuDetails sku){
    if(billingClient == null){
        return TaskValue.Exception(null, new BillingDisconnectedException());
    }
    if(purchaseFlowTask != null){
        return TaskValue.Exception(null, new BusyException());
    }
    purchaseFlowTask = new TaskValue<>();
    purchaseSku = sku;
    BillingFlowParams billingFlowParams = BillingFlowParams.newBuilder().setSkuDetails(sku).build();
    BillingResult result = billingClient.launchBillingFlow(AppContext.getActivity(), billingFlowParams);
    if(result.getResponseCode() != BillingResponseCode.OK){
        purchaseFlowNotifyException(result);
    }
    return purchaseFlowTask.getObservable();
}
private void purchaseFlowNotifyComplete(Purchase purchase){
    if(purchaseFlowTask != null){
        purchaseSku = null;
        TaskValue<Purchase> task = purchaseFlowTask;
        purchaseFlowTask = null;
        task.notifyComplete(purchase);
    }
}
private void purchaseFlowNotifyException(BillingResult result){
    purchaseFlowNotifyException(newExceptionFrom(result));
}
private void purchaseFlowNotifyException(Throwable e){
    if(purchaseFlowTask != null){
        purchaseSku = null;
        TaskValue<Purchase> task = purchaseFlowTask;
        purchaseFlowTask = null;
        task.notifyException(null, e);
    }
}
private void purchaseFlowNotifyCanceled(){
    if(purchaseFlowTask != null){
        purchaseSku = null;
        TaskValue<Purchase> task = purchaseFlowTask;
        purchaseFlowTask = null;
        task.cancel();
        task.notifyCanceled();
    }
}

private void purchaseFlowProgress(BillingResult result, List<Purchase> purchases){
    if(purchaseSku == null){
        purchaseFlowProgressUnrequested(result, purchases);
    } else if((purchases != null) && (result.getResponseCode() == BillingResponseCode.OK)){
        Purchase purchase = null;
        for(Purchase p: purchases){
            if(p.getSkus().contains(purchaseSku.getSku())){
                purchase = p;
                break;
            }
        }
        Throwable e = newExceptionFrom(purchase);
        if(e == null){
            purchaseAcknowledge(purchase).observe(new ObserverStateE(this){
                Purchase purchase;
                ObserverStateE init(Purchase purchase){
                    this.purchase = purchase;
                    return this;
                }
                @Override
                public void onComplete(){
                    purchaseFlowNotifyComplete(purchase);
                }
                @Override
                public void onException(Throwable e){
                    purchaseFlowNotifyException(e);
                }
            }.init(purchase));
        } else {
            purchaseFlowNotifyException(e);
        }
    } else {
        purchaseFlowNotifyException(result);
    }
}
private TaskState.Observable purchaseAcknowledge(Purchase purchase){
    if(purchase.isAcknowledged()){
        return TaskState.Complete();
    }
    if(billingClient == null){
        return TaskState.Exception(new BillingDisconnectedException());
    }
    TaskState task = new TaskState();
    AcknowledgePurchaseParams acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder().setPurchaseToken(purchase.getPurchaseToken()).build();
    billingClient.acknowledgePurchase(acknowledgePurchaseParams, new BillingAcknowledgePurchaseListenerW(){
        @Override
        public void onAcknowledgePurchaseResponse(@NonNull BillingResult result){
            if(result.getResponseCode() == BillingResponseCode.OK){
                task.notifyComplete();
            } else {
                task.notifyException(null, newExceptionFrom(result));
            }
        }
    });
    return task.getObservable();
}
protected void purchaseFlowProgressUnrequested(BillingResult result, List<Purchase> purchases){

DebugException.start().log("flow purchases progress with no request").end();

}
private boolean isOwned(Purchase purchase){
    return (purchase != null) && (purchase.getPurchaseState() == PurchaseState.PURCHASED) && purchase.isAcknowledged();
}
private boolean canBuy(Purchase purchase){
    return (purchase == null) || !purchase.isAcknowledged();
}

public TaskValue<Purchase>.Observable purchaseFind(String sku, boolean disconnectWhenDone){
    TaskValue<Purchase> task = new TaskValue<>();
    RunnableGroup gr = new RunnableGroup(this).name("purchaseFind");
    int LBL_DISCONNECT = gr.label();
    gr.add(new RunnableGroup.Action(){
        @Override
        public void runSafe(){
            connect().observe(new ObserverStateE(this){
                @Override
                public void onComplete(){
                    next();
                }
                @Override
                public void onException(Throwable e){
                    putException(e);
                    skipUntilLabel(LBL_DISCONNECT);
                }
            });
        }
    }.name("connect"));
    gr.add(new RunnableGroup.Action(){
        @Override
        public void runSafe(){
            billingClient.queryPurchasesAsync(SkuType.INAPP, new BillingPurchaseResponseListenerW(){
                @Override
                public void onQueryPurchasesResponse(@NonNull BillingResult billingResult, @NonNull List<Purchase> purchases){
                    Purchase purchase = null;
                    for(Purchase p: purchases){
                        if(p.getSkus().contains(sku)){
                            purchase = p;
                            break;
                        }
                    }
                    putValue(purchase);
                    next();
                }
            });
        }
    }.name("find"));
    gr.add(new RunnableGroup.Action(LBL_DISCONNECT){
        @Override
        public void runSafe(){
            if(!disconnectWhenDone){
                next();
            } else {
                disconnect().observe(new ObserverStateE(this){
                    @Override
                    public void onComplete(){
                        next();
                    }
                    @Override
                    public void onException(Throwable e){
                        next();
                    }
                });
            }
        }
    }.name("disconnect"));
    gr.setOnDone(new RunnableGroup.Action(){
        @Override
        public void runSafe(){
            Throwable e = getException();
            if(e != null){
                task.notifyException(null, e);
            } else {
                task.notifyComplete(getValue());
            }
        }
    });
    gr.start();
    return task.getObservable();
}
public TaskValue<Purchase>.Observable buy(String sku, boolean disconnectWhenDone){
    String skuFinal;
    if(SKU_TEST_PURCHASE.equals(sku)){
        skuFinal = SKU_TEST_PURCHASED;
    } else {
        skuFinal = sku;
    }
    TaskValue<Purchase> task = new TaskValue<>();
    RunnableGroup gr = new RunnableGroup(this).name("buy");
    int LBL_DISCONNECT = gr.label();
    gr.add(new RunnableGroup.Action(){
        @Override
        public void runSafe(){
            connect().observe(new ObserverStateE(this){
                @Override
                public void onComplete(){
                    next();
                }
                @Override
                public void onException(Throwable e){
                    putException(e);
                    skipUntilLabel(LBL_DISCONNECT);
                }
            });
        }
    }.name("connect"));
    gr.add(new RunnableGroup.Action(){
        @Override
        public void runSafe(){
            querySku(skuFinal).observe(new ObserverValueE<List<SkuDetails>>(this){
                @Override
                public void onComplete(List<SkuDetails> skus){
                    SkuDetails skuDetails = null;
                    if(skus != null){
                        for(SkuDetails details: skus){
                            if(skuFinal.equals(details.getSku())){
                                skuDetails = details;
                                break;
                            }
                        }
                    }
                    if(skuDetails != null){
                        putValue(skuDetails);
                        next();
                    } else {
                        putException(new SkuNullException());
                        skipUntilLabel(LBL_DISCONNECT);
                    }
                }
                @Override
                public void onException(List<SkuDetails> skus, Throwable e){
                    putException(e);
                    skipUntilLabel(LBL_DISCONNECT);
                }
            });
        }
    }.name("query sku"));
    gr.add(new RunnableGroup.Action(){
        @Override
        public void runSafe(){
            purchaseFlow(getValue()).observe(new ObserverValueE<Purchase>(this){
                @Override
                public void onComplete(Purchase purchase){
                    putValue(purchase);
                    next();
                }
                @Override
                public void onException(Purchase purchase, Throwable e){
                    putException(e);
                    skipUntilLabel(LBL_DISCONNECT);
                }
                @Override
                public void onCancel(){
                    putException(new BillingDisconnectedException());
                    skipUntilLabel(LBL_DISCONNECT);
                }
            });
        }
    }.name("request buy"));
    gr.add(new RunnableGroup.Action(LBL_DISCONNECT){
        @Override
        public void runSafe(){
            if(!disconnectWhenDone){
                next();
            } else {
                disconnect().observe(new ObserverStateE(this){
                    @Override
                    public void onComplete(){
                        next();
                    }
                    @Override
                    public void onException(Throwable e){
                        next();
                    }
                });
            }
        }
    }.name("disconnect"));
    gr.setOnDone(new RunnableGroup.Action(){
        @Override
        public void runSafe(){
            Throwable e = getException();
            if(e != null){
                task.notifyException(null, e);
            } else {
                Purchase purchase = getValue();
                task.notifyComplete(purchase);
            }
        }
    });
    gr.start();
    return task.getObservable();
}
public TaskValue<Boolean>.Observable isOwned(String sku, boolean disconnectWhenDone){
    TaskValue<Boolean> task = new TaskValue<>();
    RunnableGroup gr = new RunnableGroup(this).name("isOwned");
    int LBL_DISCONNECT = gr.label();
    gr.add(new RunnableGroup.Action(){
        @Override
        public void runSafe(){
            connect().observe(new ObserverStateE(this){
                @Override
                public void onComplete(){
                    next();
                }
                @Override
                public void onException(Throwable e){
                    putException(e);
                    skipUntilLabel(LBL_DISCONNECT);
                }
            });
        }
    }.name("connect"));
    gr.add(new RunnableGroup.Action(){
        @Override
        public void runSafe(){
            purchaseFind(sku, false).observe(new ObserverValueE<Purchase>(this){
                @Override
                public void onComplete(Purchase purchase){
                    if(isOwned(purchase)){
                        putValue(true);
                        skipUntilLabel(LBL_DISCONNECT);
                    } else if(purchase == null){
                        putValue(false);
                        skipUntilLabel(LBL_DISCONNECT);
                    } else if(!purchase.isAcknowledged()){
                        putValue(purchase);
                        next();
                    } else {
                        putException(newExceptionFrom(purchase));
                        skipUntilLabel(LBL_DISCONNECT);
                    }
                }
                @Override
                public void onException(Purchase purchase, Throwable e){
                    putException(e);
                    done();
                }
            });
        }
    }.name("check if owned"));
    gr.add(new RunnableGroup.Action(){
        @Override
        public void runSafe(){
            purchaseAcknowledge(getValue()).observe(new ObserverStateE(this){
                @Override
                public void onComplete(){
                    putValue(true);
                    skipUntilLabel(LBL_DISCONNECT);
                }
                @Override
                public void onException(Throwable e){
                    putException(e);
                    skipUntilLabel(LBL_DISCONNECT);
                }
            });
        }
    }.name("acknowledge"));
    gr.add(new RunnableGroup.Action(LBL_DISCONNECT){
        @Override
        public void runSafe(){
            if(!disconnectWhenDone){
                next();
            } else {
                disconnect().observe(new ObserverStateE(this){
                    @Override
                    public void onComplete(){
                        next();
                    }
                    @Override
                    public void onException(Throwable e){
                        next();
                    }
                });
            }
        }
    }.name("disconnect"));
    gr.setOnDone(new RunnableGroup.Action(){
        @Override
        public void runSafe(){
            Throwable e = getException();
            if(e != null){
                task.notifyException(null, e);
            } else {
                task.notifyComplete(getValue());
            }
        }
    });
    gr.start();
    return task.getObservable();
}

@Override
protected void finalize() throws Throwable{
DebugTrack.start().destroy(this).end();
    super.finalize();
}

public static class BusyException extends Throwable{}

public static class SkuNullException extends Throwable{}

public static class ExceptionBaseResult extends Throwable{
    public ExceptionBaseResult(BillingResult result){
        super(buildMessage(result));
    }
    private static String buildMessage(BillingResult result){
        StringBuilder data = new StringBuilder();
        data.append("code: ").append(result.getResponseCode());
        String debugMessage = Nullify.string(result.getDebugMessage());
        if(debugMessage != null){
            data.append(" ").append(debugMessage);
        }
        return data.toString();
    }

}

public static class UserCancelException extends ExceptionBaseResult{
    public UserCancelException(BillingResult result){
        super(result);
    }

}

public static class ItemUnavailableException extends ExceptionBaseResult{
    public ItemUnavailableException(BillingResult result){
        super(result);
    }

}

public static class ItemOwnedException extends ExceptionBaseResult{
    public ItemOwnedException(BillingResult result){
        super(result);
    }

}

public static class ItemNotOwnedException extends ExceptionBaseResult{
    public ItemNotOwnedException(BillingResult result){
        super(result);
    }

}

public static class BillingUnavailableException extends ExceptionBaseResult{
    public BillingUnavailableException(BillingResult result){
        super(result);
    }

}

public static class BillingTimeoutException extends ExceptionBaseResult{
    public BillingTimeoutException(BillingResult result){
        super(result);
    }

}

public static class BillingDisconnectedException extends Throwable{}

public static class BillingNotSupportedException extends ExceptionBaseResult{
    public BillingNotSupportedException(BillingResult result){
        super(result);
    }

}

public static class BillingErrorException extends ExceptionBaseResult{
    public BillingErrorException(BillingResult result){
        super(result);
    }

}

public static class ExceptionBasePurchase extends Throwable{
    public ExceptionBasePurchase(Purchase purchase){
        super("sku:" + purchase.getSkus());
    }

}

public static class PurchaseNullException extends Throwable{}

public static class PurchasePendingStateException extends ExceptionBasePurchase{
    public PurchasePendingStateException(Purchase purchase){
        super(purchase);
    }

}

public static class PurchaseUnspecifiedStateException extends ExceptionBasePurchase{
    public PurchaseUnspecifiedStateException(Purchase purchase){
        super(purchase);
    }

}


}

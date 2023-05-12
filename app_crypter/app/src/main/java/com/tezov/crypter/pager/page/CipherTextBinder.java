/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.crypter.pager.page;

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
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.tezov.crypter.fragment.FragmentCipherText;
import com.tezov.crypter.pager.PagerCipherTextTabManager;
import com.tezov.lib_java_android.BuildConfig;
import com.tezov.lib_java_android.toolbox.PostToHandler;
import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java.debug.DebugTrack;
import com.tezov.lib_java.type.Delayed;
import com.tezov.lib_java.wrapperAnonymous.ConsumerW;
import com.tezov.lib_java_android.wrapperAnonymous.ViewOnTouchListenerW;
import com.tezov.lib_java.type.ref.Ref;
import com.tezov.lib_java.type.ref.WR;
import com.tezov.lib_java.type.runnable.RunnableW;
import com.tezov.lib_java_android.ui.component.plain.EditText;
import com.tezov.lib_java_android.ui.layout_wrapper.GlassAnimInOut;
import com.tezov.lib_java_android.ui.misc.StateView;
import com.tezov.lib_java_android.ui.recycler.RecyclerListRowHolder;
import com.tezov.lib_java_android.ui.recycler.pager.PagerTabRowBinder;

import java.util.concurrent.atomic.AtomicBoolean;

public abstract class CipherTextBinder extends PagerTabRowBinder<CipherTextBinder.RowHolder, CipherTextBinder.DataText>{
private final WR<FragmentCipherText> fragmentWR;
private WR<View> itemViewWR = null;

public CipherTextBinder(FragmentCipherText fragment, PagerCipherTextTabManager pageManager){
    super(pageManager);
    fragmentWR = WR.newInstance(fragment);
}
private CipherTextBinder me(){
    return this;
}
public PagerCipherTextTabManager getPagerManager(){
    return getRowManager();
}
@Override
public PagerCipherTextTabManager.ViewType.Is getViewType(){
    return null;
}
protected abstract int getLayoutId();
@Override
final public RowHolder create(ViewGroup parent){
    return new RowHolder(parent, getLayoutId());
}
protected void bindView(View itemView){
    this.itemViewWR = WR.newInstance(itemView);
}
protected FragmentCipherText fragment(){
    return fragmentWR.get();
}
protected boolean isBound(){
    return Ref.isNotNull(itemViewWR);
}
protected View itemView(){
    return itemViewWR.get();
}
protected void notifyUpdate(){
    getPagerManager().setData(getViewType(), get(), false);
}
public abstract DataText get();

public abstract void set(DataText data);
public void onViewAttachedToWindow(){}
public void onViewDetachedFromWindow(){}
public abstract void onDisabledButtons(StateView stateView);

public abstract void enableButtons(FragmentCipherText.Operation operation);
public void onPasswordChanged(StateView stateView){}
public void setStep(FragmentCipherText.Step previousStep, FragmentCipherText.Step newStep, FragmentCipherText.Operation operation){
    PostToHandler.of(itemViewWR.get(), new RunnableW(){
        @Override
        public void runSafe(){
            switch(newStep){
                case IDLE:
                    stepIdle(previousStep, operation);
                    break;
                case START:
                    stepStart(previousStep, operation);
                    break;
                case STARTED:
                    stepStarted(previousStep, operation);
                    break;
                case SUCCEED:
                    stepSucceed(previousStep, operation);
                    break;
                case FAILED:
                    stepFailed(previousStep, operation);
                    break;
                case ABORT:
                    stepAbort(previousStep, operation);
                    break;
                case ABORTED:
                    stepAborted(previousStep, operation);
                    break;
            }
        }
    });
}
protected abstract void stepIdle(FragmentCipherText.Step previousStep, FragmentCipherText.Operation operation);

protected abstract void stepStart(FragmentCipherText.Step previousStep, FragmentCipherText.Operation operation);

protected abstract void stepStarted(FragmentCipherText.Step previousStep, FragmentCipherText.Operation operation);

protected abstract void stepSucceed(FragmentCipherText.Step previousStep, FragmentCipherText.Operation operation);

protected abstract void stepFailed(FragmentCipherText.Step previousStep, FragmentCipherText.Operation operation);

protected abstract void stepAbort(FragmentCipherText.Step previousStep, FragmentCipherText.Operation operation);

protected abstract void stepAborted(FragmentCipherText.Step previousStep, FragmentCipherText.Operation operation);

public static class DataText{

}

protected static class ButtonsFadeListener{
    private final static long VALIDATE_CHANGE_DELAY_ms = 100;
    protected EditText frmText;
    protected GlassAnimInOut glassButtonsFade;
    protected Delayed<Boolean> glassButtonsDelayedFade;
    protected AtomicBoolean isBusy;

    public ButtonsFadeListener(EditText frmText, GlassAnimInOut glassButtonsFade){
DebugTrack.start().create(this).end();
        this.isBusy = new AtomicBoolean(false);
        this.frmText = frmText;
        this.glassButtonsFade = glassButtonsFade;
        glassButtonsDelayedFade = new Delayed<>(false);
        glassButtonsDelayedFade.setDelayTimeToValidate(VALIDATE_CHANGE_DELAY_ms);
        glassButtonsDelayedFade.setOnChangedRunnable(new ConsumerW<Boolean>(){
            @Override
            public void accept(Boolean lastValid){
                if(lastValid){
                    me().glassButtonsFade.startOut();
                } else {
                    me().glassButtonsFade.startIn();
                }
            }
        });
        frmText.addOnTouchListener(new ViewOnTouchListenerW(){
            @Override
            public boolean onTouched(View view, MotionEvent event){
                updateOnTouch(event);
                return false;
            }
        });
    }
    protected boolean tryLockBusy(){
        return isBusy.compareAndSet(false, true);
    }
    protected boolean unLockBusy(){
        if(BuildConfig.DEBUG_ONLY){
            synchronized(this){
                boolean result = isBusy.compareAndSet(true, false);
                if(!result){
DebugException.start().log("was not locked").end();
                }
                return result;
            }
        } else {
            return isBusy.compareAndSet(true, false);
        }
    }
    private ButtonsFadeListener me(){
        return this;
    }
    protected void updateOnTouch(MotionEvent event){
        int action = event.getActionMasked();
        if(action == MotionEvent.ACTION_MOVE){
            return;
        }
        if(!frmText.hasFocus() && tryLockBusy()){
            int index = event.getActionIndex();
            if(index == 0){
                if((action == MotionEvent.ACTION_DOWN) || (action == MotionEvent.ACTION_POINTER_DOWN)){
                    if(!glassButtonsFade.isOut() && !glassButtonsFade.isRunningOut() && shouldFadeButtons()){
                        glassButtonsDelayedFade.update(true);
                    }
                } else if((action == MotionEvent.ACTION_UP) || (action == MotionEvent.ACTION_CANCEL) || (action == MotionEvent.ACTION_POINTER_UP)){
                    glassButtonsDelayedFade.update(false);
                }
            }
            unLockBusy();
        }
    }
    protected boolean shouldFadeButtons(){
        int lineCount = frmText.getLineCount();
        if(lineCount > 0){
            int bottomText = frmText.getTop() + frmText.getLayout().getLineBottom(frmText.getLineCount() - 1);
            int topButtons = glassButtonsFade.getTop();
            return bottomText >= topButtons;
        } else {
            return false;
        }
    }
    @Override
    protected void finalize() throws Throwable{
DebugTrack.start().destroy(this).end();
        super.finalize();
    }

}

public class RowHolder extends RecyclerListRowHolder<DataText>{
    public RowHolder(ViewGroup parent, int layoutId){
        super(layoutId, parent);
        me().bindView(itemView);
    }
    @Override
    public void set(DataText data){
        me().set(data);
    }
    @Override
    public DataText get(){
        return me().get();
    }

}


}

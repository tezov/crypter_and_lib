/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.crypter.dialog;

import com.tezov.lib_java.debug.DebugLog;
import com.tezov.lib_java.debug.DebugTrack;
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
import static com.tezov.lib_java_android.camera.CameraDevice.Direction.BACK;
import static com.tezov.lib_java_android.camera.CameraDevice.PERMISSION_CHECK_CAMERA;
import static com.tezov.lib_java_android.camera.CameraDevice.PERMISSION_REQUEST_CAMERA;
import static com.tezov.lib_java_android.camera.capture.CameraCaptureBase.FlashMode.OFF;
import static com.tezov.lib_java_android.camera.capture.CameraCaptureBase.FlashMode.TORCH;
import static com.tezov.lib_java_android.camera.view.CameraView.Event.ACQUIRED;
import static com.tezov.lib_java_android.camera.view.CameraView.Type.CAMERA;
import static com.tezov.lib_java.type.defEnum.Event.ON_CANCEL;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.HandlerThread;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.tezov.crypter.R;
import com.tezov.lib_java_android.application.AppContext;
import com.tezov.lib_java_android.application.AppDisplay;
import com.tezov.lib_java_android.application.VersionSDK;
import com.tezov.lib_java.async.Handler;
import com.tezov.lib_java.async.notifier.observer.state.ObserverState;
import com.tezov.lib_java.async.notifier.observer.state.ObserverStateE;
import com.tezov.lib_java.async.notifier.observer.value.ObserverValue;
import com.tezov.lib_java.async.notifier.observer.value.ObserverValueE;
import com.tezov.lib_java.async.notifier.task.TaskValue;
import com.tezov.lib_java.buffer.Splitter;
import com.tezov.lib_java_android.camera.CameraDevice;
import com.tezov.lib_java_android.camera.capture.CameraCaptureBase;
import com.tezov.lib_java_android.camera.capture.CameraCaptureBase.SizeNorm;
import com.tezov.lib_java_android.camera.capture.CameraCaptureFast;
import com.tezov.lib_java_android.camera.view.CameraView;
import com.tezov.lib_java_android.camera.view.CameraViewSnap;
import com.tezov.lib_java_android.toolbox.PostToHandler;
import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java_android.wrapperAnonymous.ViewOnClickListenerW;
import com.tezov.lib_java_android.type.image.ImageReader;
import com.tezov.lib_java_android.type.image.imageHolder.ImageHolder;
import com.tezov.lib_java.type.primaire.Size;
import com.tezov.lib_java.type.primitive.BytesTo;
import com.tezov.lib_java.type.runnable.RunnableW;
import com.tezov.lib_java_android.ui.component.plain.ButtonIconMaterial;
import com.tezov.lib_java_android.ui.component.plain.ButtonMultiIconMaterial;
import com.tezov.lib_java_android.ui.dialog.DialogNavigable;
import com.tezov.lib_java_android.ui.navigation.Navigate;
import com.tezov.lib_java_android.util.UtilsQrCode;

import java.util.concurrent.atomic.AtomicReference;

public class DialogQrScan extends DialogNavigable{
private ButtonMultiIconMaterial btnFlash = null;
private Handler handler = null;
private CameraViewSnap cameraViewSnap = null;
private CameraCaptureFast cameraCaptureFast = null;
private CameraCaptureBase.FlashMode requestFlash = null;
private TextView lblPart = null;
private Splitter.Joiner<String, byte[]> joiner = null;
private TaskValue<String> task = null;
private AtomicReference<Step> step = null;

public static TaskValue<DialogQrScan>.Observable open(Object owner, DialogNavigable.State state){
    TaskValue<DialogQrScan> task = new TaskValue<>();
    if(PERMISSION_CHECK_CAMERA()){
        dialogQrScanOpen(task, owner, state);
    } else {
        PERMISSION_REQUEST_CAMERA(true).observe(new ObserverStateE(owner){
            @Override
            public void onComplete(){
                dialogQrScanOpen(task, owner, state);
            }
            @Override
            public void onException(java.lang.Throwable e){
                task.notifyException(null, e);
            }
        });
    }
    return task.getObservable();
}
private static void dialogQrScanOpen(TaskValue<DialogQrScan> task, Object owner, DialogNavigable.State state){
    Navigate.To(DialogQrScan.class, state).observe(new ObserverValueE<DialogQrScan>(owner){
        @Override
        public void onComplete(DialogQrScan dialog){
            task.notifyComplete(dialog);
        }
        @Override
        public void onException(DialogQrScan dialog, Throwable e){
            task.notifyException(dialog, e);
        }
    });
}

private void setStep(Step stepToLock){
    step.set(stepToLock);
}
private boolean lockStepIfDifferent(Step stepToLock){
    if(step.get() == stepToLock){
        return false;
    } else {
        step.set(stepToLock);
        return true;
    }
}
private boolean lockStep(Step stepExpected, Step stepToLock){
    return step.compareAndSet(stepExpected, stepToLock);
}

@Override
protected State newState(){
    return new State();
}
@Override
public State getState(){
    return (State)super.getState();
}
@Override
public State obtainState(){
    return super.obtainState();
}
@Nullable
@Override
public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
    super.onCreateView(inflater, container, savedInstanceState);
    View view = inflater.inflate(R.layout.dialog_qr_scan, container, false);
    step = new AtomicReference<>(Step.IDLE);
    handler = Handler.newHandler(this, HandlerThread.MAX_PRIORITY);
    Param param = getParam();
    TextView lblTitle = view.findViewById(R.id.lbl_title);
    lblTitle.setText(param.getTitle());
    TextView lblType = view.findViewById(R.id.lbl_type);
    lblType.setText(param.getType());
    lblPart = view.findViewById(R.id.lbl_part);
    CameraDevice camera = CameraDevice.singleton(BACK);
    cameraViewSnap = view.findViewById(R.id.camera_capture);
    cameraViewSnap.observe(new ObserverValue<CameraView.Event>(this){
        @Override
        public void onComplete(CameraView.Event event){
            if(event == ACQUIRED){
                processCapture();
            }
        }
    });
    cameraCaptureFast = new CameraCaptureFast(camera, cameraViewSnap);
    camera.observe(new ObserverValueE<CameraDevice.Event>(this){
        @Override
        public void onComplete(CameraDevice.Event event){

        }
        @Override
        public void onException(CameraDevice.Event event, Throwable e){
            requestClose(e);
        }
    });
    if(VersionSDK.isSupEqualTo24_NOUGAT()){
        cameraViewSnap.setImageFormat(ImageReader.ImageFormat.NV21);
    } else {
        cameraViewSnap.setImageFormat(ImageReader.ImageFormat.JPEG);
    }
    Size sizeCapture = cameraCaptureFast.findSizeOptimal(SizeNorm.SMALL, cameraViewSnap.getSizeFormat());
    cameraViewSnap.setCaptureSize(sizeCapture);
    ViewGroup cameraControlView = view.findViewById(R.id.camera_control);
    btnFlash = cameraControlView.findViewById(R.id.btn_flash);
    if(!cameraCaptureFast.getCamera().hasFlash()){
        btnFlash.setVisibility(View.GONE);
    } else {
        btnFlash.setOnClickListener(new ViewOnClickListenerW(){
            @Override
            public void onClicked(View v){
                if(cameraCaptureFast.getFlashMode() == OFF){
                    requestFlash = TORCH;
                } else {
                    requestFlash = OFF;
                }
            }
        });
    }
    ButtonIconMaterial btnClose = cameraControlView.findViewById(R.id.btn_close);
    btnClose.setOnClickListener(new ViewOnClickListenerW(){
        @Override
        public void onClicked(View v){
            post(ON_CANCEL, null);
            requestClose(null);
        }
    });
    return view;
}
public TaskValue<String>.Observable observe(ObserverValue<String> observer){
    if(task == null){
        task = new TaskValue<>();
    }
    task.observe(observer);
    return task.getObservable();
}

@Override
protected int getHeight(){
    return (int)(AppDisplay.getSizeOriented().getHeight() * .90f);
}

@Override
public void onOpen(boolean hasBeenReconstructed, boolean hasBeenRestarted){
    super.onOpen(hasBeenReconstructed, hasBeenRestarted);
    State state = getState();
    setFlash(state.flashMode);
    setStep(Step.IDLE);
    cameraViewSnap.freeze(false);
    cameraStart();
}

@Override
public void onPause(){
    super.onPause();
    State state = obtainState();
    state.flashMode = cameraCaptureFast.getFlashMode();
    cameraViewSnap.freeze(true);
    cameraCaptureFast.closeCamera(true);
}

private void setFlash(CameraCaptureBase.FlashMode flashMode){
    switch(flashMode){
        case OFF:{
            if(cameraCaptureFast.getCamera().hasFlash()){
                cameraCaptureFast.getCamera().setTorchIndependent(false);
                cameraCaptureFast.getCamera().setTorch(false);
            }
            cameraCaptureFast.setFlashMode(OFF);
            btnFlash.setIndex(0);
        }
        break;
        case TORCH:{
            if(!cameraCaptureFast.getCamera().hasFlash()){
                setFlash(OFF);
            } else {
                cameraCaptureFast.getCamera().setTorchIndependent(false);
                cameraCaptureFast.getCamera().setTorch(true);
                cameraCaptureFast.setFlashMode(TORCH);
                btnFlash.setIndex(1);
            }
        }
        break;
    }
}
private void cameraStart(){
    handler.post(this, new RunnableW(){
        @Override
        public void runSafe(){
            if(lockStep(Step.IDLE, Step.STARTING)){
                if(joiner == null){
                    joiner = Splitter.newJoinerStringToBytes();
                    joiner.observeProgress(new ObserverState(this){
                        @Override
                        public void onComplete(){
                            updateParts();
                        }
                        @Override
                        public void onCancel(){

                        }
                    });
                }
                if(cameraCaptureFast.getCamera().isTorchIndependent()){
                    cameraCaptureFast.getCamera().setTorch(false);
                    cameraCaptureFast.getCamera().setTorchIndependent(false);
                }
                cameraCaptureFast.preview(true).observe(new ObserverStateE(this){
                    @Override
                    public void onComplete(){
                        cameraViewSnap.show(CAMERA, true);
                        cameraCapture();
                    }
                    @Override
                    public void onException(Throwable e){
                        requestClose(e);
                    }
                });
            }
        }
    });
}

private void cameraCapture(){
    handler.post(this, new RunnableW(){
        @Override
        public void runSafe(){
            if(lockStep(Step.STARTING, Step.CAPTURING)){
                if(requestFlash != null){
                    setFlash(requestFlash);
                    requestFlash = null;
                }
                cameraCaptureFast.capture(true).observe(new ObserverStateE(this){
                    @Override
                    public void onComplete(){
                        lockStep(Step.CAPTURING, Step.WAIT_IMAGE);
                    }
                    @Override
                    public void onException(Throwable e){
                        requestClose(e);
                    }
                });
            }
        }
    });
}
private void processCapture(){
    handler.post(this, new RunnableW(){
        @Override
        public void runSafe(){
            if((requestFlash != null) && lockStep(Step.WAIT_IMAGE, Step.STARTING)){
                cameraCapture();
            } else if(lockStep(Step.WAIT_IMAGE, Step.PROCESSING)){
                if(joiner != null){
                    ImageHolder image = cameraViewSnap.peekSnap();
                    if(!joiner.update(UtilsQrCode.fromImageToString(image, false))){
                        lockStep(Step.PROCESSING, Step.WAIT_IMAGE);
                    }
                }
            }
        }
    });
}
private void setDataPartMax(){
    Integer partMax = joiner.getPartMax();
    if(partMax != null){
        View view = getView();
        TextView lblParts = view.findViewById(R.id.lbl_parts);
        lblParts.setText(String.valueOf(partMax));
        view.findViewById(R.id.lbl_parts_sep).setVisibility(View.VISIBLE);
    }
}
private void updateDataPartObtained(){
    PostToHandler.of(getView(), new RunnableW(){
        @Override
        public void runSafe(){
            if(joiner != null){
                int dataPartObtained = joiner.getPartObtained();
                if(dataPartObtained == 1){
                    setDataPartMax();
                }
                lblPart.setText(String.valueOf(dataPartObtained));
            }
        }
    });
}
private void updateParts(){
    updateDataPartObtained();
    if(joiner.isComplete()){
        lockStep(Step.PROCESSING, Step.COMPLETE);
        String data = BytesTo.StringBase58(joiner.getData());
        joiner = null;
        TaskValue<String> tmp = task;
        task = null;
        requestClose(null);
        Handler.PRIMARY().post(this, new RunnableW(){
            @Override
            public void runSafe(){
                tmp.notifyComplete(data);
            }
        });
    }
}
@Override
public boolean onBackPressed(){
    post(ON_CANCEL, null);
    requestClose(null);
    return true;
}
@Override
public void onCancel(@NonNull DialogInterface dialog){
    requestClose(null);
}
@Override
public synchronized void close(){
    requestClose(null);
}
private void requestClose(Throwable e){
    if(lockStepIfDifferent(Step.CLOSING)){
        cameraViewSnap.freeze(true);
        handler.post(this, new RunnableW(){
            @Override
            public void runSafe(){
                if(e != null){
//        Toast
DebugException.start().log(e).end();
                }
                if(joiner != null){
                    joiner.cancel();
                    joiner = null;
                }
                cameraCaptureFast.closeCamera(true);
                closeSuper();
            }
        });
    }
}
private void closeSuper(){
    super.close();
}

@Override
protected void finalize() throws Throwable{
    handler.quit();
    super.finalize();
}
private enum Step{
    IDLE, STARTING, CAPTURING, WAIT_IMAGE, PROCESSING, COMPLETE, CLOSING
}

public static class State extends DialogNavigable.State{
    CameraCaptureBase.FlashMode flashMode;
    public State(){
        this(OFF);
    }
    public State(CameraCaptureBase.FlashMode flashMode){
        this.flashMode = flashMode;
    }
    @Override
    protected Param newParam(){
        return new Param();
    }
    @Override
    public Param obtainParam(){
        return (Param)super.obtainParam();
    }

}

public static class Param extends DialogNavigable.Param{
    public String type = null;
    public String getType(){
        return type;
    }
    public Param setType(int resourceId){
        return setType(AppContext.getResources().getString(resourceId));
    }
    public Param setType(String type){
        this.type = type;
        return this;
    }

}

}
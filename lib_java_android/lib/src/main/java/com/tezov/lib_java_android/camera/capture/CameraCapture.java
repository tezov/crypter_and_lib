/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java_android.camera.capture;

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

import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CaptureFailure;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.TotalCaptureResult;

import androidx.annotation.NonNull;

import com.tezov.lib_java.application.AppUIDGenerator;
import com.tezov.lib_java.async.notifier.observer.state.ObserverStateE;
import com.tezov.lib_java.async.notifier.observer.value.ObserverValue;
import com.tezov.lib_java.async.notifier.task.TaskState;
import com.tezov.lib_java_android.camera.CameraDevice;
import com.tezov.lib_java_android.camera.SurfacesSupplier;
import com.tezov.lib_java_android.camera.capture.CameraCaptureRequest.AF_TRIGGER;
import com.tezov.lib_java_android.camera.capture.CameraCaptureRequest.TEMPLATE;
import com.tezov.lib_java_android.camera.view.CameraView;
import com.tezov.lib_java_android.camera.view.CameraView.Type;
import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java.debug.DebugLog;
import com.tezov.lib_java_android.wrapperAnonymous.CameraCaptureSessionListenerW;

import static com.tezov.lib_java_android.camera.capture.CameraCaptureRequest.AE_MODE;
import static com.tezov.lib_java_android.camera.capture.CameraCaptureRequest.AE_PRE_CAPTURE;
import static com.tezov.lib_java_android.camera.capture.CameraCaptureRequest.AE_STATE;
import static com.tezov.lib_java_android.camera.capture.CameraCaptureRequest.AF_MODE;
import static com.tezov.lib_java_android.camera.capture.CameraCaptureRequest.AF_STATE;
import static com.tezov.lib_java_android.camera.capture.CameraCaptureRequest.AWB_MODE;
import static com.tezov.lib_java_android.camera.capture.CameraCaptureRequest.AWB_STATE;
import static com.tezov.lib_java_android.camera.capture.CameraCaptureRequest.CONTROL_MODE;
import static com.tezov.lib_java_android.camera.capture.CameraCaptureRequest.FLASH_MODE;
import static com.tezov.lib_java_android.camera.view.CameraView.Event.ACQUIRED;

public class CameraCapture extends CameraCaptureBase{
private CaptureMode mode = CaptureMode.NONE;
private Step step = Step.IDLE;
private TaskState currentTask = null;
private RequestType currentRequestType = null;
private boolean flashRequired = false;
private boolean isPreviewActive = false;
private CameraView view;

public CameraCapture(CameraDevice.Direction direction, CameraView view){
    super(direction);
    this.view = view;
}
public CameraCapture(CameraDevice camera, CameraView view){
    super(camera);
    this.view = view;
    view.attach(this);
}

@Override
protected CameraCapture me(){
    return (CameraCapture)super.me();
}

public CameraView getView(){
    return view;
}

public CameraCapture setView(CameraView view){
    this.view = view;
    return this;
}

public boolean isPreviewActive(){
    synchronized(this){
        return isPreviewActive;
    }
}

@Override
public SurfacesSupplier getSurfacesSupplier(){
    return view.getSurfacesSupplier();
}

@Override
protected CameraCaptureSessionListenerW getCaptureRequestListener(){
    if(step == Step.PREVIEW){
        return null;
    } else {
        return new CameraCaptureSessionListenerW(){
            final Step step = me().step;
            boolean isStarted = false;
            @Override
            public void onCaptureStarted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, long timestamp, long frameNumber){
                if(!isStarted && (step == Step.CAPTURE_TRIGGER) && (me().step == step)){
                    isStarted = true;
                    view.setBusy(false);
                }
            }
            @Override
            public void onCaptureCompleted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull TotalCaptureResult result){
                if(me().step == step){
                    process(step, request, result);
                }
            }
            @Override
            public void onCaptureFailed(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull CaptureFailure failure){
                setStep(Step.ERROR, new Throwable("capture frame " + failure.getFrameNumber() + " failed reason:" + failure.getReason()));

            }
        };
    }
}
@Override
protected CameraCaptureRequest getCaptureRequest() throws CameraAccessException{
    CameraCaptureRequest request;
    switch(step){
        case PREVIEW:
            request = requestPreview();
            break;
        case CONVERGE_WAIT:
            request = requestConvergeWait();
            break;
        case FOCUS_TRIGGER:
            request = requestFocusTrigger();
            break;
        case FOCUS_WAIT:
            request = requestFocusWait();
            break;
        case PRE_CAPTURE_TRIGGER:
            request = requestPreCaptureTrigger();
            break;
        case PRE_CAPTURE_WAIT:
            request = requestPreCaptureWait();
            break;
        case CAPTURE_TRIGGER:
            request = requestCaptureTrigger();
            break;
        default:

DebugException.start().unknown("mode", step.name()).end();


            return null;
    }

DebugLog.start().send(me(), request.toString()).end();

    return request;
}

private FLASH_MODE FLASH_MODE(){
    return flashRequired ? FLASH_MODE.TORCH : FLASH_MODE.OFF;
}
private AE_MODE AE_MODE(){
    return hasMode(AE_MODE.ON) ? AE_MODE.ON : AE_MODE.OFF;
}
private AF_MODE AF_MODE(){
    return hasMode(AF_MODE.AUTO) ? AF_MODE.AUTO : AF_MODE.OFF;
}
private AWB_MODE AWB_MODE(){
    return hasMode(AWB_MODE.AUTO) ? AWB_MODE.AUTO : AWB_MODE.OFF;
}

private CameraCaptureRequest requestPreview() throws CameraAccessException{
    currentRequestType = RequestType.REPEATING;
    CameraCaptureRequest request = new CameraCaptureRequest(this, currentRequestType, TEMPLATE.PREVIEW);
    request.addTarget(view.getSurface(Type.CAMERA)).set(CONTROL_MODE.AUTO).set(getCamera().hasFlash() && (getFlashMode() == FlashMode.TORCH) ? FLASH_MODE.TORCH : FLASH_MODE.OFF);
    isPreviewActive = true;
    return request;
}
private CameraCaptureRequest requestConvergeWait() throws CameraAccessException{
    currentRequestType = RequestType.REPEATING;
    AE_MODE mode = null;
    if(getCamera().hasFlash() && (getFlashMode() != FlashMode.OFF)){
        if(!flashRequired){
            if((getFlashMode() == FlashMode.AUTO) && (hasMode(AE_MODE.FLASH_AUTO))){
                mode = AE_MODE.FLASH_AUTO;
            } else {
                if((getFlashMode() == FlashMode.ALWAYS) || (getFlashMode() == FlashMode.TORCH)){
                    flashRequired = true;
                }
            }
        }
    } else {
        flashRequired = false;
    }
    if(mode == null){
        mode = AE_MODE();
    }
    CameraCaptureRequest request = new CameraCaptureRequest(this, currentRequestType, TEMPLATE.PREVIEW);
    request.addTarget(view.getSurface(Type.CAMERA))
            .set(CONTROL_MODE.AUTO).set(AF_MODE())
            .set(AF_TRIGGER.CANCEL).set(mode)
            .set(AE_PRE_CAPTURE.CANCEL).set(AWB_MODE()).set(FLASH_MODE());
    return request;
}
private CameraCaptureRequest requestFocusTrigger() throws CameraAccessException{
    currentRequestType = RequestType.CAPTURE;
    CameraCaptureRequest request = new CameraCaptureRequest(this, currentRequestType, TEMPLATE.PREVIEW);
    request.addTarget(view.getSurface(Type.CAMERA)).set(CONTROL_MODE.SCENE).set(AF_MODE()).set(AE_MODE()).set(AE_PRE_CAPTURE.IDLE).set(AWB_MODE()).set(FLASH_MODE());
    if(hasMode(AF_MODE.AUTO)){
        request.set(AF_TRIGGER.START);
    } else {
        request.set(AF_TRIGGER.IDLE);
    }
    return request;
}
private CameraCaptureRequest requestFocusWait() throws CameraAccessException{
    currentRequestType = RequestType.REPEATING;
    CameraCaptureRequest request = new CameraCaptureRequest(this, currentRequestType, TEMPLATE.PREVIEW);
    request.addTarget(view.getSurface(Type.CAMERA)).set(CONTROL_MODE.SCENE).set(AF_MODE()).set(AF_TRIGGER.IDLE).set(AE_MODE()).set(AWB_MODE()).set(FLASH_MODE());
    return request;
}
private CameraCaptureRequest requestPreCaptureTrigger() throws CameraAccessException{
    currentRequestType = RequestType.CAPTURE;
    CameraCaptureRequest request = new CameraCaptureRequest(this, currentRequestType, TEMPLATE.PREVIEW);
    request.addTarget(view.getSurface(Type.CAMERA)).set(CONTROL_MODE.SCENE).set(AF_MODE()).set(AE_MODE()).set(AWB_MODE()).set(FLASH_MODE());
    if(hasMode(AE_MODE.ON)){
        request.set(AE_PRE_CAPTURE.START);
    }
    return request;
}
private CameraCaptureRequest requestPreCaptureWait() throws CameraAccessException{
    currentRequestType = RequestType.REPEATING;
    CameraCaptureRequest request = new CameraCaptureRequest(this, currentRequestType, TEMPLATE.PREVIEW);
    request.addTarget(view.getSurface(Type.CAMERA)).set(CONTROL_MODE.SCENE).set(AF_MODE()).set(AE_MODE()).set(AE_PRE_CAPTURE.IDLE).set(AWB_MODE()).set(FLASH_MODE());
    return request;
}
private CameraCaptureRequest requestCaptureTrigger() throws CameraAccessException{
    currentRequestType = RequestType.CAPTURE;
    CameraCaptureRequest request = new CameraCaptureRequest(this, currentRequestType, TEMPLATE.STILL_CAPTURE);
    view.setBusy(true);
    view.observe(new ObserverValue<CameraView.Event>(this){
        @Override
        public void onComplete(CameraView.Event event){
            if(event == ACQUIRED){
                unsubscribe();
                nextStep();
            }
        }
    });
    if(isPreviewActive){
        request.addTarget(view.getSurface(Type.CAMERA));
    }
    request.addTarget(view.getSurface(Type.SNAPSHOT)).set(CONTROL_MODE.SCENE).set(AF_MODE()).set(AE_MODE()).set(AWB_MODE()).set(FLASH_MODE());
    //        request
    //            .set(AE_LOCK.ON)
    //            .set(AWB_LOCK.ON);
    //        if(requestFlash == RequestFlash.YES){
    //            if(FLASH_MODE() == FLASH_MODE.TORCH) request.set(FLASH_MODE.TORCH);
    //            else request.set(FLASH_MODE.SINGLE);
    //        }
    return request;
}

public Step getStep(){
    synchronized(this){
        return step;
    }
}
private void setStep(Step step, Throwable e){
DebugLog.start().send(me(), "*** " + step.name() + " ***").end();

    if(currentRequestType == RequestType.REPEATING){
        java.lang.Throwable esr = stopRepeating();
        if(esr != null){
            currentRequestType = null;
            step = Step.ERROR;
            if(e == null){
                e = esr;
            }
        }
    }
    this.step = step;
    if(currentTask != null){
        if(e == null){
            currentTask.notifyComplete();
        } else {
            currentTask.notifyException(e);
        }
    }
    if(step == Step.CAPTURE_DONE){
        currentTask = null;
    }
}
private void nextStep(){
    switch(step){
        case PREVIEW:{
            setStep(Step.PREVIEW_DONE, null);
        }
        break;
        case CONVERGE_WAIT:{
            setStep(Step.FOCUS_TRIGGER, null);
        }
        break;
        case FOCUS_TRIGGER:{
            setStep(Step.FOCUS_WAIT, null);
        }
        break;
        case FOCUS_WAIT:{
            setStep(Step.PRE_CAPTURE_TRIGGER, null);
        }
        break;
        case PRE_CAPTURE_TRIGGER:{
            setStep(Step.PRE_CAPTURE_WAIT, null);
        }
        break;
        case PRE_CAPTURE_WAIT:{
            setStep(Step.CAPTURE_TRIGGER, null);
        }
        break;
        case CAPTURE_TRIGGER:{
            setStep(Step.CAPTURE_DONE, null);
        }
        break;
        case CAPTURE_DONE:{

DebugException.start().explode("impossible state " + step.name()).end();

        }
        break;
        default:

DebugException.start().unknown("state ", step.name()).end();


    }
}

@Override
protected void onCapture(TaskState task){
    Step step = this.step;
    switch(mode){
        case PREVIEW:{
            if((step == Step.IDLE) || (step == Step.CAPTURE_DONE) || (step == Step.PREVIEW)){
                flashRequired = false;
                setStep(Step.PREVIEW, null);
            }
        }
        break;
        case CAPTURE:{
            if((step == Step.IDLE) || step == Step.PREVIEW){
                flashRequired = false;
                setStep(Step.CONVERGE_WAIT, null);
            }
        }
        break;
    }
    currentTask = task;
}
private void process(Step step, CaptureRequest request, CaptureResult result){

DebugLog.start().send(me(), CameraCaptureRequest.toStringDebugResult(result).toString()).end();

    switch(step){
        case CONVERGE_WAIT:{
            processConvergeWait(request, result);
        }
        break;
        case FOCUS_TRIGGER:{
            processFocusTrigger(request, result);
        }
        break;
        case FOCUS_WAIT:{
            processFocusWait(request, result);
        }
        break;
        case PRE_CAPTURE_TRIGGER:{
            processPreCaptureTrigger(request, result);
        }
        break;
        case PRE_CAPTURE_WAIT:{
            processPreCaptureWait(request, result);
        }
        break;
        case CAPTURE_TRIGGER:{
            processCaptureTrigger(request, result);
        }
        break;
        default:{
            setStep(Step.ERROR, new Throwable("Unknown step: " + step));
        }
    }
}
private void processConvergeWait(CaptureRequest request, CaptureResult result){
    AE_STATE ae = AE_STATE.find(result);
    AWB_STATE awb = AWB_STATE.find(result);
    if((AE_STATE.REQUIRE_FLASH == ae) && !flashRequired && (getFlashMode() != FlashMode.OFF)){
        flashRequired = true;
        setStep(Step.CONVERGE_WAIT, null);
    } else {
        if(((AE_STATE.CONVERGED == ae) || (ae == null) || ((AE_STATE.REQUIRE_FLASH == ae) && (getFlashMode() == FlashMode.OFF))) && ((AWB_STATE.CONVERGED == awb) || (awb == null))){
            nextStep();
        }
    }
}
private void processFocusTrigger(CaptureRequest request, CaptureResult result){
    nextStep();
}
private void processFocusWait(CaptureRequest request, CaptureResult result){
    AF_STATE af = AF_STATE.find(result);
    AF_MODE af_mode = AF_MODE.find(request);
    if((AF_STATE.FOCUSED_LOCK == af) || (AF_STATE.FOCUSED_NOT_LOCK == af) || (AF_MODE.OFF == af_mode)){
        nextStep();
    }
}
private void processPreCaptureTrigger(CaptureRequest request, CaptureResult result){
    nextStep();
}
private void processPreCaptureWait(CaptureRequest request, CaptureResult result){
    AE_STATE ae = AE_STATE.find(result);
    if(AE_STATE.PRECAPTURE != ae){
        nextStep();
    }
}
private void processCaptureTrigger(CaptureRequest request, CaptureResult result){

}
private void finalizeCapture(java.lang.Throwable e){
    Step step = this.step;
    if(e == null){
        if((step != Step.CAPTURE_DONE) && (step != Step.PREVIEW_DONE)){
DebugException.start().log("finalizeCapture received but currentState not CAPTURE_DONE").end();
        }
    }

    setStep(Step.IDLE, null);
    isPreviewActive = false;
    mode = CaptureMode.NONE;
    currentRequestType = null;

}

public TaskState.Observable preview(boolean start){
    Query query;
    if(start){
        query = new PreviewStartQuery(this);
    } else {
        query = new PreviewStopQuery(this);
    }
    postQuery(query);
    return query.getObservable();
}
public TaskState.Observable capture(boolean closeCameraWhenDone){
    Query query = new CaptureQuery(this, closeCameraWhenDone);
    postQuery(query);
    return query.getObservable();
}

@Override
protected void closeCameraForce(){
    super.closeCameraForce();
    mode = CaptureMode.NONE;
    step = Step.IDLE;
    currentTask = null;
    currentRequestType = null;
    flashRequired = false;
    isPreviewActive = false;
}
@Override
public TaskState.Observable closeCamera(boolean force){
    if(force){
        closeCameraForce();
        return TaskState.Complete();
    } else {
        Query query = new PreviewStopQuery(this);
        postQuery(query);
        return query.getObservable();
    }
}

private enum CaptureMode{
    NONE, PREVIEW, CAPTURE,
}
public enum Step{
    ERROR, IDLE, PREVIEW, PREVIEW_DONE, CONVERGE_WAIT, FOCUS_TRIGGER, FOCUS_WAIT, PRE_CAPTURE_TRIGGER, PRE_CAPTURE_WAIT, CAPTURE_TRIGGER, CAPTURE_DONE,
}

private static class PreviewStartQuery extends Query{
    protected PreviewStartQuery(CameraCaptureBase cameraCapture){
        super(cameraCapture, "query capture preview start");
    }
    @Override
    protected CameraCapture capture(){
        return (CameraCapture)super.capture();
    }
    @Override
    protected void onCreate(){
        add(new Action(){
            @Override
            public void runSafe(){
                if(capture().getCamera().isActive()){
                    next();
                } else {
                    camera().open(capture().getSurfacesSupplier()).observe(new ObserverStateE(this){
                        @Override
                        public void onComplete(){
                            next();
                        }
                        @Override
                        public void onException(java.lang.Throwable e){
                            queryFailed(e);
                        }
                    });
                }
            }
        }.name("openCamera"));
        add(new Action(){
            @Override
            public void runSafe(){
                capture().mode = CaptureMode.PREVIEW;
                java.lang.Throwable e = capture().createCaptureRequest(null);
                if(e == null){
                    queryComplete();
                } else {
                    queryFailed(e);
                }
            }
        }.name("preview start"));
    }

}
private static class PreviewStopQuery extends Query{
    protected PreviewStopQuery(CameraCaptureBase cameraCapture){
        super(cameraCapture, "query capture stop");
    }
    @Override
    protected CameraCapture capture(){
        return (CameraCapture)super.capture();
    }
    @Override
    protected void onCreate(){
        add(new Action(){
            @Override
            public void runSafe(){
                capture().setStep(Step.PREVIEW_DONE, null);
                next();
            }
        }.name("preview stop"));
        add(new Action(){
            @Override
            public void runSafe(){
                if(capture().getCamera().isActive()){
                    camera().close().observe(new ObserverStateE(this){
                        @Override
                        public void onComplete(){
                            Throwable ex = getException();
                            capture().finalizeCapture(ex);
                            if(ex == null){
                                queryComplete();
                            } else {
                                queryFailed(ex);
                            }
                        }
                        @Override
                        public void onException(Throwable e){
                            Throwable ex = getException();
                            if(ex == null){
                                ex = e;
                            }
                            capture().finalizeCapture(ex);
                            queryFailed(e);
                        }
                    });
                } else {
                    Throwable ex = new Throwable("camera in wrong state");
                    capture().finalizeCapture(ex);
                    queryFailed(ex);
                }
            }
        }.name("closeCamera"));
    }

}
private static class CaptureQuery extends Query{
    private final static int LBL_CLOSE_CAMERA = AppUIDGenerator.nextInt();
    boolean closeCameraWhenDone;

    protected CaptureQuery(CameraCaptureBase cameraCapture, boolean closeCameraWhenDone){
        super(cameraCapture, "query capture");
        this.closeCameraWhenDone = closeCameraWhenDone;
    }

    @Override
    protected CameraCapture capture(){
        return (CameraCapture)super.capture();
    }

    @Override
    protected void onCreate(){
        add(new Action(){
            @Override
            public void runSafe(){
                if(capture().getCamera().isActive()){
                    next();
                } else {
                    camera().open(capture().getSurfacesSupplier()).observe(new ObserverStateE(this){
                        @Override
                        public void onComplete(){
                            next();
                        }

                        @Override
                        public void onException(java.lang.Throwable e){
                            queryFailed(e);
                        }
                    });
                }
            }
        }.name("openCamera"));
        add(new Action(){
            @Override
            public void runSafe(){
                capture().mode = CaptureMode.CAPTURE;
                next();
            }
        }.name("init"));
        add(new Action(){
            @Override
            public void runSafe(){
                TaskState task = newSubTask();
                java.lang.Throwable e = capture().createCaptureRequest(task);
                if(e == null){
                    task.observe(new ObserverStateE(this){
                        @Override
                        public void onComplete(){
                            if(capture().step != Step.CAPTURE_DONE){
                                repeat();
                            } else if(closeCameraWhenDone){
                                skipUntilLabel(LBL_CLOSE_CAMERA);
                            } else {
                                next();
                            }
                        }
                        @Override
                        public void onException(java.lang.Throwable e){
                            capture().finalizeCapture(e);
                            queryFailed(e);
                        }
                    });
                } else {
                    queryFailed(e);
                }
            }
        }.name("capture"));
        add(new Action(){
            @Override
            public void runSafe(){
                if(capture().isPreviewActive){
                    capture().mode = CaptureMode.PREVIEW;
                    java.lang.Throwable e = capture().createCaptureRequest(null);
                    if(e == null){
                        queryComplete();
                    } else {
                        queryFailed(e);
                    }
                } else {
                    capture().finalizeCapture(null);
                    queryComplete();
                }
            }
        }.name("preview"));
        add(new Action(LBL_CLOSE_CAMERA){
            @Override
            public void runSafe(){
                capture().getCamera().close().observe(new ObserverStateE(this){
                    @Override
                    public void onComplete(){
                        queryComplete();
                    }

                    @Override
                    public void onException(java.lang.Throwable e){
                        queryFailed(e);
                    }
                });
            }
        }.name("closeCamera"));
    }

}

}

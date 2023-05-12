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

import androidx.annotation.NonNull;

import com.tezov.lib_java.async.notifier.observer.state.ObserverStateE;
import com.tezov.lib_java.async.notifier.observer.value.ObserverValue;
import com.tezov.lib_java.async.notifier.task.TaskState;
import com.tezov.lib_java_android.camera.CameraDevice;
import com.tezov.lib_java_android.camera.SurfacesSupplier;
import com.tezov.lib_java_android.camera.capture.CameraCaptureRequest.TEMPLATE;
import com.tezov.lib_java_android.camera.view.CameraView;
import com.tezov.lib_java_android.camera.view.CameraView.Type;
import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java.debug.DebugLog;
import com.tezov.lib_java_android.wrapperAnonymous.CameraCaptureSessionListenerW;

import static com.tezov.lib_java_android.camera.capture.CameraCaptureRequest.CONTROL_MODE;
import static com.tezov.lib_java_android.camera.capture.CameraCaptureRequest.FLASH_MODE;

public class CameraCaptureFast extends CameraCaptureBase{
private Step step = Step.IDLE;

private TaskState currentTask = null;
private CameraView view;

public CameraCaptureFast(CameraDevice.Direction direction, CameraView view){
    super(direction);
    this.view = view;
}
public CameraCaptureFast(CameraDevice camera, CameraView view){
    super(camera);
    this.view = view;
    view.attach(this);
}

@Override
protected CameraCaptureFast me(){
    return (CameraCaptureFast)super.me();
}

public CameraView getView(){
    return view;
}
public CameraCaptureFast setView(CameraView view){
    this.view = view;
    return this;
}
public boolean isActive(){
    synchronized (this){
        return step == Step.PREVIEW;
    }
}

@Override
public SurfacesSupplier getSurfacesSupplier(){
    return view.getSurfacesSupplier();
}

@Override
protected CameraCaptureSessionListenerW getCaptureRequestListener(){
    Step step = this.step;
    if(step == Step.PREVIEW){
        return null;
    }
    else if(step == Step.CAPTURE_CONTINOUS) {
        return new CameraCaptureSessionListenerW(){
            @Override
            public void onCaptureStarted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, long timestamp, long frameNumber){
                view.setBusy(false);
            }
            @Override
            public void onCaptureFailed(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull CaptureFailure failure){
                setStep(Step.ERROR, new Throwable("capture frame " + failure.getFrameNumber() + " failed reason:" + failure.getReason()));
            }
        };
    }
    else {
        return new CameraCaptureSessionListenerW(){
            boolean started = false;
            @Override
            public void onCaptureStarted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, long timestamp, long frameNumber){
                if(!started){
                    started = true;
                    view.setBusy(false);
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
    CameraCaptureRequest request = new CameraCaptureRequest(this, RequestType.REPEATING, TEMPLATE.PREVIEW);
    request.addTarget(view.getSurface(Type.CAMERA));
    Step step = this.step;
    if(step == Step.CAPTURE){
        request.addTarget(view.getSurface(Type.SNAPSHOT));
        view.setBusy(true);
        view.observe(new ObserverValue<CameraView.Event>(this){
            @Override
            public void onComplete(CameraView.Event event){
                if(event == CameraView.Event.ACQUIRED){
                    unsubscribe();
                    nextStep();
                }
            }
        });
    }
    else if(step == Step.CAPTURE_CONTINOUS){
        request.addTarget(view.getSurface(Type.SNAPSHOT));
    }
    request.set(CONTROL_MODE.AUTO).set(getCamera().hasFlash() && (getFlashMode() == FlashMode.TORCH) ? FLASH_MODE.TORCH : FLASH_MODE.OFF);
    return request;
}

public Step getStep(){
    synchronized (this){
        return step;
    }
}
private void setStep(Step step, Throwable e){
DebugLog.start().send( me(), "*** " + step.name() + " ***").end();
    if(((step == Step.PREVIEW_DONE) || (step == Step.ERROR))){
        Throwable esr = stopRepeating();
        if(esr != null){
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
    currentTask = null;
}

private void nextStep(){
    switch(step){
        case IDLE:{
            setStep(Step.PREVIEW, null);
        }
        break;
        case PREVIEW:{
            setStep(Step.CAPTURE, null);
        }
        break;
        case CAPTURE:{
            setStep(Step.PREVIEW, null);
        }
        break;
        case CAPTURE_CONTINOUS:{

        }
        break;
        case PREVIEW_DONE:{

DebugException.start().explode("impossible state " + step.name()).end();

        }
        break;
        default:

DebugException.start().unknown("state ", step.name()).end();

    }
}

@Override
protected void onCapture(TaskState task){
    if(step == Step.IDLE){
        nextStep();
    }
    currentTask = task;
}

private void finalizePreview(Throwable e){

    if(e == null){
        if(step != Step.PREVIEW_DONE){
DebugException.start().explode("finalizePreview received but currentState not PREVIEW_DONE").end();
        }
    }

    setStep(Step.IDLE, null);
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
public TaskState.Observable capture(boolean continuous){
    Query query;
    if(continuous){
        query = new PreviewQueryCaptureContinuous(this);
    }else{
        query = new PreviewQueryCapture(this);
    }
    postQuery(query);
    return query.getObservable();
}

@Override
protected void closeCameraForce(){
    super.closeCameraForce();
    step = Step.IDLE;
    currentTask = null;
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

public enum Step{
    ERROR, IDLE, PREVIEW, CAPTURE, CAPTURE_CONTINOUS, PREVIEW_DONE
}

private static class PreviewStartQuery extends Query{
    protected PreviewStartQuery(CameraCaptureBase cameraCapture){
        super(cameraCapture, "query capture start");
    }
    @Override
    protected CameraCaptureFast capture(){
        return (CameraCaptureFast)super.capture();
    }
    @Override
    protected void onCreate(){
        add(new Action(){
            @Override
            public void runSafe(){
                if(capture().step == Step.IDLE){
                    if(capture().getCamera().isActive()){
                        next();
                    } else {
                        camera().open(capture().getSurfacesSupplier()).observe(new ObserverStateE(this){
                            @Override
                            public void onComplete(){
                                next();
                            }
                            @Override
                            public void onException(Throwable e){
                                queryFailed(e);
                            }
                        });
                    }
                } else {
                    queryFailed(new Throwable("Preview not in stand by"));
                }
            }
        }.name("openCamera"));
        add(new Action(){
            @Override
            public void runSafe(){
                Throwable e = capture().createCaptureRequest(null);
                if(e == null){
                    queryComplete();
                } else {
                    queryFailed(e);
                }
            }
        }.name("preview start"));
    }
}
private static class PreviewQueryCapture extends Query{
    protected PreviewQueryCapture(CameraCaptureBase cameraCapture){
        super(cameraCapture, "query capture start");
    }
    @Override
    protected CameraCaptureFast capture(){
        return (CameraCaptureFast)super.capture();
    }
    @Override
    protected void onCreate(){
        add(new Action(){
            @Override
            public void runSafe(){
                if(capture().step == Step.PREVIEW){
                    capture().setStep(Step.CAPTURE, null);
                    TaskState task = newSubTask();
                    java.lang.Throwable e = capture().createCaptureRequest(task);
                    if(e == null){
                        task.observe(new ObserverStateE(this){
                            @Override
                            public void onComplete(){
                                next();
                            }
                            @Override
                            public void onException(java.lang.Throwable e){
                                queryFailed(e);
                            }
                        });
                    } else {
                        queryFailed(e);
                    }
                } else {
                    queryFailed(new Throwable("Preview not started"));
                }
            }
        }.name("capture"));
        add(new Action(){
            @Override
            public void runSafe(){
                java.lang.Throwable e = capture().createCaptureRequest(null);
                if(e == null){
                    queryComplete();
                } else {
                    queryFailed(e);
                }
            }
        }.name("restart preview"));
    }
}
private static class PreviewQueryCaptureContinuous extends Query{
    protected PreviewQueryCaptureContinuous(CameraCaptureBase cameraCapture){
        super(cameraCapture, "query capture start");
    }
    @Override
    protected CameraCaptureFast capture(){
        return (CameraCaptureFast)super.capture();
    }
    @Override
    protected void onCreate(){
        add(new Action(){
            @Override
            public void runSafe(){
                if((capture().step == Step.PREVIEW)||(capture().step == Step.CAPTURE_CONTINOUS)){
                    capture().setStep(Step.CAPTURE_CONTINOUS, null);
                    java.lang.Throwable e = capture().createCaptureRequest(null);
                    if(e == null){
                        queryComplete();
                    } else {
                        queryFailed(e);
                    }
                } else {
                    queryFailed(new Throwable("Preview not started"));
                }
            }
        }.name("capture"));
    }
}
private static class PreviewStopQuery extends Query{
    protected PreviewStopQuery(CameraCaptureBase cameraCapture){
        super(cameraCapture, "query capture stop");
    }
    @Override
    protected CameraCaptureFast capture(){
        return (CameraCaptureFast)super.capture();
    }
    @Override
    protected void onCreate(){
        add(new Action(){
            @Override
            public void runSafe(){
                if(capture().step == Step.PREVIEW){
                    capture().setStep(Step.PREVIEW_DONE, null);
                    next();
                } else {
                    queryFailed(new Throwable("Preview not started"));
                }
            }
        }.name("preview stop"));
        add(new Action(){
            @Override
            public void runSafe(){
                if(capture().getCamera().isActive()){
                    camera().close().observe(new ObserverStateE(this){
                        @Override
                        public void onComplete(){
                            capture().finalizePreview(null);
                            queryComplete();
                        }
                        @Override
                        public void onException(Throwable e){
                            capture().finalizePreview(e);
                            queryFailed(e);
                        }
                    });
                } else {
                    Throwable e = new Throwable("camera in wrong state");
                    capture().finalizePreview(e);
                    queryFailed(e);
                }
            }
        }.name("closeCamera"));
    }
}

}

/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java_android.camera.capture;

import com.tezov.lib_java.debug.DebugException;
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
import android.graphics.ImageFormat;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.media.MediaRecorder;
import android.os.Build;
import android.view.SurfaceHolder;

import androidx.annotation.RequiresApi;

import com.tezov.lib_java.async.notifier.observer.state.ObserverStateE;
import com.tezov.lib_java.async.notifier.task.TaskState;
import com.tezov.lib_java_android.camera.CameraDevice;
import com.tezov.lib_java_android.camera.SurfacesSupplier;
import com.tezov.lib_java.debug.DebugLog;
import com.tezov.lib_java.debug.DebugTrack;
import com.tezov.lib_java.type.primaire.Size;
import com.tezov.lib_java.type.runnable.RunnableGroup;
import com.tezov.lib_java.type.runnable.RunnableQueue;
import com.tezov.lib_java_android.wrapperAnonymous.CameraCaptureSessionListenerW;
import com.tezov.lib_java.wrapperAnonymous.ComparatorW;
import com.tezov.lib_java.util.UtilsList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.tezov.lib_java_android.camera.capture.CameraCaptureRequest.AE_MODE;
import static com.tezov.lib_java_android.camera.capture.CameraCaptureRequest.AF_MODE;
import static com.tezov.lib_java_android.camera.capture.CameraCaptureRequest.AWB_MODE;
import static com.tezov.lib_java_android.camera.capture.CameraCaptureRequest.CONTROL_MODE;

public abstract class CameraCaptureBase{
private final CameraDevice camera;
private final RunnableQueue<Query> queries;
private FlashMode flashMode = FlashMode.OFF;

public CameraCaptureBase(CameraDevice.Direction direction){
    this(CameraDevice.singleton(direction));
}
public CameraCaptureBase(CameraDevice camera){
DebugTrack.start().create(this).end();
    this.camera = camera;
    this.queries = new RunnableQueue<>(this);
}

protected CameraCaptureBase me(){
    return this;
}

public FlashMode getFlashMode(){
    return flashMode;
}

public void setFlashMode(FlashMode flashMode){
    this.flashMode = flashMode;
}

public Size findSizeOptimal(Size sizeRequested, SizeType type){
    return findSizeOptimal(sizeRequested.getWidth(), sizeRequested.getHeight(), type);
}
public Size findSizeOptimal(int widthRequested, int heightRequested, SizeType type){
    android.util.Size[] sizes = getCamera().getOutputSizes(type.getType());
    //DebugLog.start().send(sizes).end();
    List<android.util.Size> bigEnough = new ArrayList<>();
    List<android.util.Size> notBigEnough = new ArrayList<>();
    if(sizes != null){
        for(android.util.Size s: sizes){
            if((s.getWidth() >= widthRequested) && (s.getHeight() >= heightRequested)){
                bigEnough.add(s);
            } else {
                notBigEnough.add(s);
            }
        }
    }
    android.util.Size sizeOptimal = null;
    if(!bigEnough.isEmpty()){
        float ratio = ((float)widthRequested) / heightRequested;
        List<android.util.Size> bigEnoughSorted = UtilsList.sort(bigEnough, new ComparatorW<android.util.Size>(){
            @Override
            public int compare(android.util.Size s1, android.util.Size s2){
                float r1 = ((float)s1.getWidth()) / s1.getHeight();
                float r2 = ((float)s2.getWidth()) / s2.getHeight();
                if(r1 != r2){
                    if(r1 == ratio){
                        return -1;
                    }
                    if(r2 == ratio){
                        return 1;
                    }
                }
                long a1 = (long)s1.getWidth() * s1.getHeight();
                long a2 = (long)s2.getWidth() * s2.getHeight();
                return Long.compare(a1, a2);
            }
        });
        sizeOptimal = bigEnoughSorted.get(0);
    } else if(!notBigEnough.isEmpty()){
        float ratio = ((float)widthRequested) / heightRequested;
        long area = (long)widthRequested * heightRequested;
        float areaMaxErrorAllowed = 0.2f;
        List<android.util.Size> notBigEnoughSorted = UtilsList.sort(notBigEnough, new ComparatorW<android.util.Size>(){
            @Override
            public int compare(android.util.Size s1, android.util.Size s2){
                long a1 = (long)s1.getWidth() * s1.getHeight();
                float a1Error = ((float)Math.abs(area - a1)) / area;
                long a2 = (long)s2.getWidth() * s2.getHeight();
                float a2Error = ((float)Math.abs(area - a2)) / area;
                if(a1Error <= areaMaxErrorAllowed){
                    if(a2Error > areaMaxErrorAllowed){
                        return -1;
                    } else {
                        float r1 = ((float)s1.getWidth()) / s1.getHeight();
                        float r2 = ((float)s2.getWidth()) / s2.getHeight();
                        if(r1 != r2){
                            if(r1 == ratio){
                                return -1;
                            } else {
                                if(r2 == ratio){
                                    return 1;
                                }
                            }
                        }
                        return Long.compare(a1, a2);
                    }
                } else if(a2Error <= areaMaxErrorAllowed){
                    return 1;
                } else {
                    float r1 = ((float)s1.getWidth()) / s1.getHeight();
                    float r2 = ((float)s2.getWidth()) / s2.getHeight();
                    if(r1 != r2){
                        if(r1 == ratio){
                            return -1;
                        } else {
                            if(r2 == ratio){
                                return 1;
                            }
                        }
                    }
                    return Long.compare(a2, a1);
                }
            }
        });
        sizeOptimal = notBigEnoughSorted.get(0);

    }
    //        float ratioRequested = ((float)widthRequested)/heightRequested;
    //        long areaRequested = widthRequested*heightRequested;
    //        float ratioOptimal = ((float)sizeOptimal.getWidth())/sizeOptimal.getHeight();
    //        long areaOptimal = sizeOptimal.getWidth()*sizeOptimal.getHeight();
    //        DebugLog.send(
    //                "R=" + widthRequested+"x"+heightRequested +
    //                    " O=" + sizeOptimal.getWidth()+"x"+sizeOptimal.getHeight() +
    //                    " Ratio Error: " + ((ratioOptimal - ratioRequested)/ratioRequested) +
    //                    " Area Error: " + (((float)(areaOptimal - areaRequested))/areaRequested)
    //        );
    return Size.wrap(sizeOptimal);
}
public Size findSizeOptimal(SizeNorm requestedSize, SizeType type){
    if(requestedSize == SizeNorm.MIN){
        android.util.Size[] sizes = getCamera().getOutputSizes(type.getType());
        if(sizes == null){
            return null;
        }
        List<android.util.Size> sizeSorted = UtilsList.sort(List.of(sizes), new ComparatorW<android.util.Size>(){
            @Override
            public int compare(android.util.Size s1, android.util.Size s2){
                long a1 = (long)s1.getWidth() * s1.getHeight();
                long a2 = (long)s2.getWidth() * s2.getHeight();
                return Long.compare(a1, a2);
            }
        });
        return Size.wrap(sizeSorted.get(0));
    } else if(requestedSize == SizeNorm.MAX){
        android.util.Size[] sizes = getCamera().getOutputSizes(type.getType());
        if(sizes == null){
            return null;
        }
        List<android.util.Size> sizeSorted = UtilsList.sort(List.of(sizes), new ComparatorW<android.util.Size>(){
            @Override
            public int compare(android.util.Size s1, android.util.Size s2){
                long a1 = (long)s1.getWidth() * s1.getHeight();
                long a2 = (long)s2.getWidth() * s2.getHeight();
                return Long.compare(a2, a1);
            }
        });
        return Size.wrap(sizeSorted.get(0));
    } else {
        return findSizeOptimal(requestedSize.getSize(), type);
    }
}

protected abstract CameraCaptureRequest getCaptureRequest() throws CameraAccessException;

protected abstract CameraCaptureSessionListenerW getCaptureRequestListener();

protected void onCapture(TaskState task){
}

protected java.lang.Throwable createCaptureRequest(TaskState task){
    onCapture(task);
    try{
        CameraCaptureRequest request = getCaptureRequest();
        switch(request.getType()){
            case CAPTURE:{
                getCamera().capture(request, getCaptureRequestListener());
            }
            break;
            case REPEATING:{
                getCamera().setRepeatingRequest(request, getCaptureRequestListener());
            }
            break;
        }
        return null;
    } catch(java.lang.Throwable e){
        return e;
    }
}

protected void postQuery(Query query){
    queries.add(query);
    if(!queries.isBusy()){
        nextQuery();
    }
}
private void nextQuery(){
    if(!queries.isEmpty()){
        queries.next().getObservable().observe(new ObserverStateE(this){
            @Override
            public void onComplete(){
                nextQuery();
            }

            @Override
            public void onException(java.lang.Throwable e){
                nextQuery();
            }
        });
    } else {
        queries.done();
    }
}

protected java.lang.Throwable stopRepeating(){
    try{
        getCamera().getSession().stopRepeating();
        return null;
    } catch(CameraAccessException e){
        return e;
    }
}

public Integer hardwareLevel(){
    CameraCharacteristics chars = getCamera().getCharacteristics();
    return chars.get(CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL);
}

@RequiresApi(api = Build.VERSION_CODES.P)
public String infoVersion(){
    CameraCharacteristics chars = getCamera().getCharacteristics();
    return chars.get(CameraCharacteristics.INFO_VERSION);
}

public Float minDistance(){
    CameraCharacteristics chars = getCamera().getCharacteristics();
    return chars.get(CameraCharacteristics.LENS_INFO_MINIMUM_FOCUS_DISTANCE);
}

public Float focalDistance(){
    CameraCharacteristics chars = getCamera().getCharacteristics();
    return chars.get(CameraCharacteristics.LENS_INFO_HYPERFOCAL_DISTANCE);
}

public boolean hasMode(CONTROL_MODE mode){
    CameraCharacteristics chars = getCamera().getCharacteristics();
    int[] modes = chars.get(CameraCharacteristics.CONTROL_AF_AVAILABLE_MODES);
    if(modes == null){
        return false;
    }
    for(int m: modes){
        if(CONTROL_MODE.find(m) == mode){
            return true;
        }
    }
    return false;
}

public boolean hasMode(AF_MODE mode){
    CameraCharacteristics chars = getCamera().getCharacteristics();
    int[] modes = chars.get(CameraCharacteristics.CONTROL_AF_AVAILABLE_MODES);
    if(modes == null){
        return false;
    }
    for(int m: modes){
        if(AF_MODE.find(m) == mode){
            return true;
        }
    }
    return false;
}

public boolean hasMode(AE_MODE mode){
    CameraCharacteristics chars = getCamera().getCharacteristics();
    int[] modes = chars.get(CameraCharacteristics.CONTROL_AE_AVAILABLE_MODES);
    if(modes == null){
        return false;
    }
    for(int m: modes){
        if(AE_MODE.find(m) == mode){
            return true;
        }
    }
    return false;
}

@RequiresApi(api = Build.VERSION_CODES.M)
public boolean canLockAE(){
    CameraCharacteristics chars = getCamera().getCharacteristics();
    return chars.get(CameraCharacteristics.CONTROL_AE_LOCK_AVAILABLE);
}

public boolean hasMode(AWB_MODE mode){
    CameraCharacteristics chars = getCamera().getCharacteristics();
    int[] modes = chars.get(CameraCharacteristics.CONTROL_AWB_AVAILABLE_MODES);
    if(modes == null){
        return false;
    }
    for(int m: modes){
        if(AWB_MODE.find(m) == mode){
            return true;
        }
    }
    return false;
}

@RequiresApi(api = Build.VERSION_CODES.M)
public boolean canLockAWB(){
    CameraCharacteristics chars = getCamera().getCharacteristics();
    return chars.get(CameraCharacteristics.CONTROL_AWB_LOCK_AVAILABLE);
}

public void toDebugLogHardware(){
DebugLog.start().send("DEVICE " + getDirection().name()).end();
DebugLog.start().send("HARDWARE " + hardwareLevel()).end();
    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.P){
DebugLog.start().send("VERSION " + infoVersion()).end();
    }
DebugLog.start().send("MIN DISTANCE " + minDistance()).end();
DebugLog.start().send("FOCAL DISTANCE " + focalDistance()).end();
    CameraCharacteristics chars = getCamera().getCharacteristics();
DebugLog.start().send("CONTROL_AF_AVAILABLE_MODES").end();
    int[] af_modes = chars.get(CameraCharacteristics.CONTROL_AF_AVAILABLE_MODES);
    for(int modeValue: af_modes){
        AF_MODE mode = AF_MODE.find(modeValue);
        if(mode != null){
DebugLog.start().send(mode.name()).end();
        }
    }
DebugLog.start().send("CONTROL_AE_AVAILABLE_MODES").end();
    int[] ae_modes = chars.get(CameraCharacteristics.CONTROL_AE_AVAILABLE_MODES);
    for(int modeValue: ae_modes){
        AE_MODE mode = AE_MODE.find(modeValue);
        if(mode != null){
DebugLog.start().send(mode.name()).end();
        }
    }
DebugLog.start().send("CONTROL_AWB_AVAILABLE_MODES").end();
    int[] awb_modes = chars.get(CameraCharacteristics.CONTROL_AWB_AVAILABLE_MODES);
    for(int modeValue: awb_modes){
        AWB_MODE mode = AWB_MODE.find(modeValue);
        if(mode != null){
DebugLog.start().send(mode.name()).end();
        }
    }
}

public boolean isActive(){
    return getCamera().isActive();
}

public CameraDevice getCamera(){
    return camera;
}

public CameraDevice.Direction getDirection(){
    return getCamera().getDirection();
}

public abstract SurfacesSupplier getSurfacesSupplier();

public TaskState.Observable openCamera(){
    Query query = new OpenCameraQuery(this);
    postQuery(query);
    return query.getObservable();
}

public TaskState.Observable capture(){
    Query query = new CaptureQuery(this);
    postQuery(query);
    return query.getObservable();
}

public TaskState.Observable closeCamera(){
    return closeCamera(false);
}

protected void closeCameraForce(){
    queries.quitAndClear();
    flashMode = FlashMode.OFF;
    getCamera().close(true);
}
public TaskState.Observable closeCamera(boolean force){
    if(force){
        closeCameraForce();
        return TaskState.Complete();
    } else {
        Query query = new CloseCameraQuery(this);
        postQuery(query);
        return query.getObservable();
    }
}

@Override
protected void finalize() throws Throwable{
    if(isActive()){
        closeCamera(true);
    }
DebugTrack.start().destroy(this).end();
}


public enum FlashMode{
    OFF, ALWAYS, AUTO, TORCH;
    public FlashMode next(){
        FlashMode[] modes = values();
        return modes[(ordinal() + 1) % modes.length];
    }
}
public enum SizeType{
    SURFACE_HOLDER(SurfaceHolder.class), MEDIA_RECORDER(MediaRecorder.class), YUV_420_888(ImageFormat.YUV_420_888), JPEG(ImageFormat.JPEG);
    Object type;
    SizeType(Class type){
        this.type = type;
    }
    SizeType(int type){
        this.type = type;
    }
    public Object getType(){
        return type;
    }
}


public enum SizeNorm{
    MIN(0, 0), SMALL(800, 600), MEDIUM(1920, 1440), LARGE(3264, 2448), MAX(0, 0);
    Size size;
    SizeNorm(int width, int height){
        this.size = new Size(width, height);
    }
    public Size getSize(){
        return size;
    }
}


public enum RequestType{
    REPEATING, CAPTURE,
}

public static abstract class Query extends RunnableGroup{
    private final int KEY_TASK = key();
    private final int KEY_SUB_TASK = key();
    private final CameraCaptureBase cameraCapture;
    protected Query(CameraCaptureBase cameraCapture, String name){
        super(cameraCapture);
        this.cameraCapture = cameraCapture;
        name(name);
        put(KEY_TASK, new TaskState());
        onCreate();
        setOnDone(new Action(){
            @Override
            public void runSafe(){
                notify(getTask());
                clear();
            }
        });
    }
    protected abstract void onCreate();
    protected CameraDevice camera(){
        return cameraCapture.camera;
    }
    protected CameraCaptureBase capture(){
        return cameraCapture;
    }
    protected TaskState getTask(){
        return get(KEY_TASK);
    }
    public TaskState.Observable getObservable(){
        return getTask().getObservable();
    }
    protected TaskState newSubTask(){
        TaskState task = new TaskState();
        put(KEY_SUB_TASK, task);
        return task;
    }
    protected void queryComplete(){
        done();
    }
    protected void queryFailed(java.lang.Throwable e){
        done();
    }
}

protected static class OpenCameraQuery extends Query{
    OpenCameraQuery(CameraCaptureBase cameraCapture){
        super(cameraCapture, "query capture open com.tezov.lib.camera");
    }
    @Override
    protected void onCreate(){
        add(new Action(){
            @Override
            public void runSafe(){
                camera().open(capture().getSurfacesSupplier()).observe(new ObserverStateE(this){
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
        }.name("openCamera"));
    }
}
protected static class CaptureQuery extends Query{
    protected CaptureQuery(CameraCaptureBase cameraCapture){
        super(cameraCapture, "query capture");
    }
    @Override
    protected void onCreate(){
        add(new Action(){
            @Override
            public void runSafe(){
                TaskState task = newSubTask();
                java.lang.Throwable e = capture().createCaptureRequest(task);
                if(e == null){
                    task.observe(new ObserverStateE(this){
                        @Override
                        public void onComplete(){
                            queryComplete();
                        }
                        @Override
                        public void onException(java.lang.Throwable e){
                            queryFailed(e);
                        }
                    });
                } else {
                    queryFailed(e);
                }
            }
        }.name("createCaptureRequest"));
    }

}
protected static class CloseCameraQuery extends Query{
    protected CloseCameraQuery(CameraCaptureBase cameraCapture){
        super(cameraCapture, "query capture closeCamera");
    }
    @Override
    protected void onCreate(){
        add(new Action(){
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

/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java_android.camera;

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
import com.tezov.lib_java_android.R;
import com.tezov.lib_java_android.application.AppInfo;
import com.tezov.lib_java_android.ui.view.status.StatusParam;

import android.Manifest;
import android.annotation.SuppressLint;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.params.OutputConfiguration;
import android.hardware.camera2.params.SessionConfiguration;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.os.Build;
import android.util.Size;
import android.view.Surface;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.tezov.lib_java.application.AppUIDGenerator;
import com.tezov.lib_java_android.application.AppContext;
import com.tezov.lib_java_android.application.AppPermission;
import com.tezov.lib_java_android.application.VersionSDK;
import com.tezov.lib_java.async.Handler;
import com.tezov.lib_java.async.notifier.Notifier;
import com.tezov.lib_java.async.notifier.observable.ObservableValueE;
import com.tezov.lib_java.async.notifier.observer.state.ObserverStateE;
import com.tezov.lib_java.async.notifier.observer.value.ObserverValue;
import com.tezov.lib_java.async.notifier.observer.value.ObserverValueE;
import com.tezov.lib_java.async.notifier.task.TaskState;
import com.tezov.lib_java_android.camera.capture.CameraCaptureRequest;
import com.tezov.lib_java.toolbox.Compare;
import com.tezov.lib_java_android.toolbox.SingletonHolder;
import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java.debug.DebugLog;
import com.tezov.lib_java.debug.DebugTrack;
import com.tezov.lib_java_android.wrapperAnonymous.CameraCaptureSessionListenerW;
import com.tezov.lib_java_android.wrapperAnonymous.CameraCaptureSessionStateListenerW;
import com.tezov.lib_java_android.wrapperAnonymous.CameraStateListenerW;
import com.tezov.lib_java.wrapperAnonymous.UncaughtExceptionHandlerW;
import com.tezov.lib_java.type.collection.ListEntry;
import com.tezov.lib_java.type.ref.WR;
import com.tezov.lib_java.type.runnable.RunnableGroup;
import com.tezov.lib_java.type.runnable.RunnableQueue;

import java.util.ArrayList;
import java.util.List;

public class CameraDevice{
private final Direction direction;
private final RunnableQueue<Query> queries;
private Handler deviceHandler = null;
private Notifier<Void> notifier = null;
private Object device = null;
private android.hardware.camera2.CameraCaptureSession session = null;
private SurfacesSupplier surfacesSupplier = null;
private boolean isTorchOn = false;
private boolean isTorchIndependent = false;

protected CameraDevice(Direction direction){
DebugTrack.start().create(this).end();
    this.direction = direction;
    queries = new RunnableQueue<>(this);
}
public static CameraDevice singleton(Direction direction){
    if(SingletonHolder.exist(CameraDevice.class, direction.name())){
        return SingletonHolder.get(CameraDevice.class, direction.name());
    } else {
        return SingletonHolder.getWithInit(CameraDevice.class, direction.name(), direction);
    }
}
public static CameraDevice singletonRelease(Direction direction){
    return SingletonHolder.release(CameraDevice.class, direction.name());
}
public static boolean exist(Direction direction){
    return getId(direction) != null;
}
public static String getId(Direction direction){
    try{
        CameraManager manager = getManager();
        for(String id: manager.getCameraIdList()){
            CameraCharacteristics chars = manager.getCameraCharacteristics(id);
            Integer facing = chars.get(CameraCharacteristics.LENS_FACING);
            if(direction.getValue() == facing){
                return id;
            }
        }
        return null;
    } catch(java.lang.Throwable e){
DebugException.start().log(e).end();
        return null;
    }
}
public static CameraManager getManager(){
    return AppContext.getSystemService(android.content.Context.CAMERA_SERVICE);
}
public static CameraCharacteristics getCharacteristics(String cameraId){
    try{
        return getManager().getCameraCharacteristics(cameraId);
    } catch(java.lang.Throwable e){
DebugException.start().log(e).end();
        return null;
    }
}
public static Direction getDirection(android.hardware.camera2.CameraDevice cameraDevice){
    return getDirection(cameraDevice.getId());
}
public static Direction getDirection(String cameraId){
    Integer facing = getCharacteristics(cameraId).get(CameraCharacteristics.LENS_FACING);
    if(facing == null){
        return null;
    }
    return Direction.find(facing);
}
public static CameraCharacteristics getCharacteristics(android.hardware.camera2.CameraDevice camera){
    return getCharacteristics(camera.getId());
}

public static StreamConfigurationMap getStreamConfiguration(android.hardware.camera2.CameraDevice camera){
    return getStreamConfiguration(camera.getId());
}
public static StreamConfigurationMap getStreamConfiguration(String cameraId){
    return getCharacteristics(cameraId).get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
}
public static int getSensorOrientation(android.hardware.camera2.CameraDevice camera){
    return getSensorOrientation(camera.getId());
}
public static int getSensorOrientation(String cameraId){
    return getCharacteristics(cameraId).get(CameraCharacteristics.SENSOR_ORIENTATION);
}
public static Size[] getOutputSizes(android.hardware.camera2.CameraDevice camera, Class type){
    return getOutputSizes(camera.getId(), type);
}
public static Size[] getOutputSizes(String cameraId, Class type){
    return getStreamConfiguration(cameraId).getOutputSizes(type);
}
public static Size[] getOutputSizes(android.hardware.camera2.CameraDevice camera, int type){
    return getOutputSizes(camera.getId(), type);
}
public static Size[] getOutputSizes(String cameraId, int type){
    return getStreamConfiguration(cameraId).getOutputSizes(type);
}
public static boolean hasFlash(Direction direction){
    return hasFlash(getId(direction));
}
public static boolean hasFlash(String cameraId){
    CameraCharacteristics chars = getCharacteristics(cameraId);
    if(chars == null){
        return false;
    }
    return Compare.isTrue(chars.get(CameraCharacteristics.FLASH_INFO_AVAILABLE));
}
private static Class<CameraDevice> myClass(){
    return CameraDevice.class;
}
public static TaskState.Observable PERMISSION_REQUEST_CAMERA(boolean showPermissionDeniedToast){
    TaskState task = new TaskState();
    AppPermission.request().add(Manifest.permission.CAMERA).observe(new ObserverValue<ListEntry<String, Boolean>>(myClass()){
        @Override
        public void onComplete(ListEntry<String, Boolean> permissions){
            if(AppPermission.allTrue(permissions)){
                task.notifyComplete();
            } else {
                if(showPermissionDeniedToast){
                    AppInfo.toast(R.string.lbl_permission_denied, StatusParam.DELAY_FAIL_LONG_ms, StatusParam.Color.FAILED);
                }
                task.notifyException("Camera permission denied");
            }
        }
    }).start();
    return task.getObservable();
}
public static boolean PERMISSION_CHECK_CAMERA(){
    return AppPermission.allTrue(AppPermission.check().add(Manifest.permission.CAMERA).result());
}
private CameraDevice me(){
    return this;
}
public String getName(){
    return "Camera_" + direction.name();
}
private void post(Event event, java.lang.Throwable e){
    if(notifier != null){
        ObservableValueE<Event>.Access access = notifier.obtainAccess(this, null);
        access.set(event, e);
    }
}
public Notifier.Subscription observe(ObserverValueE<Event> observer){
    if(notifier == null){
        notifier = new Notifier<>(new ObservableValueE<>(), false);
    }
    return notifier.register(observer);
}
public void unObserve(Object owner){
    if(notifier == null){
        return;
    }
    notifier.unregister(owner);
    if(!notifier.hasObserver()){
        notifier = null;
    }
}
public void unObserveAll(){
    notifier.unregisterAll();
    notifier = null;
}
public Size[] getOutputSizes(Object type){
    if(type instanceof Integer){
        return getStreamConfiguration(getId()).getOutputSizes((int)type);
    }
    if(type instanceof Class){
        return getStreamConfiguration(getId()).getOutputSizes((Class)type);
    }

DebugException.start().unknown("type", type).end();

    return null;
}
public android.hardware.camera2.CameraDevice getDevice(){
    return (android.hardware.camera2.CameraDevice)device;
}
public Direction getDirection(){
    return direction;
}
public String getId(){
    if(device != null){
        return getDevice().getId();
    } else {
        return getId(direction);
    }
}
public CameraCharacteristics getCharacteristics(){
    return getCharacteristics(getId());
}
public StreamConfigurationMap getStreamConfiguration(){
    return getStreamConfiguration(getId());
}
public int getSensorOrientation(){
    return getSensorOrientation(getId());
}
public Size[] getOutputSizes(Class type){
    return getStreamConfiguration(getId()).getOutputSizes(type);
}
public Size[] getOutputSizes(int type){
    return getStreamConfiguration(getId()).getOutputSizes(type);
}
public boolean hasFlash(){
    CameraCharacteristics chars = getCharacteristics(getId());
    return Compare.isTrue(chars.get(CameraCharacteristics.FLASH_INFO_AVAILABLE));
}
@SuppressLint("MissingPermission")
private void openCamera(TaskState task){
    if(task.isCanceled()){
        task.notifyCanceled();
        return;
    }
    if(isCameraOpened()){

DebugLog.start().send(me(), getName() + " camera is already opened").end();

        task.notifyComplete();
        return;
    }

DebugLog.start().send(me(), getName() + " camera opening").end();

    try{
        String cameraId = getId();
        if(cameraId == null){

DebugLog.start().send(me(), getName() + " camera not found").end();

            task.notifyException(CameraException.NOT_FOUND.make());
            return;
        }
        getManager().openCamera(cameraId, new CameraStateListenerW(){
            TaskState task = null;
            CameraStateListenerW init(TaskState task){
                this.task = task;
                return this;
            }
            void notifyComplete(){
                if(task != null){
                    TaskState tmp = task;
                    task = null;
                    tmp.notifyComplete();
                }
            }
            void notifyException(Throwable e){
                if(task != null){
                    TaskState tmp = task;
                    task = null;
                    tmp.notifyException(e);
                }
            }
            @Override
            public void onOpened(@NonNull android.hardware.camera2.CameraDevice camera){

DebugLog.start().send(me(), getName() + " camera opened").end();

                device = camera;
                post(Event.CAMERA_OPENED, null);
                notifyComplete();
            }
            @Override
            public void onClosed(@NonNull android.hardware.camera2.CameraDevice camera){

DebugLog.start().send(me(), getName() + " camera closed").end();

                post(Event.CAMERA_CLOSED, null);
            }
            @Override
            public void onDisconnected(@NonNull android.hardware.camera2.CameraDevice camera){

DebugLog.start().send(me(), getName() + " camera disconnected").end();

                post(Event.CAMERA_DISCONNECTED, null);
                if(device != null){
                    getDevice().close();
                    device = null;
                }
            }
            @Override
            public void onError(@NonNull android.hardware.camera2.CameraDevice camera, int error){

DebugLog.start().send(me(), getName() + " camera error").end();

                notifyException(CameraException.ERROR.make(error));
                if(device != null){
                    getDevice().close();
                    device = null;
                }
            }
        }.init(task), getDeviceHandler());
    } catch(java.lang.Throwable e){
        task.notifyException(e);
    }
}
private void prepareSurfaces(SurfacesSupplier supplier, TaskState task){

DebugLog.start().send(me(), getName() + " prepare surfaces").end();

    this.surfacesSupplier = supplier;
    supplier.prepare(task);
}
private void startSession(TaskState task, Event event){
    if(task.isCanceled()){
        task.notifyCanceled();
        return;
    }
    if(isSessionStarted()){

DebugLog.start().send(me(), getName() + " session is already started").end();

        task.notifyComplete();
        return;
    }
    if(!isCameraOpened()){

DebugLog.start().send(me(), getName() + " camera is not opened").end();

        task.notifyException(CameraException.NOT_OPENED.make());
        return;
    }
DebugLog.start().send(me(), getName() + " session starting").end();
    try{
        CameraCaptureSessionStateListenerW listener = new CameraCaptureSessionStateListenerW(){
            WR<TaskState> taskWR = null;
            CameraCaptureSessionStateListenerW init(TaskState task){
                taskWR = WR.newInstance(task);
                return this;
            }
            TaskState getTask(){
                return taskWR.get();
            }
            @Override
            public void onConfigured(@NonNull CameraCaptureSession session){

DebugLog.start().send(me(), getName() + " session started").end();

                me().session = session;
                post(event, null);
                getTask().notifyComplete();
            }
            @Override
            public void onConfigureFailed(@NonNull CameraCaptureSession session){

DebugLog.start().send(me(), getName() + " session start failed").end();

                getTask().notifyException(CameraException.SESSION_CONFIGURATION_FAILED.make());
            }
            @Override
            public void onClosed(@NonNull CameraCaptureSession session){

DebugLog.start().send(me(), getName() + " session stopped").end();

                post(Event.SESSION_STOPPED, null);
            }
        }.init(task);
        if(VersionSDK.isSupEqualTo28_P()){
            createCaptureSession_after28_P(listener);
        } else {
            createCaptureSession_before28_P(listener);
        }
    } catch(java.lang.Throwable e){
        task.notifyException(e);
    }
}
@RequiresApi(api = Build.VERSION_CODES.P)
private void createCaptureSession_after28_P(CameraCaptureSessionStateListenerW listener) throws CameraAccessException{
    List<Surface> surfaces = surfacesSupplier.get();
    List<OutputConfiguration> outputConfigurations = new ArrayList<>();
    for(Surface s: surfaces){
        outputConfigurations.add(new OutputConfiguration(s));
    }
    SessionConfiguration sessionConfiguration = new SessionConfiguration(SessionConfiguration.SESSION_REGULAR, outputConfigurations, r->getDeviceHandler().post(r), listener);
    getDevice().createCaptureSession(sessionConfiguration);
}
@SuppressWarnings("deprecation")
private void createCaptureSession_before28_P(CameraCaptureSessionStateListenerW listener) throws CameraAccessException{
    getDevice().createCaptureSession(surfacesSupplier.get(), listener, getDeviceHandler());
}

public int capture(CameraCaptureRequest request, CameraCaptureSessionListenerW listener) throws CameraAccessException{
    return getSession().capture(request.getRequest(), listener, getDeviceHandler());
}
public int setRepeatingRequest(CameraCaptureRequest request, CameraCaptureSessionListenerW listener) throws CameraAccessException{
    return getSession().setRepeatingRequest(request.getRequest(), listener, getDeviceHandler());
}
private void stopSession(TaskState task){
    if(task.isCanceled()){
        task.notifyCanceled();
        return;
    }
    if(!isSessionStarted()){

DebugLog.start().send(me(), getName() + " session is already stopped").end();

        task.notifyComplete();
        return;
    }

DebugLog.start().send(me(), getName() + " session stopping").end();

    observe(new ObserverValueE<Event>(this){
        @Override
        public void onComplete(Event event){
            if(event != Event.SESSION_STOPPED){
                return;
            }
            unsubscribe();
            session = null;
            task.notifyComplete();
        }
    });
    session.close();
}
private void closeCamera(TaskState task){
    if(task.isCanceled()){
        task.notifyCanceled();
        return;
    }
    if(!isCameraOpened()){

DebugLog.start().send(me(), getName() + " camera is already closed").end();

        task.notifyComplete();
        return;
    }
    if(isSessionStarted()){

DebugLog.start().send(me(), getName() + " session is not stopped").end();

        task.notifyException(CameraException.SESSION_NOT_STOPPED.make());
        return;
    }

DebugLog.start().send(me(), getName() + " camera closing").end();

    observe(new ObserverValueE<Event>(this){
        @Override
        public void onComplete(Event event){
            if(event != Event.CAMERA_CLOSED){
                return;
            }
            unsubscribe();
            device = null;
            quitDeviceHandler(false);
            task.notifyComplete();
        }
    });
    getDevice().close();
}
private void removeSurfacesSupplier(){

DebugLog.start().send(me(), getName() + " remove surfaces supplier").end();

    this.surfacesSupplier = null;
}
private Handler getDeviceHandler(){
    if(deviceHandler == null){
        createDeviceHandler();
    }
    return deviceHandler;
}
private void createDeviceHandler(){
    deviceHandler = Handler.newHandler(this).setUncaughtExceptionHandler(new UncaughtExceptionHandlerW(){
        @Override
        public void uncaughtException(java.lang.Thread t, Throwable e){
DebugException.start().log(e).end();
            post(Event.ERROR, (java.lang.Throwable)e);
            quitDeviceHandler(true);
        }
    });
DebugLog.start().send(me(), getName() + " create thread ").end();
}
private void quitDeviceHandler(boolean force){
    if((deviceHandler != null) && ((!queries.isBusy() && queries.isEmpty()) || !deviceHandler.isAlive() || force)){
        deviceHandler.quit();
        deviceHandler = null;

DebugLog.start().send(me(), getName() + " quit thread ").end();

    }
}
private void postQuery(Query query){
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
        if(!isCameraOpened()){
            quitDeviceHandler(true);
        }
    }
}
public CameraCaptureSession getSession(){
    return session;
}
public boolean isCameraOpened(){
    return (device != null);
}
public boolean isSessionStarted(){
    return (session != null);
}
public boolean isActive(){
    return isCameraOpened() && isSessionStarted();
}
public TaskState.Observable open(SurfacesSupplier supplier){
    Query query = new OpenQuery(this, supplier);
    postQuery(query);
    return query.getObservable();
}
public TaskState.Observable restartSession(){
    return open(surfacesSupplier);
}
public TaskState.Observable close(){
    return close(false);
}
public TaskState.Observable close(boolean force){
    if(force){
        queries.quitAndClear();
        if(session != null){
            session.close();
            session = null;
        }
        if(device != null){
            getDevice().close();
            device = null;
        }
        quitDeviceHandler(true);
        if(hasFlash()){
            setTorchIndependent(true);
            setTorch(false);
        }
        return TaskState.Complete();
    } else {
        Query query = new CloseQuery(this);
        postQuery(query);
        return query.getObservable();
    }
}
public boolean isTorchOn(){
    return hasFlash() && isTorchOn;
}
public boolean isTorchIndependent(){
    return isTorchIndependent;
}
public void setTorchIndependent(boolean flag){
    isTorchIndependent = flag;
}
public TaskState.Observable setTorch(boolean flag){
    this.isTorchOn = flag;
    TaskState task = new TaskState();
    if(!isSessionStarted() && isTorchIndependent){
        if(!hasFlash(getId())){
DebugException.start().log(CameraException.HAS_NO_FLASH.make()).end();
        } else {
            try{
                if(VersionSDK.isSupEqualTo23_MARSHMALLOW()){
                    CameraManager cameraManager = getManager();
                    cameraManager.setTorchMode(getId(), flag);
                } else {
                    //NOW + TEST
//                    Camera cam = getCam();
//                    if(flag){
//                        if(cam == null){
//                            cam = Camera.open();
//                        }
//                        Camera.Parameters p = cam.getParameters();
//                        p.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
//                        cam.setParameters(p);
//                        cam.startPreview();
//                    }
//                    else {
//                        if(cam != null){
//                            cam.stopPreview();
//                            cam.release();
//                        }
//                    }
                }
            } catch(java.lang.Throwable e){
DebugException.start().log(e).end();

            }
        }
    }
    task.notifyComplete();
    post(Event.TORCH_CHANGED, null);
    return task.getObservable();
}
public TaskState.Observable torchToggle(){
    return setTorch(!isTorchOn);
}
@Override
protected void finalize() throws Throwable{
    if(session != null){
        session.close();
    }
    if(device != null){
        getDevice().close();
    }
    quitDeviceHandler(true);
DebugTrack.start().destroy(this).end();
}


public enum Direction{
    FRONT(CameraCharacteristics.LENS_FACING_FRONT), BACK(CameraCharacteristics.LENS_FACING_BACK);
    int value;
    Direction(int value){
        this.value = value;
    }
    public static Direction find(int value){
        for(Direction cd: values()){
            if(cd.value == value){
                return cd;
            }
        }
        return null;
    }
    public int getValue(){
        return value;
    }
}

public enum Event{
    CAMERA_OPENED, CAMERA_CLOSED, CAMERA_DISCONNECTED, SESSION_STARTED, SESSION_RESTARTED, SESSION_STOPPED, TORCH_CHANGED, ERROR,
}

public enum CameraException{
    NOT_FOUND, ALREADY_ACTIVE, ALREADY_INACTIVE, NOT_OPENED, ERROR, HAS_NO_FLASH, SESSION_NOT_STOPPED, SESSION_CONFIGURATION_FAILED, SURFACES_IS_EMPTY;
    private java.lang.Throwable make(){
        return new Throwable(name());
    }
    private java.lang.Throwable make(String message){
        return new Throwable(name() + " / " + message);
    }
    private java.lang.Throwable make(int code){
        return new Throwable(name() + " / error code:" + code);
    }
    public boolean equals(java.lang.Throwable e){
        return name().startsWith(e.getMessage());
    }
}

public static abstract class Query extends RunnableGroup{
    protected final int KEY_TASK = key();
    protected final int KEY_SUB_TASK = key();
    protected final int KEY_SURFACES = key();
    protected final int KEY_SESSION_START_EVENT = key();

    private final CameraDevice cameraDevice;
    protected Query(CameraDevice cameraDevice, String name){
        super(cameraDevice);
        name(name);
        this.cameraDevice = cameraDevice;
        onCreate();
        put(KEY_TASK, new TaskState());
        setOnDone(new Action(){
            @Override
            public void runSafe(){
                notify(getTask());
                clear();
            }
        });
    }
    protected CameraDevice camera(){
        return cameraDevice;
    }

    protected abstract void onCreate();

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
        camera().post(Event.ERROR, e);
        done();
    }

}

private static class OpenQuery extends Query{
    private final static int LBL_PREPARE_SURFACES = AppUIDGenerator.nextInt();
    OpenQuery(CameraDevice cameraDevice, SurfacesSupplier supplier){
        super(cameraDevice, "query open camera");
        put(KEY_SURFACES, supplier);
    }
    SurfacesSupplier getSurfacesSupplier(){
        return get(KEY_SURFACES);
    }
    Event getSessionStartEvent(){
        return get(KEY_SESSION_START_EVENT);
    }
    void setSessionStartEvent(Event event){
        put(KEY_SESSION_START_EVENT, event);
    }
    @Override
    protected void onCreate(){
        add(new Action(){
            @Override
            public void runSafe(){
                TaskState task = newSubTask();
                task.observe(new ObserverStateE(this){
                    @Override
                    public void onComplete(){
                        if(getSurfacesSupplier() == null){
                            queryFailed(CameraException.SURFACES_IS_EMPTY.make());
                        } else if(!camera().isSessionStarted()){
                            skipUntilLabel(LBL_PREPARE_SURFACES);
                        } else {
                            setSessionStartEvent(Event.SESSION_RESTARTED);
                            next();
                        }
                    }
                    @Override
                    public void onException(java.lang.Throwable e){
                        queryFailed(e);
                    }
                });
                camera().openCamera(task);
            }
        }.name("openCamera"));
        add(new Action(){
            @Override
            public void runSafe(){
                TaskState stopSessionTask = newSubTask();
                stopSessionTask.observe(new ObserverStateE(this){
                    @Override
                    public void onComplete(){
                        setSessionStartEvent(Event.SESSION_RESTARTED);
                        next();
                    }
                });
                camera().stopSession(stopSessionTask);
            }
        }.name("stopSession"));
        add(new Action(LBL_PREPARE_SURFACES){
            @Override
            public void runSafe(){
                TaskState startSessionTask = newSubTask();
                startSessionTask.observe(new ObserverStateE(this){
                    @Override
                    public void onComplete(){
                        next();
                    }
                    @Override
                    public void onException(java.lang.Throwable e){
                        queryFailed(e);
                    }
                });
                camera().prepareSurfaces(getSurfacesSupplier(), startSessionTask);
            }
        }.name("prepareSurfaces"));
        add(new Action(){
            @Override
            public void runSafe(){
                TaskState startSessionTask = newSubTask();
                startSessionTask.observe(new ObserverStateE(this){
                    @Override
                    public void onComplete(){
                        queryComplete();
                    }
                    @Override
                    public void onException(java.lang.Throwable e){
                        queryFailed(e);
                    }
                });
                camera().startSession(startSessionTask, getSessionStartEvent());
            }
        }.name("startSession"));
    }

}

private static class CloseQuery extends Query{
    CloseQuery(CameraDevice cameraDevice){
        super(cameraDevice, "query close camera");
    }

    @Override
    protected void onCreate(){
        add(new Action(){
            @Override
            public void runSafe(){
                TaskState stopSessionTask = newSubTask();
                stopSessionTask.observe(new ObserverStateE(this){
                    @Override
                    public void onComplete(){
                        next();
                    }
                });
                camera().stopSession(stopSessionTask);
            }
        }.name("stopSession"));
        add(new Action(){
            @Override
            public void runSafe(){
                TaskState closeCameraTask = newSubTask();
                closeCameraTask.observe(new ObserverStateE(this){
                    @Override
                    public void onComplete(){
                        next();
                    }

                    @Override
                    public void onException(java.lang.Throwable e){
                        queryFailed(e);
                    }
                });
                camera().closeCamera(closeCameraTask);
            }
        }.name("closeCamera"));
        add(new Action(){
            @Override
            public void runSafe(){
                camera().removeSurfacesSupplier();
                queryComplete();
            }
        }.name("removeSurfaces"));
    }
    @Override
    protected void queryComplete(){
        restoreTorchState();
        super.queryComplete();
    }
    @Override
    protected void queryFailed(java.lang.Throwable e){
        restoreTorchState();
        super.queryFailed(e);
    }
    void restoreTorchState(){
        if(!camera().isSessionStarted() && camera().isTorchIndependent() && camera().isTorchOn()){
            camera().setTorch(true);
        }
    }

}


}

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
import java.util.List;
import java.util.LinkedList;
import java.util.Set;
import com.tezov.lib_java_android.database.sqlLite.filter.dbFilterOrder;
import com.tezov.lib_java_android.database.sqlLite.filter.chunk.ChunkCommand;
import androidx.fragment.app.Fragment;

import com.tezov.lib_java_android.application.VersionSDK;

import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureResult;
import android.view.Surface;

import com.tezov.lib_java_android.camera.CameraDevice;
import com.tezov.lib_java.debug.DebugLog;
import com.tezov.lib_java.debug.DebugString;
import com.tezov.lib_java.debug.DebugTrack;

import static android.hardware.camera2.CameraDevice.TEMPLATE_MANUAL;
import static android.hardware.camera2.CameraDevice.TEMPLATE_PREVIEW;
import static android.hardware.camera2.CameraDevice.TEMPLATE_RECORD;
import static android.hardware.camera2.CameraDevice.TEMPLATE_STILL_CAPTURE;
import static android.hardware.camera2.CameraDevice.TEMPLATE_VIDEO_SNAPSHOT;
import static android.hardware.camera2.CameraDevice.TEMPLATE_ZERO_SHUTTER_LAG;

public class CameraCaptureRequest{
private final CameraCaptureBase.RequestType type;
private final CaptureRequest.Builder builder;

public CameraCaptureRequest(CameraCaptureBase camera, CameraCaptureBase.RequestType type, TEMPLATE template) throws CameraAccessException{
    this(camera.getCamera(), type, template);

}

public CameraCaptureRequest(CameraDevice camera, CameraCaptureBase.RequestType type, TEMPLATE template) throws CameraAccessException{
DebugTrack.start().create(this).end();
    this.type = type;
    builder = camera.getDevice().createCaptureRequest(template.value);
}

public static void toDebugLogRequest(CaptureRequest request){
DebugLog.start().send(toStringDebugRequest(request)).end();
}

public static void toDebugLogResult(CaptureResult result){
DebugLog.start().send(toStringDebugResult(result)).end();
}

private static <E extends Enum<E>> void appendToString(DebugString data, E o){
    if(o != null){
        data.append(o).append(" # ");
    }
}

public static DebugString toStringDebugRequest(CaptureRequest request){
    DebugString data = new DebugString();
    data.append("REQUEST::");
    appendToString(data, CONTROL_MODE.find(request));
    appendToString(data, AF_MODE.find(request));
    appendToString(data, AF_TRIGGER.find(request));
    appendToString(data, AE_MODE.find(request));
    appendToString(data, AE_PRE_CAPTURE.find(request));
    appendToString(data, AE_LOCK.find(request));
    appendToString(data, AWB_MODE.find(request));
    appendToString(data, AWB_LOCK.find(request));
    appendToString(data, FLASH_MODE.find(request));
    return data;
}

public static DebugString toStringDebugResult(CaptureResult result){
    DebugString data = new DebugString();
    data.append("RESULT::");
    data.append(result.getFrameNumber()).append("::");
    appendToString(data, AF_STATE.find(result));
    appendToString(data, AF_TRIGGER.find(result));
    appendToString(data, AE_STATE.find(result));
    appendToString(data, AE_PRE_CAPTURE.find(result));
    appendToString(data, AWB_STATE.find(result));
    appendToString(data, FLASH_STATE.find(result));
    data.append(result.get(CaptureResult.LENS_FOCUS_DISTANCE)).append("/").append(result.get(CaptureResult.LENS_FOCAL_LENGTH));
    return data;
}

public CameraCaptureRequest addTarget(Surface surface){
    builder.addTarget(surface);
    return this;
}

public CameraCaptureBase.RequestType getType(){
    return type;
}

public CaptureRequest getRequest(){
    return builder.build();
}

final public void toDebugLog(){
DebugLog.start().send(toDebugString()).end();
}

public <T> void set(CaptureRequest.Key<T> key, T value){
    builder.set(key, value);
}

public CameraCaptureRequest set(CONTROL_MODE n){
    builder.set(CONTROL_MODE.keyRequest(), n.value);
    return this;
}

public CameraCaptureRequest set(AF_MODE n){
    builder.set(AF_MODE.keyRequest(), n.value);
    return this;
}

public CameraCaptureRequest set(AF_TRIGGER n){
    builder.set(AF_TRIGGER.keyRequest(), n.value);
    return this;
}

public CameraCaptureRequest set(AE_MODE n){
    builder.set(AE_MODE.keyRequest(), n.value);
    return this;
}

public CameraCaptureRequest set(AE_PRE_CAPTURE n){
    builder.set(AE_PRE_CAPTURE.keyRequest(), n.value);
    return this;
}

public CameraCaptureRequest set(AE_LOCK n){
    builder.set(AE_LOCK.keyRequest(), n.value);
    return this;
}

public CameraCaptureRequest set(AWB_MODE n){
    builder.set(AWB_MODE.keyRequest(), n.value);
    return this;
}

public CameraCaptureRequest set(AWB_LOCK n){
    builder.set(AWB_LOCK.keyRequest(), n.value);
    return this;
}

public CameraCaptureRequest set(FLASH_MODE n){
    builder.set(FLASH_MODE.keyRequest(), n.value);
    return this;
}

public DebugString toDebugString(){
    return toStringDebugRequest(builder.build());
}

@Override
protected void finalize() throws Throwable{
DebugTrack.start().destroy(this).end();
    super.finalize();
}

// TEMPLATE //

public enum TEMPLATE{
    PREVIEW(TEMPLATE_PREVIEW), STILL_CAPTURE(TEMPLATE_STILL_CAPTURE), RECORD(TEMPLATE_RECORD), SNAPSHOT(TEMPLATE_VIDEO_SNAPSHOT), ZERO_SHUTTER_LAG(TEMPLATE_ZERO_SHUTTER_LAG), MANUAL(TEMPLATE_MANUAL);
    int value;

    TEMPLATE(int value){
        this.value = value;
    }

    public DebugString toDebugString(){
        DebugString data = new DebugString();
        data.append("TEMPLATE", name());
        return data;
    }

    final public void toDebugLog(){
DebugLog.start().send(toDebugString()).end();
    }
}

// CONTROL //

public enum CONTROL_MODE{
    OFF(CameraMetadata.CONTROL_MODE_OFF), AUTO(CameraMetadata.CONTROL_MODE_AUTO), SCENE(CameraMetadata.CONTROL_MODE_USE_SCENE_MODE), KEEP_STATE(CameraMetadata.CONTROL_MODE_OFF_KEEP_STATE);
    int value;

    CONTROL_MODE(int value){
        this.value = value;
    }

    public static CaptureRequest.Key<Integer> keyRequest(){
        return CaptureRequest.CONTROL_MODE;
    }

    public static CaptureResult.Key<Integer> keyResult(){
        return CaptureResult.CONTROL_MODE;
    }

    public static CONTROL_MODE find(CaptureRequest.Builder builder){
        return find(builder.get(keyRequest()));
    }

    public static CONTROL_MODE find(CaptureRequest request){
        return find(request.get(keyRequest()));
    }

    public static CONTROL_MODE find(CaptureResult result){
        return find(result.get(keyResult()));
    }

    public static CONTROL_MODE find(Integer value){
        if(value == null){
            return null;
        }
        for(CONTROL_MODE e: values()){
            if(value.equals(e.value)){
                return e;
            }
        }
        return null;
    }

    public DebugString toDebugString(){
        DebugString data = new DebugString();
        data.append("CONTROL_MODE", name());
        return data;
    }

    final public void toDebugLog(){
DebugLog.start().send(toDebugString()).end();
    }
}

// AUTO FOCUS //

public enum AF_MODE{
    OFF(CameraMetadata.CONTROL_AF_MODE_OFF), AUTO(CameraMetadata.CONTROL_AF_MODE_AUTO), MACRO(CameraMetadata.CONTROL_AF_MODE_MACRO), VIDEO(
            CameraMetadata.CONTROL_AF_MODE_CONTINUOUS_VIDEO), CONTINUOUS_PICTURE(CameraMetadata.CONTROL_AF_MODE_CONTINUOUS_PICTURE), EDOF(CameraMetadata.CONTROL_AF_MODE_EDOF);
    int value;

    AF_MODE(int value){
        this.value = value;
    }

    public static CaptureRequest.Key<Integer> keyRequest(){
        return CaptureRequest.CONTROL_AF_MODE;
    }

    public static CaptureResult.Key<Integer> keyResult(){
        return CaptureResult.CONTROL_AF_MODE;
    }

    public static AF_MODE find(CaptureRequest.Builder builder){
        return find(builder.get(keyRequest()));
    }

    public static AF_MODE find(CaptureRequest request){
        return find(request.get(keyRequest()));
    }

    public static AF_MODE find(CaptureResult result){
        return find(result.get(keyResult()));
    }

    public static AF_MODE find(Integer value){
        if(value == null){
            return null;
        }
        for(AF_MODE e: values()){
            if(value.equals(e.value)){
                return e;
            }
        }
        return null;
    }

    public DebugString toDebugString(){
        DebugString data = new DebugString();
        data.append("AF_MODE", name());
        return data;
    }

    final public void toDebugLog(){
DebugLog.start().send(toDebugString()).end();
    }
}

public enum AF_TRIGGER{
    START(CameraMetadata.CONTROL_AF_TRIGGER_START), IDLE(CameraMetadata.CONTROL_AF_TRIGGER_IDLE), CANCEL(CameraMetadata.CONTROL_AF_TRIGGER_CANCEL);
    int value;

    AF_TRIGGER(int value){
        this.value = value;
    }

    public static CaptureRequest.Key<Integer> keyRequest(){
        return CaptureRequest.CONTROL_AF_TRIGGER;
    }

    public static CaptureResult.Key<Integer> keyResult(){
        return CaptureResult.CONTROL_AF_TRIGGER;
    }

    public static AF_TRIGGER find(CaptureRequest.Builder builder){
        return find(builder.get(keyRequest()));
    }

    public static AF_TRIGGER find(CaptureRequest request){
        return find(request.get(keyRequest()));
    }

    public static AF_TRIGGER find(CaptureResult result){
        return find(result.get(keyResult()));
    }

    public static AF_TRIGGER find(Integer value){
        if(value == null){
            return null;
        }
        for(AF_TRIGGER e: values()){
            if(value.equals(e.value)){
                return e;
            }
        }
        return null;
    }

    public DebugString toDebugString(){
        DebugString data = new DebugString();
        data.append("AF_TRIGGER", name());
        return data;
    }

    final public void toDebugLog(){
DebugLog.start().send(toDebugString()).end();
    }
}

public enum AF_STATE{
    INACTIVE(CameraMetadata.CONTROL_AF_STATE_INACTIVE), SCAN_PASSIVE(CameraMetadata.CONTROL_AF_STATE_PASSIVE_SCAN), FOCUSED_PASSIVE(CameraMetadata.CONTROL_AF_STATE_PASSIVE_FOCUSED), SCAN_ACTIVE(
            CameraMetadata.CONTROL_AF_STATE_ACTIVE_SCAN), FOCUSED_LOCK(CameraMetadata.CONTROL_AF_STATE_FOCUSED_LOCKED), FOCUSED_NOT_LOCK(
            CameraMetadata.CONTROL_AF_STATE_NOT_FOCUSED_LOCKED), UNFOCUSED_PASSIVE(CameraMetadata.CONTROL_AF_STATE_PASSIVE_UNFOCUSED);
    int value;

    AF_STATE(int value){
        this.value = value;
    }

    public static CaptureResult.Key<Integer> keyResult(){
        return CaptureResult.CONTROL_AF_STATE;
    }

    public static AF_STATE find(CaptureResult result){
        return find(result.get(keyResult()));
    }

    public static AF_STATE find(Integer value){
        if(value == null){
            return null;
        }
        for(AF_STATE e: values()){
            if(value.equals(e.value)){
                return e;
            }
        }
        return null;
    }

    public DebugString toDebugString(){
        DebugString data = new DebugString();
        data.append("AF_STATE", name());
        return data;
    }

    final public void toDebugLog(){
DebugLog.start().send(toDebugString()).end();
    }

}

// AUTO EXPOSURE //

public enum AE_MODE{
    OFF(CameraMetadata.CONTROL_AE_MODE_OFF), ON(CameraMetadata.CONTROL_AE_MODE_ON), FLASH_ALWAYS(CameraMetadata.CONTROL_AE_MODE_ON_ALWAYS_FLASH), FLASH_AUTO(
            CameraMetadata.CONTROL_AE_MODE_ON_AUTO_FLASH), FLASH_AUTO_RED_EYE(CameraMetadata.CONTROL_AE_MODE_ON_AUTO_FLASH_REDEYE);
    int value;

    AE_MODE(int value){
        this.value = value;
    }

    public static CaptureRequest.Key<Integer> keyRequest(){
        return CaptureRequest.CONTROL_AE_MODE;
    }

    public static CaptureResult.Key<Integer> keyResult(){
        return CaptureResult.CONTROL_AE_MODE;
    }

    public static AE_MODE find(CaptureRequest.Builder builder){
        return find(builder.get(keyRequest()));
    }

    public static AE_MODE find(CaptureRequest request){
        return find(request.get(keyRequest()));
    }

    public static AE_MODE find(CaptureResult result){
        return find(result.get(keyResult()));
    }

    public static AE_MODE find(Integer value){
        if(value == null){
            return null;
        }
        for(AE_MODE e: values()){
            if(value.equals(e.value)){
                return e;
            }
        }
        return null;
    }

    public DebugString toDebugString(){
        DebugString data = new DebugString();
        data.append("AE_MODE", name());
        return data;
    }

    final public void toDebugLog(){
DebugLog.start().send(toDebugString()).end();
    }
}

public enum AE_PRE_CAPTURE{
    START(CameraMetadata.CONTROL_AE_PRECAPTURE_TRIGGER_START),
    IDLE(CameraMetadata.CONTROL_AE_PRECAPTURE_TRIGGER_IDLE),
    CANCEL(VersionSDK.isSupEqualTo23_MARSHMALLOW()?CameraMetadata.CONTROL_AE_PRECAPTURE_TRIGGER_CANCEL:CameraMetadata.CONTROL_AE_PRECAPTURE_TRIGGER_IDLE);
    int value;

    AE_PRE_CAPTURE(int value){
        this.value = value;
    }
    public static CaptureRequest.Key<Integer> keyRequest(){
        return CaptureRequest.CONTROL_AE_PRECAPTURE_TRIGGER;
    }
    public static CaptureResult.Key<Integer> keyResult(){
        return CaptureResult.CONTROL_AE_PRECAPTURE_TRIGGER;
    }
    public static AE_PRE_CAPTURE find(CaptureRequest.Builder builder){
        return find(builder.get(keyRequest()));
    }
    public static AE_PRE_CAPTURE find(CaptureRequest request){
        return find(request.get(keyRequest()));
    }
    public static AE_PRE_CAPTURE find(CaptureResult result){
        return find(result.get(keyResult()));
    }
    public static AE_PRE_CAPTURE find(Integer value){
        if(value == null){
            return null;
        }
        for(AE_PRE_CAPTURE e: values()){
            if(value.equals(e.value)){
                return e;
            }
        }
        return null;
    }
    public DebugString toDebugString(){
        DebugString data = new DebugString();
        data.append("AE_PRE_CAPTURE", name());
        return data;
    }
    final public void toDebugLog(){
DebugLog.start().send(toDebugString()).end();
    }
}

public enum AE_LOCK{
    OFF(false), ON(true);
    boolean value;

    AE_LOCK(boolean value){
        this.value = value;
    }

    public static CaptureRequest.Key<Boolean> keyRequest(){
        return CaptureRequest.CONTROL_AE_LOCK;
    }

    public static CaptureResult.Key<Boolean> keyResult(){
        return CaptureResult.CONTROL_AE_LOCK;
    }

    public static AE_LOCK find(CaptureRequest.Builder builder){
        return find(builder.get(keyRequest()));
    }

    public static AE_LOCK find(CaptureRequest request){
        return find(request.get(keyRequest()));
    }

    public static AE_LOCK find(CaptureResult result){
        return find(result.get(keyResult()));
    }

    public static AE_LOCK find(Boolean value){
        if(value == null){
            return null;
        }
        for(AE_LOCK e: values()){
            if(value.equals(e.value)){
                return e;
            }
        }
        return null;
    }

    public DebugString toDebugString(){
        DebugString data = new DebugString();
        data.append("AE_LOCK", name());
        return data;
    }

    final public void toDebugLog(){
DebugLog.start().send(toDebugString()).end();
    }
}

public enum AE_STATE{
    INACTIVE(CameraMetadata.CONTROL_AE_STATE_INACTIVE), SEARCHING(CameraMetadata.CONTROL_AE_STATE_SEARCHING), CONVERGED(CameraMetadata.CONTROL_AE_STATE_CONVERGED), LOCKED(
            CameraMetadata.CONTROL_AE_STATE_LOCKED), REQUIRE_FLASH(CameraMetadata.CONTROL_AE_STATE_FLASH_REQUIRED), PRECAPTURE(CameraMetadata.CONTROL_AE_STATE_PRECAPTURE);
    int value;

    AE_STATE(int value){
        this.value = value;
    }

    public static CaptureResult.Key<Integer> keyResult(){
        return CaptureResult.CONTROL_AE_STATE;
    }

    public static AE_STATE find(CaptureResult result){
        return find(result.get(keyResult()));
    }

    public static AE_STATE find(Integer value){
        if(value == null){
            return null;
        }
        for(AE_STATE e: values()){
            if(value.equals(e.value)){
                return e;
            }
        }
        return null;
    }

    public DebugString toDebugString(){
        DebugString data = new DebugString();
        data.append("AE_STATE", name());
        return data;
    }

    final public void toDebugLog(){
DebugLog.start().send(toDebugString()).end();
    }
}

// AUTO WHITE BALANCE //

public enum AWB_MODE{
    OFF(CameraMetadata.CONTROL_AWB_MODE_OFF), AUTO(CameraMetadata.CONTROL_AWB_MODE_AUTO), INCANDESCENT(CameraMetadata.CONTROL_AWB_MODE_INCANDESCENT), FLUORESCENT(
            CameraMetadata.CONTROL_AWB_MODE_FLUORESCENT), FLUORESCENT_WARM(CameraMetadata.CONTROL_AWB_MODE_WARM_FLUORESCENT), DAYLIGHT(CameraMetadata.CONTROL_AWB_MODE_DAYLIGHT), DAYLIGHT_CLOUDY(
            CameraMetadata.CONTROL_AWB_MODE_CLOUDY_DAYLIGHT), TWILIGHT(CameraMetadata.CONTROL_AWB_MODE_TWILIGHT), SHADE(CameraMetadata.CONTROL_AWB_MODE_SHADE);
    int value;

    AWB_MODE(int value){
        this.value = value;
    }

    public static CaptureRequest.Key<Integer> keyRequest(){
        return CaptureRequest.CONTROL_AWB_MODE;
    }

    public static CaptureResult.Key<Integer> keyResult(){
        return CaptureResult.CONTROL_AWB_MODE;
    }

    public static AWB_MODE find(CaptureRequest.Builder builder){
        return find(builder.get(keyRequest()));
    }

    public static AWB_MODE find(CaptureRequest request){
        return find(request.get(keyRequest()));
    }

    public static AWB_MODE find(CaptureResult result){
        return find(result.get(keyResult()));
    }

    public static AWB_MODE find(Integer value){
        if(value == null){
            return null;
        }
        for(AWB_MODE e: values()){
            if(value.equals(e.value)){
                return e;
            }
        }
        return null;
    }

    public DebugString toDebugString(){
        DebugString data = new DebugString();
        data.append("AWB_MODE", name());
        return data;
    }

    final public void toDebugLog(){
DebugLog.start().send(toDebugString()).end();
    }
}

public enum AWB_LOCK{
    OFF(false), ON(true);
    boolean value;

    AWB_LOCK(boolean value){
        this.value = value;
    }

    public static CaptureRequest.Key<Boolean> keyRequest(){
        return CaptureRequest.CONTROL_AWB_LOCK;
    }

    public static CaptureResult.Key<Boolean> keyResult(){
        return CaptureResult.CONTROL_AWB_LOCK;
    }

    public static AWB_LOCK find(CaptureRequest.Builder builder){
        return find(builder.get(keyRequest()));
    }

    public static AWB_LOCK find(CaptureRequest request){
        return find(request.get(keyRequest()));
    }

    public static AWB_LOCK find(CaptureResult result){
        return find(result.get(keyResult()));
    }

    public static AWB_LOCK find(Boolean value){
        if(value == null){
            return null;
        }
        for(AWB_LOCK e: values()){
            if(value.equals(e.value)){
                return e;
            }
        }
        return null;
    }

    public DebugString toDebugString(){
        DebugString data = new DebugString();
        data.append("AWB_LOCK", name());
        return data;
    }

    final public void toDebugLog(){
DebugLog.start().send(toDebugString()).end();
    }
}

public enum AWB_STATE{
    INACTIVE(CameraMetadata.CONTROL_AWB_STATE_INACTIVE), SEARCHING(CameraMetadata.CONTROL_AWB_STATE_SEARCHING), CONVERGED(CameraMetadata.CONTROL_AWB_STATE_CONVERGED), LOCKED(
            CameraMetadata.CONTROL_AWB_STATE_LOCKED);
    int value;

    AWB_STATE(int value){
        this.value = value;
    }

    public static CaptureResult.Key<Integer> keyResult(){
        return CaptureResult.CONTROL_AWB_STATE;
    }

    public static AWB_STATE find(CaptureResult result){
        return find(result.get(keyResult()));
    }

    public static AWB_STATE find(Integer value){
        if(value == null){
            return null;
        }
        for(AWB_STATE e: values()){
            if(value.equals(e.value)){
                return e;
            }
        }
        return null;
    }

    public DebugString toDebugString(){
        DebugString data = new DebugString();
        data.append("AWB_STATE", name());
        return data;
    }

    final public void toDebugLog(){
DebugLog.start().send(toDebugString()).end();
    }
}

// FLASH //

public enum FLASH_MODE{
    OFF(CameraMetadata.FLASH_MODE_OFF), SINGLE(CameraMetadata.FLASH_MODE_SINGLE), TORCH(CameraMetadata.FLASH_MODE_TORCH);
    int value;

    FLASH_MODE(int value){
        this.value = value;
    }

    public static CaptureRequest.Key<Integer> keyRequest(){
        return CaptureRequest.FLASH_MODE;
    }

    public static CaptureResult.Key<Integer> keyResult(){
        return CaptureResult.FLASH_MODE;
    }

    public static FLASH_MODE find(CaptureRequest.Builder builder){
        return find(builder.get(keyRequest()));
    }

    public static FLASH_MODE find(CaptureRequest request){
        return find(request.get(keyRequest()));
    }

    public static FLASH_MODE find(CaptureResult result){
        return find(result.get(keyResult()));
    }

    public static FLASH_MODE find(Integer value){
        if(value == null){
            return null;
        }
        for(FLASH_MODE e: values()){
            if(value.equals(e.value)){
                return e;
            }
        }
        return null;
    }

    public DebugString toDebugString(){
        DebugString data = new DebugString();
        data.append("FLASH_MODE", name());
        return data;
    }

    final public void toDebugLog(){
DebugLog.start().send(toDebugString()).end();
    }
}

public enum FLASH_STATE{
    UNAVAILABLE(CameraMetadata.FLASH_STATE_UNAVAILABLE), CHARGING(CameraMetadata.FLASH_STATE_CHARGING), READY(CameraMetadata.FLASH_STATE_READY), FIRED(CameraMetadata.FLASH_STATE_FIRED), PARTIAL(
            CameraMetadata.FLASH_STATE_PARTIAL);
    int value;

    FLASH_STATE(int value){
        this.value = value;
    }

    public static CaptureResult.Key<Integer> keyResult(){
        return CaptureResult.FLASH_STATE;
    }

    public static FLASH_STATE find(CaptureResult result){
        return find(result.get(keyResult()));
    }

    public static FLASH_STATE find(Integer value){
        if(value == null){
            return null;
        }
        for(FLASH_STATE e: values()){
            if(value.equals(e.value)){
                return e;
            }
        }
        return null;
    }

    public DebugString toDebugString(){
        DebugString data = new DebugString();
        data.append("FLASH_STATE", name());
        return data;
    }

    final public void toDebugLog(){
DebugLog.start().send(toDebugString()).end();
    }
}

}

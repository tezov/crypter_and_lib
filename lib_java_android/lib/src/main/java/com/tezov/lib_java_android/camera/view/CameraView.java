/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java_android.camera.view;

import com.tezov.lib_java.debug.DebugLog;
import com.tezov.lib_java.debug.DebugTrack;
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
import com.tezov.lib_java_android.toolbox.PostToHandler;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.SurfaceTexture;
import android.util.AttributeSet;
import android.view.Surface;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import androidx.annotation.Nullable;

import com.tezov.lib_java_android.application.AppDisplay;
import com.tezov.lib_java_android.application.VersionSDK;
import com.tezov.lib_java.async.notifier.Notifier;
import com.tezov.lib_java.async.notifier.observable.ObservableValueE;
import com.tezov.lib_java.async.notifier.observer.value.ObserverValue;
import com.tezov.lib_java.async.notifier.task.TaskState;
import com.tezov.lib_java_android.camera.SurfacesSupplier;
import com.tezov.lib_java_android.camera.capture.CameraCaptureBase;
import com.tezov.lib_java_android.camera.capture.CameraCaptureBase.SizeType;
import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java_android.type.image.ImageReader;
import com.tezov.lib_java_android.type.image.imageHolder.ImageHolder;
import com.tezov.lib_java.type.primaire.Size;
import com.tezov.lib_java.type.ref.WR;
import com.tezov.lib_java.type.runnable.RunnableW;
import com.tezov.lib_java_android.wrapperAnonymous.ImageReaderOnImageAvailableListenerW;
import com.tezov.lib_java_android.wrapperAnonymous.SurfaceTextureStateListenerW;
import com.tezov.lib_java_android.type.android.wrapper.TextureViewW;
import com.tezov.lib_java_android.ui.layout.FrameLayout;
import com.tezov.lib_java_android.ui.layout.RelativeLayout;

import java.util.ArrayList;
import java.util.List;

import static com.tezov.lib_java_android.type.image.ImageReader.ImageFormat;
import static com.tezov.lib_java_android.type.image.ImageReader.newInstance;

public abstract class CameraView extends RelativeLayout{
protected static final int IMAGE_READER_SIZE = 1;
private WR<CameraCaptureBase> capture = null;
private Notifier<Void> notifier;
private boolean isFrozen = true;
private TextureViewW surfaceView;
private ImageFormat imageFormat = ImageFormat.NV21;
private ImageReader imageReader = null;
private ImageView imageView;
private Size captureSize = null;
private boolean isBusy = false;

public CameraView(Context context){
    super(context);
    init(context, null, 0, 0);
}
public CameraView(Context context, @Nullable AttributeSet attrs){
    super(context, attrs);
    init(context, attrs, 0, 0);
}
public CameraView(Context context, @Nullable AttributeSet attrs, int defStyleAttr){
    super(context, attrs, defStyleAttr);
    init(context, attrs, defStyleAttr, 0);
}
public CameraView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes){
    super(context, attrs, defStyleAttr, defStyleRes);
    init(context, attrs, defStyleAttr, defStyleRes);
}

private void init(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes){
    notifier = new Notifier<>(new ObservableValueE(), false);
    ViewGroup.LayoutParams param = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    surfaceView = new TextureViewW(context);
    surfaceView.setLayoutParams(param);
    surfaceView.show(false);
    addView(surfaceView);

    imageView = new ImageView(context);
    imageView.setLayoutParams(param);
    imageView.setScaleType(ScaleType.CENTER_CROP);
    imageView.setVisibility(INVISIBLE);
    addView(imageView);

    surfaceView.setSurfaceTextureListener(new SurfaceTextureStateListenerW(){
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height){
            Size previewSize = capture.get().findSizeOptimal(width, height, SizeType.SURFACE_HOLDER);
            surfaceView.createSurface(previewSize);
            if(!VersionSDK.isSupEqualTo24_NOUGAT()){
                int textureViewRotation = computeOrientation() + 90;
                if(textureViewRotation == 0){
                    return;
                }
                Matrix m = new Matrix();
                float pivotX = width / 2.0f;
                float pivotY = height / 2.0f;
                m.postRotate(textureViewRotation, pivotX, pivotY);
                RectF originalTextureRect = new RectF(0, 0, width, height);
                RectF rotatedTextureRect = new RectF();
                m.mapRect(rotatedTextureRect, originalTextureRect);
                m.postScale(width / rotatedTextureRect.width(), height / rotatedTextureRect.height(), pivotX, pivotY);
                surfaceView.setTransform(m);
            }
            post(Event.AVAILABLE);
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height){
            post(Event.CHANGED);
        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface){
            surfaceView.destroySurface();
            post(Event.DESTROYED);
            return super.onSurfaceTextureDestroyed(surface);
        }
    });
}

protected CameraView me(){
    return this;
}

protected TextureViewW getSurfaceView(){
    return surfaceView;
}
protected ImageView getImageView(){
    return imageView;
}

protected ImageHolder acquireNextImage(){
    return imageReader.acquireNextImageHolder();
}
protected void discardNextImage(){
    imageReader.discardNextImage();
}

public void attach(CameraCaptureBase camera){
    capture = WR.newInstance(camera);
}

public void freeze(boolean flag){
    synchronized(this){
        isFrozen = flag;
    }
}
public boolean isFrozen(){
    synchronized(this){
        return isFrozen;
    }
}
public boolean isAvailable(){
    return surfaceView.isAvailable();
}

protected void post(Event event){
    ObservableValueE<Event>.Access access = notifier.obtainAccess(this, null);
    access.set(event, null);
}
public Notifier.Subscription observe(ObserverValue<Event> observer){
    synchronized(this){
        Notifier.Subscription subscription = notifier.register(observer);
        if(isAvailable()){
            post(Event.AVAILABLE);
        }
        if(!isAvailable()){
            post(Event.DESTROYED);
        }
        return subscription;
    }
}
public void unObserve(Object owner){
    notifier.unregister(owner);
}
public void unObserveAll(){
    notifier.unregisterAll();
}

public synchronized boolean isBusy(){
    synchronized(this){
        return isBusy;
    }
}
public synchronized CameraView setBusy(boolean busy){
    synchronized(this){
        isBusy = busy;
        return this;
    }
}

public SurfacesSupplier getSurfacesSupplier(){
    return new SurfacesSupplier(){
        @Override
        public void prepare(TaskState task){
            if(!isFrozen() && isAvailable()){
                task.notifyComplete();
            } else {
                observe(new ObserverValue<Event>(this){
                    @Override
                    public void onComplete(CameraView.Event event){
                        if(event == Event.AVAILABLE){
                            unsubscribe();
                            task.notifyComplete();
                        }
                    }
                });
            }
        }

        @Override
        public List<Surface> get(){
            List<Surface> surfaces = new ArrayList<>();
            surfaces.add(surfaceView.getSurface());
            surfaces.add(obtainSnapshotSurface().getSurface());
            return surfaces;
        }
    };
}

private int computeOrientation(){
    return 360 - (((180 - AppDisplay.getOrientationAngle()) % 180) + capture.get().getCamera().getSensorOrientation());
}

public Surface getSurface(Type type){
    switch(type){
        case CAMERA:
            return surfaceView.getSurface();
        case SNAPSHOT:
            return obtainSnapshotSurface().getSurface();
        default:

DebugException.start().unknown("type", type).end();

            return null;
    }
}

public ImageFormat getImageFormat(){
    return imageFormat;
}
public void setImageFormat(ImageFormat format){

    if((format != ImageFormat.JPEG) && (format != ImageFormat.NV21)){
DebugException.start().explode("Unsupported format " + format.name()).end();
    }


    this.imageFormat = format;
}
public SizeType getSizeFormat(){
    if(this.imageFormat == ImageFormat.JPEG){
        return SizeType.JPEG;
    } else if(this.imageFormat == ImageFormat.NV21){
        return SizeType.YUV_420_888;
    } else {

DebugException.start().explode("Unsupported format size" + imageFormat.name()).end();

        return null;
    }

}

public Size getCaptureSize(){
    return captureSize;
}
public CameraView setCaptureSize(Size captureSize){
    this.captureSize = captureSize;
    return this;
}

private Size computeCaptureSize(){
    if(captureSize != null){
        return captureSize;
    } else {
        CameraCaptureBase capture = this.capture.get();
        return capture.findSizeOptimal(getWidth(), getHeight(), getSizeFormat());
    }
}
private ImageReader newImageReader(){
    CameraCaptureBase capture = this.capture.get();
    Size captureSize = computeCaptureSize();
    ImageReader reader = newInstance(captureSize, imageFormat, IMAGE_READER_SIZE);
    if(VersionSDK.isSupEqualTo24_NOUGAT()){
        reader.setOrientation(capture.getCamera().getSensorOrientation());
    } else {
        reader.setOrientation(computeOrientation());
    }
    reader.setOnImageAvailableListener(new ImageReaderOnImageAvailableListenerW(){
        @Override
        public void onImageAvailable(){
            if(!isBusy() && !isFrozen()){
                me().onImageAvailable();
            } else {
                discardNextImage();
            }
        }
    }, null);
    return reader;
}

private ImageReader obtainSnapshotSurface(){
    if(imageReader == null){
        imageReader = newImageReader();
    }
    return imageReader;
}
public void show(Type type, boolean flag){
    switch(type){
        case CAMERA:{
            if(flag){
                PostToHandler.of(this, new RunnableW(){
                    @Override
                    public void runSafe(){
                        imageView.setVisibility(INVISIBLE);
                        surfaceView.show(true);
                    }
                });
            } else {
                PostToHandler.of(this, new RunnableW(){
                    @Override
                    public void runSafe(){
                        surfaceView.show(false);
                    }
                });
            }
        }
        break;
        case SNAPSHOT:{
            if(flag){
                PostToHandler.of(this, new RunnableW(){
                    @Override
                    public void runSafe(){
                        surfaceView.show(false);
                        imageView.setVisibility(VISIBLE);
                    }
                });
            } else {
                PostToHandler.of(this, new RunnableW(){
                    @Override
                    public void runSafe(){
                        imageView.setVisibility(INVISIBLE);
                    }
                });
            }
        }
        break;
    }

}
public void toggleVisibility(Type type){
    switch(type){
        case CAMERA:{
            show(Type.CAMERA, surfaceView.getAlpha() == 0f);
        }
        break;
        case SNAPSHOT:{
            show(Type.SNAPSHOT, imageView.getVisibility() == INVISIBLE);
        }
        break;
    }
}
public boolean isVisible(Type type){
    switch(type){
        case CAMERA:
            return surfaceView.getAlpha() == 1f;
        case SNAPSHOT:
            return imageView.getVisibility() == VISIBLE;
    }

DebugException.start().unknown("type", type).end();

    return false;
}
public void setSize(int width, int height){
    ViewGroup.LayoutParams param = getLayoutParams();
    if(param == null){
        param = new FrameLayout.LayoutParams(width, height);
    } else {
        param.width = width;
        param.height = height;
    }
    setLayoutParams(param);
}
public Size getSize(){
    return new Size(getWidth(), getHeight());
}
public void setSize(Size size){
    setSize(size.getWidth(), size.getHeight());
}
public abstract void onImageAvailable();
public void setImageBitmap(Bitmap b){
    PostToHandler.of(this, new RunnableW(){
        @Override
        public void runSafe(){
            imageView.setImageBitmap(b);
        }
    });
}

public enum Event{
    AVAILABLE, CHANGED, DESTROYED, ACQUIRED,
}
public enum Type{
    CAMERA, SNAPSHOT,
}

}

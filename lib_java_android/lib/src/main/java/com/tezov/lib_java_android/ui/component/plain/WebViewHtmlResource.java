/*
 * ********************************************************************************
 * Created by Tezov under MIT LICENCE.
 * For any request, please send me an email to tezov.app@gmail.com.
 * I'll be glad to answer you if your request is sane :)
 * ********************************************************************************
 */

package com.tezov.lib_java_android.ui.component.plain;

import com.tezov.lib_java.debug.DebugLog;
import com.tezov.lib_java.type.primitive.ObjectTo;
import com.tezov.lib_java.type.primitive.IntTo;
import com.tezov.lib_java.type.unit.UnitByte;
import com.tezov.lib_java.toolbox.CompareType;
import com.tezov.lib_java.toolbox.Clock;
import com.tezov.lib_java.util.UtilsString;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.LinkedList;
import java.util.Set;
import com.tezov.lib_java_android.database.sqlLite.filter.dbFilterOrder;
import com.tezov.lib_java_android.database.sqlLite.filter.chunk.ChunkCommand;
import androidx.fragment.app.Fragment;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.tezov.lib_java_android.R;
import com.tezov.lib_java_android.application.AppContext;
import com.tezov.lib_java.toolbox.Compare;
import com.tezov.lib_java_android.toolbox.PostToHandler;
import com.tezov.lib_java.debug.DebugException;
import com.tezov.lib_java.debug.DebugTrack;
import com.tezov.lib_java.type.runnable.RunnableW;
import com.tezov.lib_java_android.ui.misc.AttributeReader;
import com.tezov.lib_java_android.util.UtilsResourceRaw;

public class WebViewHtmlResource extends WebView{
private final static String DEFAULT_ENCODING = StandardCharsets.UTF_8.name();
private final int[] ATTRS = R.styleable.WebViewHtmlResource_lib;
private Integer id = null;
private String encoding = null;
private Integer maxHeight = null;

public WebViewHtmlResource(Context context){
    super(context);
    init(context, null, NO_ID, NO_ID);
}
public WebViewHtmlResource(Context context, AttributeSet attrs){
    super(context, attrs);
    init(context, attrs, NO_ID, NO_ID);
}
public WebViewHtmlResource(Context context, AttributeSet attrs, int defStyleAttr){
    super(context, attrs, defStyleAttr);
    init(context, attrs, defStyleAttr, NO_ID);
}
public WebViewHtmlResource(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes){
    super(context, attrs, defStyleAttr, defStyleRes);
    init(context, attrs, defStyleAttr, defStyleRes);
}

private void init(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes){
DebugTrack.start().create(this).end();
    setWebViewClient(new WebViewClient(){
        @Override
        public void onPageFinished(WebView view, String url){
            super.onPageFinished(view, url);
            View parent = (View)getParent();
            invalidate();
            if(parent != null){
                parent.invalidate();
                parent.requestLayout();
            }
            else{
                requestLayout();
            }
        }

    });
    setBackgroundColor(AppContext.getResources().getColorARGB(R.color.Transparent));
    setLayerType(WebView.LAYER_TYPE_SOFTWARE, null);
    if(attrs == null){
        return;
    }
    AttributeReader attributeReader = new AttributeReader().parse(context, ATTRS, attrs);
    maxHeight = attributeReader.asDimPx(R.styleable.WebViewHtmlResource_lib_max_height);
    id = attributeReader.getReference(R.styleable.WebViewHtmlResource_lib_raw_file);
    if(id != null){
        Boolean loadOnCreate = attributeReader.asBoolean(R.styleable.WebViewHtmlResource_lib_raw_file_load_on_create);
        if(Compare.isTrue(loadOnCreate)){
            Long delay = attributeReader.asLong(R.styleable.WebViewHtmlResource_lib_raw_file_load_delay_ms);
            if(delay == null){
                delay = 0L;
            }
            PostToHandler.of(this, delay, new RunnableW(){
                @Override
                public void runSafe(){
                    loadData();
                }
            });
        }
    }
    encoding = attributeReader.asString(R.styleable.WebViewHtmlResource_lib_html_encoding);
    if(encoding == null){
        encoding = DEFAULT_ENCODING;
    }
}

public WebViewHtmlResource setRawFileId(int rawFileId){
    return setRawFileId(rawFileId, DEFAULT_ENCODING);
}
public WebViewHtmlResource setRawFileId(int rawFileId, String encoding){
    this.id = rawFileId;
    return this;
}

public void loadData(){
    if(id != null){
        String html = UtilsResourceRaw.toString(id);
        String baseUrl = "file:///android_asset/";
//                String baseUrl = "file:///android_res/drawable/"; //do not work on API21
        loadDataWithBaseURL(baseUrl, html, "text/html", encoding, null);
    } else {
DebugException.start().log("id is null").end();
    }
}

@Override
protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec){
    if(maxHeight != null){
        int hSize = MeasureSpec.getSize(heightMeasureSpec);
        int hMode = MeasureSpec.getMode(heightMeasureSpec);
        switch(hMode){
            case MeasureSpec.AT_MOST:
                heightMeasureSpec = MeasureSpec.makeMeasureSpec(Math.min(hSize, maxHeight), MeasureSpec.AT_MOST);
                break;
            case MeasureSpec.UNSPECIFIED:
                heightMeasureSpec = MeasureSpec.makeMeasureSpec(maxHeight, MeasureSpec.AT_MOST);
                break;
            case MeasureSpec.EXACTLY:
                heightMeasureSpec = MeasureSpec.makeMeasureSpec(Math.min(hSize, maxHeight), MeasureSpec.EXACTLY);
                break;
        }
    }
    super.onMeasure(widthMeasureSpec, heightMeasureSpec);
}

@Override
protected void finalize() throws Throwable{
DebugTrack.start().destroy(this).end();
    super.finalize();
}

}


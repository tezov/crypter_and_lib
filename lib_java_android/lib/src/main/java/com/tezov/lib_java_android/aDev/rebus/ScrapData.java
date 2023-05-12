//package com.bytekoto.vine.temp.readFile;
//
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.HandlerThread;
//import android.os.Message;
//import android.webkit.URLUtil;
//
//import com.bytekoto.stockboncoin.util.Observer;
//
//import org.jsoup.Jsoup;
//import org.jsoup.nodes.Document;
//import org.jsoup.nodes.Element;
//import org.jsoup.select.Elements;
//
//import java.util.concurrent.ConcurrentLinkedQueue;
//import java.util.regex.Matcher;
//import java.util.regex.Pattern;
//
//public class ScrapData extends HandlerThread {
//    private static final Integer MESSAGE_DOWNLOAD = 0;
//
//    public static final String ON_START = ScrapData.class.getSimpleName()+".STARTED";
//    public static final String ON_ENDED = ScrapData.class.getSimpleName()+".ENDED";
//    public static final String ON_FAILED = ScrapData.class.getSimpleName()+".FAILED";
//    public static final String ON_EMPTY = ScrapData.class.getSimpleName()+".EMPTY";
//
//    public final static String INDEX_SCRAP_REQUEST = ScrapData.class.getSimpleName() + ".INDEX";
//    public final static String COUNT_DONE = ScrapData.class.getSimpleName() + ".COUNT_DONE";
//    public final static String COUNT_SUCCESS = ScrapData.class.getSimpleName() + ".COUNT_SUCCESS";
//    public final static String URL = ScrapData.class.getSimpleName() + ".URL";
//    public final static String TITLE = ScrapData.class.getSimpleName() + ".TITLE";
//    public final static String PRICE = ScrapData.class.getSimpleName() + ".PRICE";
//    public final static String DESCRIPTION = ScrapData.class.getSimpleName() + ".DESCRIPTION";
//
//    private Integer index = 0;
//    private Integer countTotal = 0;
//    private Integer countSuccess = 0;
//    private Boolean hasQuit = false;
//    private Handler mainThread;
//    private Handler thisThread = null;
//    private ConcurrentLinkedQueue<RequestItem> requests = new ConcurrentLinkedQueue<>();
//
//    private class RequestItem{
//        private Integer index;
//        private Observer observer;
//        private String url;
//        public RequestItem(Integer index, Observer observer, String url){
//            this.index = index;
//            this.url = url;
//            this.observer = observer;
//        }
//    }
//
//    public ScrapData(String name, Handler mainThread) {
//        super(name);
//        this.mainThread = mainThread;
//    }
//
//    @Override
//    public synchronized void postEvent() {
//        super.postEvent();
//        hasQuit = false;
//    }
//
//    public Integer getCountTotal() {
//        return countTotal;
//    }
//
//    public Integer getCountSuccess() {
//        return countSuccess;
//    }
//
//    @Override
//    protected void onLooperPrepared() {
//        thisThread = new Handler(){
//            @Override
//            public void handleMessage(Message msg) {
//                if(msg.what == MESSAGE_DOWNLOAD){
//                    RequestItem requestItem = (RequestItem) msg.obj;
//                    if (!hasQuit)  handleRequest(requestItem);
//                    else clearQueue();
//                }
//            }
//        };
//    }
//
//    public Integer push(Observer observer, String url){
//        if(URLUtil.isValidUrl(url)){
//            index++;
//            RequestItem requestItem = new RequestItem(index,observer,url);
//            requests.onFilterChangedObserve(requestItem);
//            thisThread  .obtainMessage(MESSAGE_DOWNLOAD,requestItem)
//                        .sendToTarget();
//            return requestItem.index;
//        }
//        return null;
//    }
//
//    private void handleRequest(final RequestItem requestItem){
//        final Bundle bundle = new Bundle();
//        bundle.putInt(INDEX_SCRAP_REQUEST,requestItem.index);
//        bundle.putString(URL,requestItem.url);
//        countTotal++;
//        bundle.putInt(COUNT_DONE, countTotal);
//        bundle.putInt(COUNT_SUCCESS,countSuccess);
//        requestItem.observer.notifyListeners(mainThread, ON_START,ScrapData.this,bundle);
//
//        try{
//            String url = requestItem.url;
//
//            // title
//            Document doc= Jsoup.connect(url).obtainQuery();
//            String title = doc.title().toString();
//
//            // price
//            Elements elements = doc.getElementsByClass("_1F5u3");
//            Pattern p = Pattern.compile("(^[0-9]*) €$");
//            Integer price = -1;
//            for(Element element:elements){
//                Matcher m = p.matcher(element.text().toString().trim());
//                if (m.find()){
//                    price = Integer.parseInt(m.group(1));
//                    break;
//                }
//            }
//
//            // desription
//            elements = doc.getElementsByClass("_2wB1z");
//            String description = elements.obtainQuery(0).text().toString();
//
//
//            bundle.putString(TITLE,title);
//            bundle.putString(DESCRIPTION,description);
//            bundle.putInt(PRICE,price);
//            if (!hasQuit){
//                countSuccess += 1;
//                bundle.putInt(COUNT_SUCCESS,countSuccess);
//                requestItem.observer.notifyListeners(mainThread, ON_ENDED,ScrapData.this,bundle);
//            }
//            else clearQueue();
//
//        }catch (Exception e){
//            if (!hasQuit) {
//                requestItem.observer.notifyListeners(mainThread, ON_FAILED, ScrapData.this, 
//                bundle);
//            }
//            else clearQueue();
//
//        }
//        finally {
//            if (!hasQuit) {
//                requests.remove(requestItem);
//                if (requests.isEmpty())
//                    requestItem.observer.notifyListeners(mainThread, ON_EMPTY, ScrapData.this, 
//                    bundle);
//            }
//        }
//    }
//
//    @Override
//    public boolean quit() {
//        hasQuit = true;
//        clearQueue();
//        return super.quit();
//    }
//
//    public void clearQueue()
//    {
//        countTotal = 0;
//        countSuccess = 0;
//        thisThread.removeMessages(MESSAGE_DOWNLOAD);
//        requests.clearView();
//    }
//
//    @Override
//    protected void finalize() throws Throwable {
//        quit();
//        super.finalize();
//    }
//}

//package com.bytekoto.vine.rebus;
//
//import androidx.lifecycle.Lifecycle;
//import androidx.lifecycle.LifecycleObserver;
//import androidx.lifecycle.LifecycleOwner;
//import androidx.lifecycle.OnLifecycleEvent;
//
//import com.bytekoto.vine.lib.adapter.Clock;
//import com.bytekoto.vine.lib.com.tezov.lib.type.collection.ListOrObject;
//import com.bytekoto.vine.lib.com.tezov.lib.type.ref.Ref;
//import com.bytekoto.vine.lib.com.tezov.lib.type.ref.SR;
//import com.bytekoto.vine.lib.com.tezov.lib.type.ref.WR;
//import com.bytekoto.vine.lib.util.debug.DebugLog;
//import com.bytekoto.vine.lib.util.debug.TrackClass;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.Iterator;
//import java.util.List;
//import java.util.Map;
//
//public class ReferenceManager {
//    protected HashMap<Ref, ListOrObject<RefDetails>> references;
//
//    public ReferenceManager() {
//        TrackClass.create(this);
//        this.references = new HashMap<>();
//    }
//
//    public class RefDetails {
//        protected Ref ref;
//        protected long timestamp;
//
//        public RefDetails(Object ref) {
//            TrackClass.create(this);
//            this.ref = new SR<>(ref);
//            timestamp = Clock.MilliSecond.now();
//        }
//        public String getNameRef(){
//            return ref.getRefFullName();
//        }
//        @Override
//        public String toString() {
//            return
//                    "[ref name=" + getNameRef() + "]" +
//                    "[timestamp=" + Clock.MilliSecondTo.DateAndTime.toString(timestamp) + "]" +
//                    "[since elapsed=" + Clock.MilliSecondTo.Minute.Elapsed.toString(timestamp)+
//                    "]";
//        }
//        @Override
//        protected void finalize() throws Throwable {
//            TrackClass.destroy(this);
//            super.finalize();
//        }
//    }
//
//    public void retain(Object ref, Object owner){
//        final WR ownerWR = WR.newInstance(owner);
//        ListOrObject<RefDetails> o = references.get(ownerWR);
//        if(o == null) o = new ListOrObject<>();
//        else {
//            for(RefDetails refDetails :o){
//                if(refDetails.ref.equals(ref)) return;
//            }
//        }
//        RefDetails refDetails = new RefDetails(ref);
//        o.add(refDetails);
//        references.put(ownerWR, o);
//        if(owner instanceof LifecycleOwner){
//            ((LifecycleOwner)owner).getLifecycle().addObserver(new LifecycleObserver() {
//                @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
//                public void removeReferences(){
//                    references.remove(ownerWR);
//                }
//            });
//        }
//    }
//    public void release(Object refToRelease, Ref ownerWR){
//        Ref refToReleaseWR = WR.newInstance(refToRelease);
//        ListOrObject<RefDetails> o = references.get(ownerWR);
//        if(o == null) return;
//        for(RefDetails refDetails :o){
//            if(refDetails.ref.equals(refToReleaseWR)){
//                o.remove(refDetails);
//                if(o.size()<=0) references.remove(ownerWR);
//                return;
//            }
//        }
//    }
//    public void release(Object refToRelease, Object owner){
//        Ref ownerWR = WR.newInstance(owner);
//        release(refToRelease, ownerWR);
//    }
//    public void releaseAllRefFromOwner(Ref ownerWR){
//        releaseAllRefFromOwner(ownerWR);
//    }
//    public void releaseAllRefFromOwner(Object owner){
//        releaseAllRefFromOwner(WR.newInstance(owner));
//    }
//    public void releaseAllRef(Object refToRelease){
//        Ref refToReleaseWR = WR.newInstance(refToRelease);
//        Iterator<ListOrObject<RefDetails>> iterator = references.values().iterator();
//        while(iterator.hasNext()){
//            ListOrObject<RefDetails> o = iterator.next();
//            for(int i=0; i<o.size();i++){
//                RefDetails refDetails = o.get(i);
//                if(refDetails.ref.equals(refToReleaseWR)){
//                    o.remove(i);
//                    i--;
//                    if(o.size()<=0) iterator.remove();
//                }
//            }
//        }
//    }
//
//    public <T> List<T> selectRefFromOwner(Ref ownerWR){
//        ListOrObject<RefDetails> o = references.get(ownerWR);
//        if(o == null) return null;
//        List<T> list = new ArrayList<>();
//        for(RefDetails refDetails :o) list.add((T) refDetails.ref.get());
//        return list;
//    }
//    public <T> List<T> selectRefFromOwner(Object owner){
//        return selectRefFromOwner(WR.newInstance(owner));
//    }
//
//    public void toDebugLogWithOwner(Ref ownerWR){
//        ListOrObject<RefDetails> o = references.get(ownerWR);
//        if(o == null) return;
//        DebugLog.start().send("OWNER: " + ownerWR.getRefFullName() + " OF ").end();
//        for(RefDetails refDetails :o){
//            DebugLog.start().send(refDetails.toString()).end();
//        }
//    }
//    public void toDebugLogWithOwner(Object owner){
//        toDebugLogWithOwner(WR.newInstance(owner));
//    }
//    public void toDebugLogWithReference(Ref refToLogWR){
//        for(Map.Entry<Ref, ListOrObject<RefDetails>> e:references.entrySet()){
//            for(RefDetails refDetails :e.getValue()){
//                if(refDetails.ref.equals(refToLogWR)){
//                    DebugLog.send("OWNER: " + e.getKey().getRefFullName() + " OF " + refDetails
//                    .toString());
//                }
//            }
//        }
//    }
//    public void toDebugLogWithReference(Object refToLog){
//        toDebugLogWithReference(WR.newInstance(refToLog));
//    }
//
//    final public void toDebugLog(){
//        for(Map.Entry<Ref, ListOrObject<RefDetails>> e:references.entrySet()){
//            DebugLog.start().send("OWNER: " + e.getKey().getRefFullName() + " OF ").end();
//            for(RefDetails refDetails :e.getValue()){
//                 DebugLog.start().send(refDetails.toString()).end();
//            }
//        }
//    }
//
//    @Override
//    protected void finalize() throws Throwable {
//        TrackClass.destroy(this);
//        super.finalize();
//    }
//}

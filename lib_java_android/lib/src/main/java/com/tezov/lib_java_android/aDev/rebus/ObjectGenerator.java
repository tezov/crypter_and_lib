//package com.bytekoto.vine.rebus;
//
//import androidx.com.tezov.lib.toolbox.debug.annotation.NonNull;
//
//import com.bytekoto.vine.lib.com.tezov.lib.toolbox.debug.annotation.DebugLogEnable;
//import com.bytekoto.vine.lib.com.tezov.lib.definition.defCreatable;
//import com.bytekoto.vine.lib.util.debug.TrackClass;
//
//import java.util.ArrayList;
//import java.util.Iterator;
//import java.util.List;
//
//
//public class ObjectGenerator<T> implements Iterable<T> {
//    private int maxGenerated = 0;
//    private defCreatable<T> com.tezov.lib.factory;
//
//    public ObjectGenerator(defCreatable<T> com.tezov.lib.factory, int maxGenerated){
//        TrackClass.create(this);
//        this.com.tezov.lib.factory = com.tezov.lib.factory;
//        this.maxGenerated = maxGenerated;
//
//    }
//    public ObjectGenerator(defCreatable<T> com.tezov.lib.factory){
//        this(com.tezov.lib.factory,0);
//    }
//
//
//    public ObjectGenerator setMaxGenerated(int n){
//        this.maxGenerated = n;
//        return this;
//    }
//
//    public List<T> newInstance(int n){
//        List<T> list = new ArrayList<>();
//        for(int i=0; i< n; i++){
//            list.add(com.tezov.lib.factory.create());
//        }
//        return list;
//    }
//    public List<T> newInstance(){
//        return newInstance(maxGenerated);
//    }
//
//    @NonNull
//    @Override
//    public Iterator<T> iterator() {
//        return new GeneratorObjectIterator(maxGenerated);
//    }
//
//    public Iterator<T> iterator(int n) {
//        return new GeneratorObjectIterator(n);
//    }
//
//    private class GeneratorObjectIterator implements Iterator<T> {
//        public GeneratorObjectIterator(int n){
//            ObjectGenerator.this.maxGenerated = n;
//        }
//        @Override
//        public boolean hasNext() {
//            return maxGenerated >0;
//        }
//        @Override
//        public T next() {
//            maxGenerated--;
//            return com.tezov.lib.factory.create();
//        }
//    }
//
//    @Override
//    protected void finalize() throws Throwable {
//        TrackClass.destroy(this);
//        super.finalize();
//    }
//}
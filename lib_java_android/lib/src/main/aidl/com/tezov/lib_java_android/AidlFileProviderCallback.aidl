package com.tezov.lib_java_android;

interface AidlFileProviderCallback {
    const int VERSION = 1;
    void links(in List<String> fileLinks);
    void file(in byte[] fileBytes);
}
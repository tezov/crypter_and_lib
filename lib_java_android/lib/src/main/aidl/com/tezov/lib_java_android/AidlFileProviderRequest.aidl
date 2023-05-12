package com.tezov.lib_java_android;

import com.tezov.lib_java_android.AidlFileProviderCallback;

interface AidlFileProviderRequest {
    const int VERSION = 1;
    void links(in AidlFileProviderCallback callback, in String directoryLink, in String patternPath, in String patternFileName, in boolean recursive);
    void file(in AidlFileProviderCallback callback, in String fileLink);
}
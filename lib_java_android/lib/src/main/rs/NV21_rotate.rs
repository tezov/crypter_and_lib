#pragma version(1)
#pragma rs java_package_name(com.tezov.lib_java_android)
#pragma rs_fp_relaxed

//#include "rs_debug.rsh"

// **************** DEBUG ************************** //
static uint32_t strlen(char* m){
    if(m == NULL) return 0;
    char* mOriginal = m;
    while(*m != '\0') m++;
    return m-mOriginal;
}
static void message(char* m){
    rsSendToClient(0,m,strlen(m));
}
// **************** DEBUG ************************** //

static uint16_t width;
static uint16_t height;
static uint16_t angle;

static uint32_t frameSize;
static uint8_t swap;
static uint8_t xFlip;
static uint8_t yFlip;

rs_allocation inPlaneY;
rs_allocation inPlaneUV;
rs_allocation outPlaneUV;

void setParam(uint16_t w, uint16_t h, uint16_t a){
    width = w;
    height = h;
    angle = a;
    frameSize = width * height;
    angle = (360 - angle) % 360;
    swap = angle % 180 != 0;
    xFlip = angle % 270 != 0;
    yFlip = angle >= 180;
//    rsDebug(">>: width ", width);
//    rsDebug(">>: height ", height);
//    rsDebug(">>: frameSize ", frameSize);
//    rsDebug(">>: angle ", angle);
//    rsDebug(">>: swap ", swap);
//    rsDebug(">>: xFlip ", xFlip);
//    rsDebug(">>: yFlip ", yFlip);
}

uint8_t RS_KERNEL rotate(uint32_t x) {
    // Y
    int jOut = swap ? x % height : x / width;
    int iOut = swap ? x / height : x % width;
    int i = xFlip ? width - iOut - 1 : iOut;
    int j = yFlip ? height - jOut - 1 : jOut;
    uint8_t Y = rsGetElementAt_uchar(inPlaneY, j * width + i);
    // U/V
    if((j%2 == 0) && (i%2 == 0)){
        int uIn = ((j >> 1) * width) + i;
        iOut = iOut & 0xFFFFFFFE;
        jOut = jOut & 0xFFFFFFFE;
        int uOut = (swap ? ((iOut >>1) * height) + jOut : ((jOut >>1) * width) + iOut);
        rsSetElementAt_uchar(outPlaneUV, rsGetElementAt_uchar(inPlaneUV, uIn), uOut);
        rsSetElementAt_uchar(outPlaneUV, rsGetElementAt_uchar(inPlaneUV, uIn+1), uOut+1);
    }
    return Y;
}








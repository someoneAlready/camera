package com.example.android.camera2basic;

import android.graphics.Bitmap;
import android.util.Log;

import org.dmlc.mxnet.Predictor;

import java.nio.ByteBuffer;
import java.util.Vector;

public class MxNetUtils {
    private static boolean libLoaded = false;
    private MxNetUtils() {}

    public static Vector<Float> identifyImage(final Bitmap bitmap) {
        ByteBuffer byteBuffer = ByteBuffer.allocate(bitmap.getByteCount());
        bitmap.copyPixelsToBuffer(byteBuffer);
        byte[] bytes = byteBuffer.array();
        float[] colors = new float[bytes.length / 4 * 3];

        float mean_b = 104;
        float mean_g = 117;
        float mean_r = 123;

        int height = 300;
        int width = 300;

        for (int i = 0; i < bytes.length; i += 4) {
            int j = i / 4;
            colors[0 * width * height + j] = (float)(((int)(bytes[i + 0])) & 0xFF) - mean_r;
            colors[1 * width * height + j] = (float)(((int)(bytes[i + 1])) & 0xFF) - mean_g;
            colors[2 * width * height + j] = (float)(((int)(bytes[i + 2])) & 0xFF) - mean_b;
        }
        Predictor predictor = WhatsApplication.getPredictor();
        predictor.forward("data", colors);
        final float[] result = predictor.getOutput(0);

        double thresh = 0.5;

        int len = result.length / 6;

        Vector<Float> ret = new Vector<Float>(0);

        for (int i=0; i<len; ++i)
            if (result[i * 6 + 0] >= 0 && result[i * 6 + 1] > thresh) {
                for (int j=0; j<6; ++j)
                    ret.add(result[i * 6 + j]);
            }
        return ret;
        /*
        int index = 0;
        for (int i = 0; i < result.length; ++i) {
            if (result[index] < result[i]) index = i;
        }
        String tag = WhatsApplication.getName(index);
        String [] arr = tag.split(" ", 2);
        return arr[1];
        */

    }
}

package com.example.boobleproject;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class ImageUtils {


    public static Bitmap convertByteArrayToBitmap(byte[] imageBytes) {
        return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
    }
}

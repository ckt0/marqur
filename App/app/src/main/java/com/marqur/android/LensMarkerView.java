package com.marqur.android;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class LensMarkerView extends LinearLayout {

    public LensMarkerView(Context context) {
        super(context);
    }

//    @Override
////    public void onDraw(Canvas canvas){
////        super.onDraw(canvas);
////
////        Paint paint=new Paint();
////        Bitmap bitmap= BitmapFactory.decodeResource(getResources(), R.mipmap.marker0);
////        paint.setColor(Color.WHITE);
////        canvas.drawBitmap(bitmap, (canvas.getWidth() / 2) - (bitmap.getWidth() / 2), (canvas.getHeight() / 2) - (bitmap.getHeight() / 2), paint);
////    }

    public void init(String title) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View markerView = inflater.inflate(R.layout.lens_marker, this);
        ((TextView) markerView.findViewById(R.id.lens_marker_view_title)).setText(title);
    }
}

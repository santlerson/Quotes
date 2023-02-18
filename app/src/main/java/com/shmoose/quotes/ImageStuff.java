package com.shmoose.quotes;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ImageStuff {
    public static Uri getImageUri(Context context, int authorId){
        File imgDir = new File(context.getFilesDir(), Values.AUTHOR_IMAGES_DIR_NAME);
        boolean done = false;
        if (imgDir.isDirectory()){
            File image = new File(imgDir, String.valueOf(authorId));
            if (image.exists()){
                return Uri.fromFile(image);
            }
        }
        return null;
    }

    @SuppressLint("ResourceType")
    public static Bitmap renderQuoteImage(String quoteContent, String authorName, String dateString, int authorId, TypedArray colorPair,Typeface font, Context context){
        /*
        Function produces rectangular image, height should be divisible by 3
         */
        Typeface typeface;
        typeface=font;

        int finalImageHeight = 1536;
        int finalImageWidth=finalImageHeight*3/4;

        int maxTextHeight = finalImageHeight/3;
        int imgHeight = 2*finalImageWidth/3;
        int paddingSize = 8;
        int maxAuthorHeight = finalImageHeight-maxTextHeight-imgHeight;
        Uri imageUri = getImageUri(context, authorId);
        Bitmap src;
        try {
            src = MediaStore.Images.Media.getBitmap(context.getContentResolver(), imageUri);
        } catch (IOException | NullPointerException e) {
            src = BitmapFactory.decodeResource(context.getResources(), R.drawable.profile_pic);
        }
        Bitmap resizedSrc = Bitmap.createScaledBitmap(src, finalImageWidth, (2*finalImageWidth/3), false);
        Bitmap dest = Bitmap.createBitmap(finalImageWidth, finalImageHeight, Bitmap.Config.ARGB_8888);
        String yourText = String.format("\"%s\"",quoteContent);
        Canvas cs = new Canvas(dest);
        Paint backgroundPaint = new Paint();
        backgroundPaint.setStyle(Paint.Style.FILL);
        backgroundPaint.setColor(colorPair.getColor(0x1, 0xffffff));

        cs.drawRect(0, 0, cs.getWidth(), cs.getHeight(), backgroundPaint);
        cs.drawBitmap(resizedSrc, 0f, maxTextHeight, null);

        TextPaint mTextPaint;
        StaticLayout mTextLayout;
        boolean done=false;
        int textSize = 144;
        do {
            mTextPaint = new TextPaint();
            mTextPaint.setTextSize(textSize);
            mTextPaint.setColor(colorPair.getColor(0, 0x000000));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                mTextPaint.setTypeface(typeface);
            }
            else{
                mTextPaint.setTypeface(Typeface.SERIF);
            }
            mTextLayout = new StaticLayout(yourText, mTextPaint, cs.getWidth()-(2*paddingSize), Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
            if (mTextLayout.getHeight() >= maxTextHeight-(2*paddingSize)){
                textSize-=paddingSize;
            }
            else{
                done=true;
            }

        }
        while(!done);

        cs.save();
// calculate x and y position where your text will be placed

        float textX = paddingSize;
        float textY = (maxTextHeight - mTextLayout.getHeight())/2;

        cs.translate(textX, textY);
        mTextLayout.draw(cs);
        cs.restore();

        yourText = String.format("- %s %s", authorName, dateString);
        done=false;
        textSize = 72;
        do {
            mTextPaint = new TextPaint();
            mTextPaint.setTextSize(textSize);
            mTextPaint.setTypeface(typeface);
            mTextPaint.setColor(colorPair.getColor(0,0x000000));
            mTextLayout = new StaticLayout(yourText, mTextPaint, cs.getWidth()-(2*paddingSize), Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
            if (mTextLayout.getHeight() >= maxAuthorHeight-(2*paddingSize)){
                textSize-=paddingSize;
            }
            else{
                done=true;
            }

        }
        while(!done);

        cs.save();
// calculate x and y position where your text will be placed

        textX = paddingSize;
        textY = finalImageHeight-maxAuthorHeight+(maxAuthorHeight-mTextLayout.getHeight())/2;

        cs.translate(textX, textY);
        mTextLayout.draw(cs);
        cs.restore();

        return dest;




    }
    private static Uri saveImageExternal(Bitmap image, Context context) {
        String TAG="SaveImage";
        //TODO - Should be processed in another thread
        Uri uri = null;
        try {
            File file = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "to-share.png");
            FileOutputStream stream = new FileOutputStream(file);
            image.compress(Bitmap.CompressFormat.PNG, 90, stream);
            stream.close();
            uri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider",file);

        } catch (IOException e) {
            Log.d(TAG, "IOException while trying to write file for sharing: " + e.getMessage());
        }
        return uri;
    }
    private static void shareImageUri(Uri uri, Context context){
        Intent intent = new Intent(android.content.Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.setType("image/png");
        context.startActivity(intent);
    }
    public static void shareImg(Bitmap icon, Context context){

        shareImageUri(saveImageExternal(icon, context), context);

    }
}

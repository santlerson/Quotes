package com.shmoose.quotes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.DragEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;

public class QuoteDisplayActivity extends AppCompatActivity {
    String authorName;
    Quote quote;
    TypedArray colorPairs;
    TypedArray fonts;
    TypedArray colorPair;
    int colorPairCounter=0;
    int fontCounter=0;
    Bitmap bitmap;
    ImageView imgView;
    Typeface font;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.quote_display_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.edit_quote:
                Intent intent = new Intent(this, AddQuoteActivity.class);
                intent.putExtra(Values.QUOTE_ID_STRING, quote.id);
                startActivity(intent);

                break;
            case android.R.id.home:
                finish();
                break;
            default:
                return false;

        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quote_display);
        // calling the action bar
        ActionBar actionBar = getSupportActionBar();

        // showing the back button in action bar
        actionBar.setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        authorName = intent.getStringExtra(Values.QUOTE_AUTHOR_NAME);
        quote = intent.getParcelableExtra(Values.QUOTE_PARCELABLE_STRING);
        colorPairs=getResources().obtainTypedArray(R.array.color_pair_array);
        fonts=getResources().obtainTypedArray(R.array.fonts);
        int firstColorId=colorPairs.getResourceId(0,R.array.black_on_white);
        imgView = findViewById(R.id.imageView2);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            font = fonts.getFont(0);
        }
        else{
            font = Typeface.SANS_SERIF;
        }
        colorPair = getResources().obtainTypedArray(firstColorId);
        renderImage();

        imgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImageStuff.shareImg(bitmap, QuoteDisplayActivity.this);
            }

        });

    }

    public void cycleColors(View view){
        colorPairCounter++;
        colorPairCounter%=colorPairs.length();
        int colorPairId = colorPairs.getResourceId(colorPairCounter, 0);
        colorPair = getResources().obtainTypedArray(colorPairId);
        renderImage();


    }
    public void cycleFonts(View view){

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            fontCounter++;
            fontCounter%=fonts.length();
            font = fonts.getFont(fontCounter);
            renderImage();

        }
        else Toast.makeText(this,"Font cycling not supported on this version of Android", Toast.LENGTH_LONG);
    }
    public void renderImage(){

        bitmap = ImageStuff.renderQuoteImage(quote.content, authorName, quote.dateString(), quote.authorId,colorPair,font, this);
        imgView.setImageBitmap(bitmap);

    }

}
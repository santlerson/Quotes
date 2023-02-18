package com.shmoose.quotes;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.soundcloud.android.crop.Crop;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

public class AuthorDisplayActivity extends AppCompatActivity {

    public static final String TAG = "ADAct";
    AppDatabase db;
    QuoteDao quoteDao;
    AuthorDao authorDao;
    int authorId;
    ImageView imageView;
    File imgDir;
    File image;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_author_display);
        // calling the action bar
        ActionBar actionBar = getSupportActionBar();

        // showing the back button in action bar
        actionBar.setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        imageView = findViewById(R.id.image_view_author);

        authorId = intent.getIntExtra(Values.AUTHOR_ID_STRING, 0);
        db = (AppDatabase) Room.databaseBuilder(this, AppDatabase.class, "db").allowMainThreadQueries().build();
        quoteDao = db.quoteDao();
        authorDao=db.authorDao();
        List<Quote> allQuotes = quoteDao.getAllQuotesByAuthor(authorId);
        imgDir = new File(getFilesDir(), Values.AUTHOR_IMAGES_DIR_NAME);
        Uri imageUri = ImageStuff.getImageUri(this, authorId);
        if (imageUri!=null){
            imageView.setImageURI(imageUri);
        }
        else{
            imageView.setImageResource(R.drawable.profile_pic);
        }




        QuoteAdapter adapter = new QuoteAdapter(this, R.id.author_list_view, allQuotes, authorDao);
        ListView listView = findViewById(R.id.author_list_view);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(AuthorDisplayActivity.this, QuoteDisplayActivity.class);
                Quote quote = adapter.getItem(i);
                intent.putExtra(Values.QUOTE_AUTHOR_NAME, authorDao.getAuthorName(quote.authorId));
                intent.putExtra(Values.QUOTE_PARCELABLE_STRING, quote);
                startActivity(intent);


            }
        });


    }

    public void setImage(View view){
        Crop.pickImage(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode,resultCode, data);
        if (resultCode==RESULT_OK){
            switch (requestCode){
                case Crop.REQUEST_PICK:
                    File imagesDir = new File(getFilesDir(), Values.AUTHOR_IMAGES_DIR_NAME);
                    if (!imagesDir.isDirectory()){
                        imagesDir.mkdirs();
                    }
                    image = new File(imagesDir, String.valueOf(authorId));
                    Log.d(TAG, "Image picked.");
                    Crop.of(data.getData(), Uri.fromFile(image)).withAspect(3,2).start(this);


                    break;
                case Crop.REQUEST_CROP:
                    Uri uri = Uri.fromFile(image);
                    imageView.setImageURI(uri);
                    Log.d(TAG, "Set image URI to "+uri);
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + requestCode);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                return true;
        }
        return false;
    }
}
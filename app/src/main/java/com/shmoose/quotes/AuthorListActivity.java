package com.shmoose.quotes;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.util.List;

public class AuthorListActivity extends AppCompatActivity {
    public static final String TAG = "AuthorListA";
    AppDatabase db;
    AuthorDao dao;
    ListView listView;
    AuthorAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_author_list);
        Log.d(TAG, "Reached Activity");

        // calling the action bar
        ActionBar actionBar = getSupportActionBar();

        // showing the back button in action bar
        actionBar.setDisplayHomeAsUpEnabled(true);

        db = Room.databaseBuilder(this, AppDatabase.class, "db").allowMainThreadQueries().build();
        dao = db.authorDao();
        Log.d(TAG, "Got DAO");

        adapter=new AuthorAdapter(dao.getSortedAuthorList());
        Log.d(TAG, "Got List");

        listView = findViewById(R.id.list_view);
        listView.setAdapter(adapter);
        Log.d(TAG, "Set adapter");
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(AuthorListActivity.this, AuthorDisplayActivity.class);
                intent.putExtra(Values.AUTHOR_ID_STRING, adapter.getItem(i).id);
                startActivity(intent);
            }
        });





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

    @Override
    protected void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();
    }

    class AuthorAdapter extends ArrayAdapter<Author>{
        static final String TAG = "AuthorAdapter";
        List<Author> list;
        Uri[] uris;
        boolean[] checked;
        public AuthorAdapter(List<Author> list) {
            super(AuthorListActivity.this, R.layout.author_item, list);
            this.list=list;
            uris=new Uri[list.size()];
            checked = new boolean[list.size()];
        }

        @Override
        public int getCount() {
            return super.getCount();
        }

        @Nullable
        @Override
        public Author getItem(int position) {
            return super.getItem(position);
        }

        @Override
        public int getPosition(@Nullable Author item) {
            return super.getPosition(item);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            if (convertView==null){
                convertView=getLayoutInflater().inflate(R.layout.author_item, parent, false);
            }
            Author author = list.get(position);
//            Log.d(TAG, String.format("Rendering %s", author));
            ImageView imageView = convertView.findViewById(R.id.imageView);
            TextView textView = convertView.findViewById(R.id.textView);
            textView.setText(author.name);

            Uri imageUri;
            if (checked[position]){
                imageUri=uris[position];
            }
            else {
                imageUri = ImageStuff.getImageUri(AuthorListActivity.this, author.id);
                checked[position]=true;
                uris[position]=imageUri;
            }
            if(imageUri!=null){
                imageView.setImageURI(imageUri);
            }
            else{
                imageView.setImageResource(R.drawable.profile_pic);
            }

            return convertView;
        }
    }
}
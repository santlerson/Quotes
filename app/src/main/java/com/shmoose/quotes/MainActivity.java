package com.shmoose.quotes;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;

import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.room.Room;

import com.shmoose.quotes.databinding.ActivityMainBinding;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MainActivity";
    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;
    AppDatabase db;
    QuoteDao quoteDao;
    AuthorDao authorDao;


    public static String loadTextFromAssets(Context context, String assetsPath, Charset charset) throws IOException {
        InputStream is = context.getResources().getAssets().open(assetsPath);
        byte[] buffer = new byte[1024];
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        for (int length = is.read(buffer); length != -1; length = is.read(buffer)) {
            baos.write(buffer, 0, length);
        }
        is.close();
        baos.close();
        return charset == null ? new String(baos.toByteArray()) : new String(baos.toByteArray(), charset);
    }
    void migrate(){
        String authorName;
        Author author;


        try {
            String jsonString = loadTextFromAssets(this, "migrate.json", null);

            JSONObject jsonObject = new JSONObject(jsonString);
            JSONArray array = jsonObject.getJSONArray("quotes");
            for(int i = 0; i<array.length(); i++){
                JSONObject obj = array.getJSONObject(i);
                authorName = obj.getString("author");
                author = authorDao.searchAuthors(authorName);
                if (author==null){
                    authorDao.addAuthor(new Author(authorName));
                    author=authorDao.searchAuthors(authorName);
                }
                quoteDao.addQuote(new Quote(obj.getString("content"), obj.getInt("year"), obj.getInt("month"), obj.getInt("day"), author.id));

            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_TITLE);

//        setTitle(R.string.app_name);

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AddQuoteActivity.class);
                startActivity(intent);
            }
        });
        setTitle("Your title");
          db = (AppDatabase) Room.databaseBuilder(this, AppDatabase.class, "db").allowMainThreadQueries().build();
          quoteDao = db.quoteDao();
          authorDao=db.authorDao();
          List<Quote> allQuotes = quoteDao.getAllQuotes();
          if (allQuotes==null || allQuotes.isEmpty()){migrate();}
          QuoteAdapter adapter = new QuoteAdapter(this, R.id.listView, allQuotes, authorDao);
          ListView listView = findViewById(R.id.listView);
          listView.setAdapter(adapter);
          listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
              @Override
              public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                  Intent intent = new Intent(MainActivity.this, QuoteDisplayActivity.class);
                  Quote quote = adapter.getItem(i);
                  intent.putExtra(Values.QUOTE_AUTHOR_NAME, authorDao.getAuthorName(quote.authorId));
                  intent.putExtra(Values.QUOTE_PARCELABLE_STRING, quote);
                  startActivity(intent);


              }
          });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id){
            case R.id.author_list:
                startActivity(new Intent(this, AuthorListActivity.class));
                return true;
            case R.id.action_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }

//    class QuoteAdapter extends BaseAdapter{
//        List<Quote> quoteList;
//
//        public QuoteAdapter(AuthorDao authorDao, QuoteDao quoteDao) {
//            quoteList=quoteDao.getAllQuotes();
//        }
//
//        @Override
//        public int getCount() {
//            return quoteList.size();
//        }
//
//        @Override
//        public Quote getItem(int i) {
//            return quoteList.get(i);
//        }
//
//        @Override
//        public long getItemId(int i) {
//            return i;
//        }
//
//        @Override
//        public View getView(int i, View view, ViewGroup viewGroup) {
//            Quote quote = quoteList.get(i);
//            if (view==null){
//                view = getLayoutInflater().inflate(R.layout.quote_list_item, viewGroup, false);
//
//            }
//            TextView authorTextView = view.findViewById(R.id.author_name);
//            TextView contextTextView = view.findViewById(R.id.quote_content);
//
//            String authorName = authorDao.getAuthorName(quote.authorId);
//            String content = quote.content;
//
//            Log.d(TAG, "Author: "+authorName);
//            Log.d(TAG, "Content: "+content);
//            authorTextView.setText(authorDao.getAuthorName(quote.authorId)+ " "+quote.day+"/"+quote.month+"/"+quote.year);
//            contextTextView.setText(quote.content);
//
//            return view;
//        }
//
//    }
}
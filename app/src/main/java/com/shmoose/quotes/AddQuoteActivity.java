package com.shmoose.quotes;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.room.Room;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.KeyListener;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.List;

public class AddQuoteActivity extends AppCompatActivity {
    public static final String TAG = "AddQuote";
    AppDatabase db;
    AutoCompleteTextView authorNameInput;
    Author selectedAuthor=null;
    AuthorDao authorDao;
    QuoteDao quoteDao;
    int quoteId=-1;
    Quote quote;
    EditText quoteInput;
    DatePicker datePicker;
    Button deleteButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_quote);
        // calling the action bar
        ActionBar actionBar = getSupportActionBar();

        // showing the back button in action bar
        actionBar.setDisplayHomeAsUpEnabled(true);

        db = Room.databaseBuilder(this, AppDatabase.class, "db").allowMainThreadQueries().build();
        authorNameInput = findViewById(R.id.author_name_input);

        deleteButton=findViewById(R.id.delete_button);
        datePicker = findViewById(R.id.date_input);

        quoteInput = findViewById(R.id.quote_input);
//        AuthorAdapter adapter = new AuthorAdapter(this, db.authorDao().listAllAuthors());

        authorDao = db.authorDao();
        quoteDao = db.quoteDao();
        List<Author> authorList = authorDao.listAllAuthors();
        Intent intent = getIntent();
        if (intent!=null && getIntent().hasExtra(Values.QUOTE_ID_STRING)){
            quoteId=intent.getIntExtra(Values.QUOTE_ID_STRING,0);
            quote=quoteDao.getQuote(quoteId);
            selectedAuthor=authorDao.getAuthor(quote.authorId);
            quoteInput.setText(quote.content);
            authorNameInput.setText(selectedAuthor.name);
            datePicker.updateDate(quote.year, quote.month-1, quote.day);
            deleteButton.setVisibility(View.VISIBLE);

        }
        ArrayAdapter<Author> adapter = new ArrayAdapter<Author>(this, android.R.layout.simple_list_item_1, authorList);
//        AuthorAdapter adapter = new AuthorAdapter(this, authorList);
//        ArrayAdapter<String>  adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, authorDao.listAllAuthorNames());
        authorNameInput.setAdapter(adapter);
        authorNameInput.setThreshold(1);

        authorNameInput.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                Log.d(TAG, "Key typed, reverting selectedAuthor to null.");
                selectedAuthor=null;
                return false;
            }
        });
        authorNameInput.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                selectedAuthor = (Author)adapterView.getItemAtPosition(i);
                Log.d(TAG, String.format("Author set, setting author of id %d, name %s", selectedAuthor.id, selectedAuthor.name));
            }
        });

    }

    public void onClick(View view) {

        int authorId;
        if (selectedAuthor!=null){
            authorId=selectedAuthor.id;
            Log.d(TAG, String.format("Found author of id %d",selectedAuthor.id));
        }
        else{
            Author author = new Author(authorNameInput.getText().toString());
            authorId=(int)authorDao.addAuthor(author);
            Log.d(TAG, String.format("No author found, created new author with id %d", authorId));
        }
        quote = new Quote(quoteInput.getText().toString(), datePicker.getYear(), datePicker.getMonth()+1, datePicker.getDayOfMonth(), authorId);

        if(this.quoteId<0){
            db.quoteDao().addQuote(quote);
        }
        else{
            quote.id=quoteId;
            quoteDao.updateQuote(quote);
        }

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);

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

    public class DeleteDialogFragment extends DialogFragment {


        @NonNull
        @Override
        public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage(R.string.delete_question)
                    .setPositiveButton(R.string.answer_positive_delete, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            quoteDao.deleteQuote(quote);
                        }
                    })
                    .setNegativeButton(R.string.answer_negative_delete, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                        }
                    });
            // Create the AlertDialog object and return it
            return builder.create();
        }
    }
    public void deleteButtonPress(View view){
        new AlertDialog.Builder(this)
                .setMessage(R.string.delete_question)
                .setNegativeButton(R.string.answer_negative_delete, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                }).setPositiveButton(R.string.answer_positive_delete, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        quoteDao.deleteQuote(quote);
                        startActivity(new Intent(AddQuoteActivity.this, MainActivity.class));
                    }
                }).show();
    }
    //    class AuthorAdapter extends ArrayAdapter<Author> {
//        Activity context;
//        List<Author> list;
//
//        public AuthorAdapter(@NonNull Activity context, @NonNull List<Author> objects) {
//            super(context, android.R.layout.simple_list_item_1, objects);
//
//            this.context = context;
//            this.list = objects;
//
//        }
//
//
//        @Override
//        public int getCount() {
//            return list.size();
//        }
//
//        @Nullable
//        @Override
//        public Author getItem(int position) {
//            return list.get(position);
//        }
//
//        @Override
//        public int getPosition(@Nullable Author item) {
//            return list.indexOf(item);
//        }
//
//        @Override
//        public long getItemId(int position) {
//            return position;
//        }
//
//        @NonNull
//        @Override
//        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
//            if (convertView == null) {
//                convertView = context.getLayoutInflater().inflate(android.R.layout.simple_list_item_1, parent, false);
//
//            }
//            TextView textView = convertView.findViewById(android.R.id.text1);
//            textView.setText(list.get(position).name);
//
//
//            return convertView;
//
//        }

//    }
}
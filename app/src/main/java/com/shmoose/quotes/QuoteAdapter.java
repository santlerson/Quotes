package com.shmoose.quotes;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class QuoteAdapter extends ArrayAdapter<Quote> {
    public static final String TAG = "QuoteAdapter";
    List<Quote> list;
    AppCompatActivity context;
    AuthorDao authorDao;
    public QuoteAdapter(@NonNull AppCompatActivity context, int resource, @NonNull List<Quote> list, AuthorDao authorDao) {
        super(context, resource, list);

        this.list=list;
        this.context = context;
        this.authorDao=authorDao;
    }

    @Override
    public void add(@Nullable Quote quote) {
        list.add(quote);
    }

    @Nullable
    @Override
    public Quote getItem(int position) {
        return list.get(position);
    }

    @Override
    public int getPosition(@Nullable Quote item) {
        return list.indexOf(item);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @NonNull
    @Override
    public View getView(int i, @Nullable View view, @NonNull ViewGroup viewGroup) {
        Quote quote = list.get(i);
        if (view==null){
            view = context.getLayoutInflater().inflate(R.layout.quote_list_item, viewGroup, false);

        }
        TextView authorTextView = view.findViewById(R.id.author_name);
        TextView contextTextView = view.findViewById(R.id.quote_content);

        String authorName = authorDao.getAuthorName(quote.authorId);
        String content = quote.content;

        Log.d(TAG, "Author: "+authorName);
        Log.d(TAG, "Content: "+content);
        authorTextView.setText(authorDao.getAuthorName(quote.authorId)+ " "+quote.day+"/"+quote.month+"/"+quote.year);
        contextTextView.setText(quote.content);

        return view;
    }
}

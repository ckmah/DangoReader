package ckmah.mangoreader.adapter;


import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.william.mangoreader.R;

import ckmah.mangoreader.activity.MangaBrowseActivity;

public class GenreTextAdapter extends RecyclerView.Adapter<GenreTextAdapter.TextViewHolder> {

    public Activity activity;
    private String[] allGenres;

    public GenreTextAdapter(Activity activity) {
        this.activity = activity;
        allGenres = activity.getResources().getStringArray(R.array.genre_list);
    }

    @Override
    public void onBindViewHolder(final TextViewHolder viewHolder, final int position) {
        viewHolder.genre.setText(allGenres[position]);
    }

    @Override
    public int getItemCount() {
        return allGenres.length;
    }

    @Override
    public TextViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.genre_label, parent, false);

        final TextViewHolder holder = new TextViewHolder(itemView);

        final TextView genreText = (TextView) itemView.findViewById(R.id.genre_text);

        genreText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, MangaBrowseActivity.class);
                intent.putExtra(activity.getString(R.string.browse_order), genreText.getText());
                activity.startActivity(intent);
            }
        });

        return holder;
    }

    public static class TextViewHolder extends RecyclerView.ViewHolder {

        public TextView genre;

        public TextViewHolder(View itemView) {
            super(itemView);
            genre = (TextView) itemView.findViewById(R.id.genre_text);
        }
    }
}

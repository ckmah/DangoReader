package com.william.mangoreader.parse;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.TextView;

import com.william.mangoreader.R;
import com.william.mangoreader.adapter.CardLayoutAdapter;
import com.william.mangoreader.model.MangaCardItem;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.URL;
import java.util.ArrayList;

public class JSONParserTask extends AsyncTask<Integer, ArrayList<MangaCardItem>, ArrayList<MangaCardItem>> {
    private View rootView;
    private Context context;
    private Activity activity;
    private CardLayoutAdapter cgAdapter;
    //    private CardGridArrayAdapter cardGridArrayAdapter;
    public ArrayList<CardView> allCards;

    public JSONParserTask(View view, Activity activity, ArrayList<CardView> cards, CardLayoutAdapter cgAdapter) {
        rootView = view;
        context = rootView.getContext();
        this.activity = activity;
        this.cgAdapter = cgAdapter;
//        this.cardGridArrayAdapter = cardGridArrayAdapter;
        allCards = cards;
    }

    @Override
    protected void onProgressUpdate(ArrayList<MangaCardItem>... cards) {
        if (cards[0] != null) {
            cgAdapter.updateList(cards[0]);
        }
    }

    @Override
    protected ArrayList<MangaCardItem> doInBackground(Integer... params) {

        int page_size = params[0];
        int page = allCards.size() % page_size;
        try {
            ArrayList<MangaCardItem> cards = ParseMangaCardItem.parseMangaEden(page, page_size);
            publishProgress(cards);
            return cards;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(ArrayList<MangaCardItem> result) {

//        if (result != null) {
//            for (MangaCardItem m : result) {
//                allCards.add(createEntry(context, m));
//            }
//            cgAdapter.notifyDataSetChanged();
//        }
    }

    // use this method to create a new manga entry
    private CardView createEntry(final Context context, MangaCardItem manga) {
        // Set supplemental actions as icon

        CardView card = new CardView(context);
        // use card.findViewById() to change cover art, title, etc.

        TextView subtitle = new TextView(context);
        subtitle.setId(R.id.card_subtitle);
        subtitle.setText(manga.title);
        card.addView(subtitle);

//        ArrayList<BaseSupplementalAction> actions = new ArrayList<BaseSupplementalAction>();
//
//        IconSupplementalAction t1 = new IconSupplementalAction(context, R.id.bookmark_but);
//        t1.setOnActionClickListener(new BaseSupplementalAction.OnActionClickListener() {
//            @Override
//            public void onClick(Card card, View view) {
//                ImageButton bmButton = (ImageButton) view.findViewById(R.id.bookmark_but);
//                bmButton.setSelected(!bmButton.isSelected());
//
//                // create popupmenu for add to library
//                PopupMenu popupMenu = new PopupMenu(context, view);
//                popupMenu.getMenuInflater()
//                        .inflate(R.menu.menu_card, popupMenu.getMenu());
//                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
//                    public boolean onMenuItemClick(MenuItem item) {
//
//                        return true;
//                    }
//                });
//
//                popupMenu.show();
//                if (bmButton.isSelected()) {
//                    //TODO: add to user library
//                    Toast.makeText(context, "Bookmarked.", Toast.LENGTH_SHORT).show();
//                } else {
//                    //TODO: remove from user library
//                    Toast.makeText(context, "Removed from library.", Toast.LENGTH_SHORT).show();
//
//                }
//            }
//        });
//        actions.add(t1);
//
//        // setup card
//        MaterialLargeImageCard card = MaterialLargeImageCard.with(context)
//                .setTitle(manga.title)
//                .setSubTitle(String.valueOf(manga.lastChapterDate))
//                .useDrawableId(R.drawable.manga2)
//                .setupSupplementalActions(R.layout.card_actions, actions)
//                .build();
//
//        card.setInnerLayout(R.layout.inner_base_main_custom);
//
//        card.setOnClickListener(new Card.OnCardClickListener() {
//            @Override
//            public void onClick(Card card, View view) {
//                // TODO: integrate with manga item model
//                Intent settingsIntent = new Intent(activity, MangaItemActivity.class);
////                settingsIntent.putExtra("mangaTitle", activity.getResources().getString(R.string.manga_title));
//                activity.startActivity(settingsIntent);
//                Toast.makeText(context, " Click on Card.", Toast.LENGTH_SHORT).show();
//            }
//        });

        return card;
    }

    public static String getJSONFromUrl(URL url) {
        StringWriter json = new StringWriter();
        BufferedReader in;
        try {
            in = new BufferedReader(
                    new InputStreamReader(url.openStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null)
                json.write(inputLine);
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return json.toString();
    }

//    public static void main(String[] args){
//        URL url = null;
//        try {
//            url = new URL("https://www.mangaeden.com/api/list/0/?p=0");
//        } catch (MalformedURLException e) {
//            e.printStackTrace();
//            System.exit(1);
//        }
//        String json = getJSONFromUrl(url);
//        System.out.print(json);
//    }
}

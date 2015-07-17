package com.william.mangoreader.parse;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.william.mangoreader.R;
import com.william.mangoreader.activity.MangaItemActivity;
import com.william.mangoreader.model.MangaCardItem;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.URL;
import java.util.ArrayList;

import it.gmariotti.cardslib.library.cards.actions.BaseSupplementalAction;
import it.gmariotti.cardslib.library.cards.actions.IconSupplementalAction;
import it.gmariotti.cardslib.library.cards.material.MaterialLargeImageCard;
import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardGridArrayAdapter;

public class JSONParserTask extends AsyncTask<Integer, Integer, ArrayList<MangaCardItem>> {
    private View rootView;
    private Context context;
    private Activity activity;
    private CardGridArrayAdapter cardGridArrayAdapter;
    public ArrayList<Card> allCards;

    public JSONParserTask(View view, Activity activity, ArrayList<Card> cards, CardGridArrayAdapter cardGridArrayAdapter) {
        rootView = view;
        context = rootView.getContext();
        this.activity = activity;
        this.cardGridArrayAdapter = cardGridArrayAdapter;
        allCards = cards;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        //TODO: pre-execution tasks
    }

    @Override
    protected ArrayList<MangaCardItem> doInBackground(Integer... params) {
        int page = params[0];
        int quantity = params[1];
        try {
            return ParseMangaCardItem.parseMangaEden(page, quantity);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(ArrayList<MangaCardItem> result) {

        if (result != null) {
            for (MangaCardItem m : result) {
                allCards.add(createEntry(context, m));
            }
            cardGridArrayAdapter.notifyDataSetChanged();
        }
    }

    // use this method to create a new manga entry
    private MaterialLargeImageCard createEntry(final Context context, MangaCardItem manga) {
        // Set supplemental actions as icon
        ArrayList<BaseSupplementalAction> actions = new ArrayList<BaseSupplementalAction>();

        IconSupplementalAction t1 = new IconSupplementalAction(context, R.id.bookmark_but);
        t1.setOnActionClickListener(new BaseSupplementalAction.OnActionClickListener() {
            @Override
            public void onClick(Card card, View view) {
                ImageButton bmButton = (ImageButton) view.findViewById(R.id.bookmark_but);
                bmButton.setSelected(!bmButton.isSelected());

                // create popupmenu for add to library
                PopupMenu popupMenu = new PopupMenu(context, view);
                popupMenu.getMenuInflater()
                        .inflate(R.menu.menu_card, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {

                        return true;
                    }
                });

                popupMenu.show();
                if (bmButton.isSelected()) {
                    //TODO: add to user library
                    Toast.makeText(context, "Bookmarked.", Toast.LENGTH_SHORT).show();
                } else {
                    //TODO: remove from user library
                    Toast.makeText(context, "Removed from library.", Toast.LENGTH_SHORT).show();

                }
            }
        });
        actions.add(t1);

        // setup card
        MaterialLargeImageCard card = MaterialLargeImageCard.with(context)
                .setTitle(manga.title)
                .setSubTitle(String.valueOf(manga.lastChapterDate))
                .useDrawableId(R.drawable.manga2)
                .setupSupplementalActions(R.layout.card_actions, actions)
                .build();

        card.setInnerLayout(R.layout.inner_base_main_custom);

        card.setOnClickListener(new Card.OnCardClickListener() {
            @Override
            public void onClick(Card card, View view) {
                // TODO: integrate with manga item model
                Intent settingsIntent = new Intent(activity, MangaItemActivity.class);
                settingsIntent.putExtra("mangaTitle", activity.getResources().getString(R.string.manga_title));
                activity.startActivity(settingsIntent);
                Toast.makeText(context, " Click on Card.", Toast.LENGTH_SHORT).show();
            }
        });

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

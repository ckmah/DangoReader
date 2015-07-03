package com.william.mangoreader.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import com.william.mangoreader.R;
import com.william.mangoreader.listener.InfiniteScrollListener;
import com.william.mangoreader.model.MangaCardItem;
import com.william.mangoreader.parse.ParseMangaCardItem;

import java.io.IOException;
import java.util.ArrayList;

import it.gmariotti.cardslib.library.cards.actions.BaseSupplementalAction;
import it.gmariotti.cardslib.library.cards.actions.IconSupplementalAction;
import it.gmariotti.cardslib.library.cards.material.MaterialLargeImageCard;
import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardGridArrayAdapter;
import it.gmariotti.cardslib.library.view.CardGridView;

public class MyLibraryFragment extends Fragment {

    private ArrayList<Card> cards;
    private CardGridView gridView;

    AppCompatActivity activity;

    public MyLibraryFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activity = (AppCompatActivity) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_my_library, container, false);
        final CardGridView gridView = (CardGridView) rootView.findViewById(R.id.library_cards);

        // TODO: asynchronous loading
        cards = new ArrayList<Card>();


        createEntry(activity, gridView, cards);

        final CardGridArrayAdapter mCardArrayAdapter = new CardGridArrayAdapter(activity, cards);
        gridView.setAdapter(mCardArrayAdapter);

        gridView.setOnScrollListener(new InfiniteScrollListener(5) {
            @Override
            public void loadMore(int page, int totalItemsCount) {

                createEntry(activity, gridView, cards);
                // TODO: load from JSON
                mCardArrayAdapter.notifyDataSetChanged();
            }
        });

        setHasOptionsMenu(true);
        // Inflate the layout for this fragment
        return rootView;
    }

    private void createEntry(final Context context, ViewGroup vgroup, ArrayList<Card> cards) {

        // Set supplemental actions as icon
        ArrayList<BaseSupplementalAction> actions = new ArrayList<BaseSupplementalAction>();

        IconSupplementalAction t1 = new IconSupplementalAction(context, R.id.bookmark_but);

        // TODO: set initial settings according user library data

        t1.setOnActionClickListener(new BaseSupplementalAction.OnActionClickListener() {
            @Override
            public void onClick(Card card, View view) {
                System.out.println("Bookmark action clicked...");
                ImageButton bmButton = (ImageButton) view.findViewById(R.id.bookmark_but);
                bmButton.setSelected(!bmButton.isSelected());
                if (bmButton.isSelected()) {

                    //TODO: add to user library

                    System.out.println("Bookmarked!");
                } else {

                    //TODO: remove from user library

                    System.out.println("Un-bookmarked!");
                }
            }
        });
        actions.add(t1);

        // setup card
        MaterialLargeImageCard card = MaterialLargeImageCard.with(context)
                .setTitle("Fullmetal Alchemist")
                .setSubTitle("Author")
                .useDrawableId(R.drawable.manga3)
                .setupSupplementalActions(R.layout.card_actions, actions)
                .build();

        // TODO: open detail on card click

        card.setOnClickListener(new Card.OnCardClickListener() {
            @Override
            public void onClick(Card card, View view) {
                Toast.makeText(context, " Click on Card.", Toast.LENGTH_SHORT).show();
            }
        });

        cards.add(card);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.menu_my_library, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        if (id == R.id.action_search) {
            Toast.makeText(getActivity().getApplicationContext(), "Search action is selected!", Toast.LENGTH_SHORT).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}

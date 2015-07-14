package com.william.mangoreader.fragment;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.william.mangoreader.R;
import com.william.mangoreader.activity.MangaItemActivity;
import com.william.mangoreader.listener.InfiniteScrollListener;

import java.util.ArrayList;

import it.gmariotti.cardslib.library.cards.actions.BaseSupplementalAction;
import it.gmariotti.cardslib.library.cards.actions.IconSupplementalAction;
import it.gmariotti.cardslib.library.cards.material.MaterialLargeImageCard;
import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardGridArrayAdapter;
import it.gmariotti.cardslib.library.internal.CardHeader;
import it.gmariotti.cardslib.library.view.CardGridView;

public class LibraryPageFragment extends Fragment {

    private ArrayList<Card> cards;
    private CardGridView gridView;

    public static LibraryPageFragment newInstance(int page) {
        Bundle args = new Bundle();
        args.putInt("ARG_PAGE", page);
        LibraryPageFragment fragment = new LibraryPageFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.card_grid, container, false);
        final CardGridView gridView = (CardGridView) rootView.findViewById(R.id.card_grid_view);

        // TODO: asynchronous loading
        cards = new ArrayList<Card>();

        createEntry(getActivity().getApplicationContext(), gridView, cards);

        final CardGridArrayAdapter mCardArrayAdapter = new CardGridArrayAdapter(getActivity().getApplicationContext(), cards);
        gridView.setAdapter(mCardArrayAdapter);

        gridView.setOnScrollListener(new InfiniteScrollListener(5) {
            @Override
            public void loadMore(int page, int totalItemsCount) {
                createEntry(getActivity(), gridView, cards);
                // TODO: load from JSON
                mCardArrayAdapter.notifyDataSetChanged();
            }
        });

//        setHasOptionsMenu(true);
        // Inflate the layout for this fragment
        return rootView;
    }

    private void createEntry(final Context context, ViewGroup container, ArrayList<Card> cards) {

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
                .setTitle("Manga Title Here")
                .setSubTitle("Author Name")
                .useDrawableId(R.drawable.manga2)
                .setupSupplementalActions(R.layout.card_actions, actions)
                .build();

        card.setInnerLayout(R.layout.inner_base_main_custom);

        CardHeader header = new CardHeader(context);
        header.setTitle("Header");
        card.addCardHeader(header);

        card.setOnClickListener(new Card.OnCardClickListener() {
            @Override
            public void onClick(Card card, View view) {
                // TODO: integrate with manga item model
                Intent settingsIntent = new Intent(getActivity(), MangaItemActivity.class);
                settingsIntent.putExtra("mangaTitle", getResources().getString(R.string.manga_title));
                getActivity().startActivity(settingsIntent);
                Toast.makeText(context, " Click on Card.", Toast.LENGTH_SHORT).show();
            }
        });

        cards.add(card);
    }

//    @Override
//    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        inflater.inflate(R.menu.menu_my_library, menu);
//        super.onCreateOptionsMenu(menu, inflater);
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//        if (id == R.id.action_search) {
//            Toast.makeText(getActivity().getApplicationContext(), "Search action is selected!", Toast.LENGTH_SHORT).show();
//            return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}

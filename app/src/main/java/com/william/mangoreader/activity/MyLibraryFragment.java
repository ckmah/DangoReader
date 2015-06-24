package com.william.mangoreader.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.william.mangoreader.R;

import java.util.ArrayList;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardGridArrayAdapter;
import it.gmariotti.cardslib.library.internal.CardHeader;
import it.gmariotti.cardslib.library.view.CardGridView;

public class MyLibraryFragment extends Fragment {

    public MyLibraryFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_my_library, container, false);

//        AppCompatActivity activity = (AppCompatActivity) getActivity();
        // TODO: asynchronous loading
//        ArrayList<Card> cards = new ArrayList<Card>();
        //Create a Card
//        Card card = new Card(activity);
        //Create a CardHeader
        //Add Header to card
//        card.addCardHeader(header);
//        cards.add(card);
//        CardGridArrayAdapter mCardArrayAdapter = new CardGridArrayAdapter(activity, cards);
//        CardGridView gridView = (CardGridView) rootView.findViewById(R.id.library_cards);
//        if (gridView != null) {
//            gridView.setAdapter(mCardArrayAdapter);
//        }
        setHasOptionsMenu(true);
        // Inflate the layout for this fragment
        return rootView;
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

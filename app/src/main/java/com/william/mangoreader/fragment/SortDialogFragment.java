package com.william.mangoreader.fragment;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatCheckBox;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;

import com.william.mangoreader.R;
import com.william.mangoreader.adapter.CardLayoutAdapter;

import java.util.ArrayList;
import java.util.List;

public class SortDialogFragment extends DialogFragment {

    private CardLayoutAdapter cardAdapter;
    private List<Integer> sortOptionIds;
    private List<Integer> genreListIds;

    private static String[] sortOptions;
    private static String[] genreList;

    private static final String CARD_ADAPTER = "CARD_ADAPTER";

    public static SortDialogFragment newInstance(CardLayoutAdapter cardAdapter) {


        Bundle args = new Bundle();
        args.putSerializable(CARD_ADAPTER, cardAdapter);
        SortDialogFragment fragment = new SortDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        cardAdapter = (CardLayoutAdapter) getArguments().getSerializable((CARD_ADAPTER));
        sortOptions = getResources().getStringArray(R.array.sort_items);
        genreList = getResources().getStringArray(R.array.genre_list);

        final LinearLayout dialogLayout =
                (LinearLayout) LayoutInflater.from(getActivity()).inflate(R.layout.dialog_sort, null);

        generateIds();

        // add radio button for each sort option, set default to first option
        RadioGroup radioGroup = (RadioGroup) dialogLayout.findViewById(R.id.sort_radiogroup);
        for (int index = 0; index < sortOptions.length; index++) {
            RadioButton radioButton = new RadioButton(dialogLayout.getContext());
            radioButton.setId(sortOptionIds.get(index));
            radioButton.setText(sortOptions[index]);
            radioGroup.addView(radioButton);
        }
        ((RadioButton) radioGroup.getChildAt(0)).setChecked(true);

        // add checkbox for each genre
        GridLayout genreLayout = (GridLayout) dialogLayout.findViewById(R.id.genre_options);
        for (int index = 0; index < genreList.length; index++) {
            AppCompatCheckBox checkBox = new AppCompatCheckBox(getActivity());
            checkBox.setId(genreListIds.get(index));
            checkBox.setText(genreList[index]);
            genreLayout.addView(checkBox);
        }

        return new AlertDialog.Builder(new ContextThemeWrapper(getActivity(), R.style.Base_Theme_AppCompat_Light))
                .setView(dialogLayout)
                .setTitle("Sort options")
                        // Add the buttons
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        // retrieve selected sort option
                        RadioGroup sortOptions = (RadioGroup) dialogLayout.findViewById(R.id.sort_radiogroup);
                        View radioButton = sortOptions.findViewById(sortOptions.getCheckedRadioButtonId());
                        int sortOptionIndex = sortOptions.indexOfChild(radioButton);

                        // listen for reverse switch
                        Switch reverseSwitch = (Switch) dialogLayout.findViewById(R.id.reverse_switch);
                        final boolean[] sortReverse = {false};
                        reverseSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                if (isChecked)
                                    sortReverse[0] = true;
                            }
                        });

                        // listen for selected genres
                        GridLayout genreList = (GridLayout) dialogLayout.findViewById(R.id.genre_options);
                        final boolean[] isCheckedGenres = new boolean[genreList.getChildCount()];
                        for (int index = 0; index < genreList.getChildCount(); index++) {
                            final int finalIndex = index;
                            AppCompatCheckBox genre = (AppCompatCheckBox) genreList.getChildAt(finalIndex);
                            isCheckedGenres[finalIndex] = genre.isChecked();
                        }

                        List<Integer> selectedGenres = new ArrayList<>();
                        for (int index = 0; index < isCheckedGenres.length; index++) {
                            if (isCheckedGenres[index])
                                selectedGenres.add(index);
                        }

                        cardAdapter.getFilter(sortOptionIndex, sortReverse[0], selectedGenres).filter("");
                        // User clicked OK button
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                })
                .create();
    }

    private void generateIds() {
        sortOptionIds = new ArrayList<>();
        genreListIds = new ArrayList<>();

        for (String option : sortOptions) {
            sortOptionIds.add(View.generateViewId());
        }

        for (String genre : genreList)
            genreListIds.add(View.generateViewId());
    }
}

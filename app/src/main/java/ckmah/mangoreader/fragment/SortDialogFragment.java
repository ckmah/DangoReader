package ckmah.mangoreader.fragment;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.SwitchCompat;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.william.mangoreader.R;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import ckmah.mangoreader.adapter.CardLayoutAdapter;

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
        final GridLayout genreLayout = (GridLayout) dialogLayout.findViewById(R.id.genre_options);
        for (int index = 0; index < genreList.length; index++) {
            AppCompatCheckBox checkBox = new AppCompatCheckBox(getActivity());
            checkBox.setId(genreListIds.get(index));
            checkBox.setText(genreList[index]);
            genreLayout.addView(checkBox);
        }

        // auto check/uncheck dialog buttons
        AppCompatCheckBox genreToggle = (AppCompatCheckBox) dialogLayout.findViewById(R.id.genre_toggle);
        genreToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                for (int index = 0; index < genreLayout.getChildCount(); index++) {
                    AppCompatCheckBox genre = (AppCompatCheckBox) genreLayout.getChildAt(index);
                    genre.setChecked(isChecked);
                }
            }
        });

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
                        SwitchCompat reverseSwitch = (SwitchCompat) dialogLayout.findViewById(R.id.reverse_switch);

                        // listen for selected genres
                        GridLayout genreList = (GridLayout) dialogLayout.findViewById(R.id.genre_options);
                        final boolean[] isCheckedGenres = new boolean[genreList.getChildCount()];
                        for (int index = 0; index < genreList.getChildCount(); index++) {
                            AppCompatCheckBox genre = (AppCompatCheckBox) genreList.getChildAt(index);
                            isCheckedGenres[index] = genre.isChecked();
                        }

                        List<Integer> selectedGenres = new ArrayList<>();
                        for (int index = 0; index < isCheckedGenres.length; index++) {
                            if (isCheckedGenres[index])
                                selectedGenres.add(index);
                        }

                        cardAdapter.getFilter(sortOptionIndex, reverseSwitch.isChecked(), selectedGenres).filter("");
//                        ((RecyclerView)getActivity().findViewById(R.id.browse_recycler_view)).scrollToPosition(0);
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
            sortOptionIds.add(generateViewId());
        }

        for (String genre : genreList)
            genreListIds.add(generateViewId());
    }

    private static final AtomicInteger sNextGeneratedId = new AtomicInteger(1);

    /**
     * Replaces View.generateViewId() for pre-v17 compat
     * Copied from Android source, see http://stackoverflow.com/a/15442997
     * <p/>
     * <p/>
     * Generate a value suitable for use in setId(int).
     * This value will not collide with ID values generated at build time by aapt for R.id.
     *
     * @return a generated ID value
     */
    public static int generateViewId() {
        for (; ; ) {
            final int result = sNextGeneratedId.get();
            // aapt-generated IDs have the high byte nonzero; clamp to the range under that.
            int newValue = result + 1;
            if (newValue > 0x00FFFFFF) newValue = 1; // Roll over to 1, not 0.
            if (sNextGeneratedId.compareAndSet(result, newValue)) {
                return result;
            }
        }
    }
}

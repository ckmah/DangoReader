package com.william.mangoreader.activity;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.support.v4.app.NavUtils;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.william.mangoreader.R;

/**
 * Created by clarence on 7/7/15.
 * <p/>
 * TODO: determine whether this needs to be an AppCompatActivity
 */
public class SettingsActivity extends PreferenceActivity {


    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);


        Toolbar mToolbar = (Toolbar) findViewById(R.id.settings_toolbar);
        mToolbar.setTitle(R.string.title_settings);
        mToolbar.setTitleTextColor(getResources().getColor(R.color.white));

        mToolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_mtrl_am_alpha);

        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavUtils.navigateUpFromSameTask(SettingsActivity.this);
            }
        });
        // TODO: crashes on rotate - Fragment com.william.mangoreader.activity.SettingsActivity$SettingsFragment did not create a view.
        getFragmentManager().beginTransaction().replace(R.id.preference_fragment, new SettingsFragment()).commit();
    }

    /**
     * Dedicated PreferenceFragment. Should only be created by SettingsActivity class.
     */
    public static class SettingsFragment extends PreferenceFragment {
        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.layout.preferences);
        }
    }
}

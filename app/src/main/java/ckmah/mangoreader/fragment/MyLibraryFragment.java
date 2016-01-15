package ckmah.mangoreader.fragment;


import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.william.mangoreader.R;

import ckmah.mangoreader.adapter.LibraryPagerAdapter;

@Deprecated // Until we decide what categories we want
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

        LibraryPagerAdapter pagerAdapter = new LibraryPagerAdapter(getActivity().getApplicationContext(), getActivity().getSupportFragmentManager());

        ViewPager viewPager = (ViewPager) rootView.findViewById(R.id.library_pager);
        viewPager.setAdapter(pagerAdapter);

        TabLayout tabLayout = (TabLayout) getActivity().findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(viewPager);

        setHasOptionsMenu(true);
//         Inflate the layout for this isBrowsing
        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//     Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.menu_my_library, menu);

        // TODO implement fetching manga
//        MenuItem refreshItem = menu.findItem(R.id.action_refresh);
//        refreshItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
//            @Override
//            public boolean onMenuItemClick(MenuItem item) {
//                Snackbar
//                        .make(getActivity().findViewById(R.id.parent_layout), "Fetching manga...", Snackbar.LENGTH_SHORT)
//                        .show();
//                return false;
//            }
//        });
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
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

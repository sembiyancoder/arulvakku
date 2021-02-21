package com.arulvakku.app.ui.home.frgament;

import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.arulvakku.R;
import com.arulvakku.app.adapter.HomeMenuAdapter;
import com.arulvakku.app.ui.bible.BooksActivity;
import com.arulvakku.app.ui.contact_us.ContactUsActivity;
import com.arulvakku.app.ui.prayer_request.PrayerRequestActivity;
import com.arulvakku.app.ui.radio.RadioActivity;
import com.arulvakku.app.ui.rosary.RosaryActivity;
import com.arulvakku.app.ui.wayofcross.WayOfCrossActivity;
import com.arulvakku.app.utils.ChromeCustomTab;
import com.arulvakku.app.utils.Constants;
import com.arulvakku.app.utils.ExternalBrowser;
import com.arulvakku.app.utils.OpenGmailApp;


public class HomeMenuItemFragment extends Fragment implements HomeMenuAdapter.onItemSelectedListener {

    private RecyclerView mRecyclerView;
    private HomeMenuAdapter mMenuAdapter;
    private TypedArray mImageTypedArray, mNameTypedArray;

    public HomeMenuItemFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home_menu_item, container, false);
        mRecyclerView = rootView.findViewById(R.id.recycler_view);
        mImageTypedArray = getResources().obtainTypedArray(R.array.menu_list_image);
        mNameTypedArray = getResources().obtainTypedArray(R.array.menu_list_name);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setAdapter();
    }

    private void setAdapter() {
        mMenuAdapter = new HomeMenuAdapter(getActivity(), mImageTypedArray, mNameTypedArray, this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(mMenuAdapter);
    }

    @Override
    public void onItemSelected(String menuName) {
        Intent intent = null;
        if (menuName.equalsIgnoreCase(getString(R.string.lbl_bible))) {
            intent = new Intent(getActivity(), BooksActivity.class);
            intent.putExtra("book_name", getString(R.string.lbl_bible));
        } else if (menuName.equalsIgnoreCase(getString(R.string.lbl_radio))) {
            intent = new Intent(getActivity(), RadioActivity.class);
        } else if (menuName.equalsIgnoreCase(getString(R.string.lbl_rosary))) {
            intent = new Intent(getActivity(), RosaryActivity.class);
        } else if (menuName.equalsIgnoreCase(getString(R.string.lbl_way_of_cross))) {
            intent = new Intent(getActivity(), WayOfCrossActivity.class);
        } else if (menuName.equalsIgnoreCase(getString(R.string.lbl_feedback))) {
            OpenGmailApp openGmailApp = new OpenGmailApp(getActivity(), getString(R.string.lbl_contact_mail), getString(R.string.lbl_subject));
            openGmailApp.openInGmail();
        } else if (menuName.equalsIgnoreCase(getString(R.string.lbl_contact_us))) {
            intent = new Intent(getActivity(), ContactUsActivity.class);
        } else if (menuName.equalsIgnoreCase(getString(R.string.lbl_calendar))) {
            ChromeCustomTab chromeCustomTab = new ChromeCustomTab(getActivity(), Constants.CALENDAR_URL);
            chromeCustomTab.openInCustomTab();
        } else if (menuName.equalsIgnoreCase(getString(R.string.lbl_donate_us))) {
            ExternalBrowser externalBrowser = new ExternalBrowser(getActivity(), Constants.DONATE_PAGE);
            externalBrowser.openExternalBrowser();
        } else if (menuName.equalsIgnoreCase(getString(R.string.lbl_prayer_request))) {
            intent = new Intent(getActivity(), PrayerRequestActivity.class);
        }

        if (intent != null) {
            startActivity(intent);
        }
    }

}

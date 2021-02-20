package com.arulvakku.ui.fragments;

import android.content.Intent;
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
import com.arulvakku.ui.activity.BooksActivity;
import com.arulvakku.ui.activity.ContactUsActivity;
import com.arulvakku.ui.activity.PrayerRequestActivity;
import com.arulvakku.ui.activity.RadioActivity;
import com.arulvakku.ui.activity.RosaryActivity;
import com.arulvakku.ui.adapter.HomeMenuAdapter;
import com.arulvakku.ui.utils.ChromeCustomTab;
import com.arulvakku.ui.utils.Constants;
import com.arulvakku.ui.utils.ExternalBrowser;
import com.arulvakku.ui.utils.OpenGmailApp;
import com.arulvakku.ui.utils.UtilSingleton;


public class HomeMenuItemFragment extends Fragment implements HomeMenuAdapter.onItemSelectedListener {


    private RecyclerView recyclerView;
    private HomeMenuAdapter adapter;

    public HomeMenuItemFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home_menu_item, container, false);
        recyclerView = rootView.findViewById(R.id.recycler_view);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setAdapter();
    }

    private void setAdapter() {
        adapter = new HomeMenuAdapter(getActivity(), UtilSingleton.getInstance().getMenuListItem(), this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onItemSelected(String menuName) {
        Intent intent = null;
        if (menuName.equalsIgnoreCase("திருவிவிலியம்")) {
            intent = new Intent(getActivity(), BooksActivity.class);
            intent.putExtra("book_name", "திருவிவிலியம்");
        } else if (menuName.equalsIgnoreCase("வானொலி")) {
            intent = new Intent(getActivity(), RadioActivity.class);
        } else if (menuName.equalsIgnoreCase("செபமாலை")) {
            intent = new Intent(getActivity(), RosaryActivity.class);
        } /*else if (menuName.equalsIgnoreCase("ஜெபங்கள்")) {
            intent = new Intent(getActivity(), RosaryActivity.class);
        } */else if (menuName.equalsIgnoreCase("உங்கள் கருத்து")) {
            OpenGmailApp openGmailApp = new OpenGmailApp(getActivity(), "arulvakkudevelopment@gmail.com", "உங்கள் கருத்து");
            openGmailApp.openInGmail();
        } else if (menuName.equalsIgnoreCase("தொடர்புக்கு")) {
            intent = new Intent(getActivity(), ContactUsActivity.class);
        } else if (menuName.equalsIgnoreCase("திருவழிபாட்டு நாட்குறிப்பு")) {
            ChromeCustomTab chromeCustomTab = new ChromeCustomTab(getActivity(), Constants.CALENDAR_URL);
            chromeCustomTab.openInCustomTab();
        } else if (menuName.equalsIgnoreCase("நன்கொடை")) {
            ExternalBrowser externalBrowser = new ExternalBrowser(getActivity(), Constants.DONATE_PAGE);
            externalBrowser.openExternalBrowser();
        }  else if (menuName.equalsIgnoreCase("செப வேண்டுதல்")) {
            intent = new Intent(getActivity(), PrayerRequestActivity.class);
        }

        if (intent != null) {
            startActivity(intent);
            getActivity().overridePendingTransition(R.anim.enter, R.anim.exit);
        }
    }

}

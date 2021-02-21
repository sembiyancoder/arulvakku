package com.arulvakku.app.ui.wayofcross;

import android.content.res.TypedArray;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.arulvakku.R;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link WayOfCrossFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WayOfCrossFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private int mCurrentStationPosition;
    private String mStationDetails;

    private TextView mTitleTextView, mSubTitleTextView, mCountTextView;
    private TypedArray mTypedArray;
    private ImageView mImageView;
    private TextView mDescriptionTextView, mPrayerTextView;

    public WayOfCrossFragment() {
        // Required empty public constructor
    }


    public static WayOfCrossFragment newInstance(int param1, JSONObject param2) {
        WayOfCrossFragment fragment = new WayOfCrossFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2.toString());
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mCurrentStationPosition = getArguments().getInt(ARG_PARAM1);
            mStationDetails = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_way_of_cross, container, false);
        try {
            JSONObject jsonObject = new JSONObject(mStationDetails);
            if (jsonObject != null) {
                mTypedArray = getResources().obtainTypedArray(R.array.way_of_the_cross_images);
                mImageView = view.findViewById(R.id.imageView);
                mTitleTextView = view.findViewById(R.id.textView5);
                mSubTitleTextView = view.findViewById(R.id.textView4);
                mSubTitleTextView = view.findViewById(R.id.textView4);
                mCountTextView = view.findViewById(R.id.textView3);
                mDescriptionTextView = view.findViewById(R.id.txt_description);
                mPrayerTextView = view.findViewById(R.id.txt_prayer);


                mTitleTextView.setText(jsonObject.optString("StationTitle"));
                mSubTitleTextView.setText(jsonObject.optString("StationSubTitle"));
                mImageView.setImageResource(mTypedArray.getResourceId(mCurrentStationPosition, 0));
                mDescriptionTextView.setText(jsonObject.optString("Description"));
                mPrayerTextView.setText(jsonObject.optString("Prayer"));
                mCountTextView.setText(jsonObject.optString("Station"));
                mTypedArray.recycle();


            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return view;
    }


}
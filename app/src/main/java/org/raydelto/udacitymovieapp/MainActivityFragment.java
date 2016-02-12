package org.raydelto.udacitymovieapp;

import android.net.Uri;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {
    private static final String LOG_TAG = MainActivityFragment.class.getSimpleName();

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_main, container, false);
        ImageView imgTest = (ImageView) root.findViewById(R.id.imgTest);
        Picasso.with(getActivity()).load("http://i.imgur.com/DvpvklR.png").into(imgTest, new Callback() {
            @Override
            public void onSuccess() {
                Log.v(LOG_TAG,"SUCCESS");
            }

            @Override
            public void onError() {
                Log.v(LOG_TAG,"FAIL");

            }


        });

        Log.v(LOG_TAG, "TEST");
        return root;

    }
}

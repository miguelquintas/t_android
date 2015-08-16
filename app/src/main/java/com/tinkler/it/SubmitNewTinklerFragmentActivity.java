package com.tinkler.it;

import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by diogoguimaraes on 04/08/15.
 */
public class SubmitNewTinklerFragmentActivity extends Fragment {

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_add_tinkler_submit, container, false);

        return view;
    }

}

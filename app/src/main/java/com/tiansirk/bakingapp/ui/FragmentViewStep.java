package com.tiansirk.bakingapp.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tiansirk.bakingapp.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class FragmentViewStep extends Fragment {

    public FragmentViewStep() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Load the saved state (the items in the step) if there is one
        if(savedInstanceState != null) {
        }

        View rootView = inflater.inflate(R.layout.view_step_fragment, container, false);
        return rootView;
    }
}

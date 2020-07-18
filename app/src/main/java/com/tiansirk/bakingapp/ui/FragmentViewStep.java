package com.tiansirk.bakingapp.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tiansirk.bakingapp.R;
import com.tiansirk.bakingapp.databinding.ViewStepFragmentBinding;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class FragmentViewStep extends Fragment {

    private final static String TAG = FragmentViewStep.class.getSimpleName();

    private final String MEDIA_PLAYER_STATE = "media_player_state";
    private final String STEPS_DESCRIPTION_STATE = "steps_description_state";

    private ViewStepFragmentBinding binding;
    private String mStepDescription;

    public FragmentViewStep() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Load the saved state (the items in the step) if there is one
        if(savedInstanceState != null) {
        }

        // Inflate the Select-Step fragment layout
        binding = ViewStepFragmentBinding.inflate(inflater, container, false);
        View rootView = binding.getRoot();

        return rootView;
    }

    public void setStepDescription(String stepDescription) {
        this.mStepDescription = stepDescription;
    }
}

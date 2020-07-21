package com.tiansirk.bakingapp.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.tiansirk.bakingapp.R;
import com.tiansirk.bakingapp.databinding.ViewStepFragmentBinding;
import com.tiansirk.bakingapp.data.*;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class FragmentViewStep extends Fragment {

    private final static String TAG = FragmentViewStep.class.getSimpleName();

    private final String MEDIA_PLAYER_STATE = "media_player_state";
    private final String STEPS_DESCRIPTION_STATE = "steps_description_state";

    private ViewStepFragmentBinding binding;
    private BottomNavigationView mBottomNavigationView;

    private Step[] mSteps;
    private int mStepsIndex;

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

        // Show the step details
        showStep();

        // Set up the bottom navigation
        setupBottomNavigation();

        return rootView;
    }

    public void setSteps(Step[] steps, int position) {
        this.mSteps = steps;
        this.mStepsIndex = position;
    }

    /** Save the current state of this fragment */
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

    }


    private void setupBottomNavigation(){
        mBottomNavigationView = binding.bottomNavigation;

        mBottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener(){
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.previous:
                        if(mStepsIndex > 0) {
                            mStepsIndex--;
                            showStep();
                        }
                        else{
                            Toast.makeText(getContext(), "This is the first step!", Toast.LENGTH_SHORT).show();
                        }
                        return true;
                    case R.id.home:
                        //showStepsFragment();
                        return true;
                    case R.id.next:
                        if(mStepsIndex < mSteps.length - 1) {
                            mStepsIndex++;
                            showStep();
                        }
                        else{
                            Toast.makeText(getContext(), "This is the last step!", Toast.LENGTH_SHORT).show();
                        }
                        return true;
                    default:
                        return true;
                }
            }
        });
    }

    private void showStep(){
        // Get a reference to the media player View in the fragment layout
        TextView videoView = binding.mediaPlayerView;
        // Get a reference to the step description View in the fragment layout
        TextView descriptionView = binding.tvViewStep;
        // If a video exists, set it to the view, otherwise show default image/text
        if(mSteps[mStepsIndex].getVideoURL() == null || mSteps[mStepsIndex].getVideoURL().isEmpty()){
            videoView.setText("There is no Video available!");
        }
        else{
            videoView.setText(mSteps[mStepsIndex].getVideoURL());
        }
        // If a description exists, set it to the view, otherwise, create a Log statement that indicates there is no step
        if(mSteps[mStepsIndex].getDescription() != null){
            descriptionView.setText(mSteps[mStepsIndex].getDescription());
        }
        else{
            Log.wtf(TAG, "This fragment has a null Step description");
        }
    }

    /** Create and display the view fragment */
    private void showStepsFragment(){
        FragmentSelectSteps stepsFragment = new FragmentSelectSteps();
        stepsFragment.setSteps(mSteps);

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();

        ft.add(R.id.select_step_container, stepsFragment)
                .commit();

        showHideFragment(this);
    }

    /** Shows or hides the
     * @param fragment according to its current state */
    private void showHideFragment(Fragment fragment){
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();

        if(fragment.isHidden()){

            ft.show(fragment);
        }
        else{
            ft.hide(fragment);
        }
        ft.commit();
    }

}

package com.tiansirk.bakingapp.ui;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.tiansirk.bakingapp.R;
import com.tiansirk.bakingapp.databinding.FragmentViewStepBinding;
import com.tiansirk.bakingapp.data.*;
import com.tiansirk.bakingapp.utils.JsonParser;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class FragmentViewStep extends Fragment {

    public final static String TAG = FragmentViewStep.class.getSimpleName();

    /** Member constants for saving state */
    private final String STATE_STEPS_INDEX = "steps_index_state";
    private final String STATE_STEPS = "steps_state";
    private final String STATE_ORIENTATION = "state_orientation";

    /** Member vars for views */
    private FragmentViewStepBinding binding;
    private BottomNavigationView mBottomNavigationView;

    /** Member vars for data to be shown */
    private Step[] mSteps;
    private int mStepsIndex;

    /** Member var for own custom data-to-be-sent listener */
    private FragmentViewStepListener listener;

    /** The interface that receives onClick messages */
    public interface FragmentViewStepListener{
        void onBackSelected(Step[] steps);
    }
    /** Member var for keeping track of UI state */
    private boolean isLandscape;

    /** Compulsory empty constructor */
    public FragmentViewStep() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Check the device's orientation
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
            isLandscape = true;
        }
        else{
            isLandscape = false;
        }

        // Load the saved state (the items in the step) if there is one
        if(savedInstanceState != null && savedInstanceState.get(STATE_STEPS) != null) {
            Log.d(TAG, "mSteps, mStepsIndex and isLandscape is recreated from onCreateView's savedInstanceState.");
            mSteps = JsonParser.getStepsFromJson(savedInstanceState.getString(STATE_STEPS));
            mStepsIndex = savedInstanceState.getInt(STATE_STEPS_INDEX);
        }

        // Inflate the View-Step fragment layout
        binding = FragmentViewStepBinding.inflate(inflater, container, false);
        View rootView = binding.getRoot();

        // Show the step details
        if(mSteps != null) showStep();

        // Set up the bottom navigation only when the device is in portrait mode
        if(!isLandscape) setupBottomNavigation();

        return rootView;
    }

    /** It is to set the fields of this fragment in order to show them to the user */
    public void setSteps(Step[] steps, int position) {
        this.mSteps = steps;
        this.mStepsIndex = position;
    }

    /** A bottom navigation bar to let the user step through the {@link Step}s of the selected {@link Recipe} */
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
                        } else{
                            Toast.makeText(getContext(), "This is the first step!", Toast.LENGTH_SHORT).show();
                        }
                        return true;
                    case R.id.home:
                        listener.onBackSelected(mSteps);
                        return true;
                    case R.id.next:
                        if(mStepsIndex < mSteps.length - 1) {
                            mStepsIndex++;
                            showStep();
                        } else{
                            Toast.makeText(getContext(), "This is the last step!", Toast.LENGTH_SHORT).show();
                        }
                        return true;
                    default:
                        return true;
                }
            }
        });
    }

    /** This presents the data, available in the fields of this fragment, to the user. The videoURL and the description of the selected {@link Step}. */
    private void showStep(){
        // Get a reference to the media player View in the fragment layout
        TextView videoView = binding.mediaPlayerView;
        // If a video exists, set it to the view, otherwise show default image/text
        if(mSteps[mStepsIndex].getVideoURL() == null || mSteps[mStepsIndex].getVideoURL().isEmpty()){
            videoView.setText("There is no Video available!");
        }
        else{
            videoView.setText(mSteps[mStepsIndex].getVideoURL());
        }

        // Setup the descriptionView only when the device is in portrait mode
        if(!isLandscape){
            if(binding.tvViewStep != null){
                // Get a reference to the step description View in the fragment layout
                TextView descriptionView = binding.tvViewStep;
                // If a description exists, set it to the view, otherwise, create a Log statement that indicates there is no step
                if(mSteps[mStepsIndex].getDescription() != null){
                    descriptionView.setText(mSteps[mStepsIndex].getDescription());
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        descriptionView.setJustificationMode(Layout.JUSTIFICATION_MODE_INTER_WORD);
                    }
                } else{
                    Log.wtf(TAG, "This fragment has a null Step description!");
                }
            } else{
                Log.wtf(TAG, "view_step TextView is null!");
            }
        }
    }

    /** When this fragment is attached to its host activity, ie {@link SelectStepActivity} the listener interface is connected
     * If not then an error exception is thrown to notify the developer.
     * @param context
     */
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof FragmentViewStepListener) {
            listener = (FragmentViewStepListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement FragmentViewStepListener");
        }
    }

    /** When this fragment is detached from the host, the listeners is set to null, to decouple. */
    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    /** Save the current state of this fragment */
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(TAG, "onSaveInstanceState is called");
        outState.putString(STATE_STEPS, JsonParser.serializeStepsToJson(mSteps));
        outState.putInt(STATE_STEPS_INDEX, mStepsIndex);
    }



    /** Shows or hides the
     * @param fragment according to its current state */
    private void showHideFragment(Fragment fragment){
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        if(fragment.isHidden()) ft.show(fragment);
        else ft.hide(fragment);
        ft.commit();
    }

}

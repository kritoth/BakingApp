package com.tiansirk.bakingapp.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;
import com.tiansirk.bakingapp.R;
import com.tiansirk.bakingapp.data.Recipe;
import com.tiansirk.bakingapp.data.Step;
import com.tiansirk.bakingapp.databinding.ActivityMainBinding;
import com.tiansirk.bakingapp.databinding.ActivitySelectRecipeStepBinding;
import com.tiansirk.bakingapp.utils.JsonParser;

import java.util.Arrays;
import java.util.List;

public class SelectRecipeStep extends AppCompatActivity {

    private static final String TAG = SelectRecipeStep.class.getSimpleName();

    private ActivitySelectRecipeStepBinding binding;

    private Step[] mRecipeSteps;
    private RecipeStepAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_recipe_step);

        // Initiating views
        initViews();

        // Initiating RecyclerView
        initRecycler();

        /** get the received {@link Step} objects */
        if(getIntent() != null){
            if(getIntent().getStringExtra("selected_recipe") !=  null){
                mRecipeSteps = JsonParser.getStepsFromJson(getIntent().getStringExtra("selected_recipe"));
            }
        }
        mAdapter.setRecipeStepsData(Arrays.asList(mRecipeSteps));

        // TODO: Set ItemClickListener to change ViewStep Fragment
        //setupItemClickListener();
    }

    private void initViews() {
        binding = ActivitySelectRecipeStepBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
    }

    private void initRecycler() {
        RecyclerView.LayoutManager linearLayoutManager = new LinearLayoutManager(this);
        binding.rvSelectRecipeSteps.setLayoutManager(linearLayoutManager);
        binding.rvSelectRecipeSteps.setHasFixedSize(true);
        mAdapter = new RecipeStepAdapter(this);
        binding.rvSelectRecipeSteps.setAdapter(mAdapter);
    }

}
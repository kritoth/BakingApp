package com.tiansirk.bakingapp;

import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.widget.TextView;

import com.tiansirk.bakingapp.ui.MainActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.IdlingResource;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.espresso.intent.rule.*;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import static androidx.test.espresso.Espresso.*;
import static androidx.test.espresso.action.ViewActions.*;
import static androidx.test.espresso.assertion.ViewAssertions.*;
import static androidx.test.espresso.intent.Intents.*;
import static androidx.test.espresso.intent.matcher.IntentMatchers.*;
import static androidx.test.espresso.matcher.ViewMatchers.*;
import static org.hamcrest.Matchers.*;

/**
 * This test demos a user clicking on the RecyclerView item in MaiActivity which opens up the
 * corresponding Activity.
 */
@RunWith(AndroidJUnit4.class)
public class MainActivityScreenTest {

    public static final String RECIPE_NAME = "Nutella Pie";

    /**
     * The ActivityTestRule is a rule provided by Android used for functional testing of a single
     * activity. The activity that will be tested will be launched before each test that's annotated
     * with @Test and before methods annotated with @Before. The activity will be terminated after
     * the test and methods annotated with @After are complete. This rule allows you to directly
     * access the activity during the test.
     */
    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    /**
     * Clicks on a RecyclerView item and checks it opens up the SelectStepActivity with the correct details.
     */
    @Test
    public void clickRecyclerViewItem_OpensSelectStepActivity() {

        // verify the visibility of recycler view on screen
        onView(withId(R.id.rv_recipes)).check(matches(isDisplayed()));
        // perform click on view at 0th position in RecyclerView
        onView(withId(R.id.rv_recipes))
                .perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));

        // checks that the OpensSelectStepActivity opens with the correct recipe name displayed in the ToolBar
        onView(allOf(isAssignableFrom(TextView.class),
                withParent(isAssignableFrom(Toolbar.class)))).check(matches(withText(RECIPE_NAME)));
    }


}

package com.group13.dynamicwayfinder;

import androidx.test.rule.ActivityTestRule;

import com.group13.dynamicwayfinder.Activities.Setting.SettingActivity;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;


import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.isClickable;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isEnabled;
import static androidx.test.espresso.matcher.ViewMatchers.isSelected;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.assertion.ViewAssertions.matches;

public class PreferenceTest {

    @Rule
    public ActivityTestRule<SettingActivity> myActivity =
            new ActivityTestRule<>(SettingActivity.class);

    @Before
    public void setUp(){
        myActivity.getActivity();
    }

    @Test
    public void trainSwitch(){
        onView(withId(R.id.trainSwitch))
                .perform(click());
    }

    @Test
    public void busSwitch(){
        onView(withId(R.id.busSwitch))
                .perform(click());
    }

    @Test
    public void walkSwitch(){
        onView(withId(R.id.walkSwitch))
                .perform(click());
    }


    @Test
    public void bicycleSwitch(){
        onView(withId(R.id.bicycleSwitch))
                .perform(click());
    }

    @Test
    public void environmentImage(){
        onView(withId(R.id.enviormentImage))
                .perform(click());
    }

    @Test
    public void costImage(){
        onView(withId(R.id.costImage))
                .perform(click());
    }

    public void logoutAct(){
        onView(withId(R.id.loginlogo))
                .check(matches(isDisplayed()));
    }

    @Test
    public void logoutFunction(){
        onView(withId(R.id.logOutButton))
                .check(matches(isClickable()));
    }



}

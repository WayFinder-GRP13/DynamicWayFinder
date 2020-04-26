package com.group13.dynamicwayfinder;

import android.view.KeyEvent;

import androidx.test.rule.ActivityTestRule;

import com.group13.dynamicwayfinder.Activities.Map.MapActivity;
import com.group13.dynamicwayfinder.Activities.Setting.SettingActivity;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.longClick;
import static androidx.test.espresso.action.ViewActions.pressKey;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

public class MapTest {

    @Rule
    public ActivityTestRule<MapActivity> myActivity =
            new ActivityTestRule<>(MapActivity.class);

    @Before
    public void setUp(){
        myActivity.getActivity();
        onView(withId(R.id.top_tap_action))
                .perform(click());
    }

    @Test
    public void firstSearchClick(){
        onView(withId(R.id.search))
                .perform(click());
    }

    @Test
    public void firstSearchType(){
        onView(withId(R.id.search))
                .perform(click());
        onView(withId(R.id.search))
                .perform(clearText(),replaceText("Test"), closeSoftKeyboard());
    }

    @Test
    public void firstSearchCancel(){
        onView(withId(R.id.search))
                .perform(click());
        onView(withId(R.id.search))
                .perform(clearText(),replaceText("Test"), closeSoftKeyboard());
        onView(withId(R.id.clear_button1))
                .perform(click());
    }

    @Test
    public void firstSearchOnMap(){
        onView(withId(R.id.search))
                .perform(click());
        onView(withId(R.id.search))
                .perform(clearText(),replaceText("Test"), pressKey(KeyEvent.KEYCODE_ENTER));
    }

    @Test
    public void secondSearchClick(){
        onView(withId(R.id.search3))
                .perform(click());
    }

    @Test
    public void secondSearchType(){
        onView(withId(R.id.search3))
                .perform(click());
        onView(withId(R.id.search3))
                .perform(clearText(),replaceText("Test"), closeSoftKeyboard());
    }

    @Test
    public void secondSearchCancel(){
        onView(withId(R.id.search3))
                .perform(click());
        onView(withId(R.id.search3))
                .perform(clearText(),replaceText("Test"), closeSoftKeyboard());
        onView(withId(R.id.clear_button2))
                .perform(click());
    }

    @Test
    public void secondSearchOnMap(){
        onView(withId(R.id.search3))
                .perform(click());
        onView(withId(R.id.search3))
                .perform(clearText(),replaceText("Test"), pressKey(KeyEvent.KEYCODE_ENTER));
    }

    @Test
    public void backArrow(){
        onView(withId(R.id.arrow_back))
                .perform(click());
    }

    @Test
    public void currentLoc(){
        onView(withId(R.id.fab))
                .perform(click());
    }

    @Test
    public void mapModes(){
        onView(withId(R.id.modesMap))
                .perform(click());
    }

    @Test
    public void longPressPop(){
        onView(withId(R.id.map))
                .perform(longClick());
    }

    @Test
    public void longPressDirection(){
        onView(withId(R.id.map))
                .perform(longClick());
        onView(withId(R.id.popup_direction))
                .perform(click());
    }
}

package com.group13.dynamicwayfinder;


import androidx.test.espresso.Espresso;
import androidx.test.espresso.ViewAction;
import androidx.test.rule.ActivityTestRule;

import com.group13.dynamicwayfinder.Activities.Authentication.MainActivity;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;


public class LoginTest  {


    @Rule
    public ActivityTestRule<MainActivity> myActivity =
            new ActivityTestRule<>(MainActivity.class);

    @Before
    public void setUp(){
        myActivity.getActivity();
    }

    @Test
    public void loginDisplayed(){
        onView(withId(R.id.loginlogo))
                .check(matches(isDisplayed()));
    }

    @Test
    public void forgotPassword(){
        onView(withId(R.id.forgotpasswordbutton))
                .perform(click());
        onView(withId(R.id.newpword))
                .check(matches(isDisplayed()));
    }

    @Test
    public void noLogin(){
        onView(withId(R.id.loginbutton))
                .perform(click());
        onView(withId(R.id.loginlogo))
                .check(matches(isDisplayed()));
    }

    @Test
    public void noSignUp(){
        onView(withId(R.id.signupbutton))
                .perform(click());
        onView(withId(R.id.loginlogo))
                .check(matches(isDisplayed()));
    }

    @Test
    public void badEmail(){
        onView(withId(R.id.user))
                .perform(clearText(),replaceText("terrible"), closeSoftKeyboard());
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.loginbutton))
                .perform(click());
        onView(withId(R.id.loginlogo))
                .check(matches(isDisplayed()));
    }

    @Test
    public void badPassword(){
        onView(withId(R.id.password))
                .perform(replaceText("badpassword"), closeSoftKeyboard());
        onView(withId(R.id.loginbutton))
                .perform(click());
        onView(withId(R.id.loginlogo))
                .check(matches(isDisplayed()));
    }

    @Test
    public void goodEmailBadPassword(){
        onView(withId(R.id.user))
                .perform(replaceText("hasanfakhra@hotmail.com"), closeSoftKeyboard());
        onView(withId(R.id.password))
                .perform(replaceText("badpassword"), closeSoftKeyboard());
        onView(withId(R.id.loginbutton))
                .perform(click());
        onView(withId(R.id.loginlogo))
                .check(matches(isDisplayed()));
    }

    @Test
    public void goodPasswordBadEmail(){
        onView(withId(R.id.user))
                .perform(replaceText("hasanfakhra@hotmail.com"), closeSoftKeyboard());
        onView(withId(R.id.password))
                .perform(replaceText("password"), closeSoftKeyboard());
        onView(withId(R.id.loginbutton))
                .perform(click());
        onView(withId(R.id.loginlogo))
                .check(matches(isDisplayed()));
    }

    @Test
    public void bEmailSignUpTest(){
        onView(withId(R.id.user))
                .perform(replaceText("terrible"), closeSoftKeyboard());
        onView(withId(R.id.signupbutton))
                .perform(click());
        onView(withId(R.id.loginlogo))
                .check(matches(isDisplayed()));
    }

    @Test
    public void bPasswordSignUpTest(){
        onView(withId(R.id.password))
                .perform(replaceText("badpassword"), closeSoftKeyboard());
        onView(withId(R.id.signupbutton))
                .perform(click());
        onView(withId(R.id.loginlogo))
                .check(matches(isDisplayed()));
    }

    @Test
    public void goodEmailBadPasswordSignUpTest(){
        onView(withId(R.id.user))
                .perform(replaceText("hasanfakhra@hotmail.com"), closeSoftKeyboard());
        onView(withId(R.id.password))
                .perform(replaceText("badpassword"), closeSoftKeyboard());
        onView(withId(R.id.signupbutton))
                .perform(click());
        onView(withId(R.id.loginlogo))
                .check(matches(isDisplayed()));
    }

    @Test
    public void gPasswordBEmailSignUpTest(){
        onView(withId(R.id.user))
                .perform(replaceText("terrible"), closeSoftKeyboard());
        onView(withId(R.id.password))
                .perform(replaceText("password"), closeSoftKeyboard());
        onView(withId(R.id.signupbutton))
                .perform(click());
        onView(withId(R.id.loginlogo))
                .check(matches(isDisplayed()));
    }

    @Test
    public void UserExistsSignUp(){
        onView(withId(R.id.user))
                .perform(replaceText("testing@hotmail.com"), closeSoftKeyboard());
        onView(withId(R.id.password))
                .perform(replaceText("123456"), closeSoftKeyboard());
        onView(withId(R.id.signupbutton))
                .perform(click());
        onView(withId(R.id.loginlogo))
                .check(matches(isDisplayed()));
    }

    @Test
    public void UserNotRegisteredLogin(){
        onView(withId(R.id.user))
                .perform(replaceText("nonuser@hotmail.com"));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.password))
                .perform(replaceText("password"), closeSoftKeyboard());
        onView(withId(R.id.loginbutton))
                .perform(click());
        onView(withId(R.id.loginlogo))
                .check(matches(isDisplayed()));
    }
}
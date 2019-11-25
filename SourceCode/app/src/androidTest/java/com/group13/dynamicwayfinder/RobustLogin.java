package com.group13.dynamicwayfinder;


import androidx.test.rule.ActivityTestRule;

import com.group13.dynamicwayfinder.Activities.Authentication.MainActivity;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;


public class RobustLogin {

    @Rule
    public ActivityTestRule<MainActivity> myActivity =
            new ActivityTestRule<>(MainActivity.class);

    @Before
    public void setUp(){
        myActivity.getActivity();
    }

    @Test
    public void loginScreen(){
        onView(withId(R.id.loginlogo))
                .check(matches(isDisplayed()));
    }



    @Test
    public void numberEmailReset(){
        onView(withId(R.id.forgotpasswordbutton))
                .perform(click());
        onView(withId(R.id.newpword))
                .check(matches(isDisplayed()));
        onView(withId(R.id.forgotpwordemail))
                .perform(replaceText("9876554"), closeSoftKeyboard());
        onView(withId(R.id.newpword))
                .perform(click());
        onView(withId(R.id.newpword))
                .check(matches(isDisplayed()));
        onView(withId(R.id.closepwordmenu))
                .perform(click());
    }


    @Test
    public void numberEmailSignupas(){
        onView(withId(R.id.loginbutton))
                .check(matches(isDisplayed()));
        onView(withId(R.id.user))
                .perform(replaceText("65336222@hotmail"), closeSoftKeyboard());
        onView(withId(R.id.signupbutton))
                .perform(click());
        onView(withId(R.id.loginbutton))
                .check(matches(isDisplayed()));
    }

    @Test
    public void SEmailPasswordReset(){
        onView(withId(R.id.forgotpasswordbutton))
                .perform(click());
        onView(withId(R.id.newpword))
                .check(matches(isDisplayed()));
        onView(withId(R.id.forgotpwordemail))
                .perform(replaceText("*&*&*&*&*&"), closeSoftKeyboard());
        onView(withId(R.id.newpword))
                .perform(click());
        onView(withId(R.id.newpword))
                .check(matches(isDisplayed()));
        onView(withId(R.id.closepwordmenu))
                .perform(click());
    }


//    @Test
//    public void badEmailPasswordResetting(){
//        onView(withId(R.id.forgotpasswordbutton))
//                .perform(click());
//        onView(withId(R.id.newpword))
//                .check(matches(isDisplayed()));
//        onView(withId(R.id.forgotpwordemail))
//                .perform(typeText("nonsenseEmail"), closeSoftKeyboard());
//        onView(withId(R.id.newpword))
//                .perform(click());
//        onView(withId(R.id.newpword))
//                .check(matches(isDisplayed()));
//        onView(withId(R.id.closepwordmenu))
//                .perform(click());
//    }

    @Test
    public void badEmails(){

        onView(withId(R.id.loginlogo))
                .check(matches(isDisplayed()));
        onView(withId(R.id.user))
                .perform(replaceText("nonsenseemail"), closeSoftKeyboard());
        onView(withId(R.id.loginbutton))
                .perform(click());
        onView(withId(R.id.loginlogo))
                .check(matches(isDisplayed()));
    }

    @Test
    public void numberEmails(){
        onView(withId(R.id.loginbutton))
                .check(matches(isDisplayed()));
        onView(withId(R.id.user))
                .perform(replaceText("242131415"), closeSoftKeyboard());
        onView(withId(R.id.loginbutton))
                .perform(click());
        onView(withId(R.id.loginbutton))
                .check(matches(isDisplayed()));
    }



    @Test
    public void SymbolEmails(){
        onView(withId(R.id.loginbutton))
                .check(matches(isDisplayed()));
        onView(withId(R.id.user))
                .perform(replaceText("(()*&^^@hotmail"), closeSoftKeyboard());
        onView(withId(R.id.loginbutton))
                .perform(click());
        onView(withId(R.id.loginbutton))
                .check(matches(isDisplayed()));
    }

    @Test
    public void SymbolEmailTesting(){
        onView(withId(R.id.loginbutton))
                .check(matches(isDisplayed()));
        onView(withId(R.id.user))
                .perform(replaceText("^%#^@%#&"), closeSoftKeyboard());
        onView(withId(R.id.loginbutton))
                .perform(click());
        onView(withId(R.id.loginbutton))
                .check(matches(isDisplayed()));
        onView(withId(R.id.loginbutton))
                .perform(click());
    }

    @Test
    public void badPasswords(){

        onView(withId(R.id.loginlogo))
                .check(matches(isDisplayed()));
        onView(withId(R.id.password))
                .perform(typeText("blahblah"), closeSoftKeyboard());
        onView(withId(R.id.loginbutton))
                .perform(click());
        onView(withId(R.id.loginlogo))
                .check(matches(isDisplayed()));
    }

    @Test
    public void numberPasswords(){
        onView(withId(R.id.loginbutton))
                .check(matches(isDisplayed()));
        onView(withId(R.id.password))
                .perform(typeText("123456789"), closeSoftKeyboard());
        onView(withId(R.id.loginbutton))
                .perform(click());
        onView(withId(R.id.loginbutton))
                .check(matches(isDisplayed()));
    }

    @Test
    public void SymbolPasswords(){
        onView(withId(R.id.loginbutton))
                .check(matches(isDisplayed()));
        onView(withId(R.id.password))
                .perform(replaceText("()**&%&&&^"), closeSoftKeyboard());
        onView(withId(R.id.loginbutton))
                .perform(click());
        onView(withId(R.id.loginbutton))
                .check(matches(isDisplayed()));
        onView(withId(R.id.loginbutton))
                .perform(click());
    }


//    @Test
//    public void numEmailPasswordwordReset(){
//        onView(withId(R.id.forgotpasswordbutton))
//                .perform(click());
//        onView(withId(R.id.newpword))
//                .check(matches(isDisplayed()));
//        onView(withId(R.id.forgotpwordemail))
//                .perform(typeText("777777@hotmail"), closeSoftKeyboard());
//        onView(withId(R.id.newpword))
//                .perform(click());
//        onView(withId(R.id.newpword))
//                .check(matches(isDisplayed()));
//        onView(withId(R.id.closepwordmenu))
//                .perform(click());
//    }


    @Test
    public void SymbolsEmailPasswordReset(){
        onView(withId(R.id.forgotpasswordbutton))
                .perform(click());
        onView(withId(R.id.newpword))
                .check(matches(isDisplayed()));
        onView(withId(R.id.forgotpwordemail))
                .perform(replaceText("*&*^&*@hotmail"), closeSoftKeyboard());
        onView(withId(R.id.newpword))
                .perform(click());
        onView(withId(R.id.newpword))
                .check(matches(isDisplayed()));
        onView(withId(R.id.closepwordmenu))
                .perform(click());
    }
    @Test
    public void bEmailSup(){

        onView(withId(R.id.loginlogo))
                .check(matches(isDisplayed()));
        onView(withId(R.id.user))
                .perform(replaceText("nonsenseemail"), closeSoftKeyboard());
        onView(withId(R.id.signupbutton))
                .perform(click());
        onView(withId(R.id.loginlogo))
                .check(matches(isDisplayed()));
    }

    @Test
    public void numberEmailSignup(){
        onView(withId(R.id.loginbutton))
                .check(matches(isDisplayed()));
        onView(withId(R.id.user))
                .perform(replaceText("984657"), closeSoftKeyboard());
        onView(withId(R.id.signupbutton))
                .perform(click());
        onView(withId(R.id.loginbutton))
                .check(matches(isDisplayed()));
    }



    @Test
    public void SymbolSignup(){
        onView(withId(R.id.loginbutton))
                .check(matches(isDisplayed()));
        onView(withId(R.id.user))
                .perform(replaceText("*(&*$#&($*@hotmail"), closeSoftKeyboard());
        onView(withId(R.id.signupbutton))
                .perform(click());
        onView(withId(R.id.loginbutton))
                .check(matches(isDisplayed()));
    }



    @Test
    public void SymbolPasswordSign(){
        onView(withId(R.id.loginbutton))
                .check(matches(isDisplayed()));
        onView(withId(R.id.password))
                .perform(replaceText("*(*(******"), closeSoftKeyboard());
        onView(withId(R.id.signupbutton))
                .perform(click());
        onView(withId(R.id.loginbutton))
                .check(matches(isDisplayed()));
        onView(withId(R.id.loginbutton))
                .perform(click());
    }

}
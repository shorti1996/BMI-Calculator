package com.liebert.bmiCalc;


import android.support.test.espresso.Espresso;
import android.support.test.espresso.ViewInteraction;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class ChangingUnitsClearsData {

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void mainActivityTest123() {
        ViewInteraction textInputEditText = onView(
                withId(R.id.mass_tiet));
        textInputEditText.perform(scrollTo(), click());

        ViewInteraction textInputEditText2 = onView(
                withId(R.id.mass_tiet));
        textInputEditText2.perform(scrollTo(), replaceText("65")/*, closeSoftKeyboard()*/);

        ViewInteraction textInputEditText3 = onView(
                withId(R.id.height_tiet));
        textInputEditText3.perform(scrollTo(), replaceText("175")/*, closeSoftKeyboard()*/);

        onView(withId(R.id.height_tiet)).perform(closeSoftKeyboard());

        ViewInteraction editText = onView(
                allOf(withId(R.id.mass_tiet), withText("65"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.mass_til),
                                        0),
                                0),
                        isDisplayed()));
        editText.check(matches(withText("65")));

        ViewInteraction editText2 = onView(
                allOf(withId(R.id.height_tiet), withText("175"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.height_til),
                                        0),
                                0),
                        isDisplayed()));
        editText2.check(matches(withText("175")));

        ViewInteraction actionMenuItemView = onView(
                allOf(withId(R.id.action_settings), withText("Settings"), withContentDescription("Settings"), isDisplayed()));
        actionMenuItemView.perform(click());

        ViewInteraction recyclerView = onView(
                allOf(withId(R.id.list),
                        withParent(allOf(withId(android.R.id.list_container),
                                withParent(withId(R.id.weather_settings_fragment)))),
                        isDisplayed()));
        recyclerView.perform(actionOnItemAtPosition(0, click()));

        ViewInteraction appCompatCheckedTextView = onView(
                allOf(withId(android.R.id.text1), withText("Metric"),
                        childAtPosition(
                                allOf(withId(R.id.select_dialog_listview),
                                        withParent(withId(R.id.contentPanel))),
                                0),
                        isDisplayed()));
        appCompatCheckedTextView.perform(click());

        ViewInteraction recyclerView2 = onView(
                allOf(withId(R.id.list),
                        withParent(allOf(withId(android.R.id.list_container),
                                withParent(withId(R.id.weather_settings_fragment)))),
                        isDisplayed()));
        recyclerView2.perform(actionOnItemAtPosition(0, click()));

        ViewInteraction appCompatCheckedTextView2 = onView(
                allOf(withId(android.R.id.text1), withText("Retard"),
                        childAtPosition(
                                allOf(withId(R.id.select_dialog_listview),
                                        withParent(withId(R.id.contentPanel))),
                                1),
                        isDisplayed()));
        appCompatCheckedTextView2.perform(click());

        ViewInteraction appCompatImageButton = onView(
                allOf(withContentDescription("Navigate up"),
                        withParent(allOf(withId(R.id.action_bar),
                                withParent(withId(R.id.action_bar_container)))),
                        isDisplayed()));
        appCompatImageButton.perform(click());

        ViewInteraction editText3 = onView(
                allOf(withId(R.id.mass_tiet),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.mass_til),
                                        0),
                                0),
                        isDisplayed()));
        editText3.check(matches(withText("")));

        ViewInteraction editText4 = onView(
                allOf(withId(R.id.height_tiet),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.height_til),
                                        0),
                                0),
                        isDisplayed()));
        editText4.check(matches(withText("")));

    }

    private static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position) {

        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    }
}

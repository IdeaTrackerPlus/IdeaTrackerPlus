package manparvesh.ideatrackerplus;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.test.espresso.ViewInteraction;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v7.preference.PreferenceManager;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.swipeLeft;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withResourceName;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class IntroTest {

    @Rule
    public ActivityTestRule<MyIntro> mMyIntroTestRule = new ActivityTestRule<>(MyIntro.class);

    @Test
    public void introTest() {

        ViewInteraction textView = onView(
                allOf(withId(R.id.description), withText("All your ideas and tasks\nin one place"),
                        childAtPosition(
                                allOf(withId(R.id.main),
                                        withParent(withId(R.id.view_pager))),
                                2),
                        isDisplayed()));
        textView.check(matches(withText("All your ideas and tasks\nin one place")));

        ViewInteraction appCompatImageButton = onView(
                allOf(withId(R.id.next),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.bottom),
                                        1),
                                2),
                        isDisplayed()));
        appCompatImageButton.perform(click());

        ViewInteraction appIntroViewPager = onView(
                allOf(withId(R.id.view_pager),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                1),
                        isDisplayed()));
        appIntroViewPager.perform(swipeLeft());

        ViewInteraction textView2 = onView(allOf(allOf(withResourceName("description"), withText("Class your ideas in projects\nto stay on focus")),
                childAtPosition(
                        allOf(withId(R.id.main),
                                withParent(withId(R.id.view_pager))),
                        2),
                isDisplayed()));
        textView2.check(matches(withText("Class your ideas in projects\nto stay on focus")));

        ViewInteraction appIntroViewPager2 = onView(
                allOf(withId(R.id.view_pager),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                1),
                        isDisplayed()));
        appIntroViewPager2.perform(swipeLeft());

        ViewInteraction textView3 = onView(
                allOf(withId(R.id.description), withText("And keep track of your progress"),
                        childAtPosition(
                                allOf(withId(R.id.main),
                                        withParent(withId(R.id.view_pager))),
                                2),
                        isDisplayed()));
        textView3.check(matches(withText("And keep track of your progress")));

        ViewInteraction appCompatImageButton3 = onView(
                allOf(withId(R.id.done),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.bottom),
                                        1),
                                3),
                        isDisplayed()));
        appCompatImageButton3.perform(click());
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

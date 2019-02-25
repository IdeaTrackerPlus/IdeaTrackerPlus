package manparvesh.ideatrackerplus;


import android.content.Intent;
import android.support.test.espresso.ViewInteraction;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.hamcrest.core.IsInstanceOf;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.pressBack;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.assertion.ViewAssertions.doesNotExist;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static android.support.test.espresso.matcher.ViewMatchers.hasFocus;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withClassName;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withResourceName;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class AddProject extends AbstractUITest {

    @Override
    protected boolean isIntrosActive() {
        return false;
    }

    @Override
    public boolean isCleanData() {
        return true;
    }

    @Test
    public void addProject() {
        ViewInteraction leftDrawerButton = onView(
                allOf(withContentDescription("Open"),
                        childAtPosition(
                                allOf(withId(R.id.toolbar),
                                        childAtPosition(
                                                withId(R.id.appbar),
                                                0)),
                                1),
                        isDisplayed()));
        leftDrawerButton.perform(click());

        ViewInteraction openProjectList = onView(
                allOf(allOf(withId(R.id.material_drawer_recycler_view), hasFocus()),
                        childAtPosition(
                                withId(R.id.material_drawer_slider_layout),
                                1)));
        openProjectList.perform(actionOnItemAtPosition(5, click()));

        ViewInteraction appCompatEditText = onView(
                allOf(withId(R.id.editProjectName),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.ld_custom_view_container),
                                        0),
                                1)));
        String addProjectTest = "Ad"+System.currentTimeMillis();
        appCompatEditText.perform(scrollTo(), replaceText(addProjectTest), closeSoftKeyboard());

        ViewInteraction appCompatButton = onView(
                allOf(withId(R.id.projectDoneButton), withText("CREATE"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.LinearLayout")),
                                        0),
                                1),
                        isDisplayed()));
        appCompatButton.perform(click());


        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onView(allOf(withId(R.id.material_drawer_recycler_view), hasFocus())).perform(pressBack());
        ViewInteraction textView = onView(
                allOf(withText(addProjectTest)
                        ,
                        isDisplayed()));
        textView.check(matches(withText(addProjectTest)));
        leftDrawerButton.perform(click());
        openProjectList.perform(actionOnItemAtPosition(4, click()));

        ViewInteraction textView2 = onView(
                allOf(withResourceName("material_drawer_email"), withText(addProjectTest),
                        isDisplayed()));
        textView2.check(matches(withText(addProjectTest)));
        textView2.perform(click());


        leftDrawerButton.perform(click());


        ViewInteraction recyclerView3 = onView(
                allOf(allOf(withId(R.id.material_drawer_recycler_view), hasFocus()),
                        childAtPosition(
                                withId(R.id.material_drawer_slider_layout),
                                1)));
        recyclerView3.perform(actionOnItemAtPosition(2, click()));

        ViewInteraction deleteButton = onView(
                allOf(withId(R.id.ld_btn_yes), withText("DELETE"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.LinearLayout")),
                                        2),
                                3),
                        isDisplayed()));

        deleteButton.perform(click());
        leftDrawerButton.perform(click());
        openProjectList.perform(actionOnItemAtPosition(4, click()));

        ViewInteraction textView3 = onView(
                allOf(withId(R.id.material_drawer_email), withText(addProjectTest),
                        childAtPosition(
                                childAtPosition(
                                        IsInstanceOf.<View>instanceOf(android.widget.LinearLayout.class),
                                        1),
                                0),
                        isDisplayed()));
        textView3.check(doesNotExist());
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

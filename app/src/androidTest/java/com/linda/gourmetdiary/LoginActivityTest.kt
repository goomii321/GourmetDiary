package com.linda.gourmetdiary


import android.view.View
import android.view.ViewGroup
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.filters.LargeTest
import androidx.test.rule.ActivityTestRule
import androidx.test.runner.AndroidJUnit4
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers.allOf
import org.hamcrest.TypeSafeMatcher
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@LargeTest
@RunWith(AndroidJUnit4::class)
class LoginActivityTest {

    @Rule
    @JvmField
    var mActivityTestRule = ActivityTestRule(MainActivity::class.java)

    @Test
    fun loginActivityTest() {

        Thread.sleep(3000)

        val floatingActionButton = onView(
            allOf(
                withId(R.id.add_fbtn),
                isDisplayed()
            )
        )
        Thread.sleep(3000)
        floatingActionButton.perform(click())

        Thread.sleep(3000)

        val floatingActionButton2 = onView(
            allOf(
                withId(R.id.add_default)
            )
        )
        Thread.sleep(3000)
        floatingActionButton2.perform(forceClick())
//
//        val appCompatTextView = onView(
//            allOf(
//                withId(R.id.food_info), withText("餐點資訊"),
//                childAtPosition(
//                    childAtPosition(
//                        withId(R.id.scroll),
//                        0
//                    ),
//                    4
//                )
//            )
//        )
//        appCompatTextView.perform(scrollTo(), click())
//
//        val textInputEditText = onView(
//            allOf(
//                withId(R.id.food_input),
//                childAtPosition(
//                    childAtPosition(
//                        withId(R.id.food_title),
//                        0
//                    ),
//                    0
//                )
//            )
//        )
//        textInputEditText.perform(scrollTo(), replaceText("可樂"), closeSoftKeyboard())
//
//        pressBack()
    }

    private fun childAtPosition(
        parentMatcher: Matcher<View>, position: Int
    ): Matcher<View> {

        return object : TypeSafeMatcher<View>() {
            override fun describeTo(description: Description) {
                description.appendText("Child at position $position in parent ")
                parentMatcher.describeTo(description)
            }

            public override fun matchesSafely(view: View): Boolean {
                val parent = view.parent
                return parent is ViewGroup && parentMatcher.matches(parent)
                        && view == parent.getChildAt(position)
            }
        }
    }

    fun forceClick(): ViewAction? {
        return object : ViewAction {
            override fun getConstraints(): Matcher<View> {
                return allOf(isClickable(), isEnabled(), isDisplayed())
            }

            override fun getDescription(): String {
                return "force click"
            }

            override fun perform(uiController: UiController, view: View) {
                view.performClick() // perform click without checking view coordinates.
                uiController.loopMainThreadUntilIdle()
            }
        }
    }
}

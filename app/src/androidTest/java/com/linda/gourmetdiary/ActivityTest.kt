package com.linda.gourmetdiary


import android.view.View
import android.view.ViewGroup
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
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
class ActivityTest {

    @Rule
    @JvmField
    var mActivityTestRule = ActivityTestRule(MainActivity::class.java)

    @Test
    fun activityTest() {

        val floatingActionButton = onView(
            withId(R.id.add_fbtn)).perform(click())
        floatingActionButton

        Thread.sleep(5000)

        val floatingActionButton2 = onView(withId(R.id.add_default)).perform(click())
        floatingActionButton2

        Thread.sleep(5000)

//        val appCompatTextView2 = onView(withId(R.id.food_info)).perform(click())
//        appCompatTextView2
//
//        Thread.sleep(2000)

//        val setTextFood = onView(withId(R.id.food_input))
//        setTextFood.perform(typeText("起司牛肉堡"),closeSoftKeyboard())

//        Thread.sleep(3000)

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
}

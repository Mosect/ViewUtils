package com.mosect.viewutils.library;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

//        assertEquals("com.mosect.viewutils.library.test", appContext.getPackageName());
        int spec = View.MeasureSpec.makeMeasureSpec(200, View.MeasureSpec.UNSPECIFIED);
        int size = View.MeasureSpec.getSize(spec);
        assertEquals(size, 200);
    }
}

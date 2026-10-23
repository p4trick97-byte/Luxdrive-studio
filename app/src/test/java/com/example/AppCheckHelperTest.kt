package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.util.AppCheckHelper
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class AppCheckHelperTest {

    @Test
    fun `getOrCreateDebugSecret returns non empty stable UUID`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val secret1 = AppCheckHelper.getOrCreateDebugSecret(context)
        val secret2 = AppCheckHelper.getOrCreateDebugSecret(context)

        assertNotNull(secret1)
        assertTrue(secret1.isNotBlank())
        assertEquals("Il secret deve rimanere stabile e identico tra letture successive", secret1, secret2)
    }

    @Test
    fun `setCustomDebugSecret updates stored debug token`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val customToken = "test-custom-debug-token-12345"

        val success = AppCheckHelper.setCustomDebugSecret(context, customToken)
        assertTrue(success)

        val retrieved = AppCheckHelper.getOrCreateDebugSecret(context)
        assertEquals(customToken, retrieved)
    }

    @Test
    fun `setCustomDebugSecret rejects blank input`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val success = AppCheckHelper.setCustomDebugSecret(context, "   ")
        assertFalse(success)
    }
}

package template.core.common

import android.os.StrictMode
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.After
import org.junit.runner.RunWith
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

@RunWith(AndroidJUnit4::class)
class PlatformServiceAndroidHostTest {
    private val platformService: PlatformService = PlatformServiceAndroid()

    @After
    fun resetStrictMode() {
        StrictMode.setThreadPolicy(StrictMode.ThreadPolicy.LAX)
        StrictMode.setVmPolicy(StrictMode.VmPolicy.LAX)
    }

    @Test
    fun setStrictMode_shouldSetLoggingPolicies_whenDisabled() {
        platformService.setStrictMode(isEnabled = false)

        assertEquals(expectedThreadPolicy(withDeath = false).toString(), StrictMode.getThreadPolicy().toString())
        assertEquals(expectedVmPolicy(withDeath = false).toString(), StrictMode.getVmPolicy().toString())
    }

    @Test
    fun setStrictMode_shouldSetDeathPenaltyPolicies_whenEnabled() {
        platformService.setStrictMode(isEnabled = true)

        assertEquals(expectedThreadPolicy(withDeath = true).toString(), StrictMode.getThreadPolicy().toString())
        assertEquals(expectedVmPolicy(withDeath = true).toString(), StrictMode.getVmPolicy().toString())
    }

    @Test
    fun setStrictMode_shouldProduceDifferentPolicies_forEnabledAndDisabled() {
        platformService.setStrictMode(isEnabled = false)
        val disabledThreadPolicy = StrictMode.getThreadPolicy().toString()
        val disabledVmPolicy = StrictMode.getVmPolicy().toString()

        platformService.setStrictMode(isEnabled = true)

        assertNotEquals(disabledThreadPolicy, StrictMode.getThreadPolicy().toString())
        assertNotEquals(disabledVmPolicy, StrictMode.getVmPolicy().toString())
        assertNotEquals(StrictMode.ThreadPolicy.LAX.toString(), disabledThreadPolicy)
        assertNotEquals(StrictMode.VmPolicy.LAX.toString(), disabledVmPolicy)
    }

    private fun expectedThreadPolicy(withDeath: Boolean): StrictMode.ThreadPolicy =
        StrictMode.ThreadPolicy
            .Builder()
            .detectDiskReads()
            .detectDiskWrites()
            .detectNetwork()
            .penaltyLog()
            .also { if (withDeath) it.penaltyDeath() }
            .build()

    private fun expectedVmPolicy(withDeath: Boolean): StrictMode.VmPolicy =
        StrictMode.VmPolicy
            .Builder()
            .detectLeakedSqlLiteObjects()
            .detectLeakedClosableObjects()
            .penaltyLog()
            .also { if (withDeath) it.penaltyDeath() }
            .build()
}

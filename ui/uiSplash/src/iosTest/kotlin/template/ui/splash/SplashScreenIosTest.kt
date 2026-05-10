package template.ui.splash

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.v2.runComposeUiTest
import com.github.takahirom.roborazzi.ExperimentalRoborazziApi
import io.github.takahirom.roborazzi.captureRoboImage
import kotlin.test.Test

@OptIn(ExperimentalTestApi::class, ExperimentalRoborazziApi::class)
internal class SplashScreenIosTest {
    @Test
    fun loadingLight() =
        runComposeUiTest {
            setContent { SplashScreenLoadingPreviewLight() }
            waitForIdle()
            onRoot().captureRoboImage(this, filePath = "template.ui.splash.SplashScreenIosTest.loadingLight.png")
        }

    @Test
    fun successLight() =
        runComposeUiTest {
            setContent { SplashScreenSuccessPreviewLight() }
            waitForIdle()
            onRoot().captureRoboImage(this, filePath = "template.ui.splash.SplashScreenIosTest.successLight.png")
        }

    @Test
    fun errorLight() =
        runComposeUiTest {
            setContent { SplashScreenErrorPreviewLight() }
            waitForIdle()
            onRoot().captureRoboImage(this, filePath = "template.ui.splash.SplashScreenIosTest.errorLight.png")
        }
}

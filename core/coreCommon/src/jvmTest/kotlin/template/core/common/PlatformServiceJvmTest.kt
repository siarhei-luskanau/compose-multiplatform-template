package template.core.common

import kotlin.test.Test

internal class PlatformServiceJvmTest {
    private val platformService = PlatformServiceJvm()

    @Test
    fun setStrictMode_shouldNotThrow_whenEnabled() {
        platformService.setStrictMode(true)
    }

    @Test
    fun setStrictMode_shouldNotThrow_whenDisabled() {
        platformService.setStrictMode(false)
    }
}

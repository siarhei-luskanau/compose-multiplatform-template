package template.navigation

import androidx.navigation3.runtime.NavKey
import template.ui.main.MainNavigationCallback
import template.ui.splash.SplashNavigationCallback

internal class AppNavigation(
    private val backStack: MutableList<NavKey>,
) : MainNavigationCallback, SplashNavigationCallback {
    override fun goBack() {
        backStack.removeLastOrNull()
    }

    override fun goMainScreen() {
        backStack.add(AppRoutes.Main)
    }
}

package template.navigation

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import kotlinx.serialization.Serializable
import org.koin.compose.getKoin
import template.ui.common.theme.AppTheme

@Preview
@Composable
fun NavApp() =
    AppTheme {
        val koin = getKoin()
        val backStack = mutableStateListOf<NavKey>(AppRoutes.Splash)
        val appNavigation = AppNavigation(backStack = backStack)
        NavDisplay(
            backStack = backStack,
            onBack = { backStack.removeLastOrNull() },
            entryProvider =
                entryProvider {
                    entry<AppRoutes.Splash> {
                        Text("Splash")
                    }
                    entry<AppRoutes.Main> {
                        Text("Main")
                    }
                },
        )
    }

internal sealed interface AppRoutes : NavKey {
    @Serializable
    data object Splash : AppRoutes

    @Serializable
    data object Main : AppRoutes
}

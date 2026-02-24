package template.di

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import org.koin.compose.KoinApplication
import org.koin.plugin.module.dsl.koinConfiguration
import template.navigation.NavApp

@Preview
@Composable
fun KoinApp() =
    KoinApplication(
        configuration = koinConfiguration<DiKoinApplication>(),
    ) {
        NavApp()
    }

package template.di

import org.koin.core.annotation.KoinApplication
import template.core.common.CoreCommonCommonModule
import template.core.pref.CorePrefCommonModule
import template.ui.main.MainCommonModule
import template.ui.splash.SplashCommonModule

@KoinApplication(
    modules = [
        CoreCommonCommonModule::class,
        CorePrefCommonModule::class,
        DiCommonModule::class,
        MainCommonModule::class,
        SplashCommonModule::class,
    ],
)
internal class DiKoinApplication

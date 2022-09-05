package io.github.pravinyo.docscanner.dependencyInjection

import dagger.Component
import io.github.pravinyo.docscanner.dependencyInjection.app.AppComponent
import io.github.pravinyo.docscanner.dependencyInjection.app.AppModule
import io.github.pravinyo.docscanner.dependencyInjection.app.AppScope
import io.github.pravinyo.docscanner.ui.PageFragmentTest
import io.github.pravinyo.docscanner.ui.UIAutomatorTest

@AppScope
@Component(modules = [AppModule::class, ExternalDependency::class])
interface AndroidTestComponent: AppComponent {
    fun inject(test: PageFragmentTest)
    fun inject(test: UIAutomatorTest)
}
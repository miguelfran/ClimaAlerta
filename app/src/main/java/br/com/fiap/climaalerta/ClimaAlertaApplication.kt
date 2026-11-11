package br.com.fiap.climaalerta

import android.app.Application
import br.com.fiap.climaalerta.di.AppContainer

class ClimaAlertaApplication : Application() {

    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}

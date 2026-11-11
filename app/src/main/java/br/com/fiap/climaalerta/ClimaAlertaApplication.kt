package br.com.fiap.climaalerta

import android.app.Application
import br.com.fiap.climaalerta.di.AppContainer

/** Application do app: guarda o container de dependências usado pelas ViewModels. */
class ClimaAlertaApplication : Application() {

    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}

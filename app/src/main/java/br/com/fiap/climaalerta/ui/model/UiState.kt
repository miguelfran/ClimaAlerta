package br.com.fiap.climaalerta.ui.model

import androidx.annotation.StringRes
import br.com.fiap.climaalerta.R
import kotlinx.coroutines.CancellationException
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException

/**
 * Estados possíveis de uma informação que vem do WebService.
 */
sealed interface UiState<out T> {
    /** Estado inicial: nada foi solicitado ainda. */
    data object Idle : UiState<Nothing>

    /** Requisição em andamento. */
    data object Loading : UiState<Nothing>

    /** Dados recebidos com sucesso. */
    data class Success<T>(val data: T) : UiState<T>

    /** Falha na requisição, com mensagem amigável para o usuário. */
    data class Error(@param:StringRes val messageRes: Int) : UiState<Nothing>
}

/** Executa uma chamada e converte o resultado em [UiState.Success] ou [UiState.Error]. */
suspend fun <T> loadUiState(block: suspend () -> T): UiState<T> =
    try {
        UiState.Success(block())
    } catch (exception: CancellationException) {
        throw exception
    } catch (exception: Exception) {
        UiState.Error(exception.toUserMessageRes())
    }

/** Traduz exceções técnicas em mensagens compreensíveis para o cidadão. */
@StringRes
fun Throwable.toUserMessageRes(): Int = when (this) {
    is SocketTimeoutException -> R.string.error_timeout
    is IOException -> R.string.error_no_connection
    is HttpException -> if (code() >= 500) R.string.error_server else R.string.error_request
    else -> R.string.error_unexpected
}

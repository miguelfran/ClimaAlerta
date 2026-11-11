package br.com.fiap.climaalerta.ui.model

import androidx.annotation.StringRes
import br.com.fiap.climaalerta.R
import kotlinx.coroutines.CancellationException
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException

sealed interface UiState<out T> {
    data object Idle : UiState<Nothing>

    data object Loading : UiState<Nothing>

    data class Success<T>(val data: T) : UiState<T>

    data class Error(@param:StringRes val messageRes: Int) : UiState<Nothing>
}

suspend fun <T> loadUiState(block: suspend () -> T): UiState<T> =
    try {
        UiState.Success(block())
    } catch (exception: CancellationException) {
        throw exception
    } catch (exception: Exception) {
        UiState.Error(exception.toUserMessageRes())
    }

@StringRes
fun Throwable.toUserMessageRes(): Int = when (this) {
    is SocketTimeoutException -> R.string.error_timeout
    is IOException -> R.string.error_no_connection
    is HttpException -> if (code() >= 500) R.string.error_server else R.string.error_request
    else -> R.string.error_unexpected
}

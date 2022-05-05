package com.mbitsystem.sliide.usersmanager.usecases

import androidx.annotation.StringRes
import com.mbitsystem.sliide.usersmanager.R
import retrofit2.HttpException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.inject.Inject

class GetErroDialogDataUseCase @Inject constructor(): (Throwable) -> GetErroDialogDataUseCase.DialogData {

    data class DialogData(
        @StringRes val title: Int,
        @StringRes val subtitle: Int
    )

    override fun invoke(error: Throwable): DialogData {
        val commonErrorDialogData = DialogData(
            title = R.string.error_dialog_common_title,
            subtitle = R.string.error_dialog_common_subtitle
        )
        return when (error) {
            is UnknownHostException, is SocketTimeoutException -> DialogData(
                    title = R.string.error_dialog_common_title,
                    subtitle = R.string.error_dialog_no_internet_subtitle
                )
            is HttpException -> {
                when (error.code()) {
                    ERROR_VALIDATION_CODE -> DialogData(
                        title = R.string.error_dialog_common_title,
                        subtitle = R.string.error_dialog_validation_subtitle
                    )
                    ERROR_SERVER_CODE -> DialogData(
                        title = R.string.error_dialog_common_title,
                        subtitle = R.string.error_dialog_server_subtitle
                    )
                    ERROR_AUTHORIZATION_FAILED_CODE, ERROR_FORBIDDEN_CODE -> DialogData(
                            title = R.string.error_dialog_common_title,
                            subtitle = R.string.error_dialog_authorization_subtitle
                        )
                    else -> commonErrorDialogData
                }
            }
            else -> commonErrorDialogData
        }
    }

    companion object {
        private const val ERROR_VALIDATION_CODE = 422
        private const val ERROR_SERVER_CODE = 500
        private const val ERROR_AUTHORIZATION_FAILED_CODE = 401
        private const val ERROR_FORBIDDEN_CODE = 403
    }
}

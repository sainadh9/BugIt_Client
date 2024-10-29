package com.bugit.bugit.utils

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.os.Build
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import kotlinx.coroutines.flow.Flow
import retrofit2.HttpException
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


object UniversalManager {

    val calendar = Calendar.getInstance()

    fun getLocalDateTimeInNumberFormat(isTimeStamp:Boolean = false): String {
        calendar.setTimeInMillis(System.currentTimeMillis())
        val formatterDateTime = SimpleDateFormat(if (isTimeStamp)"dd-MM-yyyy, hh:mm:ss a" else "dd-MM-yyyy", Locale.getDefault())
        return formatterDateTime.format(calendar.getTime())

    }

    fun <T1, T2, T3, R> combine(
        flow: Flow<T1>,
        flow2: Flow<T2>,
        flow3: Flow<T3>,
        transform: suspend (T1, T2, T3) -> R
    ): Flow<R> {
        return kotlinx.coroutines.flow.combine(
            flow,
            flow2,
            flow3,

            ) { args: Array<*> ->
            @Suppress("UNCHECKED_CAST")
            transform(
                args[0] as T1,
                args[1] as T2,
                args[2] as T3,
            )
        }
    }

    fun createAlertDialog(
        context: Context,
        title: String = "",
        message: String = "",
        isPositiveButton: Boolean = false,
        isPositiveButtonText: String = "",
        positiveAction: () -> Unit = {},
        isNegativeButton: Boolean = false,
        isNegativeButtonText: String = "",
        negativeAction: () -> Unit = {}
    ) {
        var alertDialog: AlertDialog? = null
        val alertDialogBuilder = AlertDialog.Builder(context)
        if (title.isNotEmpty())
            alertDialogBuilder.setTitle(title)
        if (message.isNotEmpty())
            alertDialogBuilder.setMessage(message)
        if (isPositiveButton) {
            alertDialogBuilder.setPositiveButton(isPositiveButtonText) { dialogInterface: DialogInterface, i: Int ->
                positiveAction()
            }
        }
        if (isNegativeButton) {
            alertDialogBuilder.setNegativeButton(isNegativeButtonText) { dialogInterface: DialogInterface, i: Int ->
                negativeAction()
            }
        }
        alertDialog = alertDialogBuilder.create()
        alertDialog.setCancelable(false)
        if (!alertDialog.isShowing)
            alertDialog.show()

    }


    fun throwError(e: Exception, context: Context) {
        UniversalManager.createAlertDialog(context = context,
            "", e.message.toString(),
            true, context.getString(com.bugit.bugit.R.string.ok),
            {

            }, false, "",
            {}
        )

    }


    fun throwError(e: Exception): String {
        return when (e) {
            is IOException -> "Network Error ${e.message.toString()}"
            is HttpException -> "HTTP Error  ${e.message}"
            else -> e.message.toString().trim()
        }

    }


    fun showToastMethod(context: Context?, text: String?, duration: Boolean) {
        if (context == null) return

        val toast = Toast(context)
        toast.duration = if (duration) Toast.LENGTH_LONG else Toast.LENGTH_SHORT
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view: View = inflater.inflate(com.bugit.bugit.R.layout.toast, null)

        // Toast toast = Toast.makeText(context, text, duration ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT);
        // View view = toast.getView();
        view.setBackground(
            ContextCompat.getDrawable(
                context,
                com.bugit.bugit.R.drawable.toast_background_frame
            )
        )
        val text1: TextView = view.findViewById(com.bugit.bugit.R.id.text)
        text1.setText(text ?: "")
        text1.setTextColor(ContextCompat.getColor(context, com.bugit.bugit.R.color.white))
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            text1.setTextAppearance(com.bugit.bugit.R.style.toastTextStyle)
        }
        text1.setLineSpacing(context.resources.getDimension(com.bugit.bugit.R.dimen.dimen_5), 1.0f)
        text1.setPadding(25, 5, 5, 5)
        toast.setGravity(Gravity.CENTER, 0, 0)
        toast.setView(view)
        toast.show()
    }


}
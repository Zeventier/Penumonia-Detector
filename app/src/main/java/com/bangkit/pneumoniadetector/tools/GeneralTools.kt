package com.bangkit.pneumoniadetector.tools

import android.content.Context
import android.content.DialogInterface
import android.content.pm.PackageManager
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat

// For general purpose
object GeneralTools {

    // A method which will show an Alert Dialog
    fun showAlertDialog(
        context: Context,
        message: String,
        dialogOnClickListener: DialogInterface.OnClickListener =
            DialogInterface.OnClickListener{ _, _ -> }
    ) {
        val alertDialog = AlertDialog.Builder(context)
            .setMessage(message)
            .setPositiveButton("OK", dialogOnClickListener)

        val create = alertDialog.create()
        create.show()
    }

    // A method which will return true if a required permission is granted
    fun allPermissionGranted(required: Array<String>, context: Context) = required.all{
        ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
    }
}
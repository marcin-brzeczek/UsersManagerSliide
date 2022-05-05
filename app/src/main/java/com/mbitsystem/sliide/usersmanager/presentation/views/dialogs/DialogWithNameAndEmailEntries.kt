package com.mbitsystem.sliide.usersmanager.presentation.views.dialogs

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.widget.ArrayAdapter
import androidx.annotation.StringRes
import androidx.appcompat.view.ContextThemeWrapper
import com.mbitsystem.sliide.usersmanager.R
import com.mbitsystem.sliide.usersmanager.databinding.DialogWithNameAndEmailEntriesBinding

class DialogWithNameAndEmailEntries(context: Context) :
    AlertDialog.Builder(ContextThemeWrapper(context, R.style.AlertDialog)) {

    fun show(
        @StringRes title: Int,
        @StringRes message: Int? = null,
        onPositiveButtonClick: (String, String, String) -> Unit
    ): AlertDialog {
        val inflater = LayoutInflater.from(context)
        val binding = DialogWithNameAndEmailEntriesBinding.inflate(inflater)
        val genderTypes = context.resources.getStringArray(R.array.gender_type)
        val adapter = ArrayAdapter(context, android.R.layout.simple_spinner_item, genderTypes)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.genderTypeSpinner.adapter = adapter

        val builder = this.setCancelable(false)
            .setTitle(title)
            .setPositiveButton(R.string.add_button_title)
            { _, _ ->
                onPositiveButtonClick(
                    binding.inputName.text.toString(),
                    binding.inputEmail.text.toString(),
                    binding.genderTypeSpinner.selectedItem.toString()
                )
            }
            .setView(binding.root)
        message?.let { nonNulalbleMessageRes ->
            builder.setMessage(nonNulalbleMessageRes)
        }
        return builder.show()
    }
}

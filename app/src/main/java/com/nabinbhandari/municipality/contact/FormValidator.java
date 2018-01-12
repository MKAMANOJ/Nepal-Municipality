package com.nabinbhandari.municipality.contact;

import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

/**
 * Created at 11:32 PM on 1/12/2018.
 *
 * @author bnabin51@gmail.com
 */
abstract class FormValidator {

    private EditText editText;

    /**
     * @param validationListener is used to send callback every time any text is changed.
     */
    FormValidator(@NonNull final TextInputLayout textInputLayout,
                  @NonNull final Runnable validationListener) {
        this.editText = textInputLayout.getEditText();
        if (editText == null) {
            throw new RuntimeException("Supplied input layout do not have an EditText.");
        }

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                textInputLayout.setError(getError(s.toString()));
                validationListener.run();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    textInputLayout.setError(getError(editText.getText().toString()));
                }
            }
        });
    }

    boolean isInvalid() {
        return getError(editText.getText().toString()) != null;
    }

    /**
     * Checks for error in the input field.
     *
     * @param input the text entered by the user.
     * @return null, or error message if exists.
     */
    abstract String getError(String input);

}

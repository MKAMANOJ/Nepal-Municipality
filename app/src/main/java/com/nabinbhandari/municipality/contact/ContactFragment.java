package com.nabinbhandari.municipality.contact;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nabinbhandari.firebaseutils.ValueEventAdapter;
import com.nabinbhandari.municipality.AppUtils;
import com.nabinbhandari.municipality.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created at 8:49 PM on 1/11/2018.
 *
 * @author bnabin51@gmail.com
 */

public class ContactFragment extends Fragment implements Runnable {

    private List<FormValidator> validators = new ArrayList<>();
    private Button sendButton;

    private DatabaseReference reference;
    private ValueEventListener listener;

    public ContactFragment() {
    }

    public static ContactFragment newInstance() {
        return new ContactFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.layout_contact, container, false);
        updateInfo(rootView);

        sendButton = rootView.findViewById(R.id.contact_send_button);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendFeedback(rootView);
            }
        });
        sendButton.setEnabled(false);

        initValidators(rootView);

        EditText messageEditText = rootView.findViewById(R.id.contact_message_edit_text);
        messageEditText.setHorizontallyScrolling(false);
        messageEditText.setMaxLines(Integer.MAX_VALUE);
        return rootView;
    }

    private void updateInfo(View rootView) {
        final TextView titleTextView = rootView.findViewById(R.id.contact_title_text);
        final TextView addressTextView = rootView.findViewById(R.id.contact_address_text);
        final View phoneHolder = rootView.findViewById(R.id.contact_phone_holder);
        final TextView phone1TextView = rootView.findViewById(R.id.contact_phone1_text);
        final TextView phone2TextView = rootView.findViewById(R.id.contact_phone2_text);
        final View faxHolder = rootView.findViewById(R.id.contact_fax_holder);
        final TextView faxTextView = rootView.findViewById(R.id.contact_fax_text);
        final View emailHolder = rootView.findViewById(R.id.contact_email_holder);
        final TextView emailTextView = rootView.findViewById(R.id.contact_email_text);
        final View websiteHolder = rootView.findViewById(R.id.contact_website_holder);
        final TextView websiteTextView = rootView.findViewById(R.id.contact_website_text);
        final View facebookHolder = rootView.findViewById(R.id.contact_facebook_holder);
        final TextView facebookTextView = rootView.findViewById(R.id.contact_facebook_text);
        final View twitterHolder = rootView.findViewById(R.id.contact_twitter_holder);
        final TextView twitterTextView = rootView.findViewById(R.id.contact_twitter_text);
        final View googlePlusHolder = rootView.findViewById(R.id.contact_google_plus_holder);
        final TextView googlePlusTextView = rootView.findViewById(R.id.contact_google_plus_text);

        listener = new ValueEventAdapter(getContext()) {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Contact contact = dataSnapshot.getValue(Contact.class);
                if (contact == null) return;

                titleTextView.setText(contact.title);
                addressTextView.setText(contact.address);

                if (contact.phone1 == null && contact.phone2 == null)
                    phoneHolder.setVisibility(View.GONE);
                else {
                    phoneHolder.setVisibility(View.VISIBLE);
                    if (contact.phone1 == null) phone1TextView.setVisibility(View.GONE);
                    else {
                        phone1TextView.setVisibility(View.VISIBLE);
                        AppUtils.setPhoneNumber(phone1TextView, contact.phone1, contact.phone1);
                    }

                    if (contact.phone2 == null) phone2TextView.setVisibility(View.GONE);
                    else {
                        phone2TextView.setVisibility(View.VISIBLE);
                        AppUtils.setPhoneNumber(phone2TextView, contact.phone2, contact.phone2);
                    }
                }

                if (contact.fax == null) faxHolder.setVisibility(View.GONE);
                else {
                    faxHolder.setVisibility(View.VISIBLE);
                    AppUtils.setPhoneNumber(faxTextView, contact.fax, contact.fax);
                }

                if (contact.email == null) emailHolder.setVisibility(View.GONE);
                else {
                    emailHolder.setVisibility(View.VISIBLE);
                    emailTextView.setText(contact.email);
                }

                if (contact.website == null) websiteHolder.setVisibility(View.GONE);
                else {
                    websiteHolder.setVisibility(View.VISIBLE);
                    websiteTextView.setText(contact.website);
                }

                if (contact.facebook == null) facebookHolder.setVisibility(View.GONE);
                else {
                    facebookHolder.setVisibility(View.VISIBLE);
                    facebookTextView.setText(contact.facebook);
                }

                if (contact.twitter == null) twitterHolder.setVisibility(View.GONE);
                else {
                    twitterHolder.setVisibility(View.VISIBLE);
                    twitterTextView.setText(contact.twitter);
                }

                if (contact.google_plus == null) googlePlusHolder.setVisibility(View.GONE);
                else {
                    googlePlusHolder.setVisibility(View.VISIBLE);
                    googlePlusTextView.setText(contact.google_plus);
                }
            }
        };

        reference = FirebaseDatabase.getInstance().getReference("tbl_contact_us_info/1");
        reference.addValueEventListener(listener);
    }

    @Override
    public void onDestroy() {
        if (reference != null) reference.removeEventListener(listener);
        super.onDestroy();
    }

    private void initValidators(View rootView) {
        validators.clear();
        validators.add(new FormValidator((TextInputLayout) rootView.findViewById(R.id.nameInoutLayout), this) {
            @Override
            String getError(String input) {
                return input.equals("") ? "Enter your name." : null;
            }
        });
        validators.add(new FormValidator((TextInputLayout) rootView.findViewById(R.id.addressInputLayout), this) {
            @Override
            String getError(String input) {
                return input.equals("") ? "Enter your address." : null;
            }
        });
        validators.add(new FormValidator((TextInputLayout) rootView.findViewById(R.id.numberInputLayout), this) {
            @Override
            String getError(String input) {
                if (input.equals("")) return "Enter your number.";
                else if (input.length() < 5) return "Enter at least 5 digits.";
                else if (input.length() > 15) return "Enter at most 15 digits.";
                else return null;
            }
        });
        validators.add(new FormValidator((TextInputLayout) rootView.findViewById(R.id.emailInputLayout), this) {
            @Override
            String getError(String input) {
                if (input.equals("")) return "Enter your email.";
                return Patterns.EMAIL_ADDRESS.matcher(input).matches() ? null : "Enter a valid email address.";
            }
        });
        validators.add(new FormValidator((TextInputLayout) rootView.findViewById(R.id.messageInputLayout), this) {
            @Override
            String getError(String input) {
                return input.equals("") ? "Enter message to send." : null;
            }
        });
    }

    private void sendFeedback(View rootView) {
        EditText fullNameEditText = rootView.findViewById(R.id.contact_full_name_edit_text);
        EditText addressEditText = rootView.findViewById(R.id.contact_address_edit_text);
        EditText numberEditText = rootView.findViewById(R.id.contact_contact_number_edit_text);
        EditText emailEditText = rootView.findViewById(R.id.contact_email_edit_text);
        EditText messageEditText = rootView.findViewById(R.id.contact_message_edit_text);

        String fullName = fullNameEditText.getText().toString().trim();
        String address = addressEditText.getText().toString().trim();
        String contactNumber = numberEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String message = messageEditText.getText().toString().trim();
        EmailUtils.sendEmail(rootView.getContext(), fullName, address, contactNumber, email, message);

        /*fullNameEditText.getText().clear();
        addressEditText.getText().clear();
        emailEditText.getText().clear();
        numberEditText.getText().clear();
        messageEditText.getText().clear();*/
    }

    /*private void addFeedback(String fullName, String address, String contactNumber,
                             String email, String message) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("feedback");
        reference.keepSynced(true);
        HashMap<String, String> feedback = new HashMap<>();
        feedback.put("full_name", fullName);
        feedback.put("address", address);
        feedback.put("contact_number", contactNumber);
        feedback.put("email", email);
        feedback.put("message", message);
        reference.push().setValue(feedback);
    }*/

    @Override
    public void run() {
        if (sendButton == null) return;
        boolean valid = true;
        for (FormValidator validator : validators) {
            if (validator.isInvalid()) {
                valid = false;
                break;
            }
        }
        sendButton.setEnabled(valid);
    }

}

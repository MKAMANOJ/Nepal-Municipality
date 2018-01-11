package com.nabinbhandari.municipality.contact;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.nabinbhandari.firebaseutils.ValueEventAdapter;
import com.nabinbhandari.municipality.AppUtils;
import com.nabinbhandari.municipality.R;

import java.util.HashMap;

/**
 * Created at 8:49 PM on 1/11/2018.
 *
 * @author bnabin51@gmail.com
 */

public class ContactFragment extends Fragment {

    public ContactFragment() {
    }

    public static ContactFragment newInstance() {
        return new ContactFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.layout_contact, container, false);
        updateInfo(view);

        Button sendButton = view.findViewById(R.id.contact_send_button);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendFeedback(view);
            }
        });

        EditText messageEditText = view.findViewById(R.id.contact_message_edit_text);
        messageEditText.setHorizontallyScrolling(false);
        messageEditText.setMaxLines(Integer.MAX_VALUE);
        return view;
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

        FirebaseDatabase.getInstance().getReference("tbl_contact_us_info/1")
                .addListenerForSingleValueEvent(new ValueEventAdapter(getContext()) {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Contact contact = dataSnapshot.getValue(Contact.class);
                        if (contact == null) return;

                        titleTextView.setText(contact.title);
                        addressTextView.setText(contact.address);

                        if (contact.phone1 == null && contact.phone2 == null)
                            phoneHolder.setVisibility(View.GONE);
                        else {
                            if (contact.phone1 == null) phone1TextView.setVisibility(View.GONE);
                            else
                                AppUtils.setPhoneNumber(phone1TextView, contact.phone1, contact.phone1);

                            if (contact.phone2 == null) phone2TextView.setVisibility(View.GONE);
                            else
                                AppUtils.setPhoneNumber(phone2TextView, contact.phone2, contact.phone2);
                        }

                        if (contact.fax == null) faxHolder.setVisibility(View.GONE);
                        else AppUtils.setPhoneNumber(faxTextView, contact.fax, contact.fax);

                        if (contact.email == null) emailHolder.setVisibility(View.GONE);
                        else emailTextView.setText(contact.email);

                        if (contact.website == null) websiteHolder.setVisibility(View.GONE);
                        else websiteTextView.setText(contact.website);

                        if (contact.facebook == null) facebookHolder.setVisibility(View.GONE);
                        else facebookTextView.setText(contact.facebook);

                        if (contact.twitter == null) twitterHolder.setVisibility(View.GONE);
                        else twitterTextView.setText(contact.twitter);

                        if (contact.google_plus == null) googlePlusHolder.setVisibility(View.GONE);
                        else googlePlusTextView.setText(contact.google_plus);
                    }
                });
    }

    private void sendFeedback(View view) {
        EditText fullNameEditText = view.findViewById(R.id.contact_full_name_edit_text);
        String fullName = fullNameEditText.getText().toString().trim();
        if (fullName.equals("")) {
            fullNameEditText.requestFocus();
            fullNameEditText.setError("Please enter your name.");
            return;
        }

        EditText addressEditText = view.findViewById(R.id.contact_address_edit_text);
        String address = addressEditText.getText().toString().trim();
        if (address.equals("")) {
            addressEditText.requestFocus();
            addressEditText.setError("Please enter your address.");
            return;
        }

        EditText numberEditText = view.findViewById(R.id.contact_contact_number_edit_text);
        String contactNumber = numberEditText.getText().toString().trim();
        if (contactNumber.equals("")) {
            numberEditText.requestFocus();
            numberEditText.setError("Please enter your number.");
            return;
        }

        EditText emailEditText = view.findViewById(R.id.contact_email_edit_text);
        String email = emailEditText.getText().toString();
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.requestFocus();
            emailEditText.setError("Please enter a valid email address.");
            return;
        }

        EditText messageEditText = view.findViewById(R.id.contact_message_edit_text);
        String message = messageEditText.getText().toString();
        if (message.equals("")) {
            messageEditText.requestFocus();
            messageEditText.setError("Please enter a message to send.");
            return;
        }

        addFeedback(fullName, address, contactNumber, email, message);

        fullNameEditText.getText().clear();
        addressEditText.getText().clear();
        emailEditText.getText().clear();
        numberEditText.getText().clear();
        messageEditText.getText().clear();
        Toast.makeText(getContext(), "Thank you for your feedback.", Toast.LENGTH_SHORT).show();
    }

    private void addFeedback(String fullName, String address, String contactNumber,
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
    }

}

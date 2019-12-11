package com.example.coworkandroid.fragments;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.coworkandroid.R;
import com.example.coworkandroid.model.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class TicketCreationFragment extends Fragment {

    ArrayList<User> users;
    String [] names;
    Button btSubmit;
    EditText etTitle, etDescription, etDateCreation, etDateExpected;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        try{
            new GetUsersList().execute().get();
        }catch (Exception e){
            e.printStackTrace();
        }

        names = new String[users.size()];

        for (int i = 0; i < users.size(); i++) {
            StringBuilder sb = new StringBuilder(users.get(i).getSurname());
            sb.append(" ");
            sb.append(users.get(i).getName());
            names[i] = sb.toString();
        }
        return inflater.inflate(R.layout.fragment_ticket_creation, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        //get the spinner from the xml.
        final Spinner dropdown = getActivity().findViewById(R.id.spinner);
        final Spinner creator = getActivity().findViewById(R.id.spinnerCreator);
        final Spinner assignee = getActivity().findViewById(R.id.spinnerAssignee);
        //create a list of items for the spinner.
        final String[] status = new String[]{"new", "ongoing", "done"};
        //create an adapter to describe how the items are displayed, adapters are used in several places in android.
        //There are multiple variations of this, but this is the basic variant.
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, status);
        ArrayAdapter<String> adapter2 = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, names);
        //set the spinners adapter to the previously created one.
        dropdown.setAdapter(adapter);
        creator.setAdapter(adapter2);
        assignee.setAdapter(adapter2);

        etTitle = getActivity().findViewById(R.id.etTitle);
        etDescription = getActivity().findViewById(R.id.etDescription);
        etDateCreation = getActivity().findViewById(R.id.etCreationDate);
        etDateExpected = getActivity().findViewById(R.id.editText2);

        btSubmit = getActivity().findViewById(R.id.buttonSubmit);
        btSubmit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try{
                    if(etTitle.getText().toString().equals("")
                            || etDateCreation.getText().toString().equals("")
                            || etDateExpected.getText().toString().equals(""))
                        showToast("All fields other than Description are mandatory");
                    else{

                        try {
                            new CreateTicket().execute(etTitle.getText().toString(), etDescription.getText().toString(),
                                    users.get(creator.getSelectedItemPosition()).getName(),
                                    users.get(creator.getSelectedItemPosition()).getSurname(),
                                    users.get(assignee.getSelectedItemPosition()).getName(),
                                    users.get(assignee.getSelectedItemPosition()).getSurname(),
                                    status[dropdown.getSelectedItemPosition()],
                                    etDateCreation.getText().toString(), etDateExpected.getText().toString()).get();
                        }catch (Exception e){
                            e.printStackTrace();
                        }


                        Fragment fragment = new TicketListFragment();
                        if (fragment != null) {
                            FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                            ft.replace(R.id.content_frame, fragment);
                            ft.commit();
                        }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
        super.onViewCreated(view, savedInstanceState);

    }

    public class GetUsersList extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {

            User user;

            HttpUrl httpUrl = new HttpUrl.Builder()
                    .scheme("https")
                    .host("apicowork.herokuapp.com")
                    .addPathSegment("user")
                    .addPathSegment("users")
                    .build();


            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .connectTimeout(5, TimeUnit.SECONDS)
                    .writeTimeout(5, TimeUnit.SECONDS)
                    .readTimeout(5, TimeUnit.SECONDS)
                    .build();

            Request request = new Request.Builder()
                    .url(httpUrl)
                    .get()
                    .build();

            Response response;

            try {

                users = new ArrayList<>();
                response = okHttpClient.newCall(request).execute();

                if (response.isSuccessful()) {

                    String result;

                    if (response.body() != null) {
                        result = response.body().string();
                        JSONArray ticketArray = new JSONArray(result);

                        for(int i = 0; i < ticketArray.length(); i++){
                            JSONObject JSingleTicket = ticketArray.getJSONObject(i);
                            user = new User(JSingleTicket.getString("uuidUser"),
                                    JSingleTicket.getString("name"),
                                    JSingleTicket.getString("surname"),
                                    JSingleTicket.getString("mail"));
                            users.add(user);
                        }
                    }


                }
            } catch (Exception e) {

                e.printStackTrace();
                showToast("Connexion problem");

            }
            return null;
        }
    }

    public class CreateTicket extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {

            String title = strings[0];
            String description = strings[1];
            String nameCreator = strings[2];
            String surnameCreator = strings[3];
            String nameAssignee = strings[4];
            String surnameAssignee = strings[5];
            String status = strings[6];
            String dateTicketCreation = strings[7];
            String dateExpectedResolution = strings[8];

            HttpUrl httpUrl = new HttpUrl.Builder()
                    .scheme("https")
                    .host("apicowork.herokuapp.com")
                    .addPathSegment("ticket")
                    .addPathSegment("insertTicket")
                    .build();


            JSONObject json = new JSONObject();
            try {
                json.put("title", title);
                json.put("description", description);
                json.put("nameCreator", nameCreator);
                json.put("surnameCreator", surnameCreator);
                json.put("nameAssignee", nameAssignee);
                json.put("surnameAssignee", surnameAssignee);
                json.put("status", status);
                json.put("dateTicketCreation", dateTicketCreation);
                json.put("dateExpectedResolution", dateExpectedResolution);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            String jsonString = json.toString();


            RequestBody body = RequestBody.create(MediaType.parse("application/json"), jsonString);

            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .connectTimeout(5, TimeUnit.SECONDS)
                    .writeTimeout(5, TimeUnit.SECONDS)
                    .readTimeout(5, TimeUnit.SECONDS)
                    .build();

            Request request = new Request.Builder()
                    .url(httpUrl)
                    .post(body)
                    .build();

            try {

                okHttpClient.newCall(request).execute();

            } catch (Exception e) {

                e.printStackTrace();
                showToast("Connexion problem");

            }
            return null;
        }
    }

    public void showToast(final String Text) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getActivity(),
                        Text, Toast.LENGTH_LONG).show();
            }
        });
    }
}

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
import android.widget.Spinner;
import android.widget.Toast;

import com.example.coworkandroid.R;
import com.example.coworkandroid.model.Ticket;
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

public class TicketModificationFragment extends Fragment {

    ArrayList<Ticket> tickets;
    String[] TicketTitle;
    Button button;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        try {
            new TicketStatus().execute().get();
        } catch (Exception e) {
            e.printStackTrace();
        }


        TicketTitle = new String[tickets.size()];

        for(int i = 0; i < tickets.size(); i++){
            TicketTitle[i] = tickets.get(i).getTitle();
        }
        return inflater.inflate(R.layout.fragment_ticket_modification, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        final Spinner ticketSpinner = getActivity().findViewById(R.id.spinner3);
        final Spinner statusSpinner = getActivity().findViewById(R.id.spinnerTicketReview);
        final String[] status = new String[]{"new", "ongoing","done"};
        ArrayAdapter<String> titles = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, TicketTitle);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, status);
        ticketSpinner.setAdapter(titles);
        statusSpinner.setAdapter(adapter);

        button = getActivity().findViewById(R.id.button2);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try{

                    new ChangeStatus().execute(tickets.get(ticketSpinner.getSelectedItemPosition()).getUuidTicket(),
                            status[statusSpinner.getSelectedItemPosition()]).get();

                        Fragment fragment = new TicketListFragment();
                        if (fragment != null) {
                            FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                            ft.replace(R.id.content_frame, fragment);
                            ft.commit();
                        }

                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });



        super.onViewCreated(view, savedInstanceState);

    }

    public class TicketStatus extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {


            Ticket ticket;

            HttpUrl httpUrl = new HttpUrl.Builder()
                    .scheme("https")
                    .host("apicowork.herokuapp.com")
                    .addPathSegment("ticket")
                    .addPathSegment("tickets")
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

                tickets = new ArrayList<>();
                response = okHttpClient.newCall(request).execute();

                System.out.println("Response: " + response);


                if (response.isSuccessful()) {

                    String result;

                    if (response.body() != null) {
                        result = response.body().string();
                        JSONArray ticketArray = new JSONArray(result);
                        System.out.println("Result:" + result);

                        for(int i = 0; i < ticketArray.length(); i++){
                            JSONObject JSingleTicket = ticketArray.getJSONObject(i);
                            ticket = new Ticket(JSingleTicket.getString("uuidTicket"),
                                    JSingleTicket.getString("title"),
                                    JSingleTicket.getString("description"),
                                    JSingleTicket.getString("uuidCreator"),
                                    JSingleTicket.getString("uuidAssignee"),
                                    JSingleTicket.getString("status"),
                                    JSingleTicket.getString("dateTicketCreation"),
                                    JSingleTicket.getString("dateExpectedResolution"));

                            System.out.println("Id:" + ticket.getUuidTicket());
                            System.out.println("status:" + ticket.getStatus());
                            System.out.println("uuidAssigne: " + ticket.getUuidAssignee());

                            tickets.add(ticket);
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

    public class ChangeStatus extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {

            String uuidTicket = strings[0];
            String status = strings[1];

            HttpUrl httpUrl = new HttpUrl.Builder()
                    .scheme("https")
                    .host("apicowork.herokuapp.com")
                    .addPathSegment("ticket")
                    .addPathSegment("statuschange")
                    .build();


            JSONObject json = new JSONObject();
            try {
                json.put("uuidTicket", uuidTicket);
                json.put("status", status);
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

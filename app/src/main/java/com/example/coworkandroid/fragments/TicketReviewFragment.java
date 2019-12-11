package com.example.coworkandroid.fragments;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.coworkandroid.R;
import com.example.coworkandroid.model.Ticket;
import com.example.coworkandroid.model.User;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class TicketReviewFragment extends Fragment {

    ArrayList<Ticket> tickets;
    String[][] Ticket_Data;
    String[] TicketIds;
    TextView tvDescription,tvStatus,tvDateCreation,tvDateResolution,tvCreator,tvAssignee;
    ArrayList<User> users;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        tickets = new ArrayList<>();

        try {
            new TicketStatus().execute().get();
            new GetUsersList().execute().get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Ticket_Data = new String[tickets.size()][8];
        TicketIds = new String[tickets.size()];

        for(int i = 0; i < tickets.size(); i++){
            TicketIds[i] = tickets.get(i).getTitle();
            for(int j = 0; j < 8; j++){
                switch (j){
                    case 0:
                        Ticket_Data[i][j] = tickets.get(i).getUuidTicket();
                        break;
                    case 1:
                        Ticket_Data[i][j] = tickets.get(i).getTitle();
                        break;
                    case 2:
                        Ticket_Data[i][j] = tickets.get(i).getDescription();
                        break;
                    case 3:
                        Ticket_Data[i][j] = tickets.get(i).getUuidCreator();
                        break;
                    case 4:
                        Ticket_Data[i][j] = tickets.get(i).getUuidAssignee();
                        break;
                    case 5:
                        Ticket_Data[i][j] = tickets.get(i).getStatus();
                        break;
                    case 6:
                        Ticket_Data[i][j] = tickets.get(i).getDateTicketCreation();
                        break;
                    case 7:
                        Ticket_Data[i][j] = tickets.get(i).getDateExpectedResolution();
                        break;

                    default: break;
                }
            }
        }


        return inflater.inflate(R.layout.fragment_ticket_review, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);
        Spinner ticketSpinner = getActivity().findViewById(R.id.spinnerTicketReview);
        tvDescription = getActivity().findViewById(R.id.textView16);
        tvStatus = getActivity().findViewById(R.id.textView18);
        tvDateCreation = getActivity().findViewById(R.id.textView20);
        tvDateResolution = getActivity().findViewById(R.id.textView22);
        tvCreator = getActivity().findViewById(R.id.textView12);
        tvAssignee = getActivity().findViewById(R.id.textView25);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, TicketIds);
        ticketSpinner.setAdapter(adapter);
        ticketSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                try {
                    initalizeFields(position);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {

            }

        });
        initalizeFields(0);

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


                System.out.println("Response: " + response);


                if (response.isSuccessful()) {


                    String result = null;

                    if (response.body() != null) {
                        result = response.body().string();
                        JSONArray ticketArray = new JSONArray(result);
                        System.out.println("Result:" + result);

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

    public void showToast(final String Text) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getActivity(),
                        Text, Toast.LENGTH_LONG).show();
            }
        });
    }

    public void initalizeFields(int i){
        tvDescription.setText(Ticket_Data[i][2]);
        tvStatus.setText(Ticket_Data[i][5]);
        tvDateCreation.setText(Ticket_Data[i][6]);
        tvDateResolution.setText(Ticket_Data[i][7]);
        tvCreator.setText(getNameFromUuid(Ticket_Data[i][3]));
        tvAssignee.setText(getNameFromUuid(Ticket_Data[i][4]));
        System.out.println("TITLE: " + Ticket_Data[i][1]);
    }

    public String getNameFromUuid(String uuid) {
        StringBuilder sb = new StringBuilder();
        for (User user: users ) {
            if(user.getUuidUser().equals(uuid)) {
                sb.append(user.getSurname());
                sb.append(" ");
                sb.append(user.getName());
                return sb.toString();
            }
        }
        return "";
    }
}

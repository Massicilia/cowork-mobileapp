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
import android.widget.Toast;

import com.example.coworkandroid.R;
import com.example.coworkandroid.model.Ticket;
import com.example.coworkandroid.model.User;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import de.codecrafters.tableview.TableView;
import de.codecrafters.tableview.toolkit.SimpleTableDataAdapter;
import de.codecrafters.tableview.toolkit.SimpleTableHeaderAdapter;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class UserTicketReviewFragment extends Fragment {

    private static final String[] TABLE_HEADERS = { "Title", "Status", "Created on"};
    ArrayList<User> users;
    ArrayList<Ticket> tickets;
    String [][] UserData;
    String [] names;
    TableView<String[]> tableView;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        try{
            new GetUsersList().execute().get();
            new TicketStatus().execute().get();
        }catch (Exception e){

        }

        UserData = new String[users.size()][2];
        names = new String[users.size()];

        for (int i = 0; i < users.size(); i++) {
            StringBuilder sb = new StringBuilder(users.get(i).getSurname());
            sb.append(" ");
            sb.append(users.get(i).getName());
            names[i] = sb.toString();
        }

        return inflater.inflate(R.layout.fragment_user_ticket_review, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        final Spinner type = getActivity().findViewById(R.id.spinner2);
        final Spinner uuid = getActivity().findViewById(R.id.spinnerTicketReview);
        //create a list of items for the spinner.
        String[] types = new String[]{"Creator", "Assignee"};
        //create an adapter to describe how the items are displayed, adapters are used in several places in android.
        //There are multiple variations of this, but this is the basic variant.
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, types);
        ArrayAdapter<String> adapter2 = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, names);
        //set the spinners adapter to the previously created one.
        type.setAdapter(adapter);
        uuid.setAdapter(adapter2);




        tableView = getActivity().findViewById(R.id.ticketsUserReview);
        tableView.setHeaderAdapter(new SimpleTableHeaderAdapter(getContext() , TABLE_HEADERS));


        type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if (position == 0) {
                    UserData = new String[users.size()][2];
                    UserData = getTicketofCreator(uuid.getSelectedItemPosition());
                }
                else {
                    UserData = new String[users.size()][2];
                    UserData = getTicketofAssginee(uuid.getSelectedItemPosition());
                }
                tableView.setDataAdapter(new SimpleTableDataAdapter(getContext(), UserData));

            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });


        uuid.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if (type.getSelectedItemPosition() == 0) {
                    UserData = new String[users.size()][2];
                    UserData = getTicketofCreator(position);
                }else {
                    UserData = new String[users.size()][2];
                    UserData = getTicketofAssginee(position);
                }
                tableView.setDataAdapter(new SimpleTableDataAdapter(getContext(), UserData));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });




        tableView.setDataAdapter(new SimpleTableDataAdapter(getContext(), UserData));
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

                System.out.println("Response: " + response);


                if (response.isSuccessful()) {


                    String result;

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

                            System.out.println("IdUser:" + user.getUuidUser());
                            System.out.println("name:" + user.getName());
                            System.out.println("surnamee: " + user.getSurname());

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

    String[][] getTicketofCreator(int position){

        ArrayList<Ticket> filter = new ArrayList<>();
        for (Ticket ticket: tickets) {
            filter.add(ticket);
        }

        for(int i = 0; i < tickets.size(); i++) {
            if (!tickets.get(i).getUuidCreator().equals(users.get(position).getUuidUser())) filter.remove(tickets.get(i));
        }


        String [][] res = new String[filter.size()][3];
        for(int i = 0; i < filter.size(); i++){
            for(int j = 0; j < 3; j++){
                switch (j){
                    case 0:
                        res[i][j] = filter.get(i).getTitle();
                        break;
                    case 1:
                        res[i][j] = filter.get(i).getStatus();
                        break;
                    case 2:
                        res[i][j] = filter.get(i).getDateTicketCreation();
                        break;
                        default:break;
                }
            }
        }
        return res;
    }

    String[][] getTicketofAssginee(int position){

        ArrayList<Ticket> filter = new ArrayList<>();
        for (Ticket ticket: tickets) {
            filter.add(ticket);
        }


        for(int i = 0; i < tickets.size(); i++) {
            if (!tickets.get(i).getUuidAssignee().equals(users.get(position).getUuidUser())) filter.remove(tickets.get(i));
        }


        String [][] res = new String[filter.size()][3];
        for(int i = 0; i < filter.size(); i++){
            for(int j = 0; j < 3; j++){
                switch (j){
                    case 0:
                        res[i][j] = filter.get(i).getTitle();
                        break;
                    case 1:
                        res[i][j] = filter.get(i).getStatus();
                        break;
                    case 2:
                        res[i][j] = filter.get(i).getDateTicketCreation();
                        break;
                    default:break;
                }
            }
        }
        return res;
    }

}

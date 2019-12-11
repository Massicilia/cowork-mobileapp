package com.example.coworkandroid.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.coworkandroid.R;
import com.example.coworkandroid.model.Ticket;

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


public class TicketListFragment extends Fragment {

    ArrayList<Ticket> tickets;

    private static final String[] TABLE_HEADERS = { "Title", "Status", "Created on"};
    String[][] Ticket_Data;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        tickets = new ArrayList<>();

        try {
            new TicketStatus().execute().get();
        } catch (Exception e) {
            e.printStackTrace();
        }

        Ticket_Data = new String[tickets.size()][3];

        for(int i = 0; i < tickets.size(); i++){
            for(int j = 0; j < 3; j++){
                switch (j){
                    case 0:
                        Ticket_Data[i][j] = tickets.get(i).getTitle();
                        break;
                    case 1:
                        Ticket_Data[i][j] = tickets.get(i).getStatus();
                        break;
                    case 2:
                        Ticket_Data[i][j] = tickets.get(i).getDateTicketCreation();
                        break;

                    default: break;
                }
            }
        }


        return inflater.inflate(R.layout.fragment_ticket_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        TableView<String[]> tableView = getActivity().findViewById(R.id.tickets);
        tableView.setHeaderAdapter(new SimpleTableHeaderAdapter(getContext() , TABLE_HEADERS));
        tableView.setDataAdapter(new SimpleTableDataAdapter(getContext(), Ticket_Data));
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




                if (response.isSuccessful()) {


                    String result;

                    if (response.body() != null) {
                        result = response.body().string();
                        JSONArray ticketArray = new JSONArray(result);

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

}

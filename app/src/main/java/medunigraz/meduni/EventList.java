package medunigraz.meduni;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import java.util.List;



public class EventList extends Fragment {
    EventGetter EventGetter;
    EventAdapter adapter;

    public EventList() {
    }

    public static EventList newInstance() {
        EventList fragment = new EventList();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final List<String> LinkListe=new ArrayList<>();
        View view = inflater.inflate(R.layout.fragment_event_list, container, false);
        final ListView listView = (ListView) view.findViewById(R.id.EventListView);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.i("DEBUG",listView.getItemAtPosition(position).toString());
                Log.i("DEBUG",EventGetter.LinkListe.get(position).toString());
                if (!EventGetter.LinkListe.get(position).toString().isEmpty()) {
                    String url = EventGetter.LinkListe.get(position).toString();
                    if (!url.startsWith("https://") && !url.startsWith("http://")){
                        url = "http://" + url;
                    }
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                }
            }
        });

        EventGetter = new EventGetter("https://api.medunigraz.at/v1/typo3/events/",getContext());
        adapter = new EventAdapter(getActivity(),EventGetter.TitelListe,EventGetter.TeaserListe,EventGetter.DatumListe);
        listView.setAdapter(adapter);
        new GetList().execute();
        return view;
    }
    class GetList extends AsyncTask<Void, Integer, String>
    {
        protected String doInBackground(Void...arg0) {
            Log.d("DEBUG" + " DoINBackGround","On doInBackground...");
            EventGetter.FetchInfos();

            return "You are at PostExecute";
        }
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if(EventGetter.getErrorState()>0)
            {
                Toast.makeText(getContext(), "Kein Internet!",
                        Toast.LENGTH_LONG).show();
            }else {
                adapter.notifyDataSetChanged();
            }

        }
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();

    }

}

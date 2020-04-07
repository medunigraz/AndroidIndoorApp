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
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


public class EventList extends Fragment {
    EventGetter EventGetter;
    EventAdapter adapter;
    int offset = 0, lastseen = 0;
    List<String> TitelListe = new ArrayList<>();
    List<String> TeaserListe = new ArrayList<>();
    List<String> DatumListe = new ArrayList<>();
    List<String> LinkListe = new ArrayList<>();

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
        View view = inflater.inflate(R.layout.fragment_event_list, container, false);
        final ListView listView = view.findViewById(R.id.EventListView);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.i("DEBUG", listView.getItemAtPosition(position).toString());
                Log.i("DEBUG", LinkListe.get(position));
                if (!LinkListe.get(position).isEmpty()) {
                    String url = LinkListe.get(position);
                    if (!url.startsWith("https://") && !url.startsWith("http://")) {
                        url = "http://" + url;
                    }
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                }
            }
        });
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE
                        && (listView.getLastVisiblePosition() - listView.getHeaderViewsCount() -
                        listView.getFooterViewsCount()) >= (adapter.getCount() - 1)) {
                    offset = offset + 10;
                    lastseen = listView.getLastVisiblePosition();
                    EventGetter = new EventGetter("https://api.medunigraz.at/v1/typo3/events/?limit=20&offset=" + offset);
                    new GetList().execute();
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });
        EventGetter = new EventGetter("https://api.medunigraz.at/v1/typo3/events/?limit=20&offset=" + offset);
        adapter = new EventAdapter(getActivity(), TitelListe, TeaserListe, DatumListe);
        listView.setAdapter(adapter);
        new GetList().execute();
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();

    }

    class GetList extends AsyncTask<Void, Integer, String> {
        protected String doInBackground(Void... arg0) {
            EventGetter.FetchInfos();
            return "Fertig";
        }

        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (EventGetter.getErrorState() > 0) {
                Toast.makeText(getContext(), "Kein Internet!",
                        Toast.LENGTH_LONG).show();
            } else {
                TitelListe.addAll(EventGetter.getTitles());
                TeaserListe.addAll(EventGetter.getTeaser());
                DatumListe.addAll(EventGetter.getDatum());
                LinkListe.addAll(EventGetter.getLinks());
                adapter.notifyDataSetChanged();
            }

        }
    }

}

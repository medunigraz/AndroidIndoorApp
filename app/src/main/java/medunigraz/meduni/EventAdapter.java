package medunigraz.meduni;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;



public class EventAdapter extends ArrayAdapter<String> {
    List<String> Titel, Teaser,Datum;
    Activity Context;
    public EventAdapter(Activity Context,List<String> Titel, List<String> Teaser,List<String> Datum) {
        super(Context, R.layout.event_list_item, Titel);
        this.Titel = Titel;
        this.Context = Context;
        this.Teaser = Teaser;
        this.Datum = Datum;
    }
    @Override
    public View getView(int Position, View view, ViewGroup parent)
    {
        LayoutInflater inflater = Context.getLayoutInflater();
        View rowView= inflater.inflate(R.layout.event_list_item, null, true);
        TextView txtTitle = (TextView) rowView.findViewById(R.id.Titel);
        TextView txtTeaser = (TextView) rowView.findViewById(R.id.Teaser);
        TextView txtDatum = (TextView) rowView.findViewById(R.id.Datum);
        txtTitle.setText(Titel.get(Position));
        txtTeaser.setText(Teaser.get(Position));
        txtDatum.setText(Datum.get(Position));

        return rowView;
    }
}

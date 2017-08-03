package medunigraz.meduni;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;



public class NewsGetter {
    URL Link;
    final List<String> TitelListe = new ArrayList<>();
    final List<String> DatumListe=new ArrayList<>();
    final List<String> TeaserListe=new ArrayList<>();
    public NewsGetter(String url)
    {
        try {
            Link = new URL(url);
        }catch (Exception e)
        {

        }
    }
    public List<String> getTitles()
    {
        return TitelListe;

    }
    public List<String> getTeaser()
    {
        return TeaserListe;

    }
    public List<String> getDatum()
    {
        return DatumListe;

    }
    public void FetchInfos()
    {
        try {

            HttpURLConnection urlConnection;
            BufferedReader reader;
            urlConnection = (HttpURLConnection) Link.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                return;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                return;
            }
            JSONObject data = new JSONObject(buffer.toString());
            JSONArray Results = data.getJSONArray("results");
            for (int i = 0; i < Results.length(); i++) {
                JSONObject c = Results.getJSONObject(i);
                TitelListe.add(c.getString("title"));
                TeaserListe.add(c.getString("teaser"));
                DatumListe.add(c.getString("datetime").split("T")[0]);
            }
        }catch (Exception e)
        {
            Log.i("DEBUG",e.toString());
        }
    }
}

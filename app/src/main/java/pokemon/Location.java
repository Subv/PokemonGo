package pokemon;

import android.os.AsyncTask;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.orm.SugarRecord;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

class RequestLocations extends AsyncTask<String, Void, JSONArray> {
    private GoogleMap map;

    public RequestLocations(GoogleMap map) {
        this.map = map;
    }

    @Override
    protected JSONArray doInBackground(String... strings) {
        try {
            URL url = new URL(strings[0]);
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            InputStreamReader in = new InputStreamReader(connection.getInputStream());
            BufferedReader reader = new BufferedReader(in);

            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }

            return new JSONArray(sb.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(JSONArray jsonArray) {
        if (jsonArray == null || Location.count(Location.class) > 0)
            return;
        try {
            for (int i = 0; i < jsonArray.length(); ++i) {
                JSONObject object = jsonArray.getJSONObject(i);
                Location loc = new Location(object.getDouble("lt"), object.getDouble("lng"));
                loc.save();

                // Add a marker to the map
                LatLng pokeLatLng = new LatLng(loc.getLatitude(), loc.getLongitude());
                Marker marker = map.addMarker(new MarkerOptions().position(pokeLatLng).title("Pokemon"));
                marker.setTag(loc);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}

public class Location extends SugarRecord {
    private static final String LOCATION_JSON_URL = "http://190.144.171.172/function.php";

    private double latitude;
    private double longitude;

    public Location() {
    }

    public Location(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    /*
     * Grabs the Json with the Pokemon locations from the server and stores them in the database
     */
    public static void PopulateLocations(GoogleMap map, double latitude, double longitude) {
        // Only repopulate the locations if we've already used them all up
        if (Location.count(Location.class) > 0)
            return;
        String url = LOCATION_JSON_URL + "?lat=" + latitude + "&lon=" + longitude;
        new RequestLocations(map).execute(url);
    }
}

package pokemon;

import android.os.AsyncTask;

import com.orm.SugarRecord;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

class RequestTemplates extends AsyncTask<String, Void, JSONArray> {
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
        if (jsonArray == null || PokemonTemplate.count(PokemonTemplate.class) > 0)
            return;
        try {
            for (int i = 0; i < jsonArray.length(); ++i) {
                JSONObject object = jsonArray.getJSONObject(i);
                PokemonTemplate template = new PokemonTemplate(object.getInt("ev_id"),
                        object.getInt("Attack"), object.getInt("Defense"), object.getInt("HP"),
                        object.getString("Name"), 0, object.getString("ImgFront"));
                template.setId(Long.valueOf(object.getInt("Id")));
                template.save();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}

public class PokemonTemplate extends SugarRecord {
    private static final String TEMPLATES_JSON_URL = "http://190.144.171.172/proyectoMovil/pokemon.json";

    private int evolution_id;
    private int max_attack;
    private int max_defense;
    private int max_hp;
    private String name;
    private int types;
    private String img_path;

    public PokemonTemplate(int evolution_id, int max_attack, int max_defense, int max_hp, String name, int types, String img_path) {
        this.evolution_id = evolution_id;
        this.max_attack = max_attack;
        this.max_defense = max_defense;
        this.max_hp = max_hp;
        this.name = name;
        this.types = types;
        this.img_path = img_path;
    }

    public int getMaxAttack() {
        return max_attack;
    }

    public int getMaxDefense() {
        return max_defense;
    }

    public static void PopulateTemplates() {
        // Only repopulate the templates if we haven't done that yet
        if (PokemonTemplate.count(PokemonTemplate.class) > 0)
            return;

        new RequestTemplates().execute(TEMPLATES_JSON_URL);
    }
}

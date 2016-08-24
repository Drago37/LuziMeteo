package com.luziweb.luzimeteo.utils;

import com.luziweb.luzimeteo.models.MeteoCurrent;
import com.luziweb.luzimeteo.models.MeteoWeek;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Anthony on 28/07/16.
 */
public class JsonTools {

    /**
     * Méthode pour se connecter et récupéer le json pour un groupe d'id donné afin de mettre a jour les favoris.
     * Retourne une réponse.
     *
     * @param arrayId
     * @return String response
     * @throws IOException
     */
    public String getDataMeteoDayByIdForFavouritesCity(final ArrayList<Integer> arrayId) throws IOException {

        URL url = null;

        String listId = "";
        for(int i=0; i < arrayId.size(); i++){
            if(i != arrayId.size()-1) {
                listId += arrayId.get(i) + ",";
            } else {
                listId += ""+arrayId.get(i);
            }
        }

        url = new URL("http://api.openweathermap.org/data/2.5/group?id=" + listId + "&APPID=" + GlobalTools.keyAPI + "&lang=fr&units=metric");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String response = "";
        String line;

        while ((line = reader.readLine()) != null) {
            response += line;
        }
        reader.close();

        return response;
    }

    /**
     * Méthode pour modifier les infos météo de la ville.
     *
     * @param data
     */
    public void updateMeteoCurrentFavouritesCity(JSONObject data, ArrayList<MeteoCurrent> arrayListFavouritesCity) {
        try {
            //récupère le nb de ville dans la requete
            int nbCity = data.getInt("cnt");
            //on boucle pour récupérer toutes les infos météo du json array et on les insère dans une array list
            JSONArray jsonArray = data.getJSONArray("list");
            for (int i=0; i<nbCity; i++){
                int id = jsonArray.getJSONObject(i).getInt("id");
                String ville = jsonArray.getJSONObject(i).getString("name");
                double temperature = jsonArray.getJSONObject(i).getJSONObject("main").getDouble("temp");
                String temps = jsonArray.getJSONObject(i).getJSONArray("weather").getJSONObject(0).getString("description");
                String imageTemps = jsonArray.getJSONObject(i).getJSONArray("weather").getJSONObject(0).getString("icon");
                arrayListFavouritesCity.add(new MeteoCurrent(id, ville, temperature, temps, imageTemps));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Méthode pour se connecter et récupérer le json pour une ville donnée en paramètre.
     * Retourne une réponse.
     *
     * @param city
     * @return String response
     * @throws IOException
     */
    public String getDataMeteoDayByCity(final String city) throws IOException {

        URL url = null;

        url = new URL("http://api.openweathermap.org/data/2.5/weather?q=" + city + "&APPID=" + GlobalTools.keyAPI + "&lang=fr&units=metric");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String response = "";
        String line;

        while ((line = reader.readLine()) != null) {
            response += line;
        }
        reader.close();

        return response;
    }

    /**
     * Méthode pour se connecter et récupéer le json pour une ville donnée en paramètre.
     * Retourne une réponse.
     *
     * @return String response
     * @throws IOException
     */
    public String getDataMeteoDayByLocation(final double lat, final double lon) throws IOException {

        URL url = null;

        url = new URL("http://api.openweathermap.org/data/2.5/weather?lat=" + lat + "&lon=" + lon + "&APPID=" + GlobalTools.keyAPI + "&lang=fr&units=metric");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String response = "";
        String line;

        while ((line = reader.readLine()) != null) {
            response += line;
        }
        reader.close();

        return response;
    }

    /**
     * Méthode pour se connecter et récupéer les données météo sur une semaine selon geoloc.
     * Retourne une réponse.
     *
     * @param lat
     * @param lon
     * @return String response
     * @throws IOException
     */
    public String getDataMeteoWeekCity(final double lat, double lon) throws IOException {

        URL url = null;

        url = new URL("http://api.openweathermap.org/data/2.5/forecast/daily?lat="
                + lat + "&lon=" + lon + "&cnt=10&APPID=" + GlobalTools.keyAPI + "&lang=fr&units=metric");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String response = "";
        String line;

        while ((line = reader.readLine()) != null) {
            response += line;
        }
        reader.close();

        return response;
    }

    /**
     * Méthode pour modifier les infos météo de la ville.
     *
     * @param data
     */
    public MeteoCurrent updateMeteoCityDay(JSONObject data) {
        try {
            int id = data.getInt("id");
            String ville = data.getString("name");
            double temperature = data.getJSONObject("main").getDouble("temp");
            String temps = data.getJSONArray("weather").getJSONObject(0).getString("description");
            String imageTemps = data.getJSONArray("weather").getJSONObject(0).getString("icon");
            return new MeteoCurrent(id, ville, temperature, temps, imageTemps);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Récupère les informations json pour la météo selon le nombre de jour demandé en paramètre.
     * Retourne une arraylist d'objet MeteoWeek.
     * Traitement du timestamp vi GlobalTools avec getDay();
     *
     * @param data
     * @param nbJour
     * @return
     */
    public ArrayList<MeteoWeek> updateMeteoCityWeek(JSONObject data, int nbJour) {
        try {
            ArrayList<MeteoWeek> listDataMeteoWeek = new ArrayList<>();
            for (int i = 1; i <= nbJour; i++) {
                JSONObject list = data.getJSONArray("list").getJSONObject(i);
                JSONObject weather = list.getJSONArray("weather").getJSONObject(0);
                long timestamp = list.getLong("dt");
                String jour = GlobalTools.getDay(timestamp);
                String imageTemps = weather.getString("icon");
                double tempMin = list.getJSONObject("temp").getDouble("min");
                double tempMax = list.getJSONObject("temp").getDouble("max");
                MeteoWeek meteoWeek = new MeteoWeek(jour, imageTemps, tempMin, tempMax);
                listDataMeteoWeek.add(meteoWeek);
            }
            return listDataMeteoWeek;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }


}

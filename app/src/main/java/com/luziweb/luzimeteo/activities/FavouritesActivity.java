package com.luziweb.luzimeteo.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.luziweb.luzimeteo.R;
import com.luziweb.luzimeteo.adapters.FavouritesAdapter;
import com.luziweb.luzimeteo.models.MeteoCurrent;
import com.luziweb.luzimeteo.utils.GlobalTools;
import com.luziweb.luzimeteo.utils.JsonTools;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;

public class FavouritesActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "MyPrefsFile";
    private static final String PREFS_METEO = "MyMeteoFav";

    private ArrayList<MeteoCurrent> mArrayListFavouritesCity;
    private ArrayList<Integer> mArrayListIdFavourites;

    private FavouritesAdapter mAdapterFavouritesCity;
    private ListView mListViewFavouritesCity;

    private Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourites);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /**
         * Initialisation du Handler pour le thread UI
         */
        mHandler = new Handler();

        /**
         * action du bouton ajout
         */
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ajoutFavoris();
            }
        });

        /**
         * Initialisation des ArrayList des favoris
         */
        mArrayListIdFavourites = new ArrayList<>();
        mArrayListFavouritesCity = new ArrayList<>();

        /**
         * restaure les favoris via le shared preference
         */
        // Restore preferences
        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, 0);
        String favCity = preferences.getString(PREFS_METEO, "");
        Log.d("favCity------------>", "lol" + favCity);

        if (favCity.length() > 1) {
            Gson gson = new Gson();
            Type collectionType = new TypeToken<ArrayList<Integer>>() {
            }.getType();
            mArrayListIdFavourites = gson.fromJson(favCity, collectionType);
            getCurrentMeteoFavourites();
        }

        /**
         * Atrribution de la vue listview
         */
        mListViewFavouritesCity = (ListView) findViewById(R.id.list_favourites_city);

        /**
         * Initialisation et utilisation de l'adapter FavouritesAdapter
         */
        mAdapterFavouritesCity = new FavouritesAdapter(this, mArrayListFavouritesCity);
        mListViewFavouritesCity.setAdapter(mAdapterFavouritesCity);

        /**
         * Appel de méthodes
         */
        //supprimer des favoris via un click long
        supprimerFavori();
        //localiser des favoris sur google map dans une nouvelle activité via un click simple
        localiseCityOnGmap();

    }

    public void ajoutFavoris() {

        LayoutInflater layoutInflater = LayoutInflater.from(FavouritesActivity.this);
        View viewFavCity = layoutInflater.inflate(R.layout.dialog_search_fav, null);

        final EditText city = (EditText) viewFavCity.findViewById(R.id.dialog_search_city);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Recherchez une ville");
        builder.setView(viewFavCity);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                if (city.getText().toString().compareTo("") == 0) {
                    Toast.makeText(FavouritesActivity.this, "Merci de rentrer une ville", Toast.LENGTH_SHORT).show();
                } else {
                    /**
                     * Nouveau thread pour modifier l'UI
                     * Récupère les infos json de la météo du jour
                     * le handler fait le lien entre le new thread et l'ui thread
                     */
                    new Thread() {
                        public void run() {

                            final JsonTools jsonTools = new JsonTools();
                            String response_city = null;
                            try {
                                response_city = jsonTools.getDataMeteoDayByCity(city.getText().toString());
                                final JSONObject data = new JSONObject(response_city);

                                mHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        updateFavoris(jsonTools.updateMeteoCityDay(data));
                                    }
                                });

                            } catch (IOException | JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    }.start();
                }

            }
        })
                .setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    /**
     * Mise a jour des villes favorites à l'affichage de l'activité
     */
    private void getCurrentMeteoFavourites() {

        //on clear l'array list pour la recompléter à jour par la suite
        mArrayListFavouritesCity.clear();
        /**
         * Nouveau thread pour modifier l'UI
         * Récupère les infos json de la météo du jour
         * le handler fait le lien entre le new thread et l'ui thread
         */
        new Thread() {
            public void run() {

                final JsonTools jsonTools = new JsonTools();
                String responseFavCity = null;
                try {
                    responseFavCity = jsonTools.getDataMeteoDayByIdForFavouritesCity(mArrayListIdFavourites);
                    final JSONObject data = new JSONObject(responseFavCity);

                    Log.d("jsonFavCitybyId------->", responseFavCity);

                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            jsonTools.updateMeteoCurrentFavouritesCity(data, mArrayListFavouritesCity);
                            mAdapterFavouritesCity.notifyDataSetChanged();
                            Log.d("listfavCityjson----->", mArrayListFavouritesCity.toString());
                        }
                    });

                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }


            }
        }.start();
    }

    /**
     * Méthode pour récupérer les infos météos via un objet MeteoCurrent créé lors de l'appel JSon
     * Ajout dans une array list des infos meteo afin de compléter la listview
     * notification à l'adapter que l'array list a changé sinon ne prend pas compte pour l'affichage
     *
     * @param meteoCurrent
     */
    public void updateFavoris(MeteoCurrent meteoCurrent) {

        //parcours de l'arraylist pour savoir si la ville est deja existante
        Boolean verifCity = false;
        String city = meteoCurrent.getmVille();
        if (mArrayListFavouritesCity.size() > 0) {
            for (int i = 0; i < mArrayListFavouritesCity.size(); i++) {
                if (city.compareTo(mArrayListFavouritesCity.get(i).getmVille()) == 0) {
                    verifCity = true;
                    break;
                }
            }
        }
        //enregistrement de la ville et des infos si la ville n'existe pas deja dans l'arraylist
        if (!verifCity) {
            mArrayListFavouritesCity.add(meteoCurrent);
            mArrayListIdFavourites.add(meteoCurrent.getmId());
            mAdapterFavouritesCity.notifyDataSetChanged();
        } else {
            Toast.makeText(this, "Ville déjà dans vos favoris", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Méthode pour supprimer une adresse ip des favori via un long click
     */
    public void supprimerFavori() {

        mListViewFavouritesCity.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int i, long l) {
                AlertDialog.Builder builder = new AlertDialog.Builder(FavouritesActivity.this);
                builder.setTitle("Voulez-vous supprimer cette ville?")
                        .setMessage(mArrayListFavouritesCity.get(i).getmVille())
                        .setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                mArrayListFavouritesCity.remove(i);
                                mAdapterFavouritesCity.notifyDataSetChanged();
                            }
                        })
                        .setNegativeButton("Non", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
                return true;
            }

        });
    }

    /**
     * Méthode pour localiser l'ip favori sélectionnée via un click sur google map
     */
    public void localiseCityOnGmap() {
        mListViewFavouritesCity.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                final String city = mArrayListFavouritesCity.get(i).getmVille();
                new Thread() {
                    public void run() {

                        final JsonTools jsonTools = new JsonTools();
                        String response_city = null;
                        try {
                            response_city = jsonTools.getDataMeteoDayByCity(city);
                            final JSONObject data = new JSONObject(response_city);

                            final Double lat = data.getJSONObject("coord").getDouble("lat");
                            final Double lon = data.getJSONObject("coord").getDouble("lon");

                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    Intent intent = new Intent(FavouritesActivity.this, MapsActivity.class);
                                    intent.putExtra(GlobalTools.KEY_LAT, lat);
                                    intent.putExtra(GlobalTools.KEY_LON, lon);
                                    intent.putExtra(GlobalTools.KEY_VILLE, city);
                                    startActivityForResult(intent, GlobalTools.REQUEST_CODE);
                                }
                            });

                        } catch (IOException | JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }.start();
            }
        });
    }

    /**
     * Action lorsque le user quitte l'application
     */
    @Override
    protected void onStop() {
        super.onStop();
        if (!mArrayListIdFavourites.isEmpty()) {
            SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
            SharedPreferences.Editor editor = settings.edit();
            Gson gson = new Gson();

            String json = gson.toJson(mArrayListIdFavourites);
            editor.putString(PREFS_METEO, json);

            editor.apply();
        }

    }
}

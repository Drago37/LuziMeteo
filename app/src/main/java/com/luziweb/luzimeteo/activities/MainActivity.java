package com.luziweb.luzimeteo.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.luziweb.luzimeteo.R;
import com.luziweb.luzimeteo.models.MeteoCurrent;
import com.luziweb.luzimeteo.models.MeteoWeek;
import com.luziweb.luzimeteo.utils.GlobalTools;
import com.luziweb.luzimeteo.utils.JsonTools;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_ASK_PERMISSIONS = 1;
    private Handler mHandler;
    private LocationManager mLocationManager;
    private Location mCurrentLocation;
    private LocationListener mLocationListener;


    private TextView mTextViewVille;
    private TextView mTextViewTemps;
    private TextView mTextViewTemperature;
    private ImageView mImageViewIcone;

    private TextView mTextViewJ1;
    private TextView mTextViewJ2;
    private TextView mTextViewJ3;
    private TextView mTextViewJ4;
    private ArrayList<TextView> mArrayListTextViewJ = new ArrayList<>();


    private ImageView mImageView1;
    private ImageView mImageView2;
    private ImageView mImageView3;
    private ImageView mImageView4;
    private ArrayList<ImageView> mArrayListImageView = new ArrayList<>();


    private TextView mTextViewTmin1;
    private TextView mTextViewTmin2;
    private TextView mTextViewTmin3;
    private TextView mTextViewTmin4;
    private ArrayList<TextView> mArrayListTextViewTmin = new ArrayList<>();

    private TextView mTextViewTmax1;
    private TextView mTextViewTmax2;
    private TextView mTextViewTmax3;
    private TextView mTextViewTmax4;
    private ArrayList<TextView> mArrayListTextViewTmax = new ArrayList<>();

    private double mDoubleLat;
    private double mDoubleLon;
    private String mStringVille;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, FavouritesActivity.class);
                startActivityForResult(intent, GlobalTools.REQUEST_CODE);
            }
        });

        /**
         * Attributions des différentes vues
         */

        //Vues de la météo actuelle
        mTextViewVille = (TextView) findViewById(R.id.textview_ville);
        mTextViewTemps = (TextView) findViewById(R.id.textview_temps);
        mTextViewTemperature = (TextView) findViewById(R.id.textview_temperature);
        mImageViewIcone = (ImageView) findViewById(R.id.imageview_temps);

        //Vues de la météo sur 4 jour
        mTextViewJ1 = (TextView) findViewById(R.id.prev_jour1);
        mArrayListTextViewJ.add(mTextViewJ1);
        mTextViewJ2 = (TextView) findViewById(R.id.prev_jour2);
        mArrayListTextViewJ.add(mTextViewJ2);
        mTextViewJ3 = (TextView) findViewById(R.id.prev_jour3);
        mArrayListTextViewJ.add(mTextViewJ3);
        mTextViewJ4 = (TextView) findViewById(R.id.prev_jour4);
        mArrayListTextViewJ.add(mTextViewJ4);

        mImageView1 = (ImageView) findViewById(R.id.prev_img1);
        mArrayListImageView.add(mImageView1);
        mImageView2 = (ImageView) findViewById(R.id.prev_img2);
        mArrayListImageView.add(mImageView2);
        mImageView3 = (ImageView) findViewById(R.id.prev_img3);
        mArrayListImageView.add(mImageView3);
        mImageView4 = (ImageView) findViewById(R.id.prev_img4);
        mArrayListImageView.add(mImageView4);

        mTextViewTmax1 = (TextView) findViewById(R.id.prev_tempmaxi1);
        mArrayListTextViewTmax.add(mTextViewTmax1);
        mTextViewTmax2 = (TextView) findViewById(R.id.prev_tempmaxi2);
        mArrayListTextViewTmax.add(mTextViewTmax2);
        mTextViewTmax3 = (TextView) findViewById(R.id.prev_tempmaxi3);
        mArrayListTextViewTmax.add(mTextViewTmax3);
        mTextViewTmax4 = (TextView) findViewById(R.id.prev_tempmaxi4);
        mArrayListTextViewTmax.add(mTextViewTmax4);

        mTextViewTmin1 = (TextView) findViewById(R.id.prev_tempmini1);
        mArrayListTextViewTmin.add(mTextViewTmin1);
        mTextViewTmin2 = (TextView) findViewById(R.id.prev_tempmini2);
        mArrayListTextViewTmin.add(mTextViewTmin2);
        mTextViewTmin3 = (TextView) findViewById(R.id.prev_tempmini3);
        mArrayListTextViewTmin.add(mTextViewTmin3);
        mTextViewTmin4 = (TextView) findViewById(R.id.prev_tempmini4);
        mArrayListTextViewTmin.add(mTextViewTmin4);

        /**
         * Récupération de la localisation de l'appareil
         */

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // La permission n’a pas été acceptée, on appelle donc la méthode pour proposer à l'utilisateur d'accepter
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_ASK_PERMISSIONS);
        } else {
            getLocation();
        }
    }

    /**
     * Methode pour récupérer les infos json de la météo actuelle et les afficher dans un new thread
     * utilise la géolocalisation
     * utilise la class JsonTools
     */
    private void getMeteoCurrentByLocation() {
        new Thread() {
            public void run() {

                final JsonTools jsonTools = new JsonTools();
                String response = null;
                try {
                    response = jsonTools.getDataMeteoDayByLocation(mDoubleLat, mDoubleLon);
                    final JSONObject data = new JSONObject(response);

                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            //Affichage météo actuelle
                            MeteoCurrent meteo_day = jsonTools.updateMeteoCityDay(data);
                            mTextViewVille.setText(meteo_day.getmVille());
                            mTextViewTemperature.setText(Math.round(meteo_day.getmTemperature()) + " °C");
                            mTextViewTemps.setText(meteo_day.getmTemps());
                            Picasso.with(MainActivity.this).load("http://openweathermap.org/img/w/" + meteo_day.getmImageTemps() + ".png").fit().into(mImageViewIcone);
                        }
                    });

                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }

            }
        }.start();
    }

    /**
     * Methode pour récupérer les infos json et les afficher dans un new thread
     * Utilise la class JsonTools
     */
    private void getMeteoWeek() {
        new Thread() {
            public void run() {

                final JsonTools jsonTools = new JsonTools();
                String response_city = null;
                String response_week = null;
                try {
                    response_city = jsonTools.getDataMeteoDayByLocation(mDoubleLat, mDoubleLon);
                    final JSONObject data = new JSONObject(response_city);

                    mDoubleLat = data.getJSONObject("coord").getDouble("lat");
                    mDoubleLon = data.getJSONObject("coord").getDouble("lon");
                    mStringVille = data.getString("name");

                    response_week = jsonTools.getDataMeteoWeekCity(mDoubleLat, mDoubleLon);
                    final JSONObject data_week = new JSONObject(response_week);

                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            //Affichage météo à 4 jours
                            ArrayList<MeteoWeek> listMeteoWeek = jsonTools.updateMeteoCityWeek(data_week, GlobalTools.mIntNombreJourMeteoWeek);
                            for (int i = 0; i < GlobalTools.mIntNombreJourMeteoWeek; i++) {
                                mArrayListTextViewJ.get(i).setText(listMeteoWeek.get(i).getmJour());
                                mArrayListTextViewTmin.get(i).setText(Math.round(listMeteoWeek.get(i).getmTempMini()) + " °C");
                                mArrayListTextViewTmax.get(i).setText(Math.round(listMeteoWeek.get(i).getmTempMax()) + " °C");
                                Picasso.with(MainActivity.this).load("http://openweathermap.org/img/w/" + listMeteoWeek.get(i).getmImageTemps() + ".png").fit().into(mArrayListImageView.get(i));
                            }
                        }
                    });

                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }

            }
        }.start();
    }

    /**
     * Récupère la position de l'appareil via le network
     */
    private void getLocation() {
        //On récupère le service de localisation
        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        mLocationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Log.d("géolocalisation", "La position a changé.");
                mCurrentLocation = location;
                mDoubleLat = mCurrentLocation.getLatitude();
                mDoubleLon = mCurrentLocation.getLongitude();

                /**
                 * Nouveau thread pour modifier l'UI
                 * Récupère les infos json de la météo du jour
                 * le handler fait le lien entre le new thread et l'ui thread
                 */
                mHandler = new Handler();
                getMeteoCurrentByLocation();

                /**
                 * Nouveau thread pour modifier l'UI
                 * Récupère les infos json de la météo à 4jours
                 * le handler fait le lien entre le new thread et l'ui thread
                 */
                getMeteoWeek();

                //mise a jour exécutée une seule fois pour éviter les répétitions d'appel
                if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    mLocationManager.removeUpdates(mLocationListener);
                }

            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {
                Log.d("géolocalisation", "Le statut de la source a changé.");
            }

            @Override
            public void onProviderEnabled(String s) {
                Log.d("géolocalisation", "La source a été activé.");
            }

            @Override
            public void onProviderDisabled(String s) {
                //Lorsque la source (GSP ou réseau GSM) est désactivé
                Log.d("géolocalisation", "La source a été désactivé");
            }
        };

        getProvider();

    }

    /**
     * Get provider name. Choix entre GPS ou network.
     *
     * @return Name of best suiting provider.
     */
    private void getProvider() {
        Criteria criteria = new Criteria();
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setCostAllowed(false);
        criteria.setPowerRequirement(Criteria.POWER_LOW);

        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        String providerFine = mLocationManager.getBestProvider(criteria, true);

        criteria.setAccuracy(Criteria.ACCURACY_COARSE);
        String providerCoarse = mLocationManager.getBestProvider(criteria, true);

        if (providerCoarse != null) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            }
            mLocationManager.requestLocationUpdates(providerCoarse, 0, 0, mLocationListener);
        }
        if (providerFine != null) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            }
            mLocationManager.requestLocationUpdates(providerFine, 0, 0, mLocationListener);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.google_maps) {
            Intent intent = new Intent(MainActivity.this, MapsActivity.class);
            intent.putExtra(GlobalTools.KEY_LAT, mDoubleLat);
            intent.putExtra(GlobalTools.KEY_LON, mDoubleLon);
            intent.putExtra(GlobalTools.KEY_VILLE, mStringVille);
            startActivityForResult(intent, GlobalTools.REQUEST_CODE);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getLocation();
                } else {
                    // Permission Denied
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}

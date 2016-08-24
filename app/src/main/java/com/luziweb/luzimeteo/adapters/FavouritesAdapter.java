package com.luziweb.luzimeteo.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.luziweb.luzimeteo.R;
import com.luziweb.luzimeteo.models.MeteoCurrent;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Anthony on 22/08/16.
 */
public class FavouritesAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    private ArrayList<MeteoCurrent> mArrayListFavouritesCity;

    private Context mContext;

    public FavouritesAdapter(Context context, ArrayList<MeteoCurrent> list) {
        inflater = LayoutInflater.from(context);
        mContext = context;
        mArrayListFavouritesCity = list;
    }

    @Override
    public int getCount() {
        //Taille de la liste des favoris
        return mArrayListFavouritesCity.size();
    }

    @Override
    public Object getItem(int i) {
        return mArrayListFavouritesCity.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {

        ViewHolder holder;

        if (convertView == null) {
            // Créer une nouvelle vue et la sauvegarder
            // Nouvel objet avec les composants de l’item
            holder = new ViewHolder();
            // La nouvelle vue est initialisé avec le layout d’un item
            convertView = inflater.inflate(R.layout.listview_favourites_city, null);
            // Initialisation des attributs de notre objet ViewHolder
            holder.mTextViewCityName = (TextView)
                    convertView.findViewById(R.id.listview_city_name);
            holder.mTextViewTemp = (TextView)
                    convertView.findViewById(R.id.listview_city_temp);
            holder.mTextViewTemps = (TextView)
                    convertView.findViewById(R.id.listview_city_temps);
            holder.mImageViewIcon = (ImageView)
                    convertView.findViewById(R.id.listview_city_icon);
            // On lie l’objet ViewHolder à la vue pour le sauvegarder grace à setTag
            convertView.setTag(holder);
        } else {
            // Vue à recycler
            // On initialise l’objet ViewHolder grace au tag de la vue qui avait été
            // sauvegardé
            holder = (ViewHolder) convertView.getTag();
        }

        holder.mTextViewCityName.setText(mArrayListFavouritesCity.get(i).getmVille());
        holder.mTextViewTemp.setText(String.valueOf(Math.round(mArrayListFavouritesCity.get(i).getmTemperature()) + " °C"));
        holder.mTextViewTemps.setText(String.valueOf(mArrayListFavouritesCity.get(i).getmTemps()));
        Picasso.with(mContext).load("http://openweathermap.org/img/w/" + mArrayListFavouritesCity.get(i).getmImageTemps() + ".png").fit().into(holder.mImageViewIcon);

        return convertView;
    }

    public class ViewHolder {
        public TextView mTextViewCityName;
        public TextView mTextViewTemp;
        public TextView mTextViewTemps;
        public ImageView mImageViewIcon;
    }
}

package com.dungkk.gasorder.fragment;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dungkk.gasorder.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Admin on 1/6/2018.
 */

public class RecyclerViewAdapter_Product extends RecyclerView.Adapter<RecyclerViewAdapter_Product.RecyclerViewHolder> {


    private JSONArray products = new JSONArray();

    public RecyclerViewAdapter_Product(JSONArray products) {
        this.products = products;
    }

    public RecyclerViewAdapter_Product(JSONArray products, int type) throws JSONException {
        int i;
        for(i = 0; i < products.length(); i++) {
            if( products.getJSONObject(i).getInt("type") == type ) {
//                Log.d("/////////////////", products.getJSONObject(i).getInt("type")+"");
                this.products.put(products.getJSONObject(i));
            }
        }
    }


    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_list_product, parent, false);
        return new RecyclerViewAdapter_Product.RecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder holder, int position) {

        try {
            JSONObject product = products.getJSONObject(position);
            holder.name.setText(product.getString("name"));
            holder.price.setText(product.getString("price"));

            int type = product.getInt("type");
            String typeGas = "";
            if(type == 1) {
                typeGas = "Gas";
            }

            if(type == 2) {
                typeGas = "Devices";
            }

            if(type == 3) {
                typeGas = "Maintanance Service";
            }

            holder.type.setText(typeGas);
            holder.brand.setText(product.getString("brand"));

            String urlString = product.getString("img");
            DownloadImageTask task = new DownloadImageTask(holder.img);
            task.execute(urlString);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return products.length();
    }

    public class RecyclerViewHolder extends RecyclerView.ViewHolder {
        ImageView img;
        TextView name, price, type, brand;


        public RecyclerViewHolder(View itemView) {
            super(itemView);
             img = itemView.findViewById(R.id.productImg);
             name = itemView.findViewById(R.id.productName);
             price = itemView.findViewById(R.id.productPrice);
             type = itemView.findViewById(R.id.productType);
             brand = itemView.findViewById(R.id.productBrand);
        }
    }
}

class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {

    private ImageView imageView;

    public DownloadImageTask(ImageView imageView)  {
        this.imageView= imageView;
    }

    @Override
    protected Bitmap doInBackground(String... params) {
        String imageUrl = params[0];

        InputStream in = null;
        try {
            URL url = new URL(imageUrl);
            HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();

            httpConn.setAllowUserInteraction(false);
            httpConn.setInstanceFollowRedirects(true);
            httpConn.setRequestMethod("GET");
            httpConn.connect();
            int resCode = httpConn.getResponseCode();

            if (resCode == HttpURLConnection.HTTP_OK) {
                in = httpConn.getInputStream();
            } else {
                return null;
            }

            Bitmap bitmap = BitmapFactory.decodeStream(in);
            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(in);
        }
        return null;
    }


    @Override
    protected void onPostExecute(Bitmap result) {
        if(result  != null){
            this.imageView.setImageBitmap(result);
        } else{
            Log.e("MyMessage", "Failed to fetch data!");
        }
    }
}

class IOUtils {

    public static void closeQuietly(InputStream in)  {
        try {
            in.close();
        }catch (Exception e) {

        }
    }

    public static void closeQuietly(Reader reader)  {
        try {
            reader.close();
        }catch (Exception e) {

        }
    }

}


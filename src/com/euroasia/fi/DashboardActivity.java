package com.euroasia.fi;

import java.io.File;

import com.euroasiamp3.eula.GUtils;
import com.euroasiamp3.eula.SimpleEula;
import com.euroasiamp3.services.DownloadService;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class DashboardActivity extends Activity {
	//Ui
	public Button topten,search,downloads,rate,offers,share;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard);
        
        GUtils.setGTRACKER(new GooTracker(DashboardActivity.this));
    	GUtils.getGTRACKER(DashboardActivity.this).trackAppStartedEvent();
    	GUtils.getGTRACKER(DashboardActivity.this).trackPageViewEvent("DashboardActivity");

		startService(new Intent(this, DownloadService.class));
		
		//Action Bar Setup:		
		final Context thisact = this;
		
		TextView headerview = (TextView)this.findViewById(R.id.title_bar_text);
		headerview.setText("Koti");
		
		ImageView searchbutton = (ImageView)this.findViewById(R.id.action_search);
		searchbutton.setOnClickListener(new OnClickListener(){
			public void onClick(View v){
				if(!thisact.toString().contains("SearchActivity")){
					Intent searchintent = new Intent(thisact, SearchActivity.class);
					searchintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					startActivity(searchintent);
				}
			}
		});
		
		ImageView homeicon = (ImageView)this.findViewById(R.id.logo_icon);
		homeicon.setOnClickListener(new OnClickListener(){
			public void onClick(View v){
				if(!thisact.toString().contains("DashboardActivity")){
					Intent searchintent = new Intent(thisact, DashboardActivity.class);
					searchintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					startActivity(searchintent);
				}
			}
		});
        
		//Prepare Pre-Requisites
		String path=Environment.getExternalStorageDirectory()+"/music/"+getString(R.string.app_name);  
		boolean exists = (new File(path)).exists();
		if (!exists){
			new File(path).mkdirs();
		}
		
		//Button
		topten = (Button)findViewById(R.id.btn_topten);
		search = (Button)findViewById(R.id.btn_search);
		downloads = (Button)findViewById(R.id.btn_downloads);
		rate = (Button)findViewById(R.id.btn_rate);
		offers = (Button)findViewById(R.id.btn_freeapps);
		share = (Button)findViewById(R.id.btn_share);
		
		topten.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
				Intent searchintent = new Intent(thisact, Top10Activity.class);
				searchintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(searchintent);
        		
        	}
		});
		
		search.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
				Intent searchintent = new Intent(thisact, SearchActivity.class);
				searchintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(searchintent);
        	}
		});
		
		downloads.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
				Intent searchintent = new Intent(DashboardActivity.this, DownloadingActivity.class);
				searchintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(searchintent);
        	}
		});
		
		rate.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		DashboardActivity.this.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id="+getString(R.string.market_url))));
        	}
		});
		
		offers.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
				Intent searchintent = new Intent(thisact, adsTopAppsOfDay.class);
				searchintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(searchintent);
        	}
		});
		
		share.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
				Intent i = new Intent(Intent.ACTION_SEND);
				i.setType("text/plain");
				i.putExtra(Intent.EXTRA_SUBJECT, "Tsekkaa t�m� sovellus");
				i.putExtra(Intent.EXTRA_TEXT, "Mukanaan tehokas hakutoiminto, "+getString(R.string.app_name)+" tarjoaa miljoonia laadukkaita kappaleita. Voit joko nauttia niist� verkossa tai ladata niit� ilmaiseksi. Koe kuuloaistin juhla nyt! "+getString(R.string.app_name)+" Download Link: https://market.android.com/details?id="+getString(R.string.market_url));
				startActivity(Intent.createChooser(i, "Jaa sovellus"));
        	}
		});
		
		//Check For Active Internet Connection
		if(!isInternetConnectionActive(getApplicationContext())) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage("Tarvitset aktiivisen Internet-yhteyden voidaksesi k�ytt�� sovelluksen useimpia ominaisuuksia. Ole hyv� ja mene omiin asetuksiisi ja ota k�ytt��n wifi!")
				   .setCancelable(false)
				   .setPositiveButton("Asetukset", new DialogInterface.OnClickListener() {
					   public void onClick(DialogInterface dialog, int id) {
						   final Intent intent = new Intent(Intent.ACTION_MAIN, null);
                           intent.addCategory(Intent.CATEGORY_LAUNCHER);
                           final ComponentName cn = new ComponentName("com.android.settings", "com.android.settings.wifi.WifiSettings");
                           intent.setComponent(cn);
                           intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                           startActivity( intent);
        	           }
 
        	       })
        	       .setNegativeButton("Sulje", new DialogInterface.OnClickListener() {
        	           public void onClick(DialogInterface dialog, int id) {
        	                dialog.cancel();
        	           }
        	       });
        	AlertDialog alert = builder.create();
        	alert.show();
        }
		
		//Prepare Rate App
		rateapp();

		//Prepare Eula
		new SimpleEula(this).show();
		
    }
    
	public void rateapp(){		
		final String ekey = "voted";
		final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		boolean hasBeenShown = prefs.getBoolean(ekey, false);
		if(hasBeenShown == false){
			String title = "Anna meille Android Market-arvio";
			
			String message = "Arvoisat k�ytt�j�t, jos pid�tte sovelluksestamme, annattehan meille 5 t�hte�. Jatkuva tukenne on parannustemme perusta.";
 
			AlertDialog.Builder builder = new AlertDialog.Builder(this)
					.setTitle(title)
					.setMessage(message)
					.setPositiveButton("��nest�", new Dialog.OnClickListener() {
						public void onClick(DialogInterface dialogInterface, int i) {
							DashboardActivity.this.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id="+getString(R.string.market_url))));
							SharedPreferences.Editor editor = prefs.edit();
    						editor.putBoolean(ekey, true);
							editor.commit();
							dialogInterface.dismiss();
						}
					})
					.setNegativeButton("Peruuta", new Dialog.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							SharedPreferences.Editor editor = prefs.edit();
							editor.putBoolean(ekey, true);
							editor.commit();
							dialog.dismiss();
						}
					});
			builder.create().show();
		}
	}
	
	private boolean isInternetConnectionActive(Context context) {
	   	NetworkInfo networkInfo = ((ConnectivityManager) context
	   		.getSystemService(Context.CONNECTIVITY_SERVICE))
	   		.getActiveNetworkInfo();
	
	   	if(networkInfo == null || !networkInfo.isConnected()) {
	   		return false;
	   	}
		return true;
	}	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		GUtils.getGTRACKER(this).endsession();
	}
}
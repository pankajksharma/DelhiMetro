package project.android.delhimetro;

import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;

import android.view.View;
import android.widget.Button;
import android.widget.AutoCompleteTextView;
import android.widget.ArrayAdapter;

public class MainActivity extends Activity {
	AutoCompleteTextView actvSource;
	AutoCompleteTextView actvDestination;
	Button btnGo,btnExit,btnClear,btnFind;
	WebSurfer websurfer_instance;
	StationCode stationcode_instance;
	
	String from_station_id,to_station_id;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        mainActivityLoader();
    }
    
    private void onTerminate(){
    	this.finish();
    }
    
    private void mainActivityLoader(){
    	
    	websurfer_instance = new WebSurfer();
    	stationcode_instance = new StationCode();
    	actvSource = (AutoCompleteTextView)findViewById(R.id.actxtSource);
    	actvDestination = (AutoCompleteTextView)findViewById(R.id.actxtDestination);
    	btnGo = (Button)findViewById(R.id.btnGo);
    	btnClear = (Button)findViewById(R.id.btnClear);
    	btnExit = (Button)findViewById(R.id.btnExit);
    	btnFind =  (Button) findViewById(R.id.btnFindNearStatsions);
    	
    	String[] stations = getResources().getStringArray(R.array.station_array);
    	actvSource.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_dropdown_item_1line,stations));
    	actvDestination.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_dropdown_item_1line,stations));
    	
    	btnGo.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				if(stationcode_instance.codes.containsKey(actvSource.getText().toString().toUpperCase().trim()))
					from_station_id = stationcode_instance.codes.get(actvSource.getText().toString().toUpperCase().trim());
				
				if(stationcode_instance.codes.containsKey(actvDestination.getText().toString().toUpperCase().trim()))
					to_station_id = stationcode_instance.codes.get(actvDestination.getText().toString().toUpperCase().trim());
				
				websurfer_instance.makeGetRequest();
				websurfer_instance.makePostRequest(from_station_id, to_station_id);
				websurfer_instance.tokenizeResult();
				websurfer_instance.printResult();
				
				Intent result_activity_opener = new Intent();
				result_activity_opener.setClassName("project.android.delhimetro","project.android.delhimetro.ResultActivity");
				result_activity_opener.putExtra("route", websurfer_instance.route_array);
				result_activity_opener.putExtra("fare", websurfer_instance.fare);
				startActivity(result_activity_opener);
			}
		});
    	
    	btnFind.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				//Toast.makeText(getApplicationContext(), "text", Toast.LENGTH_SHORT).show();
				//startActivity(new Intent().setClassName("project.android.delhimetro", "project.android.delhimetro.NearestStation"));
				Intent result_activity_opener = new Intent();
				result_activity_opener.setClassName("project.android.delhimetro","project.android.delhimetro.NearestStation");
				//result_activity_opener.putExtra("route", websurfer_instance.route_array);
				//result_activity_opener.putExtra("fare", websurfer_instance.fare);
				startActivity(result_activity_opener);
			}
		});
    	
    	btnClear.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				actvSource.setText("");
				actvDestination.setText("");				
			}
		});
    	
    	btnExit.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				onTerminate();
			}
		});
    }
    
}
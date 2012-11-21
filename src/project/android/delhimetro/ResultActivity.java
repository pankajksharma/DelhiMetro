package project.android.delhimetro;

import android.app.Activity;
import android.os.Bundle;

import android.view.View;
import android.widget.TextView;
import android.widget.ListView; 	
import android.widget.Button;
import android.widget.ArrayAdapter;

public class ResultActivity extends Activity {
	TextView txtFare,txtPath;
	Button btnBack;
	ListView lstRoute;
	String[] route_array;
	String fare;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        route_array = getIntent().getStringArrayExtra("route");
        fare = getIntent().getStringExtra("fare");
        setContentView(R.layout.result);
        resultActivityLoader();
    }
    
    private void onTerminate(){
    	this.finish();
    }
    
    private void resultActivityLoader(){
    	txtFare = (TextView)findViewById(R.id.txtFare);
    	txtPath = (TextView)findViewById(R.id.txtPath);
    	btnBack = (Button)findViewById(R.id.btnBack);
    	lstRoute = (ListView)findViewById(R.id.lstRoute);
    	
    	txtFare.setText("Fare = Rs. "+fare);
    	txtPath.setText(route_array[0]+" to "+route_array[route_array.length-1]);
    	lstRoute.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,route_array));
    	
    	btnBack.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				onTerminate();
			}
		});
    }
    
}    
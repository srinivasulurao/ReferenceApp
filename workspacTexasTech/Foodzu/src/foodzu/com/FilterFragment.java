package foodzu.com;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.graphics.drawable.AnimationDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import foodzu.com.Utils.URLs;
import foodzu.com.Utils.Utils;
import foodzu.com.adapters.AdapterFilterCategory;
import foodzu.com.adapters.AdapterFilterItem;
import foodzu.com.models.FilterCategory;
import foodzu.com.models.FilterItem;

public class FilterFragment  extends DialogFragment {
	
	
	FilterListener mListener;
	ListView lvFilterCategory,lvFilterItem;
    ArrayList<FilterCategory> filterCategoryList;
    ArrayList<FilterItem> filterItemList;
    String categoryId;
    AdapterFilterCategory adapterCategory;
    AdapterFilterItem adapterItem;
    FilterCategory filterCategory;
    FilterItem filterItem;
    TextView tvApply,tvClearFilter;
    Integer selectPosition;
   
   
    
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.Full_Screen);
        categoryId=getArguments().getString("categoryId");
        
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog d = getDialog();
        if (d != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            d.getWindow().setLayout(width, height);
        }
    }
    
  
    
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception.
        try {

        	 mListener = (FilterListener) activity;
           // mListener = (SyncDialogListener) getTargetFragment();
            


        } catch (ClassCastException e) {

        }
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_filter, container,
                false);
        
        lvFilterCategory=(ListView)root.findViewById(R.id.lvfiltercategory);
        lvFilterItem=(ListView)root.findViewById(R.id.lvfilteritem);
        
        tvApply=(TextView)root.findViewById(R.id.tvapply);
        tvClearFilter=(TextView)root.findViewById(R.id.tvclear);
        
        filterCategoryList=new ArrayList<FilterCategory>();
        filterItemList=new ArrayList<FilterItem>();
        filterCategoryList.add(new FilterCategory("0","Brand","0"));
        filterCategoryList.add(new FilterCategory("1","Price","0"));
        lvFilterCategory.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        lvFilterCategory.setSelection(0);
        lvFilterCategory .setItemChecked(0,true);
       
        selectPosition=0;
      //  lvFilterCategory.getSelectedView().setSelected(true);
        adapterCategory=new AdapterFilterCategory(getActivity(), R.layout.adapter_filtercategory, filterCategoryList,FilterFragment.this);
        lvFilterCategory.setAdapter(adapterCategory);
      
        adapterItem=new AdapterFilterItem(getActivity(), R.layout.adapter_filteritem, filterItemList);
        lvFilterItem.setAdapter(adapterItem);
        
        if (new Utils(getActivity()).isNetworkAvailable())
			new getFilterItem().execute();
		else {
			
		}
        
        tvApply.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				 mListener.onFilterSelect();
				 FilterFragment.this.dismiss();
			}
		});
        
        tvClearFilter.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				((HomeActivity)getActivity()).getBrandSelactList().clear();
				((HomeActivity)getActivity()).getPriceSelactList().clear();
				adapterCategory.notifyDataSetChanged();
				adapterItem.notifyDataSetChanged();
				
			}
		});
        
        lvFilterCategory.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				
				
				filterItemList.clear();
			selectPosition=position;
			if (lvFilterCategory != null) {
				lvFilterCategory.setItemChecked(position, true);
			}
				 if (new Utils(getActivity()).isNetworkAvailable())
						new getFilterItem().execute();
					else {
						
					}
				
				
				
			}
        
        });
        
        lvFilterItem.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				
				filterItem=(FilterItem)parent.getItemAtPosition(position);
				
				if(filterItem.getType().equals("1")){
					
					if (((HomeActivity)getActivity()).getBrandSelactList().containsKey(
							filterItem.getId())) {

						((HomeActivity)getActivity()).getBrandSelactList().remove(
								filterItem.getId());

					} else {

						((HomeActivity)getActivity()).getBrandSelactList().put(
								filterItem.getId(), filterItem);

					}
					}else{
						
						if (((HomeActivity)getActivity()).getPriceSelactList().containsKey(
								filterItem.getId())) {

							((HomeActivity)getActivity()).getPriceSelactList().remove(
									filterItem.getId());

						} else {

							((HomeActivity)getActivity()).getPriceSelactList().put(
									filterItem.getId(), filterItem);

						}
						
					}
				
				adapterCategory.notifyDataSetChanged();
				adapterItem.notifyDataSetChanged();
				

					/*if (filterItem.getIsSelected().equals("0")) {
						;
						
					} else {
						
						
						if (((HomeActivity)context).getBrandSelactList().containsKey(filterItem.getId())) {
							
							((HomeActivity)context).getBrandSelactList().remove(filterItem.getId());
							
						}*/
						
					
				
			}
	        
        });
        
			
        
        return root;
    }
    
    public class getFilterItem extends AsyncTask<Void, Void, Void> {

		 ProgressDialog dialog;
		AnimationDrawable spinner;

		protected void onPreExecute() {
			
			 dialog = Utils.createProgressDialog(getActivity());
			dialog.setCancelable(false);
			 dialog.show();

		}

		protected Void doInBackground(Void... params) {

			
			String filterItemStr = getFilterItem(selectPosition);
			
			try {
				if (filterItemStr != null && filterItemStr.length() > 0) {

					JSONObject jObj_main = new JSONObject(filterItemStr);
					
					if(selectPosition==0){
					JSONArray result = jObj_main.getJSONArray("result");
					
					if (result.length() > 0) {
						for (int i = 0; i < result.length(); i++) {
							filterItem = new FilterItem();
							JSONObject items = result.getJSONObject(i);
							
							filterItem.setItemName(items.getString("brand_name"));
							filterItem.setId(items.getString("brand_id"));
							filterItem.setType("1");
							filterItemList.add(filterItem);
							
						}
					}
					}else{
						JSONObject items = jObj_main.getJSONObject("result");
						Integer startprice=Integer.parseInt(items.getString("startprice"));
						Integer endPrice=Integer.parseInt(items.getString("lastprice"));
						String differencestr=String.valueOf(endPrice-startprice);
						Integer difflength=differencestr.length();
						Integer firstvalue,secondValue;
						int id=1;
						Boolean validate=false;
						if(difflength==2){
							
							filterItem = new FilterItem();
							filterItem.setItemName("Below Rs."+endPrice);
							filterItem.setId(String.valueOf(id));
							filterItem.setType("2");
							filterItem.setStartValue("0");
							filterItem.setEndValue(endPrice.toString());
							filterItemList.add(filterItem);
							
							
						}else{
						
						Integer multiplenumb=Integer.parseInt(String.format("%1$-" + difflength + "s", "1").replace(' ', '0'));
						
					
						Integer firstrounded = ((startprice + multiplenumb-1) / multiplenumb ) * multiplenumb;
						filterItem = new FilterItem();
						filterItem.setItemName("Below Rs."+firstrounded);
						filterItem.setId(String.valueOf(id));
						filterItem.setType("2");
						filterItem.setStartValue("0");
						filterItem.setEndValue(firstrounded.toString());
						filterItemList.add(filterItem);
						id++;
						
						firstvalue=firstrounded;
						do{
							
						secondValue=firstvalue+multiplenumb;
						if(secondValue<endPrice){
							filterItem = new FilterItem();
							filterItem.setItemName("Rs."+firstvalue+" to "+"Rs."+(secondValue-1));
							filterItem.setId(String.valueOf(id));
							filterItem.setType("2");
							filterItem.setStartValue(firstvalue.toString());
							filterItem.setEndValue((secondValue).toString());
							filterItemList.add(filterItem);
							id++;
							firstvalue=secondValue;
							validate=true;
						}else{
							filterItem = new FilterItem();
                            filterItem.setItemName("Rs."+firstvalue+" to "+"Rs."+endPrice);
                            filterItem.setId(String.valueOf(id));
                            filterItem.setStartValue(firstvalue.toString());
							filterItem.setEndValue((endPrice).toString());
                            filterItem.setType("2");
							filterItemList.add(filterItem);
							id++;
							validate=false;
						}
						
						}while(validate);
						}
						
						//String.valueOf(i);
						
						
						
						
						
						
					}
					
				}
				
					
				}catch (Exception e) {
					// TODO: handle exception
				}
			
		
		return null;
	}

	protected void onPostExecute(Void result) {
		
		dialog.dismiss();
		
		adapterItem.notifyDataSetChanged();
	}
	
	}
    
    public String getFilterItem(Integer Position) {
		InputStream inputStream = null;
		String result = null;
		String URL;
		if(Position==0){
		URL =URLs.FILTER_BRAND;
		}else{
	    URL =URLs.FILTER_PRICE;
		}
		
		try {
			HttpClient httpclient = new DefaultHttpClient();
			HttpResponse httpResponse = httpclient.execute(new HttpGet(URL+categoryId));
			inputStream = httpResponse.getEntity().getContent();
			if (inputStream != null)
				result = convertInputStreamToString(inputStream);

		} catch (Exception e) {
			Log.d("InputStream", e.getLocalizedMessage());
		}
		return result;
	}
    
    private static String convertInputStreamToString(InputStream inputStream)
			throws IOException {
		BufferedReader bufferedReader = new BufferedReader(
				new InputStreamReader(inputStream));
		String line = "";
		String result = "";
		while ((line = bufferedReader.readLine()) != null)
			result += line;

		inputStream.close();
		return result;

	}
    
    
    

   
    public Integer getSelectPosition() {
		return selectPosition;
	}

	public void setSelectPosition(Integer selectPosition) {
		this.selectPosition = selectPosition;
	}

	public static interface FilterListener {
        void onFilterSelect();
        
    }





}

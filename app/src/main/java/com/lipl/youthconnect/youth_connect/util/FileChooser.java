package com.lipl.youthconnect.youth_connect.util;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.lipl.youthconnect.youth_connect.R;
import com.lipl.youthconnect.youth_connect.pojo.FieldDetails;

public class FileChooser extends ListActivity {
	
	private File currentDir;
	FileArrayAdapter adapter;
    private ArrayList<FieldDetails> fieldDetailsList;
    private int fieldId;
	
	/** Called when the activity is first created. */
		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);

            if(getIntent().getExtras() != null){
                fieldDetailsList = getIntent().getExtras().getParcelableArrayList(Constants.INTENT_KEY_FIELD_DETAILS);
                fieldId = getIntent().getExtras().getInt(Constants.INTENT_KEY_CURRENT_FILE_TO_BE_UPDATE);
            }

			currentDir = new File("/sdcard/");
			fill(currentDir);
	}
		
		private void fill(File f)
	    {
	        File[]dirs = f.listFiles();
	         this.setTitle("Current Dir: "+f.getName());
	         List<Option>dir = new ArrayList<Option>();
	         List<Option>fls = new ArrayList<Option>();
	         try{
	             for(File ff: dirs)
	             {
	                if(ff.isDirectory())
	                    dir.add(new Option(ff.getName(),"Folder",ff.getAbsolutePath()));
	                else
	                {
	                    fls.add(new Option(ff.getName(),"File Size: "+ff.length(),ff.getAbsolutePath()));
	                }
	             }
	         }catch(Exception e)
	         {
	             
	         }
	         Collections.sort(dir);
	         Collections.sort(fls);
	         dir.addAll(fls);
	         if(!f.getName().equalsIgnoreCase("sdcard"))
	             dir.add(0,new Option("..","Parent Directory",f.getParent()));
	         
	         adapter = new FileArrayAdapter(FileChooser.this, R.layout.file_view,dir);
			 this.setListAdapter(adapter);
	    }
		
		@Override
		protected void onListItemClick(ListView l, View v, int position, long id) {
			// TODO Auto-generated method stub
			super.onListItemClick(l, v, position, id);
			Option o = adapter.getItem(position);
			if(o.getData().equalsIgnoreCase("folder")||o.getData().equalsIgnoreCase("parent directory")){
					currentDir = new File(o.getPath());
					fill(currentDir);
			}else
			{
				onFileClick(o);
			}
		}
		
		private void onFileClick(Option o)
	    {
	    	//Toast.makeText(this, "File Clicked: "+o.getName(), Toast.LENGTH_SHORT).show();
			
			Intent returnIntent = new Intent();
			returnIntent.putExtra(Constants.INTENT_KEY_FILE_PATH, o.getPath());
            returnIntent.putExtra(Constants.INTENT_KEY_FIELD_DETAILS, fieldDetailsList);
            returnIntent.putExtra(Constants.INTENT_KEY_CURRENT_FILE_TO_BE_UPDATE, fieldId);
            setResult(RESULT_OK, returnIntent);
			finish();
	    }

    @Override
    public void onBackPressed() {
        Intent returnIntent = new Intent();
        returnIntent.putExtra(Constants.INTENT_KEY_FILE_PATH, "");
        returnIntent.putExtra(Constants.INTENT_KEY_FIELD_DETAILS, fieldDetailsList);
        returnIntent.putExtra(Constants.INTENT_KEY_CURRENT_FILE_TO_BE_UPDATE, fieldId);
        setResult(RESULT_OK, returnIntent);
        finish();
    }
}
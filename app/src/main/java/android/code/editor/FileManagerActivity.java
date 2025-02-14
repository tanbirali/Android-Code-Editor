package android.code.editor;

import android.code.editor.CodeEditorActivity;
import android.code.editor.files.utils.FileIcon;
import android.code.editor.files.utils.FileManager;
import android.code.editor.ui.MaterialColorHelper;
import android.code.editor.ui.Utils;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FileManagerActivity extends AppCompatActivity {
	private ProgressBar progressbar;
	private RecyclerView list;
	private FileList filelist;
	
	private ArrayList<String> listString = new ArrayList<>();
	private ArrayList<HashMap<String, Object>> listMap = new ArrayList<>();
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		
		// Set Layout in Activity
		setContentView(R.layout.activity_file_manager);
		
		initViews();
		
		ExecutorService loadFileList = Executors.newSingleThreadExecutor();
			
		loadFileList.execute(
			new Runnable() {
				@Override
				public void run() {
					// TODO: Implement this method
					
					runOnUiThread(
						new Runnable() {
							@Override
							public void run() {
								progressbar.setVisibility(View.VISIBLE);
							}
						}
					);
					
					// Get file path from intent and list dir in array
					FileManager.listDir(getIntent().getStringExtra("path"),listString);
					FileManager.setUpFileList(listMap,listString);
					
					runOnUiThread(
						new Runnable() {
							@Override
							public void run() {
								// Set Data in list
								progressbar.setVisibility(View.GONE);
								filelist = new FileList(listMap);
								list.setAdapter(filelist);
								list.setLayoutManager(new LinearLayoutManager(FileManagerActivity.this));
							}
						}
					);
				}
			}
		);
    }
	
	private void initViews() {
		// Setup toolbar
		Toolbar toolbar = findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeButtonEnabled(true);
        
		// Define view
		list = findViewById(R.id.list);
		progressbar = findViewById(R.id.progressbar);
	}
	
	// Adapter of Recycler View
	private class FileList extends RecyclerView.Adapter<FileList.ViewHolder> {
		
		
		ArrayList<HashMap<String, Object>> _data;
		private ImageView icon;
		private TextView path;
		private LinearLayout mainlayout;
		
		public FileList(ArrayList<HashMap<String, Object>> _arr) {
			_data = _arr;
		}
		
		@Override
		public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
			
			LayoutInflater _inflater = getLayoutInflater();
			View _v = _inflater.inflate(R.layout.filelist, null);
			RecyclerView.LayoutParams _lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
			_v.setLayoutParams(_lp);
			return new ViewHolder(_v);
		}
		
		@Override
		public void onBindViewHolder(ViewHolder _holder, final int _position) {
			View _view = _holder.itemView;
			mainlayout = _view.findViewById(R.id.layout);
			icon = _view.findViewById(R.id.icon);
			path = _view.findViewById(R.id.path);
			Utils.applyRippleEffect(mainlayout,"#ffffff","#000000");
			FileIcon.setUpIcon(FileManagerActivity.this,_data.get(_position).get("path").toString(),icon);
			path.setText(_data.get(_position).get("lastSegmentOfFilePath").toString());
			final String path = _data.get(_position).get("path").toString();
			if (new File(path).isDirectory()) {
				mainlayout.setOnClickListener(
					new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							Intent intent = new Intent();
							intent.putExtra("path",path);
							intent.setClass(FileManagerActivity.this,FileManagerActivity.class);
							startActivity(intent);
						}
					}
				);
			} else if (FileManager.ifFileFormatIsEqualTo(path,"java")) {
				mainlayout.setOnClickListener(
					new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							Intent intent = new Intent();
							intent.putExtra("path",path);
							intent.setClass(FileManagerActivity.this,CodeEditorActivity.class);
							startActivity(intent);
						}
					}
				);
			} else
			{
				mainlayout.setOnClickListener(null);
			}
		}
		
		@Override
		public int getItemCount() {
			return _data.size();
		}
		
		public class ViewHolder extends RecyclerView.ViewHolder {
			public ViewHolder(View v) {
				super(v);
			}
		}
	}
}

package com.dieptt.sleepingwell;

import java.io.File;
import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;

public class FileBrowser extends Activity implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener, AdapterView.OnItemSelectedListener
{
        private GridView mGrid;
        private File mCurrentDir;
        private ArrayList<File> mFiles;
        private String[] mFilters;
        private String[] mAudioExt;
        private String[] mImageExt;
        private String[] mArchiveExt;
        private String[] mWebExt;
        private String[] mTextExt;
        private String[] mVideoExt;
        private String[] mGeoPosExt;
        private boolean mStandAlone;
        private IconView mLastSelected;
        
        /** Called when the activity is first created. */
        @Override
        public void onCreate(Bundle savedInstanceState)
        {
                super.onCreate(savedInstanceState);
                
                mFiles = new ArrayList<File>();
                
                mAudioExt = getResources().getStringArray(R.array.fileEndingAudio);
                mImageExt = getResources().getStringArray(R.array.fileEndingImage);
                mArchiveExt = getResources().getStringArray(R.array.fileEndingPackage);
                mWebExt = getResources().getStringArray(R.array.fileEndingWebText);
                mTextExt = getResources().getStringArray(R.array.fileEndingText);
                mVideoExt = getResources().getStringArray(R.array.fileEndingVideo);
                mGeoPosExt = getResources().getStringArray(R.array.fileEndingGeoPosition);
                
                Intent intent = getIntent();
                String action = intent.getAction();
                
                if(action == null || action.compareTo(Intent.ACTION_MAIN) == 0)
                        mStandAlone = true;
                else
                        mStandAlone = false;
                
                mFilters = intent.getStringArrayExtra("FileFilter");
                
                if(intent.getData() == null) browseTo(new File("/"));
                else browseTo(new File(intent.getDataString()));
                
                Display display = getWindowManager().getDefaultDisplay();

                mGrid = new GridView(this);
                mGrid.setNumColumns(display.getWidth() / 60);
                mGrid.setOnItemClickListener(this);
                mGrid.setOnItemLongClickListener(this);
                mGrid.setOnItemSelectedListener(this);
                mGrid.setAdapter(new IconAdapter());
                
                setContentView(mGrid);
        }
        
        private synchronized void browseTo(final File location)
        {
                mCurrentDir = location;
                
                mFiles.clear();
                
                this.setTitle(mCurrentDir.getName().compareTo("") == 0 ? mCurrentDir.getPath() : mCurrentDir.getName());
                
                if(location.getParentFile() != null) mFiles.add(mCurrentDir.getParentFile());
                
                for(File file : mCurrentDir.listFiles())
                {
                        if(file.isDirectory())
                        {
                                mFiles.add(file);
                        }
                        else if(mFilters != null)
                        {
                                for(String ext : mFilters)
                                {
                                        if(file.getName().endsWith(ext))
                                        {
                                                mFiles.add(file);
                                                continue;
                                        }
                                }
                        }
                        else
                        {
                                mFiles.add(file);
                        }
                }
                
                if(mGrid != null) mGrid.setAdapter(new IconAdapter());
        }

        public class IconAdapter extends BaseAdapter
        {
                @Override
                public int getCount()
                {
                        return mFiles.size();
                }

                @Override
                public Object getItem(int index)
                {
                        return mFiles.get(index);
                }

                @Override
                public long getItemId(int index)
                {
                        return index;
                }

                @Override
                public View getView(int index, View convertView, ViewGroup parent)
                {
                        IconView icon;
                        File currentFile = mFiles.get(index);
                        
                        int iconId;
                        String filename;
                        
                        if(index == 0 && (currentFile.getParentFile() == null || currentFile.getParentFile().getAbsolutePath().compareTo(mCurrentDir.getAbsolutePath()) != 0))
                        {
                                iconId = R.drawable.updirectory;
                                filename = new String("..");
                        }
                        else
                        {
                                iconId = getIconId(index);
                                filename = currentFile.getName();
                        }

                        if(convertView == null)
                        {                   
                                icon = new IconView(FileBrowser.this, iconId, filename);
                        }
                        else
                        {
                                icon = (IconView)convertView;
                                icon.setIconResId(iconId);
                                icon.setFileName(filename);
                        }
                        
                        return icon;
                }
                
                private int getIconId(int index)
                {
                        File file = mFiles.get(index);
                        
                        if(file.isDirectory()) return R.drawable.directory;
                        
                        for(String ext : mAudioExt)
                        {
                                if(file.getName().endsWith(ext)) return R.drawable.audio;
                        }
                        
                        for(String ext : mArchiveExt)
                        {
                                if(file.getName().endsWith(ext)) return R.drawable.archive;
                        }
                        
                        for(String ext : mImageExt)
                        {
                                if(file.getName().endsWith(ext)) return R.drawable.image;
                        }
                        
                        for(String ext : mWebExt)
                        {
                                if(file.getName().endsWith(ext)) return R.drawable.webdoc;
                        }
                        
                        for(String ext : mTextExt)
                        {
                                if(file.getName().endsWith(ext)) return R.drawable.text;
                        }
                        
                        for(String ext : mVideoExt)
                        {
                                if(file.getName().endsWith(ext)) return R.drawable.video;
                        }
                        
                        for(String ext : mGeoPosExt)
                        {
                                if(file.getName().endsWith(ext)) return R.drawable.geoposition;
                        }
                        
                        return R.drawable.unknown;
                }
        }

        @Override
        public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long id)
        {
                File file = mFiles.get((int)id);
                
                if(file.isDirectory())
                {
                        browseTo(file);
                }
                else
                {
                        if(!mStandAlone)
                        {
                                // Send back the file that was selected
                                Uri path = Uri.fromFile(file);
                                Intent intent = new Intent(Intent.ACTION_PICK, path);
                                setResult(RESULT_OK, intent);                   
                                finish();
                        }
                        else
                        {
                                // Try to open it
                                Intent intent = new Intent(Intent.ACTION_VIEW);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.setDataAndType(Uri.fromFile(file), getMimeType(file));
                                startActivity(Intent.createChooser(intent, null));
                        }
                }
        }

        @Override
        public boolean onItemLongClick(AdapterView<?> parentView, View view, int arg2, long id)
        {
                final File file = mFiles.get((int)id);
                final File parent = file.getParentFile();
                
                new AlertDialog.Builder(FileBrowser.this)
        .setIcon(android.R.drawable.ic_menu_agenda)
        .setItems(R.array.file_options, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                switch(whichButton)
                {
//              case 0: // Rename
//                      file.renameTo(new File(parent, "Bubba.txt"));
//                      browseTo(parent);
//                      break;
                case 0: // Delete
                        file.delete();
                        browseTo(parent);
                        break;
//              case 2: // Cut
//                      break;
//              case 3: // Copy
//                      break;
                case 1: // Send To...
                        Intent intent = new Intent(Intent.ACTION_SEND);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.setType(getMimeType(file));
                        intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
                        startActivity(Intent.createChooser(intent, null));
                        break;
                }
                dialog.dismiss();
            }
        })
       .create().show();
                
                return true;
        }
        
        private String getMimeType(File file)
        {
                for(String ext : mAudioExt)
                {
                        if(file.getName().endsWith(ext)) return "audio/*";
                }
                
                for(String ext : mArchiveExt)
                {
                        if(file.getName().endsWith(ext)) return "archive/*";
                }
                
                for(String ext : mImageExt)
                {
                        if(file.getName().endsWith(ext)) return "image/*";
                }
                
                for(String ext : mWebExt)
                {
                        if(file.getName().endsWith(ext)) return "text/html";
                }
                
                for(String ext : mTextExt)
                {
                        if(file.getName().endsWith(ext)) return "text/plain";
                }
                
                for(String ext : mVideoExt)
                {
                        if(file.getName().endsWith(ext)) return "video/*";
                }
                
                for(String ext : mGeoPosExt)
                {
                        if(file.getName().endsWith(ext)) return "audio/*";
                }
                
                return "";
        }

        @Override
        public void onItemSelected(AdapterView<?> grid, View icon, int arg2, long index)
        {
                if(mLastSelected != null)
                {
                        mLastSelected.deselect();
                }
                
                if(icon != null)
                {
                        mLastSelected = (IconView)icon;
                        mLastSelected.select();
                }
                
        }

        @Override
        public void onNothingSelected(AdapterView<?> grid)
        {
                if(mLastSelected != null)
                {
                        mLastSelected.deselect();
                        mLastSelected = null;
                }
        }
}
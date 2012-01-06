package com.stylingandroid.backuprestore;

import android.app.Activity;
import android.app.backup.BackupManager;
import android.app.backup.RestoreObserver;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

public class BackupRestoreActivity extends Activity implements
		OnSharedPreferenceChangeListener
{
	public static final String TAG = "BackupRestoreActivity";

	public static final String PREFS = "prefs";

	public static final String KEY = "key";

	private EditText edit;
	private BackupManager backupMgr = null;

	private SharedPreferences sharedPrefs;

	@Override
	public void onCreate( Bundle savedInstanceState )
	{
		Log.d( TAG, "onCreate" );
		super.onCreate( savedInstanceState );
		setContentView( R.layout.main );

		sharedPrefs = getSharedPreferences( BackupRestoreActivity.PREFS,
				MODE_PRIVATE );

		edit = (EditText) findViewById( R.id.editText );
		edit.addTextChangedListener( new TextWatcher()
		{
			private int start = 0;
			private int end = 0;

			@Override
			public void onTextChanged( CharSequence s, int start, int before,
					int count )
			{
				SharedPreferences.Editor editor = sharedPrefs.edit();
				editor.putString( BackupRestoreActivity.KEY, s.toString() );
				editor.commit();
			}

			@Override
			public void beforeTextChanged( CharSequence s, int start,
					int count, int after )
			{
				this.start = after;
				this.end = after;
			}

			@Override
			public void afterTextChanged( Editable s )
			{
				edit.setSelection( start, end );
			}
		} );

		backupMgr = new BackupManager( getApplicationContext() );
	}

	@Override
	protected void onResume()
	{
		Log.d( TAG, "onResume" );
		sharedPrefs.registerOnSharedPreferenceChangeListener( this );
		String val = sharedPrefs.getString( KEY, null );
		if ( val != null )
		{
			edit.setText( val );
		}
		super.onResume();
	}

	@Override
	protected void onPause()
	{
		Log.d( TAG, "onPause" );
		sharedPrefs.unregisterOnSharedPreferenceChangeListener( this );
		super.onPause();
	}

	public void backup( View v )
	{
		Log.d( TAG, "backup" );
		backupMgr.dataChanged();
	}

	public void restore( View v )
	{
		Log.d( TAG, "restore" );
		backupMgr.requestRestore( new RestoreObserver()
		{
			@Override
			public void restoreStarting( int numPackages )
			{
				Log.d( TAG, "restoreStarting: " + numPackages );
				super.restoreStarting( numPackages );
			}

			@Override
			public void restoreFinished( int error )
			{
				Log.d( TAG, "restoreFinished: " + error );
				super.restoreFinished( error );
			}

			@Override
			public void onUpdate( int nowBeingRestored, String currentPackage )
			{
				Log.d( TAG, "onUpdate: " + currentPackage );
				super.onUpdate( nowBeingRestored, currentPackage );
			}
		} );
	}

	@Override
	public void onSharedPreferenceChanged( SharedPreferences sharedPreferences,
			String key )
	{
		if ( key.equals( KEY ) )
		{
			String newVal = sharedPreferences.getString( key, null );
			if ( newVal != null )
			{
				edit.setText( newVal );
			}
		}
	}
}
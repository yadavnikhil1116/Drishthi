package com.example.drishthi;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

public class ContactsActivity extends AppCompatActivity {

    private ArrayList<String> contactArrayList;
    private TextView infoTitle;
    private ListView listView;
    private static final int PERMISSION_CODE = 100;

    @SuppressLint({"Range", "MissingInflatedId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        contactArrayList = new ArrayList<>();
        infoTitle = findViewById(R.id.titleTextView);
        listView = findViewById(R.id.listView);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            fetchContacts();
        }else {
            requestPermissions();
        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(ContactsActivity.this,CallActivity.class);
                intent.putExtra("name",contactArrayList.get(position));
                startActivity(intent);
                finish();
            }
        });
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(this,new String[] {android.Manifest.permission.READ_CONTACTS}, PERMISSION_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == PERMISSION_CODE){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                fetchContacts();
                Toast.makeText(this, "Permissions Accepted...", Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(this, "Permissions Denied...", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @SuppressLint("Range")
    private void fetchContacts() {
        ContentResolver contentResolver = getContentResolver();
        String[] projection = {ContactsContract.Contacts.DISPLAY_NAME};
        Cursor cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI,projection,null,null,ContactsContract.Contacts.DISPLAY_NAME);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                contactArrayList.add(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)));
            }
            cursor.close();
            ArrayAdapter adapter = new ArrayAdapter<String>(ContactsActivity.this,android.R.layout.simple_list_item_1,contactArrayList);
            listView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }
    }
}
package com.pcquispe.appbotonayuda.fragmentos;
import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.pcquispe.appbotonayuda.R;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class ContactosFragment extends Fragment {
    ListView listView;
    TextView textView;
    private static final int PERMISSION_REQUEST_READ_CONTACTS = 100;
    Cursor c;
    ArrayList<String> contacts;
    ArrayAdapter<String> adapter;
    public ContactosFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_contactos, container, false);
        listView = (ListView) view.findViewById(R.id.lista_item_contactos);
        permisosReadContacts();
        return view;
    }
    //permisos para leer los contactos
    public void permisosReadContacts() {
        int permissionCheck = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_CONTACTS);
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            showContacts(getActivity());

        } else {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_CONTACTS}, PERMISSION_REQUEST_READ_CONTACTS);
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, contacts);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(Intent.ACTION_CALL);
               // Cursor  itemValue    =  (Cursor)  listView.getItemAtPosition(i);
                String listChoice = (String) listView.getItemAtPosition(i);
                if (listChoice.trim().isEmpty()) {
                    intent.setData(Uri.parse("tel:"+ listChoice));
                }
                else {
                    Toast.makeText(getActivity(), "Llamando a " + listChoice, Toast.LENGTH_SHORT).show();
                    intent.setData(Uri.parse("tel:"+listChoice));
                }
                if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getContext(), "Please conceda permisos para llamar", Toast.LENGTH_LONG).show();
                    requestPermission();
                } else {
                    startActivity(intent);
                }
            }
        });
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_READ_CONTACTS) {
            // If request is cancelled, the result arrays are empty.
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showContacts(getActivity());
            } else {
                Toast.makeText(getActivity(), "No tiene permisos", Toast.LENGTH_LONG).show();
            }
        }
    }
    //mostrar los contactos
    public void showContacts(Activity act) {
        c = act.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, ContactsContract.Contacts.DISPLAY_NAME + " ASC ");
        contacts = new ArrayList<String>();
        while (c.moveToNext()) {
            String contacName = c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            String phoneNumber = c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            contacts.add(contacName+ " : "+phoneNumber);
        }
        c.close();
    }
    //metodo para llamar a mis contactos
    public void llamarSerenoCastilla() {
        Intent i = new Intent(Intent.ACTION_CALL);
        String npolicia = "073 347080";
        if (npolicia.trim().isEmpty()) {
            i.setData(Uri.parse("tel:073 347080"));
        } else {
            i.setData(Uri.parse("tel:" + npolicia));
        }
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getContext(), "Please conceda permisos para llamar", Toast.LENGTH_LONG).show();
            requestPermission();
        } else {
            startActivity(i);
        }
    }
    //permisos para llamadas
    private void requestPermission() {
        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CALL_PHONE}, 1);
    }
}

package com.pcquispe.appbotonayuda;
import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import com.pcquispe.appbotonayuda.fragmentos.HomeFragment;
import com.pcquispe.appbotonayuda.fragmentos.MapsUbicameFragment;
import com.pcquispe.appbotonayuda.fragmentos.NumeroSOSFragment;
import com.pcquispe.appbotonayuda.fragmentos.ProgramaMensajeFragment;
import com.pcquispe.appbotonayuda.util.DataBaseHelper;
public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    //variables para loading
    ProgressDialog progressDoalog, progreso;
    Vibrator vibrator;
    //variable para el metodo enviar mensaje
    private static final int MY_PERMISSION_REQUEST_SEND_SMS = 0;
    //variables db sqlite
    DataBaseHelper myDB;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        //varaiable to vibrate
        vibrator = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Presione por 2 segundos para enviar un sms SOS\n o llamar de emergencia", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        //boton para enviar mensaje SOS
        fab.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                enviarMensajeSOS(); //llamo a mi metodo del mensaje SOS
                crear_dialogo().show();
                return false;
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        //activar gps

    }

    private Dialog crear_dialogo() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setCancelable(false);
        builder.setMessage("¿Desea Llamar al 105?");
        builder.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getApplication(), "Llamando al 105", Toast.LENGTH_LONG).show();
                llamdadeEmergencia();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                //dialog.cancel();
            }
        });
        return builder.create();
    }
    //metodo para iniciar el servicios y enviar mensaje SOS
    public void enviarMensajeSOS() {
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS);
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            miMensaje();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, MY_PERMISSION_REQUEST_SEND_SMS);
        }
    }
    //mensaje de ayuda
    public void miMensaje() {
        String num = "934416164";
        String sms = "Necesisto ayuda porfavor llámame urgente!";
        if (num.equals("") || sms.equals("")) {
            Toast.makeText(getApplication(), "No tienes mensaje programado", Toast.LENGTH_LONG).show();
        } else {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(num, null, sms, null, null);
            Toast.makeText(getApplication(), "Enviaste un sms SOS al Nro: " + num, Toast.LENGTH_LONG).show();
            vibrator.vibrate(1000);
        }
    }
    //permisos para enviar mensajes
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSION_REQUEST_SEND_SMS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    miMensaje();
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(getApplication(), "No tiene permisos", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    //metoso para realizar llamadas
    public void llamdadeEmergencia() {
        Intent i = new Intent(Intent.ACTION_CALL);
        String npolicia = "105";
        if (npolicia.trim().isEmpty()) {
            i.setData(Uri.parse("tel:105"));
        } else {
            i.setData(Uri.parse("tel:" + npolicia));
        }
        if (ActivityCompat.checkSelfPermission(getApplication(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getApplication(), "Please conceda permisos para llamar", Toast.LENGTH_LONG).show();
            requestPermission();
        } else {
            startActivity(i);
        }
    }
    public void llamarBomberos() {
        Intent i = new Intent(Intent.ACTION_CALL);
        String nbomberos = "119";
        if (nbomberos.trim().isEmpty()) {
            i.setData(Uri.parse("tel:119"));
        } else {
            i.setData(Uri.parse("tel:" + nbomberos));
        }
        if (ActivityCompat.checkSelfPermission(getApplication(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getApplication(), "Please conceda permisos para llamar", Toast.LENGTH_LONG).show();
            requestPermission();
        } else {
            startActivity(i);
        }
    }

    public void llamarSerenoCastilla() {
        Intent i = new Intent(Intent.ACTION_CALL);
        String ncastilla = "073 347080";
        if (ncastilla.trim().isEmpty()) {
            i.setData(Uri.parse("tel:073 347080"));
        } else {
            i.setData(Uri.parse("tel:" + ncastilla));
        }
        if (ActivityCompat.checkSelfPermission(getApplication(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getApplication(), "Please conceda permisos para llamar", Toast.LENGTH_LONG).show();
            requestPermission();
        } else {
            startActivity(i);
        }
    }

    public void llamarSerenoPiura() {
        Intent i = new Intent(Intent.ACTION_CALL);
        String npiura = "(073) 602323";
        if (npiura.trim().isEmpty()) {
            i.setData(Uri.parse("tel:(073) 602323"));
        } else {
            i.setData(Uri.parse("tel:" + npiura));
        }
        if (ActivityCompat.checkSelfPermission(getApplication(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getApplication(), "Please conceda permisos para llamar", Toast.LENGTH_LONG).show();
            requestPermission();
        } else {
            startActivity(i);
        }
    }

    //permisos para llamadas
    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, 1);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        //MenuInflater inflater = getMenuInflater();
        //inflater.inflate(R.menu.menu_search, menu);
        //MenuItem item = menu.findItem(R.id.menuSearch);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_policia) {
            llamdadeEmergencia();
            return true;
        }
        if (id == R.id.action_bomberos) {
            llamarBomberos();
            return true;
        }
        if (id == R.id.id_sereno_piura) {
            llamarSerenoPiura();
            return true;
        }
        if (id == R.id.id_sereno_castilla) {
            llamarSerenoCastilla();
            return true;
        } if (id == R.id.id_action_salir) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.nav_home) {
            //Intent intent = new Intent(MainActivity.this , MainActivity.class);
            //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            //startActivity(intent);
            HomeFragment homeFragment = new HomeFragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.contenedorFragment, homeFragment);
            transaction.addToBackStack(null).commit();
            setTitle("BotonAyuda");
        }
        if (id == R.id.nav_ubicame) {
            MapsUbicameFragment mapsUbicameFragment = new MapsUbicameFragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.contenedorFragment, mapsUbicameFragment);
            transaction.addToBackStack(null).commit();
            setTitle("Ubicame");

        }
        else if (id == R.id.nav_mensajes) {
            ProgramaMensajeFragment mensajeFragment = new ProgramaMensajeFragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.contenedorFragment, mensajeFragment);
            transaction.addToBackStack(null).commit();
            setTitle("Programa mensaje SOS");

        }
        else if (id == R.id.nav_numero_sos) {
            NumeroSOSFragment numeroSOSFragment = new NumeroSOSFragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.contenedorFragment, numeroSOSFragment);
            transaction.addToBackStack(null).commit();
            setTitle("Números SOS");

        } else if (id == R.id.nav_settings) {
            Intent intent=new Intent(getApplication(),SettingsActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_share) {
            compartirRedes();

        } else if (id == R.id.nav_send) {
            Intent intent=new Intent(getApplication(),MapsUbicameActivity.class);
            startActivity(intent);
        }else if (id == R.id.nav_exit) {
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    public void compartirRedes(){
        //compartir por gmail
        Intent intent=new Intent(Intent.ACTION_SEND);
        intent.setData(Uri.parse("email"));
        String[]s={"example@gmail.com"};
        intent.putExtra(Intent.EXTRA_EMAIL,s);
        intent.putExtra(Intent.EXTRA_SUBJECT,"Download");
        intent.putExtra(Intent.EXTRA_TEXT,"download this app in https://www.facebook.com/kquisperojas");
        intent.setType("message/ffc822");
        Intent chooser=Intent.createChooser(intent,"Compartir por email");
        startActivity(chooser);
        //compartir solor ws
        Intent intent1 = new Intent(Intent.ACTION_SEND);
        intent1.setType("text/plain");
        intent1.putExtra(Intent.EXTRA_TEXT,"download this app in https://www.facebook.com/kquisperojas");
        intent1.setPackage("com.whatsapp");
        Intent chooser1=Intent.createChooser(intent1,"Compartir por whatsApp");
        startActivity(chooser1);
    }
    Handler handle = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            progressDoalog.incrementProgressBy(1);
        }
    };
}

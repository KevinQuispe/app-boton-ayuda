package com.pcquispe.appbotonayuda.fragmentos;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.pcquispe.appbotonayuda.R;
import com.pcquispe.appbotonayuda.util.DataBaseHelper;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProgramaMensajeFragment extends Fragment  implements View.OnClickListener{

    EditText nombre,numero;
    CheckBox estado;
    Button btnguardar;
    //DECLARE MY DB
    DataBaseHelper myDB;

    //declare
    public ProgramaMensajeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_programa_mensaje, container, false);
        nombre=(EditText) view.findViewById(R.id.et_nombre);
        numero=(EditText) view.findViewById(R.id.et_numero);
        estado=(CheckBox) view.findViewById(R.id.checkBox);
        btnguardar=(Button) view.findViewById(R.id.btn_guardar);
        //instance mydb
        myDB=new DataBaseHelper(getContext());
        btnguardar.setOnClickListener(this);
        return  view;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.btn_guardar:
                guardarDatos();

        }
    }
    public void guardarDatos(){
        String nom,num;

        nom=nombre.getText().toString();
        num=numero.getText().toString();
        int estado = 1;
        Boolean result=myDB.insertData(nom,num,estado);
        if (TextUtils.isEmpty(nombre.getText())){
            nombre.setError("Ingrese Nombre");
        }
        else if(result==true){
            Toast.makeText(getActivity(),"Datos Insertados correctamnte",Toast.LENGTH_LONG).show();
            limapiarCampos();
        }
        else{
            Toast.makeText(getActivity(),"Error al insertar datos",Toast.LENGTH_LONG).show();
        }
    }
    public void limapiarCampos(){
        nombre.setText("");
        numero.setText("");


    }
}

package com.androidavanzado.minitwitter.userinterface.tweets;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProviders;

import com.androidavanzado.minitwitter.R;
import com.androidavanzado.minitwitter.common.Constantes;
import com.androidavanzado.minitwitter.common.SharedPreferencesManager;
import com.androidavanzado.minitwitter.data.TweetViewModel;
import com.bumptech.glide.Glide;

public class NuevoTweetDialogFragment extends DialogFragment implements View.OnClickListener {

    ImageView ivClose, ivAvatar;
    Button btnTwittear;
    EditText etMensaje;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Aplicación de estilo
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialogStyle);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        // El inflater sirve para cargar un archivo xml como interface
        View view = inflater.inflate(R.layout.nuevo_tweet_full_dialog, container, false);

        ivClose = view.findViewById(R.id.imageViewClose);
        ivAvatar = view.findViewById(R.id.imageViewAvatar);
        btnTwittear = view.findViewById(R.id.buttonTwittear);
        etMensaje = view.findViewById(R.id.editTextMensaje);

        // Eventos
        btnTwittear.setOnClickListener(this);
        ivClose.setOnClickListener(this);

        // Seteamos la imagen del usuario de perfil
        String photoUrl = SharedPreferencesManager.getSomeStringValue(Constantes.PREF_PHOTOURL);
        if(!photoUrl.isEmpty()){
            Glide.with(getActivity())
                    .load(Constantes.API_MINITWITTER_FILES_URL + photoUrl)
                    .into(ivAvatar);
        }

        return view;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        String mensaje = etMensaje.getText().toString();

        if(id == R.id.buttonTwittear){
            if(mensaje.isEmpty()){
                Toast.makeText(getActivity(), "Debe escribir un mensaje", Toast.LENGTH_SHORT).show();
            }else{
                TweetViewModel tweetViewModel = ViewModelProviders.of(getActivity()).get(TweetViewModel.class);
                tweetViewModel.insertTweet(mensaje);
                getDialog().dismiss();
            }
        }else if(id == R.id.imageViewClose){
            if(!mensaje.isEmpty()){
                showDialogConfirm();
            }else{
                getDialog().dismiss();
            }
        }
    }

    // Método que configura el dialogo
    private void showDialogConfirm() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setMessage("¿Desea realmente eliminar el tweet? El mensaje se borrará")
                .setTitle("Cancelar tweet");

        builder.setPositiveButton("Eliminar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
                getDialog().dismiss();
            }
        });
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();

        dialog.show();
    }
}

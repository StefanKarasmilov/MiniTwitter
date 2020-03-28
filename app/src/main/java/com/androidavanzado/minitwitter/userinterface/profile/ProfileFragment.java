package com.androidavanzado.minitwitter.userinterface.profile;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.Manifest;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.androidavanzado.minitwitter.R;
import com.androidavanzado.minitwitter.common.Constantes;
import com.androidavanzado.minitwitter.data.ProfileViewModel;
import com.androidavanzado.minitwitter.retrofit.request.RequestUserProfile;
import com.androidavanzado.minitwitter.retrofit.response.ResponseUserProfile;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.listener.single.CompositePermissionListener;
import com.karumi.dexter.listener.single.DialogOnDeniedPermissionListener;
import com.karumi.dexter.listener.single.PermissionListener;

public class ProfileFragment extends Fragment {

    private ProfileViewModel profileViewModel;

    ImageView ivAvatar;
    EditText etUsername, etEmail, etPassword, etWebsite, etDescription;
    Button btnSave, btnChangePassword;
    boolean loadingData = false;
    PermissionListener allPermissionsListener;

    public static ProfileFragment newInstance() {
        return new ProfileFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        profileViewModel = ViewModelProviders.of(getActivity()).get(ProfileViewModel.class);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v =  inflater.inflate(R.layout.profile_fragment, container, false);

        ivAvatar = v.findViewById(R.id.imageViewAvatar);
        etUsername = v.findViewById(R.id.editTextUsername);
        etEmail = v.findViewById(R.id.editTextEmail);
        etPassword = v.findViewById(R.id.editTextCurrentPassword);
        etWebsite = v.findViewById(R.id.editTextWebsite);
        etDescription = v.findViewById(R.id.editTextDescription);
        btnSave = v.findViewById(R.id.buttonSave);
        btnChangePassword = v.findViewById(R.id.buttonChangePassword);
        
        // Eventos
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = etUsername.getText().toString();
                String email = etEmail.getText().toString();
                String descripcion = etDescription.getText().toString();
                String website = etWebsite.getText().toString();
                String password = etPassword.getText().toString();

                if(username.isEmpty()){
                    etUsername.setError("El nombre de usuario es requerido");
                }else if(email.isEmpty()){
                    etEmail.setError("El email es requerido");
                }else if(password.isEmpty()){
                    etPassword.setError("La contraseña es requerida");
                }else {
                    RequestUserProfile requestUserProfile = new RequestUserProfile(username, email, descripcion, website, password);
                    profileViewModel.updateProfile(requestUserProfile);
                    Toast.makeText(getActivity(), "Enviando datos al servidor", Toast.LENGTH_SHORT).show();
                    btnSave.setEnabled(false);
                }
            }
        });

        // Método para cambiar la foto de perfil
        ivAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Invocamos a la selección de la fotografía
                // Invocamos el método de comprobación de permisos
                checkPermissions();
            }
        });

        btnChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        // ViewModel
        profileViewModel.userProfile.observe(getActivity(), new Observer<ResponseUserProfile>() {
            @Override
            public void onChanged(ResponseUserProfile responseUserProfile) {
                loadingData = false;
                etUsername.setText(responseUserProfile.getUsername());
                etEmail.setText(responseUserProfile.getEmail());
                etWebsite.setText(responseUserProfile.getWebsite());
                etDescription.setText(responseUserProfile.getDescripcion());

                if(!responseUserProfile.getPhotoUrl().isEmpty()){
                    Glide.with(getActivity())
                            .load(Constantes.API_MINITWITTER_FILES_URL + responseUserProfile.getPhotoUrl())
                            .dontAnimate()
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .skipMemoryCache(true)
                            .into(ivAvatar);
                }

                if(!loadingData){
                    btnSave.setEnabled(true);
                    Toast.makeText(getActivity(), "Datos guardados correctamente", Toast.LENGTH_SHORT).show();
                }
            }
        });

        profileViewModel.photoProfile.observe(getActivity(), new Observer<String>() {
            @Override
            public void onChanged(String photo) {
                if(!photo.isEmpty()) {
                    Glide.with(getActivity())
                            .load(Constantes.API_MINITWITTER_FILES_URL + photo)
                            .dontAnimate()
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .skipMemoryCache(true)
                            .into(ivAvatar);
                }
            }
        });

        return v;
    }

    private void checkPermissions() {
        PermissionListener dialogOnDeniedPermissionListener =
                DialogOnDeniedPermissionListener.Builder.withContext(getActivity())
                .withTitle("Permisos")
                .withMessage("Los permisos solicitados son necesarios para poder seleccionar una foto de perfil")
                .withButtonText("Aceptar")
                .withIcon(R.mipmap.ic_launcher)
                .build();

        allPermissionsListener = new CompositePermissionListener(
                (PermissionListener) getActivity(),
                dialogOnDeniedPermissionListener
        );

        // Inicia la petición de permisos para acceder a la galeria de fotos
        Dexter.withActivity(getActivity())
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(allPermissionsListener)
                .check();
    }


}

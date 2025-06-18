package com.pucp.limatraf;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.pucp.limatraf.auth.LoginActivity;
import com.pucp.limatraf.databinding.ActivityMainBinding;
import com.pucp.limatraf.fragments.LimaPassFragment;
import com.pucp.limatraf.fragments.Linea1Fragment;
import com.pucp.limatraf.fragments.ResumenFragment;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        auth = FirebaseAuth.getInstance();

        // Fragmento inicial
        if (savedInstanceState == null) {
            cargarFragmentoInicial();
        }

        configurarBottomNavigation();
    }

    private void cargarFragmentoInicial() {
        reemplazarFragmento(new Linea1Fragment());
    }

    private void configurarBottomNavigation() {
        binding.bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.menu_linea1) {
                reemplazarFragmento(new Linea1Fragment());
                return true;
            } else if (id == R.id.menu_limapass) {
                reemplazarFragmento(new LimaPassFragment());
                return true;
            } else if (id == R.id.menu_resumen) {
                reemplazarFragmento(new ResumenFragment());
                return true;
            } else if (id == R.id.menu_logout) {
                mostrarDialogoCerrarSesion();
                return true;
            }

            return false;
        });
    }

    private void reemplazarFragmento(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }

    private void mostrarDialogoCerrarSesion() {
        new AlertDialog.Builder(this)
                .setTitle("Cerrar sesión")
                .setMessage("¿Estás seguro de que deseas salir?")
                .setPositiveButton("Sí", (dialog, which) -> cerrarSesion())
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void cerrarSesion() {
        auth.signOut(); // Solo Firebase Auth (sin Facebook)
        redirigirALogin();
    }

    private void redirigirALogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Redirige si no hay usuario autenticado
        if (auth.getCurrentUser() == null) {
            redirigirALogin();
        }
    }
}
package com.pucp.limatraf.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.pucp.limatraf.R;

public class RegistroActivity extends AppCompatActivity {

    private TextInputEditText etNombres, etApellidos, etCorreo, etNuevaContrasena, etConfirmarContrasena;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        // Inicializar Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Referencias a los elementos de la UI
        etNombres = findViewById(R.id.etNombres);
        etApellidos = findViewById(R.id.etApellidos);
        etCorreo = findViewById(R.id.etCorreo);
        etNuevaContrasena = findViewById(R.id.etNuevaContrasena);
        etConfirmarContrasena = findViewById(R.id.etConfirmarContrasena);
        MaterialButton btnFinalizar = findViewById(R.id.btnFinalizarRegistro);
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        btnFinalizar.setOnClickListener(v -> {
            String nombres = etNombres.getText().toString().trim();
            String apellidos = etApellidos.getText().toString().trim();
            String correo = etCorreo.getText().toString().trim();
            String contrasena = etNuevaContrasena.getText().toString().trim();
            String confirmar = etConfirmarContrasena.getText().toString().trim();

            if (!validarCampos(nombres, apellidos, correo, contrasena, confirmar)) return;

            registrarUsuarioFirebase(nombres, apellidos, correo, contrasena);
        });
    }

    private boolean validarCampos(String nombres, String apellidos, String correo, String contrasena, String confirmar) {
        if (TextUtils.isEmpty(nombres) || TextUtils.isEmpty(apellidos) ||
                TextUtils.isEmpty(correo) || TextUtils.isEmpty(contrasena) || TextUtils.isEmpty(confirmar)) {
            Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!contrasena.equals(confirmar)) {
            Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (contrasena.length() < 8 ||
                !contrasena.matches(".*[A-Z].*") ||
                !contrasena.matches(".*[a-z].*") ||
                !contrasena.matches(".*\\d.*")) {
            Toast.makeText(this, "La contraseña no cumple con los requisitos de seguridad", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    private void registrarUsuarioFirebase(String nombres, String apellidos, String correo, String contrasena) {
        mAuth.createUserWithEmailAndPassword(correo, contrasena)
                .addOnSuccessListener(authResult -> {
                    FirebaseUser user = mAuth.getCurrentUser();
                    if (user != null) {
                        Toast.makeText(this, "Registro exitoso", Toast.LENGTH_SHORT).show();
                        mAuth.signOut();
                        irAVistaLogin();
                    }
                })
                .addOnFailureListener(e -> {
                    if (e.getMessage() != null && e.getMessage().contains("email address is already in use")) {
                        Toast.makeText(this, "El correo ya está registrado", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(this, "Error en el registro: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void irAVistaLogin() {
        Intent intent = new Intent(RegistroActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}

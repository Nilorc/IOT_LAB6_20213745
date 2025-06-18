package com.pucp.limatraf.fragments;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.pucp.limatraf.R;
import com.pucp.limatraf.adapters.Linea1Adapter;
import com.pucp.limatraf.model.MovimientoLinea1;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class Linea1Fragment extends Fragment {

    private EditText etIdTarjeta, etFecha, etEntrada, etSalida, etTiempo;
    private EditText etFechaDesde, etFechaHasta;
    private Button btnGuardar, btnFiltrar;
    private RecyclerView rvMovimientos;
    private Linea1Adapter adapter;
    private FirebaseFirestore db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_linea1, container, false);

        db = FirebaseFirestore.getInstance();

        // Inputs y botones
        etIdTarjeta = view.findViewById(R.id.etIdTarjeta);
        etFecha = view.findViewById(R.id.etFecha);
        etEntrada = view.findViewById(R.id.etEntrada);
        etSalida = view.findViewById(R.id.etSalida);
        etTiempo = view.findViewById(R.id.etTiempo);
        etFechaDesde = view.findViewById(R.id.etFechaDesde);
        etFechaHasta = view.findViewById(R.id.etFechaHasta);
        btnGuardar = view.findViewById(R.id.btnGuardar);
        btnFiltrar = view.findViewById(R.id.btnFiltrar);
        rvMovimientos = view.findViewById(R.id.rvMovimientos);

        configurarSelectorFecha(etFecha);
        configurarSelectorFecha(etFechaDesde);
        configurarSelectorFecha(etFechaHasta);
        configurarRecyclerView();

        btnGuardar.setOnClickListener(v -> guardarMovimiento());
        btnFiltrar.setOnClickListener(v -> filtrarMovimientos());

        return view;
    }

    private void configurarSelectorFecha(EditText campo) {
        campo.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            DatePickerDialog dialog = new DatePickerDialog(getContext(),
                    (view, year, month, dayOfMonth) -> {
                        String fecha = String.format(Locale.getDefault(), "%04d-%02d-%02d", year, month + 1, dayOfMonth);
                        campo.setText(fecha);
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH));
            dialog.show();
        });
    }

    private void configurarRecyclerView() {
        rvMovimientos.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new Linea1Adapter(new ArrayList<>(), new Linea1Adapter.OnItemClickListener() {
            @Override
            public void onEditar(MovimientoLinea1 mov) {
                etIdTarjeta.setText(mov.getIdTarjeta());
                etFecha.setText(mov.getFecha());
                etEntrada.setText(mov.getEstacionEntrada());
                etSalida.setText(mov.getEstacionSalida());
                etTiempo.setText(mov.getTiempoViaje());
                btnGuardar.setTag(mov);
            }

            @Override
            public void onEliminar(MovimientoLinea1 mov) {
                new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                        .setTitle("Confirmar eliminación")
                        .setMessage("¿Deseas eliminar este movimiento?")
                        .setPositiveButton("Sí", (dialog, which) -> {
                            db.collection("movimientos-linea1")
                                    .document(mov.getId())
                                    .delete()
                                    .addOnSuccessListener(unused -> configurarRecyclerView());
                        })
                        .setNegativeButton("No", null)
                        .show();
            }
        });
        rvMovimientos.setAdapter(adapter);

        // Carga inicial
        db.collection("movimientos-linea1")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<MovimientoLinea1> lista = new ArrayList<>();
                    for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                        MovimientoLinea1 m = doc.toObject(MovimientoLinea1.class);
                        m.setId(doc.getId());
                        lista.add(m);
                    }
                    adapter.actualizarLista(lista);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Error al cargar: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void filtrarMovimientos() {
        String desde = etFechaDesde.getText().toString();
        String hasta = etFechaHasta.getText().toString();

        if (desde.isEmpty() || hasta.isEmpty()) {
            Toast.makeText(getContext(), "Completa ambas fechas", Toast.LENGTH_SHORT).show();
            return;
        }

        db.collection("movimientos-linea1")
                .whereGreaterThanOrEqualTo("fecha", desde)
                .whereLessThanOrEqualTo("fecha", hasta)
                .get()
                .addOnSuccessListener(snapshot -> {
                    List<MovimientoLinea1> filtrados = new ArrayList<>();
                    for (DocumentSnapshot doc : snapshot) {
                        MovimientoLinea1 m = doc.toObject(MovimientoLinea1.class);
                        m.setId(doc.getId());
                        filtrados.add(m);
                    }
                    adapter.actualizarLista(filtrados);
                });
    }

    private void guardarMovimiento() {
        String idTarjeta = etIdTarjeta.getText().toString().trim();
        String fecha = etFecha.getText().toString().trim();
        String entrada = etEntrada.getText().toString().trim();
        String salida = etSalida.getText().toString().trim();
        String tiempo = etTiempo.getText().toString().trim();

        if (idTarjeta.isEmpty() || fecha.isEmpty() || entrada.isEmpty() || salida.isEmpty() || tiempo.isEmpty()) {
            Toast.makeText(getContext(), "Completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        MovimientoLinea1 movEditando = (MovimientoLinea1) btnGuardar.getTag();

        Map<String, Object> movimiento = new HashMap<>();
        movimiento.put("idTarjeta", idTarjeta);
        movimiento.put("fecha", fecha);
        movimiento.put("estacionEntrada", entrada);
        movimiento.put("estacionSalida", salida);
        movimiento.put("tiempoViaje", tiempo);

        if (movEditando != null && movEditando.getId() != null) {
            db.collection("movimientos-linea1")
                    .document(movEditando.getId())
                    .update(movimiento)
                    .addOnSuccessListener(unused -> {
                        Toast.makeText(getContext(), "Movimiento actualizado", Toast.LENGTH_SHORT).show();
                        limpiarCampos();
                        btnGuardar.setTag(null);
                        configurarRecyclerView();
                    })
                    .addOnFailureListener(e -> Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        } else {
            db.collection("movimientos-linea1")
                    .add(movimiento)
                    .addOnSuccessListener(ref -> {
                        Toast.makeText(getContext(), "Movimiento guardado", Toast.LENGTH_SHORT).show();
                        limpiarCampos();
                        configurarRecyclerView();
                    })
                    .addOnFailureListener(e -> Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        }
    }

    private void limpiarCampos() {
        etIdTarjeta.setText("");
        etFecha.setText("");
        etEntrada.setText("");
        etSalida.setText("");
        etTiempo.setText("");
    }
}

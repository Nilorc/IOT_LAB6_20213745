package com.pucp.limatraf.fragments;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.*;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.pucp.limatraf.R;

import java.text.SimpleDateFormat;
import java.util.*;

public class ResumenFragment extends Fragment {

    private EditText etDesde, etHasta;
    private Button btnAplicarFiltro;
    private BarChart barChart;
    private PieChart pieChart;

    private final SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    private FirebaseFirestore db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_resumen, container, false);

        // Referencias de vista
        etDesde = view.findViewById(R.id.etDesde);
        etHasta = view.findViewById(R.id.etHasta);
        btnAplicarFiltro = view.findViewById(R.id.btnAplicarFiltro);
        barChart = view.findViewById(R.id.barChart);
        pieChart = view.findViewById(R.id.pieChart);

        db = FirebaseFirestore.getInstance();

        configurarPickers();
        btnAplicarFiltro.setOnClickListener(v -> aplicarFiltro());

        return view;
    }

    // Configurador de fechas
    private void configurarPickers() {
        View.OnClickListener listener = v -> {
            Calendar calendar = Calendar.getInstance();
            DatePickerDialog dialog = new DatePickerDialog(requireContext(),
                    (view, year, month, dayOfMonth) -> {
                        String fecha = String.format(Locale.getDefault(), "%04d-%02d-%02d", year, month + 1, dayOfMonth);
                        ((EditText) v).setText(fecha);
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH));
            dialog.show();
        };
        etDesde.setOnClickListener(listener);
        etHasta.setOnClickListener(listener);
    }

    // Al hacer clic en Aplicar Filtro
    private void aplicarFiltro() {
        String desdeStr = etDesde.getText().toString().trim();
        String hastaStr = etHasta.getText().toString().trim();

        if (desdeStr.isEmpty() || hastaStr.isEmpty()) return;

        try {
            Date desde = formato.parse(desdeStr);
            Date hasta = formato.parse(hastaStr);

            Map<String, Integer> conteoLinea1 = new TreeMap<>();
            Map<String, Integer> conteoLimaPass = new TreeMap<>();

            db.collection("movimientos-linea1")
                    .get()
                    .addOnSuccessListener(snapshot1 -> {
                        for (QueryDocumentSnapshot doc : snapshot1) {
                            String fechaStr = doc.getString("fecha");
                            try {
                                Date fecha = formato.parse(fechaStr);
                                if (fecha != null && !fecha.before(desde) && !fecha.after(hasta)) {
                                    String mes = fechaStr.substring(0, 7);
                                    conteoLinea1.put(mes, conteoLinea1.getOrDefault(mes, 0) + 1);
                                }
                            } catch (Exception ignored) {}
                        }

                        db.collection("movimientos-limapass")
                                .get()
                                .addOnSuccessListener(snapshot2 -> {
                                    for (QueryDocumentSnapshot doc : snapshot2) {
                                        String fechaStr = doc.getString("fecha");
                                        try {
                                            Date fecha = formato.parse(fechaStr);
                                            if (fecha != null && !fecha.before(desde) && !fecha.after(hasta)) {
                                                String mes = fechaStr.substring(0, 7);
                                                conteoLimaPass.put(mes, conteoLimaPass.getOrDefault(mes, 0) + 1);
                                            }
                                        } catch (Exception ignored) {}
                                    }

                                    mostrarGraficos(conteoLinea1, conteoLimaPass);
                                });
                    });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Mostrar gráficos de barra y torta
    private void mostrarGraficos(Map<String, Integer> linea1, Map<String, Integer> limapass) {
        // Barras
        List<BarEntry> entriesLinea1 = new ArrayList<>();
        List<BarEntry> entriesLimaPass = new ArrayList<>();
        List<String> labels = new ArrayList<>();

        Set<String> todosLosMeses = new TreeSet<>();
        todosLosMeses.addAll(linea1.keySet());
        todosLosMeses.addAll(limapass.keySet());

        int i = 0;
        for (String mes : todosLosMeses) {
            labels.add(mes);
            entriesLinea1.add(new BarEntry(i, linea1.getOrDefault(mes, 0)));
            entriesLimaPass.add(new BarEntry(i, limapass.getOrDefault(mes, 0)));
            i++;
        }

        BarDataSet set1 = new BarDataSet(entriesLinea1, "Línea 1");
        BarDataSet set2 = new BarDataSet(entriesLimaPass, "Lima Pass");
        set1.setColors(ColorTemplate.COLORFUL_COLORS);
        set2.setColors(ColorTemplate.MATERIAL_COLORS);

        BarData data = new BarData(set1, set2);
        data.setBarWidth(0.4f);

        barChart.setData(data);
        barChart.getDescription().setEnabled(false);
        barChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        barChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(labels));
        barChart.getXAxis().setGranularity(1f);
        barChart.getXAxis().setGranularityEnabled(true);
        barChart.groupBars(0f, 0.2f, 0.02f);
        barChart.invalidate();

        // Torta
        int totalLinea1 = linea1.values().stream().mapToInt(Integer::intValue).sum();
        int totalLimaPass = limapass.values().stream().mapToInt(Integer::intValue).sum();

        List<PieEntry> pieEntries = new ArrayList<>();
        if (totalLinea1 > 0) pieEntries.add(new PieEntry(totalLinea1, "Línea 1"));
        if (totalLimaPass > 0) pieEntries.add(new PieEntry(totalLimaPass, "Lima Pass"));

        PieDataSet pieDataSet = new PieDataSet(pieEntries, "");
        pieDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        pieChart.setData(new PieData(pieDataSet));
        pieChart.setUsePercentValues(true);
        pieChart.getDescription().setEnabled(false);
        pieChart.invalidate();
    }

}

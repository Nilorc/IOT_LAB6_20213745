package com.pucp.limatraf.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pucp.limatraf.R;
import com.pucp.limatraf.model.MovimientoLimaPass;

import java.util.List;

public class LimaPassAdapter extends RecyclerView.Adapter<LimaPassAdapter.MovimientoViewHolder> {

    private List<MovimientoLimaPass> lista;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onEditar(MovimientoLimaPass movimiento);
        void onEliminar(MovimientoLimaPass movimiento);
    }

    public LimaPassAdapter(List<MovimientoLimaPass> lista, OnItemClickListener listener) {
        this.lista = lista;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MovimientoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_movimiento, parent, false);
        return new MovimientoViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MovimientoViewHolder holder, int position) {
        MovimientoLimaPass mov = lista.get(position);
        holder.tvFecha.setText("Fecha: " + mov.getFecha());
        holder.tvEstaciones.setText(mov.getParaderoEntrada() + " → " + mov.getParaderoSalida());
        holder.tvTiempo.setText("Tiempo: " + mov.getTiempoViaje());

        holder.btnEditar.setOnClickListener(v -> listener.onEditar(mov));
        holder.btnEliminar.setOnClickListener(v -> listener.onEliminar(mov));
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    public static class MovimientoViewHolder extends RecyclerView.ViewHolder {
        TextView tvFecha, tvEstaciones, tvTiempo;
        ImageButton btnEditar, btnEliminar;

        public MovimientoViewHolder(@NonNull View itemView) {
            super(itemView);
            tvFecha = itemView.findViewById(R.id.tvFecha);
            tvEstaciones = itemView.findViewById(R.id.tvEstaciones);
            tvTiempo = itemView.findViewById(R.id.tvTiempo);
            btnEditar = itemView.findViewById(R.id.btnEditar);
            btnEliminar = itemView.findViewById(R.id.btnEliminar);
        }
    }

    public void actualizarLista(List<MovimientoLimaPass> nuevaLista) {
        this.lista = nuevaLista;
        notifyDataSetChanged();
    }
}

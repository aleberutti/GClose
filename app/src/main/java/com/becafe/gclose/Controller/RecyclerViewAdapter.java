package com.becafe.gclose.Controller;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.becafe.gclose.Model.Usuario;
import com.becafe.gclose.R;

import org.w3c.dom.Text;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    public static class ViewHolder extends  RecyclerView.ViewHolder{
        private TextView cantante, nacionalidad;
        ImageView imgCantante;

        public ViewHolder(View itemView) {
            super(itemView);
            cantante = itemView.findViewById(R.id.tvCantante);
            nacionalidad = itemView.findViewById(R.id.tvNacionalidad);
            imgCantante = itemView.findViewById(R.id.imageCantante);
        }
    }

    public List<Usuario> usuarioLista;

    public RecyclerViewAdapter(List<Usuario> usuarioLista) {
        this.usuarioLista = usuarioLista;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.get_close_card,parent,false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.cantante.setText(usuarioLista.get(position).getApellido());
        holder.nacionalidad.setText(usuarioLista.get(position).getNombre());
        holder.imgCantante.setImageResource(R.drawable.gclose_logo);
    }

    @Override
    public int getItemCount() {
        return usuarioLista.size();
    }
}
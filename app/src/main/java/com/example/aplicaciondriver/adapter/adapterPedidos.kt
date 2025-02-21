package com.example.aplicaciondriver.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.aplicaciondriver.dataclass.dataclassPedidos
import com.geinzDriver.aplicaciondriver.R
import com.geinzDriver.aplicaciondriver.databinding.DesingAdapterPedidosBinding
import com.geinzDriver.aplicaciondriver.pedidos_info

class adapterPedidos(private var listaPedidos: MutableList<dataclassPedidos>) :
    RecyclerView.Adapter<adapterPedidos.viewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolder {
        val binding =
            DesingAdapterPedidosBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return viewHolder(binding)
    }

    override fun getItemCount(): Int {
        return listaPedidos.size
    }

    override fun onBindViewHolder(holder: viewHolder, position: Int) {
        val item = listaPedidos[position]
        holder.render(item)
    }

    inner class viewHolder(val binding: DesingAdapterPedidosBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val listener = binding.listener
        val estado = binding.estadoPedido
        val n_pedido = binding.pedidioN
        val nombteTienda = binding.tiendaNombre
        val direccion = binding.direccionTienda
        val estadoPedido = binding.estado
        fun render(item: dataclassPedidos) {
            estadoPedido.text = item.estado
            n_pedido.text = item.N_pedido
            direccion.text = item.direccion
            nombteTienda.text = item.NombreTienda

            val drawableRes = when (item.estado.toString()) {
                "listo" -> R.drawable.esfera_estado_listo_recojo
                "faltante" -> R.drawable.esfera_estado_pedido_proceso
                "entregado" -> R.drawable.esfera_estado_entregado
                else -> R.drawable.esfera_estado_pedido_proceso
            }

            // Encuentra el View y establece el fondo
            val estadoPedidoView = itemView.findViewById<View>(R.id.estadoPedido)
            estadoPedidoView.setBackgroundResource(drawableRes)



            listener.setOnClickListener {
                val vista = Intent(itemView.context, pedidos_info::class.java).apply {
                    putExtra("idPedido", item.idPedido)
                    putExtra("idDriver", item.idDriver)
                    putExtra("idUser", item.iduser)
                    putExtra("idTienda", item.idTienda)
                    putExtra("tipo", item.tipo)
                    putExtra("idDocument", item.documentIDs)
                }

                itemView.context.startActivity(vista)
            }
        }

    }
}
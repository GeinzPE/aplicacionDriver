package com.geinzDriver.aplicaciondriver.fragmentos_main

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.aplicaciondriver.adapter.adapterPedidos
import com.example.aplicaciondriver.dataclass.dataclassPedidos
import com.geinzDriver.aplicaciondriver.R
import com.geinzDriver.aplicaciondriver.databinding.FragmentPedidosFrBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.core.Context
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot

class pedidos_fr : Fragment() {
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var binding: FragmentPedidosFrBinding
    private val lista = mutableListOf<dataclassPedidos>()
    private lateinit var mcontex: android.content.Context
    private lateinit var handler: Handler
    private lateinit var runnable: Runnable

    override fun onAttach(context: android.content.Context) {
        mcontex = context
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPedidosFrBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        firebaseAuth = FirebaseAuth.getInstance()
        obtenerPedidosFirestore(firebaseAuth.uid.toString())
        // Inicializar Handler y Runnable
        handler = Handler()
        runnable = object : Runnable {
            override fun run() {
                obtenerPedidosFirestore(firebaseAuth.uid.toString())
                handler.postDelayed(this, 5000)
            }
        }

        // Iniciar la tarea repetitiva
        handler.post(runnable)
    }

    private fun obtenerPedidosFirestore(idDriver: String) {
        val db = FirebaseFirestore.getInstance().collection("Trabajadores_Usuarios_Drivers")
            .document("drivers").collection("drivers").document(idDriver).collection("pendiente")
        db.get().addOnSuccessListener { res ->
            lista.clear()
            for (datos in res) {
                val data = datos.data
                val estado = data?.get("estado") as? String ?: ""
                val idDriver = data?.get("idDriver") as? String ?: ""
                val idPedido = data?.get("idPedido") as? String ?: ""
                val idTienda = data.get("idTienda") as? String ?: ""
                val idUSer = data?.get("idUSer") as? String ?: ""
                val tipo = data?.get("tipo") as? String ?: ""
                val nombreTienda = data?.get("nombreTienda") as? String ?: ""
                val fecha = data?.get("fecha") as? String ?: ""
                val direccion = data?.get("direccion") as? String ?: ""
                val documentoID = data?.get("idDocumento") as? String ?: ""
                val listaDataclass = dataclassPedidos(
                    N_pedido = idPedido,
                    NombreTienda = nombreTienda,
                    fechaR = fecha,
                    direccion = direccion,
                    estado = estado,
                    idPedido = idPedido,
                    idTienda = idTienda,
                    iduser = idUSer,
                    idDriver = idDriver,
                    tipo = tipo,
                    documentIDs=documentoID
                )
                lista.add(listaDataclass)
            }
            inicializarReclicle(lista)
        }
    }

    private fun inicializarReclicle(lista: MutableList<dataclassPedidos>) {
        val recicle = binding.reciclePedidos
        recicle.layoutManager = LinearLayoutManager(mcontex, LinearLayoutManager.VERTICAL, false)
        recicle.adapter = adapterPedidos(lista)
    }

}
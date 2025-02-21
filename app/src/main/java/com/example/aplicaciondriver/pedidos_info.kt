package com.geinzDriver.aplicaciondriver

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.geinzDriver.aplicaciondriver.databinding.ActivityPedidosInfoBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import java.util.HashMap

class pedidos_info : AppCompatActivity() {
    private lateinit var binding: ActivityPedidosInfoBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPedidosInfoBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        obtnerDatosPedido()

    }

    private fun cambiarEstadoPEdido(idDocument: String, idDriver: String) {
        val db = FirebaseFirestore.getInstance()
            .collection("Trabajadores_Usuarios_Drivers")
            .document("drivers")
            .collection("drivers")
            .document(idDriver)
            .collection("pendiente")
            .document(idDocument)

        val db2 = FirebaseFirestore.getInstance()
            .collection("Trabajadores_Usuarios_Drivers")
            .document("drivers")
            .collection("drivers")
            .document(idDriver)
            .collection("entregado")

        db.get().addOnSuccessListener { res ->
            if (res.exists()) {
                val data = res.data ?: return@addOnSuccessListener
                val idDriver = data["idDriver"] as? String ?: ""
                val idPedido = data["idPedido"] as? String ?: ""
                val idTienda = data["idTienda"] as? String ?: ""
                val idUser = data["idUSer"] as? String ?: ""
                val precioDriver = binding.precioDriver.text.toString()
                val idDocumento = res.id

                val hashmapEntregado = hashMapOf<String, Any>(
                    "idDriver" to idDriver,
                    "idPedido" to idPedido,
                    "idTienda" to idTienda,
                    "idUser" to idUser,
                    "precioDriver" to precioDriver,
                    "idDocumento" to idDocumento
                )

                db2.add(hashmapEntregado).addOnSuccessListener {
                    Log.d("cambiado", "El pedido fue cambiado correctamente")
                    // Eliminar el documento de "pendiente"
                    db.delete().addOnSuccessListener {
                        Log.d("eliminado", "Documento de pendiente eliminado correctamente")
                    }.addOnFailureListener { e ->
                        Log.e("eliminado", "Error al eliminar el documento de pendiente: ${e.message}")
                    }
                }.addOnFailureListener { e ->
                    Log.e("cambiado", "El pedido no se pudo cambiar: ${e.message}")
                }
            }
        }.addOnFailureListener { e ->
            Log.e("error", "Error al actualizar el estado: ${e.message}")
        }
    }

    private fun obtnerDatosPedido() {
        var idPedido = intent.getStringExtra("idPedido").toString()
        val idTienda = intent.getStringExtra("idTienda").toString()
        val idUser = intent.getStringExtra("idUser").toString()
        val idDriver = intent.getStringExtra("idDriver").toString()
        val tipo = intent.getStringExtra("tipo").toString()
        val documentoID = intent.getStringExtra("idDocument").toString()

        Log.d("obtneemos_datos", "$idPedido $idTienda $idUser $idDriver $tipo")

        val RealTime = FirebaseDatabase.getInstance().getReference("CompraTienda").child(idTienda)
            .child(idUser).child(tipo).child(idPedido)
        RealTime.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val idPedido = snapshot.child("idPedido").getValue(String::class.java)
                    val metodoPago = snapshot.child("metodoPago").getValue(String::class.java)
                    val horaLlegada = snapshot.child("hora_llegada").getValue(String::class.java)
                    val direccion = snapshot.child("direccion").getValue(String::class.java)
                    val comentarioAdicionl =
                        snapshot.child("comentario_adicional").getValue(String::class.java)

                    val nombre = snapshot.child("nombre").getValue(String::class.java)
                    val apellido = snapshot.child("apellido").getValue(String::class.java)
                    val numero = snapshot.child("numero").getValue(String::class.java)

                    val totalDriver = snapshot.child("totalDriver").getValue(String::class.java)
                    val totalCancelar = snapshot.child("totalCancelar").getValue(String::class.java)

                    val totalProductos =
                        snapshot.child("totalProductos").getValue(String::class.java)?.toDouble()
                    val Total_item_selecionado =
                        snapshot.child("Total_item_selecionado").getValue(String::class.java)
                            ?.toDouble()

                    binding.idPedido.text = idPedido ?: "No disponible"
                    binding.modoPago.text = metodoPago ?: "No disponible"
                    binding.horaEntrega.text = horaLlegada ?: "No disponible"
                    binding.direccion.text = direccion ?: "No disponible"
                    binding.comentarioAdicional.text = comentarioAdicionl ?: "No disponible"
                    binding.tipo.text = tipo

                    obtnerDatosTienda(idTienda) { nombreTienda, numeroTienda, direccionEntrega, tipoTienda ->
                        binding.nombreTienda.text = nombreTienda
                        binding.numeroTienda.text = numeroTienda
                        binding.direccionTienda.text = direccionEntrega
                        binding.tipoTienda.text = tipoTienda
                    }


                    binding.NombreUSer.text = nombre
                    binding.apellidoUSer.text = apellido
                    binding.numeroUSer.text = numero
                    binding.precioDriver.text = totalDriver
                    binding.TotalCancelar.text = totalCancelar

                    if (tipo.equals("reserva")) {
                        val totalSeteo = totalProductos!! * Total_item_selecionado!!
                        binding.totalProductos.text = totalSeteo.toString()

                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("error", "error al obtener los datos $error")
            }

        })
        binding.entregado.setOnClickListener {
            cambiarEstadoPEdido(documentoID, idDriver)
        }
    }

    private fun obtnerDatosTienda(
        idTienda: String,
        datos: (String, String, String, String) -> Unit
    ) {
        val db = FirebaseFirestore.getInstance().collection("Tiendas").document(idTienda)
        db.get().addOnSuccessListener { res ->
            if (res.exists()) {
                val data = res.data
                val nombreTienda = data?.get("nombre") as? String ?: ""
                val numeroTienda = data?.get("numero") as? String ?: ""
                val direccionEntrega = data?.get("ubicacion") as? String ?: ""
                val tipoTienda = data?.get("tipoTienda") as? String ?: ""
                datos(nombreTienda, numeroTienda, direccionEntrega, tipoTienda)
            }

        }.addOnFailureListener { e ->
            datos("", "", "", "")
        }
    }
}
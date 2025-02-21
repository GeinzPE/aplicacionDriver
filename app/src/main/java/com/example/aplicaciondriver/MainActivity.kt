package com.geinzDriver.aplicaciondriver

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.geinzDriver.aplicaciondriver.fragmentos_main.cuenta_fr
import com.geinzDriver.aplicaciondriver.fragmentos_main.pedidos_fr
import com.geinzDriver.aplicaciondriver.fragmentos_main.review_fr
import com.geinzDriver.aplicaciondriver.fragmentos_main.soporte_fr
import com.geinzDriver.aplicaciondriver.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        pedidos()
        binding.bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.pedidos -> {
                    pedidos()
                    true
                }

                R.id.soporte -> {
                    soporte()
                    true
                }

                R.id.review -> {
                    review()
                    true
                }

                R.id.perfil -> {
                    perfil()
                    true
                }

                else -> {
                    false
                }
            }
        }
    }

    private fun pedidos() {
        val fragment = pedidos_fr()
        val trasition = supportFragmentManager.beginTransaction()
        trasition.replace(binding.frameLAyout.id, fragment, "pedido")
        trasition.commit()
    }

    private fun soporte() {
        val fragment = soporte_fr()
        val trasition = supportFragmentManager.beginTransaction()
        trasition.replace(binding.frameLAyout.id, fragment, "soporte")
        trasition.commit()
    }

    private fun review() {
        val fragment = review_fr()
        val trasition = supportFragmentManager.beginTransaction()
        trasition.replace(binding.frameLAyout.id, fragment, "review")
        trasition.commit()
    }

    private fun perfil() {
        val fragment = cuenta_fr()
        val trasition = supportFragmentManager.beginTransaction()
        trasition.replace(binding.frameLAyout.id, fragment, "prefil")
        trasition.commit()
    }

}
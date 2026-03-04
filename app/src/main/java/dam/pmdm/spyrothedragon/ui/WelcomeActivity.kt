package dam.pmdm.spyrothedragon.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import dam.pmdm.spyrothedragon.databinding.ActivityWelcomeBinding
import android.content.Intent
import dam.pmdm.spyrothedragon.MainActivity

class WelcomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWelcomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Si la guía ya fue vista, ir directamente a MainActivity sin mostrar la bienvenida
        val prefs = getSharedPreferences("guiaPrefs", MODE_PRIVATE)
        if (prefs.getBoolean("guideDisplayed", false)) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
            return
        }

        binding = ActivityWelcomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        animarLineaTitulo()
        binding.btnStart.setOnClickListener {

            prefs.edit().putBoolean("guideDisplayed", true).apply()
            val intent = Intent(this, MainActivity::class.java)

            // Le decimos a MainActivity que muestre la guía
            intent.putExtra("showGuide", true)
            startActivity(intent)
            overridePendingTransition(dam.pmdm.spyrothedragon.R.drawable.fade_in, dam.pmdm.spyrothedragon.R.drawable.fade_out)
            finish()
        }

    }

    //Animamos la línea bajo el título
    private fun animarLineaTitulo() {
        val linea = binding.vLinea

        linea.scaleX = 0f
        linea.pivotX = 0f

        linea.animate()
            .scaleX(1f)
            .setDuration(2000)
            .start()
    }
}
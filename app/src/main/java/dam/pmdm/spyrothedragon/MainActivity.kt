package dam.pmdm.spyrothedragon

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import dam.pmdm.spyrothedragon.databinding.ActivityMainBinding
import android.view.ViewGroup
import android.view.View
import androidx.core.content.ContextCompat
import android.view.animation.OvershootInterpolator
import dam.pmdm.spyrothedragon.ui.SummaryGuide
import dam.pmdm.spyrothedragon.ui.WelcomeActivity
import android.media.MediaPlayer
import android.animation.ObjectAnimator
import android.animation.ValueAnimator


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    var navController: NavController? = null

    private var currentStep = 0;
    private var btnAnimator: ObjectAnimator? = null
    private var infoAnimator: ObjectAnimator? = null
    private var floatAnimator: ObjectAnimator? = null
    private var activeGuide = false





    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment: Fragment? =
            supportFragmentManager.findFragmentById(R.id.navHostFragment)

        navHostFragment?.let {
            navController = NavHostFragment.findNavController(it)
            NavigationUI.setupWithNavController(binding.navView, navController!!)
            NavigationUI.setupActionBarWithNavController(this, navController!!)
        }

        binding.navView.setOnItemSelectedListener { menuItem ->
            selectedBottomMenu(menuItem)
        }

        navController?.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.navigation_characters,
                R.id.navigation_worlds,
                R.id.navigation_collectibles -> {
                    // En las pantallas de los tabs no mostramos la flecha atrás
                    supportActionBar?.setDisplayHomeAsUpEnabled(false)
                }

                else -> {
                    // En el resto de pantallas sí
                    supportActionBar?.setDisplayHomeAsUpEnabled(true)
                }
            }
        }

        // SharedPreferences para mostrar una vez la guía
        val prefs = getSharedPreferences("guiaPrefs", MODE_PRIVATE)
        val guideDisplayed = prefs.getBoolean("guideDisplayed", false)

        // Comprobamos si venimos de WelcomeActivity con la orden de mostrar la guía
        val showGuide = intent.getBooleanExtra("showGuide", false)

        if (showGuide) {
            // Mostrar el overlay y arrancar la guía
            binding.overlayGuia.visibility = View.VISIBLE
            activeGuide = true
            setNavigationEnabled(true)

        } else if (!guideDisplayed) {

            val intent = Intent(this, WelcomeActivity::class.java)
            startActivity(intent)
        } else {
            // Guía ya completada, ocultar overlay
            binding.overlayGuia.visibility = View.GONE
        }

        // Gestión de botones para ir avanzando por la guía
        binding.btnNext.setOnClickListener {
            btnAnimator?.cancel()
            btnAnimator = null
            currentStep++
            if (currentStep > 5) {
                closeGuide()
            } else {
                updateBubble()
                playSound(R.raw.pop)
            }
        }

        binding.btnOmitir.setOnClickListener {
            closeGuide()
        }

    }

    private fun selectedBottomMenu(menuItem: MenuItem): Boolean {
        when (menuItem.itemId) {
            R.id.nav_characters ->
                navController?.navigate(R.id.navigation_characters)

            R.id.nav_worlds ->
                navController?.navigate(R.id.navigation_worlds)

            else ->
                navController?.navigate(R.id.navigation_collectibles)
        }
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.about_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (activeGuide) return true  // Bloquear  durante la guía
        return if (item.itemId == R.id.action_info) {
            showInfoDialog()
            true
        } else {
            super.onOptionsItemSelected(item)
        }
    }

    private fun showInfoDialog() {
        AlertDialog.Builder(this)
            .setTitle(R.string.title_about)
            .setMessage(R.string.text_about)
            .setPositiveButton(R.string.accept, null)
            .show()
    }

    //Reutilizamos el cardview cambiandole el texto, su animación y su posición
    private fun updateBubble() {

        when (currentStep) {
            1 -> {
                navController?.navigate(R.id.navigation_characters)
                binding.tvSpeedBubble.text = "Aquí puedes explorar todos \n los personajes del mundo Spyro"
                moveNavBubble()
                highlightNavButton(0)
            }
            2 -> {
                navController?.navigate(R.id.navigation_worlds)
                binding.tvSpeedBubble.text = "Aquí están los mundos del juego"
                moveNavBubble()
                highlightNavButton(1)

            }
            3 -> {
                navController?.navigate(R.id.navigation_collectibles)
                binding.tvSpeedBubble.text = "Aquí verás los coleccionables\n que puedes conseguir"
                moveNavBubble()
                highlightNavButton(2)

            }
            4 -> {
                binding.tvSpeedBubble.text = "Pulsa aquí para ver la \n información de la app"
                placeInfoBubble()
                highlightInfoButton()

            }
            //Pantalla de resumen
            5 -> {
                showSummary()
                closeGuide()
            }
        }


    }

    // Colocamos el bocadillo en la posición correcta
    private fun moveNavBubble() {
        binding.speechBubble.post {
            // Centrar horizontalmente
            val centerX = (binding.overlayGuia.width - binding.speechBubble.width) / 2
            binding.speechBubble.x = centerX.toFloat()

            // Justo encima del Nav
            binding.speechBubble.translationY = 0f // Resetear animación previa antes de posicionar
            val finalY = binding.navView.y - binding.speechBubble.height - 16f
            binding.speechBubble.y = finalY

            binding.speechBubble.visibility = View.VISIBLE

            // Animación
            binding.speechBubble.apply {
                alpha = 0f
                scaleX = 0.8f
                scaleY = 0.8f
                visibility = View.VISIBLE
                animate()
                    .alpha(1f)
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(500)
                    .setInterpolator(OvershootInterpolator())
                    .start()
            }
        }}


    // Animamos los botones del nav
    private fun highlightNavButton(indice: Int) {

        val menuView = binding.navView.getChildAt(0) as ViewGroup

        // Cancelamos cualquier animación previa
        btnAnimator?.cancel()
        btnAnimator = null

        for (i in 0 until menuView.childCount) {

            val item = menuView.getChildAt(i)

            if (i == indice) {
                // Fondo para resaltar
                item.setBackgroundColor(ContextCompat.getColor(this, R.color.purple_transparent))

                // Animación de parpadeo
                btnAnimator = ObjectAnimator.ofFloat(item, "alpha", 1f, 0.3f)
                btnAnimator?.duration = 500
                btnAnimator?.repeatMode = ValueAnimator.REVERSE
                btnAnimator?.repeatCount = ValueAnimator.INFINITE
                btnAnimator?.start()

            } else {
                // Restaurar fondo y alpha normal
                item.setBackgroundColor(android.graphics.Color.TRANSPARENT)
                item.alpha = 1f
            }
        }

    }
    // Colocamos el bocadillo en la posición correcta para el icono de info
    private fun placeInfoBubble() {

        binding.speechBubble.post {
            // Posición
            val offsetDerecha = 50f
            val offsetSuperior = 200f
            val screenWidth = binding.overlayGuia.width
            binding.speechBubble.translationY = 0f // Resetear animación previa antes de posicionar
            binding.speechBubble.x = screenWidth - binding.speechBubble.width - offsetDerecha
            binding.speechBubble.y = offsetSuperior

            binding.speechBubble.visibility = View.VISIBLE

            // Animación de aparición igual que los otros bocadillos
            binding.speechBubble.apply {
                alpha = 0f
                scaleX = 0.8f
                scaleY = 0.8f
                visibility = View.VISIBLE
                animate()
                    .alpha(1f)
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(500)
                    .setInterpolator(OvershootInterpolator())
                    .start()
            }
        }
    }
    // Animación para el botón de info
    private fun highlightInfoButton() {
        val infoItem = findViewById<View>(R.id.action_info)

        infoItem?.let {
            // Cancelar animación previa si existía
            infoAnimator?.cancel()

            // Animación de parpadeo
            infoAnimator = ObjectAnimator.ofFloat(it, "alpha", 1f, 0.3f)
            infoAnimator?.duration = 500
            infoAnimator?.repeatMode = ValueAnimator.REVERSE
            infoAnimator?.repeatCount = ValueAnimator.INFINITE
            infoAnimator?.start()
        }
    }

    // Mostramos el resumen de la guía, siendo la última pantalla de ésta
    private fun showSummary() {
        val intent = Intent(this, SummaryGuide::class.java)
        startActivity(intent)

    }

    private fun closeGuide() {
        binding.overlayGuia.visibility = View.GONE

        // Restaurar botones del nav
        val menuView = binding.navView.getChildAt(0) as ViewGroup
        for (i in 0 until menuView.childCount) {
            val item = menuView.getChildAt(i)
            item.setBackgroundColor(android.graphics.Color.TRANSPARENT)
            item.alpha = 1f
        }
        btnAnimator?.cancel()
        btnAnimator = null
        floatAnimator?.cancel()
        floatAnimator = null
        // Desbloqueamos la barra de navegación inferior
        activeGuide = false
        binding.navView.isEnabled = true
        setNavigationEnabled(false)


        val prefs = getSharedPreferences("guiaPrefs", MODE_PRIVATE)
        prefs.edit().putBoolean("guiaMostrada", true).apply()
    }


    private fun playSound(isSound: Int) {
        val mediaPlayer = MediaPlayer.create(this, isSound)
        mediaPlayer.start()
        mediaPlayer.setOnCompletionListener {
            it.release() // libera recursos cuando termina
        }
    }

    // Bloquear/desbloquear cada item del menú inferior durante la guía
    private fun setNavigationEnabled(isLocked: Boolean) {
        val menu = binding.navView.menu
        for (i in 0 until menu.size()) {
            menu.getItem(i).isEnabled = !isLocked
        }
    }

}



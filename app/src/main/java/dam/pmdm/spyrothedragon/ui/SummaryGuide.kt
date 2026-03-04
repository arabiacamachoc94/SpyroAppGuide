package dam.pmdm.spyrothedragon.ui

import android.animation.ObjectAnimator
import android.media.MediaPlayer
import android.os.Bundle
import dam.pmdm.spyrothedragon.databinding.ActivitySummaryGuideBinding
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import dam.pmdm.spyrothedragon.R




class SummaryGuide : AppCompatActivity() {

    private lateinit var binding: ActivitySummaryGuideBinding
    private var animationTitle: ObjectAnimator? = null
    private var animationButton: ObjectAnimator? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySummaryGuideBinding.inflate(layoutInflater)
        setContentView(binding.root)

        animationTitle = animateBlink(binding.tvTittle)
        animationButton = animateBlink(binding.btnStart)

        binding.btnStart.setOnClickListener {
            playSound(R.raw.finish)
            animationTitle?.cancel()
            animationButton?.cancel()
            closeGuide()
            overridePendingTransition(dam.pmdm.spyrothedragon.R.drawable.fade_in, dam.pmdm.spyrothedragon.R.drawable.fade_out)

        }

}
    private fun closeGuide(){
        val prefs = getSharedPreferences("guide", MODE_PRIVATE)
        prefs.edit().putBoolean("guideDisplayed", true).apply()
        finish()
    }

    // Animamos tanto el título como el botón
    private fun animateBlink(vista: View): ObjectAnimator {
        return ObjectAnimator.ofFloat(vista, "alpha", 0.5f, 1f).apply {
            duration = 600
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
            start()
        }
}
    // Añadimos sonido
    private fun playSound(idSound: Int) {
        val mediaPlayer = MediaPlayer.create(this, idSound)
        mediaPlayer.start()
        mediaPlayer.setOnCompletionListener {
            it.release()
        }
    }

}
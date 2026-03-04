package dam.pmdm.spyrothedragon

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.VideoView
import androidx.fragment.app.DialogFragment
import android.net.Uri
import android.widget.Button


// Clase para el diálogo del easter egg con el video
class VideoEasterEggFragment : DialogFragment() {


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_dialog, container, false)
        val videoView = view.findViewById<VideoView>(R.id.videoViewEasterEgg)
        val btnOmitir = view.findViewById<Button>(R.id.btnOmitir2)


        // Gestión del btn omitir
        btnOmitir.setOnClickListener {
            dismiss() // cierra el diálogo
            returnToWorlds()
        }

        val uri = Uri.parse("android.resource://${requireActivity().packageName}/${R.raw.video_spyro}")
        videoView.setVideoURI(uri)

        videoView.setOnCompletionListener {
            dismiss()
            returnToWorlds()
        }
        videoView.start()

        return view
    }

    // Función para volver a la pestaña mundos
    private fun returnToWorlds() {
        (activity as? MainActivity)?.navController?.navigate(R.id.navigation_worlds)
    }

    // El dialog ocupa toda la pantalla
    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
    }

}
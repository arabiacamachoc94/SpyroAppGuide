package dam.pmdm.spyrothedragon.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dam.pmdm.spyrothedragon.R
import dam.pmdm.spyrothedragon.adapters.WorldsAdapter
import dam.pmdm.spyrothedragon.databinding.FragmentWorldsBinding
import dam.pmdm.spyrothedragon.models.World
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import java.io.InputStream
import dam.pmdm.spyrothedragon.VideoEasterEggFragment

class WorldsFragment : Fragment() {

    private var _binding: FragmentWorldsBinding? = null
    private val binding get() = _binding!!

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: WorldsAdapter
    private val worldsList = mutableListOf<World>()

    private var lastClick = -1
    private var clickCounter = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentWorldsBinding.inflate(inflater, container, false)

        recyclerView = binding.recyclerViewWorlds
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = WorldsAdapter(worldsList){ position ->
            // Lógica del Easter Egg
            if (lastClick == position) {
                clickCounter++
            } else {
                lastClick = position
                clickCounter = 1
            }

            if (clickCounter == 3) {
                clickCounter = 0
                showEasterEggVideo()
            }}
        recyclerView.adapter = adapter

        loadWorlds()
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun loadWorlds() {
        try {
            val inputStream: InputStream =
                resources.openRawResource(R.raw.worlds)

            val factory = XmlPullParserFactory.newInstance()
            factory.isNamespaceAware = true
            val parser = factory.newPullParser()
            parser.setInput(inputStream, null)

            var eventType = parser.eventType
            var currentWorld: World? = null

            while (eventType != XmlPullParser.END_DOCUMENT) {
                when (eventType) {
                    XmlPullParser.START_TAG -> {
                        when (parser.name) {
                            "world" -> currentWorld = World()
                            "name" -> currentWorld?.name = parser.nextText()
                            "description" -> currentWorld?.description = parser.nextText()
                            "image" -> currentWorld?.image = parser.nextText()
                        }
                    }

                    XmlPullParser.END_TAG -> {
                        if (parser.name == "world" && currentWorld != null) {
                            worldsList.add(currentWorld)
                        }
                    }
                }
                eventType = parser.next()
            }

            adapter.notifyDataSetChanged()

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    private fun showEasterEggVideo() {
        VideoEasterEggFragment().show(parentFragmentManager, "videoEasterEgg")
    }
}

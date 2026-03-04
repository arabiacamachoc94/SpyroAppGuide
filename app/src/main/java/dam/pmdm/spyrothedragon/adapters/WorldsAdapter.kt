package dam.pmdm.spyrothedragon.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import dam.pmdm.spyrothedragon.R
import dam.pmdm.spyrothedragon.models.World

class WorldsAdapter(
    private val list: List<World>,
    private val contarClick: (position: Int) -> Unit
) : RecyclerView.Adapter<WorldsAdapter.WorldsViewHolder>() {

    private val worldImages = mapOf(
        "sunny_beach" to R.drawable.sunny_beach,
        "midday_gardens" to R.drawable.midday_gardens,
        "autumn_plains" to R.drawable.autumn_plains,
        "glimmer" to R.drawable.glimmer,
        "cloud_spires" to R.drawable.cloud_spires,
        "hurricane_halls" to R.drawable.hurricane_halls,
        "frozen_altars" to R.drawable.frozen_altars,
        "lost_fleet" to R.drawable.lost_fleet,
        "sunset_beach" to R.drawable.sunset_beach
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorldsViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.cardview, parent, false)
        return WorldsViewHolder(view)
    }

    override fun onBindViewHolder(holder: WorldsViewHolder, position: Int) {
        val world = list[position]
        holder.nameTextView.text = world.name

        val drawableRes = worldImages[world.image] ?: R.drawable.placeholder
        holder.imageImageView.setImageResource(drawableRes)

        //Contamos los clicks en el botón Mundos
        holder.itemView.setOnClickListener {
            contarClick(position)
        }
    }

    override fun getItemCount(): Int = list.size

    class WorldsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.name)
        val imageImageView: ImageView = itemView.findViewById(R.id.image)
    }
}

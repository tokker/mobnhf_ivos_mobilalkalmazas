package hu.bme.aut.guillotine

import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import hu.bme.aut.guillotine.databinding.*

class PlayerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlayerBinding
    private lateinit var rowBinding: CardRowBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val intent = getIntent()
        val player = intent.getBundleExtra("Player")
        val cards = player?.getSerializable("cards") as ArrayList<Card>
        val player_name = intent.getStringExtra("player_name")
        val turn = intent.getIntExtra("turn", 0)
        binding.name.text = player_name
        for (i in 0 until turn){
            val place = "drawable/c" + cards[i].colour.toString() + cards[i].number.toString()
            val PACKAGE_NAME = applicationContext.packageName
            val imgId = resources.getIdentifier("$PACKAGE_NAME:$place", null, null)
            rowBinding = CardRowBinding.inflate(layoutInflater)
            rowBinding.cardImage.setImageBitmap(BitmapFactory.decodeResource(getResources(),imgId))
            if(i < 3) {
                binding.cardRows.addView(rowBinding.root)
            }
            else {
                binding.cardRows2.addView(rowBinding.root)
            }
        }
    }
}
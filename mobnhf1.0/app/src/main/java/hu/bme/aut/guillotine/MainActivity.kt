package hu.bme.aut.guillotine

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation
import android.view.inputmethod.InputMethodManager
import com.google.android.material.snackbar.Snackbar
import hu.bme.aut.guillotine.databinding.ActivityMainBinding
import hu.bme.aut.guillotine.databinding.PlayerRowBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var rowBinding: PlayerRowBinding
    var players: ArrayList<Player> = ArrayList(8)
    private var playerCount = 0
    val rotate = RotateAnimation(
        0F,
        360F,
        Animation.RELATIVE_TO_SELF,
        0.5f,
        Animation.RELATIVE_TO_SELF,
        0.5f
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        rotate.duration = 1000
        rotate.interpolator = LinearInterpolator()
        binding.addButton.setOnClickListener {
            it.hideKeyboard()
            if (binding.playerName.text.toString().isEmpty()) {
                Snackbar.make(binding.root, R.string.hiba1, Snackbar.LENGTH_LONG).show()
                return@setOnClickListener
            }
            if(playerCount == 8){
                Snackbar.make(binding.root, R.string.hiba2, Snackbar.LENGTH_LONG).show()
                return@setOnClickListener
            }
            for(i in 0 until playerCount){
                if(players[i].name == binding.playerName.text.toString()){
                    Snackbar.make(binding.root, R.string.hiba4, Snackbar.LENGTH_LONG).show()
                    return@setOnClickListener
                }
            }
            rowBinding = PlayerRowBinding.inflate(layoutInflater)
            rowBinding.rowPlayerName.text = binding.playerName.text.toString()
            binding.playerRows.addView(rowBinding.root)
            val p = Player()
            p.name = binding.playerName.text.toString()
            players.add(p)
            ++playerCount
            binding.playerName.setText("")
            rowBinding.beerIcon.startAnimation(rotate)
        }

        binding.startButton.setOnClickListener {
            if (playerCount < 2) {
                it.hideKeyboard()
                Snackbar.make(binding.root, R.string.hiba3, Snackbar.LENGTH_LONG).show()
                return@setOnClickListener
            }
            val intent = Intent(this, GameActivity::class.java)
            val args = Bundle()
            args.putSerializable("players", players)
            intent.putExtra("Players", args)
            intent.putExtra("playerCount", playerCount)
            startActivity(intent)
        }
    }
}

private fun View.hideKeyboard() {
    val inputManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    inputManager.hideSoftInputFromWindow(windowToken, 0)
}

fun Context.popToRoot()
{
    val intent = Intent(this, MainActivity::class.java)
    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
    startActivity(intent)
}

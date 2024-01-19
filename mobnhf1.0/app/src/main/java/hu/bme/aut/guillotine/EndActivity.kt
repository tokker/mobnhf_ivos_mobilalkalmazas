package hu.bme.aut.guillotine

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import hu.bme.aut.guillotine.databinding.ActivityEndBinding
import hu.bme.aut.guillotine.databinding.ActivityGameBinding
import hu.bme.aut.guillotine.databinding.PlayerButtonRowBinding
import hu.bme.aut.guillotine.databinding.PlayerDrinkRowBinding

class EndActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEndBinding
    private lateinit var rowBinding: PlayerDrinkRowBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEndBinding.inflate(layoutInflater)
        setContentView(binding.root)
        rowBinding = PlayerDrinkRowBinding.inflate(layoutInflater)
        rowBinding.playerName.text = ""
        rowBinding.drinks.text = getString(R.string.drinks)
        rowBinding.drinkGiven.text = getString(R.string.drinkGiven)
        binding.drinkRows.addView(rowBinding.root)
        val intent = getIntent()
        val Players = intent.getBundleExtra("Players")
        val players = Players?.getSerializable("players") as ArrayList<Player>
        val playerCount = intent.getIntExtra("playerCount", 0)
        for (i in 0 until playerCount) {
            rowBinding = PlayerDrinkRowBinding.inflate(layoutInflater)
            rowBinding.playerName.text = players[i].name
            rowBinding.drinks.text = players[i].drinks.toString()
            rowBinding.drinkGiven.text = players[i].drinksGiven.toString()
            binding.drinkRows.addView(rowBinding.root)
        }
        binding.startButton.setOnClickListener {
            AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle(getString(R.string.starNewGame))
                .setMessage(getString(R.string.samePlayers))
                .setPositiveButton(getString(R.string.yes)) { _, _ -> finish() }
                .setNegativeButton(getString(R.string.no)) { _, _ -> newGame()}
                .show()
        }

    }

    fun newGame(){
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}

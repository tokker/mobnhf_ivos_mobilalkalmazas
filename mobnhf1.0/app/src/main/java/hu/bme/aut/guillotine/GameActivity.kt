package hu.bme.aut.guillotine

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import hu.bme.aut.guillotine.databinding.*
import kotlin.random.Random
import android.content.DialogInterface
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation
import android.view.animation.ScaleAnimation





class GameActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGameBinding
    private lateinit var rowBinding: PlayerButtonRowBinding
    private lateinit var rowBinding2: CardRow2Binding
    private lateinit var rowBinding3: CardRow2Binding
    private lateinit var rowBinding4: AnswerButtonRowBinding
    private lateinit var rowBinding5: AnswerButtonRowBinding
    private var deck : ArrayList<Card> = ArrayList(52)
    private var actualPlayer = 0
    private lateinit var players: ArrayList<Player>
    private var question = true
    private var selected = Card()
    private var turnSix = 0
    private var turnSixDeck : ArrayList<Card> = ArrayList(12)
    private var drinking = ""
    private var drinks = true
    private var playerCount = 0
    private var multiplier = 1
    private var everyone = false
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
        for (i in 1 until 5){
            for (j in 2 until 15) {
                val card = Card()
                card.colour = i
                card.number = j
                deck.add(card)
            }
        }
        rotate.duration = 1000
        rotate.interpolator = LinearInterpolator()
        binding = ActivityGameBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val intent = getIntent()
        val Players = intent.getBundleExtra("Players")
        players = Players?.getSerializable("players") as ArrayList<Player>
        playerCount = intent.getIntExtra("playerCount", 0)
        for (i in 0 until playerCount){
            rowBinding = PlayerButtonRowBinding.inflate(layoutInflater)
            rowBinding.playerButton.text = players[i].name
            if(i < 4) {
                binding.playerButtonRows.addView(rowBinding.root)
            }
            else {
                binding.playerButtonRows2.addView(rowBinding.root)
            }
            rowBinding.playerButton.setOnClickListener {
                val intent = Intent(this, PlayerActivity::class.java)
                val args = Bundle()
                args.putSerializable("cards", players[i].cards)
                intent.putExtra("Player", args)
                intent.putExtra("player_name", players[i].name)
                intent.putExtra("turn", players[i].turn)
                startActivity(intent)
            }
        }
        binding.toolbarButton.setOnClickListener{
            AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle(getString(R.string.endGame))
                .setMessage(getString(R.string.youSure))
                .setPositiveButton(getString(R.string.yes)) { _, _ -> newGame() }
                .setNegativeButton(getString(R.string.no), null)
                .show()
        }
        drawNextCard()
        showInstructions()
        showCards()
        showAnswers()
    }

    fun showCards(){
        binding.cardRows.removeAllViews()
        binding.cardRows2.removeAllViews()
        if(players[actualPlayer].turn < 5) {
            for (i in 0 until players[actualPlayer].turn) {
                val place =
                    "drawable/c" + players[actualPlayer].cards[i].colour.toString() + players[actualPlayer].cards[i].number.toString()
                val PACKAGE_NAME = applicationContext.packageName
                val imgId = resources.getIdentifier("$PACKAGE_NAME:$place", null, null)
                rowBinding2 = CardRow2Binding.inflate(layoutInflater)
                rowBinding2.cardImage.setImageBitmap(BitmapFactory.decodeResource(getResources(), imgId))
                binding.cardRows.addView(rowBinding2.root)
            }
            val place: String
            if (question)
                place = "drawable/back"
            else
                place = "drawable/c" + selected.colour.toString() + selected.number.toString()
            val PACKAGE_NAME = applicationContext.packageName
            val imgId = resources.getIdentifier("$PACKAGE_NAME:$place", null, null)
            rowBinding3 = CardRow2Binding.inflate(layoutInflater)
            rowBinding3.cardImage.setImageBitmap(BitmapFactory.decodeResource(getResources(), imgId))
            binding.cardRows2.addView(rowBinding3.root)
            rowBinding3.cardImage.getLayoutParams().width = 500
            if (!question) {
                rowBinding3.cardImage.startAnimation(rotate)
            }
        }
        else{
            for (i in 0 until 12) {
                var place : String
                if(i < turnSix)
                    place = "drawable/c" + turnSixDeck[i].colour.toString() + turnSixDeck[i].number.toString()
                else
                    place = "drawable/back"
                val PACKAGE_NAME = applicationContext.packageName
                val imgId = resources.getIdentifier("$PACKAGE_NAME:$place", null, null)
                if(i%2 == 0) {
                    rowBinding2 = CardRow2Binding.inflate(layoutInflater)
                    rowBinding2.cardImage.setImageBitmap(BitmapFactory.decodeResource(getResources(), imgId))
                    binding.cardRows.addView(rowBinding2.root)
                    if(i == turnSix-1)
                        rowBinding2.cardImage.startAnimation(rotate);
                }
                else{
                    rowBinding3 = CardRow2Binding.inflate(layoutInflater)
                    rowBinding3.cardImage.setImageBitmap(BitmapFactory.decodeResource(getResources(), imgId))
                    binding.cardRows2.addView(rowBinding3.root)
                    if(i == turnSix-1)
                        rowBinding3.cardImage.startAnimation(rotate);
                }
            }
            ++turnSix
        }
    }

    @SuppressLint("SetTextI18n")
    fun showInstructions(){
        if(question){
            if(players[actualPlayer].turn == 0)
                binding.instructions.text = players[actualPlayer].name + getString(R.string.question1)
            else if(players[actualPlayer].turn == 1)
                binding.instructions.text = players[actualPlayer].name + getString(R.string.question2)
            else if(players[actualPlayer].turn == 2)
                binding.instructions.text = players[actualPlayer].name + getString(R.string.question3)
            else if(players[actualPlayer].turn == 3)
                binding.instructions.text = players[actualPlayer].name + getString(R.string.question4)
            else if(players[actualPlayer].turn == 4)
                binding.instructions.text = players[actualPlayer].name + getString(R.string.question5)
            else
                binding.instructions.text = getString(R.string.information)
        }
        else if(drinks) {
            showDrinkingPerson()
            if(drinking != "")
                binding.instructions.text = drinking + ' ' + getString(R.string.command1) + ' ' + ((players[actualPlayer].turn + 1) * multiplier).toString() + ' ' + getString(R.string.command3)
            else
                binding.instructions.text = getString(R.string.nobody1)
        }
        else{
            showDrinkingPerson()
            if(drinking != "")
                binding.instructions.text = drinking + ' ' +getString(R.string.command2)+ ' ' + ((players[actualPlayer].turn+1) * multiplier).toString() + ' ' + getString(R.string.command3)
            else
                binding.instructions.text = getString(R.string.nobody2)
        }
    }

    fun showAnswers(){
        binding.answerButtonRows.removeAllViews()
        binding.answerButtonRows2.removeAllViews()
        if(!question){
            rowBinding4 = AnswerButtonRowBinding.inflate(layoutInflater)
            rowBinding4.answerButton.text = getString(R.string.tovabb)
            binding.answerButtonRows.addView(rowBinding4.root)
            rowBinding4.answerButton.setOnClickListener {
                if(players[actualPlayer].turn < 5) {
                    question = true
                    ++players[actualPlayer].turn
                    drinks = true
                }
                else
                    drinks = !drinks
                players[actualPlayer].cards.add(selected)
                if(actualPlayer < playerCount-1)
                    ++actualPlayer
                else
                    actualPlayer = 0
                if(turnSix > 12){
                    val intent = Intent(this, EndActivity::class.java)
                    val args = Bundle()
                    args.putSerializable("players", players)
                    intent.putExtra("Players", args)
                    intent.putExtra("playerCount", playerCount)
                    startActivity(intent)
                    finish()
                }
                else {
                    drawNextCard()
                    showInstructions()
                    showCards()
                    showAnswers()
                }
            }
        }
        else if (players[actualPlayer].turn < 5){
            var a1 = ""
            var a2 = ""
            if(players[actualPlayer].turn == 0){
                a1 = getString(R.string.a1)
                a2 = getString(R.string.a2)
            }
            else if(players[actualPlayer].turn == 1){
                a1 = getString(R.string.a3)
                a2 = getString(R.string.a4)
            }
            else if(players[actualPlayer].turn == 2){
                a1 = getString(R.string.a5)
                a2 = getString(R.string.a6)
            }
            else if(players[actualPlayer].turn == 3){
                a1 = getString(R.string.a7)
                a2 = getString(R.string.a8)
            }
            else {
                a1 = getString(R.string.a9)
                a2 = getString(R.string.a10)
            }
            rowBinding4 = AnswerButtonRowBinding.inflate(layoutInflater)
            rowBinding4.answerButton.text = a1
            binding.answerButtonRows.addView(rowBinding4.root)
            rowBinding4.answerButton.setOnClickListener {
                question = false
                showCards()
                DrinkingOrNot(1)
                showInstructions()
                showAnswers()
            }
            rowBinding4 = AnswerButtonRowBinding.inflate(layoutInflater)
            rowBinding4.answerButton.text = a2
            binding.answerButtonRows.addView(rowBinding4.root)
            rowBinding4.answerButton.setOnClickListener {
                question = false
                showCards()
                DrinkingOrNot(2)
                showInstructions()
                showAnswers()
            }
            if(players[actualPlayer].turn == 4){
                rowBinding5 = AnswerButtonRowBinding.inflate(layoutInflater)
                rowBinding5.answerButton.text = getString(R.string.a11)
                binding.answerButtonRows2.addView(rowBinding5.root)
                rowBinding5.answerButton.setOnClickListener {
                    question = false
                    showCards()
                    DrinkingOrNot(3)
                    showInstructions()
                    showAnswers()
                }
                rowBinding5 = AnswerButtonRowBinding.inflate(layoutInflater)
                rowBinding5.answerButton.text = getString(R.string.a12)
                binding.answerButtonRows2.addView(rowBinding5.root)
                rowBinding5.answerButton.setOnClickListener {
                    question = false
                    showCards()
                    DrinkingOrNot(4)
                    showInstructions()
                    showAnswers()
                }
            }
        }
        else{
            rowBinding4 = AnswerButtonRowBinding.inflate(layoutInflater)
            rowBinding4.answerButton.text = getString(R.string.tovabb)
            binding.answerButtonRows.addView(rowBinding4.root)
            rowBinding4.answerButton.setOnClickListener {
                question = false
                showCards()
                showInstructions()
                showAnswers()
            }
        }
    }

    fun drawNextCard(){
        var cardNum = 0
        if(turnSix == 0)
            cardNum = Random.nextInt(52-playerCount*players[actualPlayer].turn-actualPlayer)
        else
            cardNum = Random.nextInt(52-playerCount*players[actualPlayer].turn-turnSix + 1)
        selected = deck[cardNum]
        deck.removeAt(cardNum)
        if(players[actualPlayer].turn == 5)
            turnSixDeck.add(selected)
    }

    fun showDrinkingPerson(){
        if(players[actualPlayer].turn < 5) {
            if(everyone){
                drinking = getString(R.string.everyone)
                for(i in 0 until playerCount)
                    players[i].drinks += players[actualPlayer].turn+1
            }
            else {
                drinking = players[actualPlayer].name
                if(drinks)
                    players[actualPlayer].drinks += players[actualPlayer].turn+1
                else
                    players[actualPlayer].drinksGiven += players[actualPlayer].turn+1
            }
        }
        else{
            val playersDrinking : ArrayList<Int> = ArrayList (playerCount)
            for (i in 0 until playerCount){
                playersDrinking.add(0)
            }
            multiplier = 1
            for (i in 0 until playerCount){
                for (j in 0 until 5){
                    if(players[i].cards[j].number == selected.number) {
                        ++playersDrinking[i]
                        if(drinks)
                            players[i].drinks += 6
                        else
                            players[i].drinksGiven += 6
                    }
                }
            }
            var multiplied = false
            for (i in 0 until playerCount){
                if(playersDrinking[i] > 1) {
                    multiplied = true
                    multiplier = playersDrinking[i]
                }
            }
            var oneDrinkers = ""
            if(multiplied){
                var first = true
                for (i in 0 until playerCount){
                    if(playersDrinking[i] == 1) {
                        if(!first)
                            oneDrinkers += ", "
                        else
                            first = false
                        oneDrinkers += players[i].name
                    }
                }
                if(!first)
                    if(drinks)
                        oneDrinkers += ' ' + getString(R.string.onedrinkers) + ' '
                    else
                        oneDrinkers += ' ' + getString(R.string.onedrinkers2) + ' '
            }
            var first = true
            for (i in 0 until playerCount){
                if(playersDrinking[i] == multiplier) {
                    if(!first)
                        oneDrinkers += ", "
                    else
                        first = false
                    oneDrinkers += players[i].name
                }
            }
            drinking = oneDrinkers
        }
    }

    fun DrinkingOrNot(buttonNum : Int){
        drinks = true
        everyone = false
        if(players[actualPlayer].turn == 0){
            if((buttonNum == 1 && (selected.colour == 2 || selected.colour == 3)) || (buttonNum == 2 && (selected.colour == 1 || selected.colour == 4)))
                drinks = false
        }
        if(players[actualPlayer].turn == 1){
            if((buttonNum == 1 && selected.number < players[actualPlayer].cards[0].number) || (buttonNum == 2 && selected.number > players[actualPlayer].cards[0].number))
                drinks = false
            if(selected.number == players[actualPlayer].cards[0].number)
                everyone = true
        }
        if(players[actualPlayer].turn == 2){
            if((buttonNum == 1 && ((selected.number < players[actualPlayer].cards[0].number && selected.number > players[actualPlayer].cards[1].number)
                        || (selected.number > players[actualPlayer].cards[0].number && selected.number < players[actualPlayer].cards[1].number)))
                || (buttonNum == 2 && ((selected.number < players[actualPlayer].cards[0].number && selected.number < players[actualPlayer].cards[1].number)
                        || (selected.number > players[actualPlayer].cards[0].number && selected.number > players[actualPlayer].cards[1].number))))
                drinks = false
            if(selected.number == players[actualPlayer].cards[0].number || selected.number == players[actualPlayer].cards[1].number)
                everyone = true
        }
        if(players[actualPlayer].turn == 3){
            if((buttonNum == 1 && selected.number < 11) || (buttonNum == 2 && selected.number > 10))
                drinks = false
        }
        if(players[actualPlayer].turn == 4){
            if((buttonNum == 1 && selected.colour == 3) || (buttonNum == 2 && selected.colour == 4) || (buttonNum == 3 && selected.colour == 2) || (buttonNum == 4 && selected.colour == 1))
                drinks = false
        }
    }

    override fun onBackPressed() {
        AlertDialog.Builder(this)
            .setIcon(android.R.drawable.ic_dialog_alert)
            .setTitle(getString(R.string.endGame))
            .setMessage(getString(R.string.youSure))
            .setPositiveButton(getString(R.string.yes)) { _, _ -> finish() }
            .setNegativeButton(getString(R.string.no), null)
            .show()
    }

    fun newGame(){
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}
package pl.wsei.pam.lab03

import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Menu
import android.view.MenuItem
import androidx.gridlayout.widget.GridLayout
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import pl.wsei.pam.lab01.R

class Lab03Activity : AppCompatActivity() {

    private lateinit var mBoard: GridLayout
    private lateinit var mBoardModel: MemoryBoardView
    private lateinit var completionPlayer: MediaPlayer
    private lateinit var negativePlayer: MediaPlayer
    private var isSound = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_lab03)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.memoryBoard)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        mBoard = findViewById(R.id.memoryBoard)

        val size = intent.getIntArrayExtra("size")
        if (size != null && size.size == 2) {
            val columns = size[0]
            val rows = size[1]

            mBoard.columnCount = columns
            mBoard.rowCount = rows

            Toast.makeText(this, "Plansza: ${rows}x${columns}", Toast.LENGTH_SHORT).show()

            completionPlayer = MediaPlayer.create(this, R.raw.completion)
            negativePlayer = MediaPlayer.create(this, R.raw.negative_guitar)

            if (savedInstanceState != null) {
                val savedState = savedInstanceState.getIntegerArrayList("game_state") ?: listOf()
                mBoardModel = MemoryBoardView(mBoard, columns, rows, completionPlayer, negativePlayer)
                mBoardModel.setState(savedState)
            } else {
                mBoardModel = MemoryBoardView(mBoard, columns, rows, completionPlayer, negativePlayer)
            }

            mBoardModel.setOnGameChangeListener { event ->
                runOnUiThread {
                    when (event.state) {
                        GameStates.Matching -> {
                            event.tiles.forEach { it.revealed = true }
                        }
                        GameStates.Match -> {
                            event.tiles.forEach { it.revealed = true }
                            if (isSound) completionPlayer.start()
                        }
                        GameStates.NoMatch -> {
                            event.tiles.forEach { it.revealed = true }
                            Handler(Looper.getMainLooper()).postDelayed({
                                event.tiles.forEach { it.revealed = false }
                            }, 2000)
                            if (isSound) negativePlayer.start()
                        }
                        GameStates.Finished -> {
                            Toast.makeText(this, "Gra zakończona!", Toast.LENGTH_SHORT).show()
                            if (isSound) completionPlayer.start()
                        }
                    }
                }
            }
        } else {
            Toast.makeText(this, "Błąd: Nie otrzymano rozmiaru planszy!", Toast.LENGTH_LONG).show()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putIntegerArrayList("game_state", ArrayList(mBoardModel.getState()))
    }

    override fun onPause() {
        super.onPause()
        completionPlayer.release()
        negativePlayer.release()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.board_activity_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.board_activity_sound -> {
                isSound = !isSound
                Toast.makeText(this, if (isSound) "Dźwięk włączony" else "Dźwięk wyłączony", Toast.LENGTH_SHORT).show()

                val newIcon = if (isSound) R.drawable.baseline_alarm_add_24 else R.drawable.baseline_alarm_off_24
                item.setIcon(newIcon)

                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}

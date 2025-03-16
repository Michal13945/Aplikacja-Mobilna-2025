package pl.wsei.pam.lab03

import android.os.Bundle
import android.os.Handler
import android.os.Looper
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_lab03)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.memoryBoard)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        mBoard = findViewById<GridLayout>(R.id.memoryBoard)

        val size = intent.getIntArrayExtra("size")
        if (size != null && size.size == 2) {
            val columns = size[0]
            val rows = size[1]

            mBoard.columnCount = columns
            mBoard.rowCount = rows

            Toast.makeText(this, "Plansza: ${rows}x${columns}", Toast.LENGTH_SHORT).show()

            if (savedInstanceState != null) {
                val savedState = savedInstanceState.getIntegerArrayList("game_state") ?: listOf()
                mBoardModel = MemoryBoardView(mBoard, columns, rows)
                mBoardModel.setState(savedState)
            } else {
                mBoardModel = MemoryBoardView(mBoard, columns, rows)
            }

            mBoardModel.setOnGameChangeListener { event ->
                runOnUiThread {
                    when (event.state) {
                        GameStates.Matching -> {
                            event.tiles.forEach { it.revealed = true }
                        }
                        GameStates.Match -> {
                            event.tiles.forEach { it.revealed = true }
                        }
                        GameStates.NoMatch -> {
                            event.tiles.forEach { it.revealed = true }
                            Handler(Looper.getMainLooper()).postDelayed({
                                event.tiles.forEach { it.revealed = false }
                            }, 2000)
                        }
                        GameStates.Finished -> {
                            Toast.makeText(this, "Gra zakończona!", Toast.LENGTH_SHORT).show()
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
}

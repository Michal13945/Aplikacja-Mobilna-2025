package pl.wsei.pam.lab03

import android.view.View
import android.widget.GridLayout
import android.widget.ImageButton
import pl.wsei.pam.lab01.R
import java.util.*

class MemoryBoardView(
    private val gridLayout: androidx.gridlayout.widget.GridLayout,
    private val cols: Int,
    private val rows: Int
) {
    private val tiles: MutableMap<String, Tile> = mutableMapOf()
    private val icons: List<Int> = listOf(
        R.drawable.baseline_accessibility_24,
        R.drawable.baseline_accessible_24,
        R.drawable.baseline_adjust_24,
        R.drawable.baseline_air_24,
        R.drawable.baseline_auto_fix_high_24,
        R.drawable.baseline_assist_walker_24,
        R.drawable.baseline_arrow_forward_24,
        R.drawable.baseline_app_shortcut_24,
        R.drawable.baseline_cable_24,
        R.drawable.baseline_cabin_24,
        R.drawable.baseline_bus_alert_24,
        R.drawable.baseline_bungalow_24,
        R.drawable.baseline_build_circle_24,
        R.drawable.baseline_bug_report_24,
        R.drawable.baseline_bubble_chart_24,
        R.drawable.baseline_brush_24,
        R.drawable.baseline_brunch_dining_24,
        )

    private val deckResource: Int = R.drawable.baseline_auto_fix_high_24
    private var onGameChangeStateListener: (MemoryGameEvent) -> Unit = { _ -> }
    private val matchedPair: Stack<Tile> = Stack()
    private val logic: MemoryGameLogic = MemoryGameLogic(cols * rows / 2)

    init {
        val shuffledIcons = mutableListOf<Int>().apply {
            addAll(icons.subList(0, cols * rows / 2))
            addAll(icons.subList(0, cols * rows / 2))
            shuffle()
        }

        for (i in 0 until rows) {
            for (j in 0 until cols) {
                val button = ImageButton(gridLayout.context).apply {
                    layoutParams = GridLayout.LayoutParams().apply {
                        width = 200
                        height = 200
                        setMargins(8, 8, 8, 8)
                    }
                    tag = "$i-$j"
                    setOnClickListener(::onClickTile)
                }

                val tile = Tile(button, shuffledIcons.removeAt(0), deckResource)
                tiles[button.tag.toString()] = tile
                gridLayout.addView(button)
            }
        }
    }

    private fun onClickTile(v: View) {
        val tile = tiles[v.tag] ?: return
        matchedPair.push(tile)
        val matchResult = logic.process { tile.tileResource }

        onGameChangeStateListener(MemoryGameEvent(matchedPair.toList(), matchResult))

        if (matchResult != GameStates.Matching) {
            matchedPair.clear()
        }
    }

    fun setOnGameChangeListener(listener: (MemoryGameEvent) -> Unit) {
        onGameChangeStateListener = listener
    }

    fun getState(): List<Int> {
        return tiles.values.map { if (it.revealed) it.tileResource else -1 }
    }

    fun setState(state: List<Int>) {
        tiles.values.forEachIndexed { index, tile ->
            tile.revealed = state[index] != -1
        }
    }
}

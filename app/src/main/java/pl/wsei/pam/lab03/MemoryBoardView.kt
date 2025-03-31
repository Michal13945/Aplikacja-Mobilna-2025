package pl.wsei.pam.lab03

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.media.MediaPlayer
import android.os.Handler
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.ImageButton
import android.widget.Toast
import androidx.gridlayout.widget.GridLayout
import pl.wsei.pam.lab01.R
import java.util.*

class MemoryBoardView(
    private val gridLayout: GridLayout,
    private val cols: Int,
    private val rows: Int,
    private val completionPlayer: MediaPlayer,
    private val negativePlayer: MediaPlayer
) {
    private val tiles: MutableMap<String, Tile> = mutableMapOf()
    private val icons: List<Int> = listOf(
        R.drawable.baseline_accessibility_24,
        R.drawable.baseline_accessible_24,
        R.drawable.baseline_adjust_24,
        R.drawable.baseline_air_24,
        R.drawable.baseline_alarm_off_24,
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
        R.drawable.baseline_business_center_24
    )

    private val deckResource: Int = R.drawable.baseline_auto_fix_high_24
    private var onGameChangeStateListener: (MemoryGameEvent) -> Unit = { _ -> }
    private val matchedPair: Stack<Tile> = Stack()
    private val logic: MemoryGameLogic = MemoryGameLogic(cols * rows / 2)

    init {
        gridLayout.post {
            val screenWidth = gridLayout.width
            val screenHeight = gridLayout.height

            val tileSize = screenWidth / cols
            val tileHeight = screenHeight / rows

            val shuffledIcons = mutableListOf<Int>().apply {
                addAll(icons.subList(0, cols * rows / 2))
                addAll(icons.subList(0, cols * rows / 2))
                shuffle()
            }

            for (i in 0 until rows) {
                for (j in 0 until cols) {
                    val button = ImageButton(gridLayout.context).apply {
                        layoutParams = GridLayout.LayoutParams().apply {
                            width = 0
                            height = 0
                            rowSpec = GridLayout.spec(i, 1f)
                            columnSpec = GridLayout.spec(j, 1f)
                            setMargins(10, 10, 10, 10)
                        }
                        tag = "$i-$j"
                        setImageResource(deckResource)
                        setOnClickListener(::onClickTile)
                    }

                    val tile = Tile(button, shuffledIcons.removeAt(0), deckResource)
                    tiles[button.tag.toString()] = tile
                    gridLayout.addView(button)
                }
            }
        }
    }

    private fun onClickTile(v: View) {
        val tile = tiles[v.tag] ?: return
        if (tile.revealed) return

        matchedPair.push(tile)
        tile.button.setImageResource(tile.tileResource)
        tile.revealed = true

        val matchResult = logic.process { tile.tileResource }
        onGameChangeStateListener(MemoryGameEvent(matchedPair.toList(), matchResult))

        when (matchResult) {
            GameStates.Matching -> { }

            GameStates.Match -> {
                if (logic.isGameFinished()) {
                    animatePairedButton(matchedPair[0].button) {}
                    animatePairedButton(matchedPair[1].button) {
                        Toast.makeText(gridLayout.context, "Gratulacje! Gra zakończona!", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    matchedPair.forEach { it.button.isEnabled = false }
                    animatePairedButton(matchedPair[0].button) {}
                    animatePairedButton(matchedPair[1].button) {}
                }
                matchedPair.clear()
            }

            GameStates.NoMatch -> {
                Handler().postDelayed({
                    animateWrongPair(matchedPair[0].button, matchedPair[1].button) {
                        matchedPair.forEach {
                            it.button.setImageResource(deckResource)
                            it.revealed = false
                        }
                        matchedPair.clear()
                    }
                }, 1000)
            }

            GameStates.Finished -> {
                Toast.makeText(gridLayout.context, "Gratulacje! Gra zakończona!", Toast.LENGTH_SHORT).show()
            }
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
            tile.button.setImageResource(if (tile.revealed) tile.tileResource else deckResource)
        }
    }

    private fun animatePairedButton(button: ImageButton, action: Runnable) {
        val set = AnimatorSet()
        val random = Random()

        button.pivotX = random.nextFloat() * button.width
        button.pivotY = random.nextFloat() * button.height

        val rotation = ObjectAnimator.ofFloat(button, "rotation", 1080f)
        val scalingX = ObjectAnimator.ofFloat(button, "scaleX", 1f, 4f)
        val scalingY = ObjectAnimator.ofFloat(button, "scaleY", 1f, 4f)
        val fade = ObjectAnimator.ofFloat(button, "alpha", 1f, 0f)

        set.startDelay = 500
        set.duration = 2000
        set.interpolator = DecelerateInterpolator()
        set.playTogether(rotation, scalingX, scalingY, fade)

        set.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animator: Animator) {}

            override fun onAnimationEnd(animator: Animator) {
                button.scaleX = 1f
                button.scaleY = 1f
                button.alpha = 0.0f
                action.run()
            }

            override fun onAnimationCancel(animator: Animator) {}

            override fun onAnimationRepeat(animator: Animator) {}
        })
        set.start()
    }

    private fun animateWrongPair(button1: ImageButton, button2: ImageButton, action: Runnable) {
        val set = AnimatorSet()

        val shake1 = ObjectAnimator.ofFloat(button1, "rotation", -10f, 10f, -10f, 10f, 0f)
        val shake2 = ObjectAnimator.ofFloat(button2, "rotation", -10f, 10f, -10f, 10f, 0f)

        set.playTogether(shake1, shake2)
        set.duration = 500

        set.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {}

            override fun onAnimationEnd(animation: Animator) {
                action.run()
            }

            override fun onAnimationCancel(animation: Animator) {}

            override fun onAnimationRepeat(animation: Animator) {}
        })
        set.start()
    }
}

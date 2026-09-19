package com.xxmassdeveloper.mpchartexample.design

import android.content.Context
import android.content.Intent
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.ViewGroup
import android.widget.FrameLayout
import com.github.mikephil.charting.utils.Utils
import com.xxmassdeveloper.mpchartexample.databinding.ActivityDesignBinding
import com.xxmassdeveloper.mpchartexample.databinding.DesignCardBinding
import com.xxmassdeveloper.mpchartexample.notimportant.DemoBase

/** Shows every chart type in the Nightfall design, in its dark or light variant. */
class DesignActivity : DemoBase() {

    private lateinit var binding: ActivityDesignBinding
    private var dark = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dark = intent.getBooleanExtra(EXTRA_DARK, true)
        val only = intent.getIntExtra(EXTRA_ONLY, -1)
        val from = intent.getIntExtra(EXTRA_FROM, 0)
        val theme = if (dark) Nightfall.dark else Nightfall.light
        title = if (dark) "All chart types, dark" else "All chart types, light"

        binding = ActivityDesignBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.root.setBackgroundColor(theme.stage)

        val cards = DesignCharts.all(this, theme)
        val shown = if (only in cards.indices) listOf(cards[only]) else cards.drop(from.coerceIn(0, cards.size - 1))
        for (card in shown) {
            val cardBinding = DesignCardBinding.inflate(layoutInflater, binding.cards, false)
            cardBinding.card.background = GradientDrawable().apply {
                cornerRadius = Utils.convertDpToPixel(18f)
                setColor(theme.card)
                setStroke(1, theme.grid)
            }
            cardBinding.tvTitle.text = card.title
            cardBinding.tvTitle.setTextColor(theme.text)
            cardBinding.tvSubtitle.text = card.subtitle
            cardBinding.tvSubtitle.setTextColor(theme.muted)
            if (card.delta != null) {
                cardBinding.tvDelta.visibility = android.view.View.VISIBLE
                cardBinding.tvDelta.text = card.delta
                cardBinding.tvDelta.setTextColor(Nightfall.green)
                cardBinding.tvDelta.background = GradientDrawable().apply {
                    cornerRadius = Utils.convertDpToPixel(8f)
                    setColor(Nightfall.withAlpha(Nightfall.green, 30))
                }
            }
            cardBinding.chartContainer.addView(
                card.chart,
                FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            )
            binding.cards.addView(cardBinding.root)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menu.add(0, MENU_TOGGLE, 0, if (dark) "Light variant" else "Dark variant")
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == MENU_TOGGLE) {
            startActivity(intent(this, dark = !dark, only = intent.getIntExtra(EXTRA_ONLY, -1)))
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun saveToGallery() {}

    companion object {
        private const val EXTRA_DARK = "dark"
        private const val EXTRA_ONLY = "only"
        private const val EXTRA_FROM = "from"
        private const val MENU_TOGGLE = 1

        fun intent(context: Context, dark: Boolean, only: Int = -1): Intent =
            Intent(context, DesignActivity::class.java).putExtra(EXTRA_DARK, dark).putExtra(EXTRA_ONLY, only)
    }
}

package com.fiap.ariachallenge.export

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import androidx.core.content.res.ResourcesCompat
import com.fiap.ariachallenge.R
import com.fiap.ariachallenge.ui.lider.analises.AnaliseUiState
import com.fiap.ariachallenge.util.formatCurrencyCompact
import java.io.File
import java.io.FileOutputStream
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

object AriaAnalyticsPdfExporter {

    private const val PAGE_W = 595
    private const val PAGE_H = 842
    private const val MARGIN = 48f

    private object Palette {
        val primary = Color.parseColor("#1A2540")
        val accent = Color.parseColor("#C87D0E")
        val pageBg = Color.parseColor("#FAF9F7")
        val textPrimary = Color.parseColor("#1A1A2E")
        val textSecondary = Color.parseColor("#5A5040")
        val textTertiary = Color.parseColor("#7A7060")
        val rule = Color.parseColor("#E6E3DC")
    }

    fun export(context: Context, state: AnaliseUiState): File {
        val dir = File(context.cacheDir, "exports").apply { mkdirs() }
        val file = File(dir, "aria_analytics_${System.currentTimeMillis()}.pdf")
        FileOutputStream(file).use { out ->
            val doc = PdfDocument()
            try {
                PdfLayout(context, state, doc).build()
                doc.writeTo(out)
            } finally {
                doc.close()
            }
        }
        return file
    }

    private class PdfLayout(
        private val context: Context,
        private val state: AnaliseUiState,
        private val document: PdfDocument,
    ) {
        private var pageIndex = 0
        private var page: PdfDocument.Page? = null
        private var canvas: Canvas? = null
        private var y = MARGIN

        private val textColumnWidth: Int
            get() = (PAGE_W - 2 * MARGIN).toInt().coerceAtLeast(1)

        private val sectionPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Palette.textPrimary
            textSize = 13f
            typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
            letterSpacing = 0.02f
        }
        private val bodyPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Palette.textSecondary
            textSize = 11f
            typeface = Typeface.SANS_SERIF
        }
        private val smallPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Palette.textTertiary
            textSize = 9.5f
            typeface = Typeface.SANS_SERIF
        }
        private val metricPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Palette.primary
            textSize = 28f
            typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
        }

        fun build() {
            startFirstPageAfterHeader()
            metaBlock()
            roiHero()
            sectionTitle(context.getString(R.string.pdf_analytics_section_monthly))
            state.months.forEach { m ->
                bulletRow("${context.getString(m.monthRes)}\t\t${formatCurrencyCompact(m.value.toDouble())}")
            }
            sectionTitle(context.getString(R.string.pdf_analytics_section_projects))
            state.topProjects.forEach { p ->
                bulletRow("${p.title}  ·  ${formatCurrencyCompact(p.realizedReais)} / ${formatCurrencyCompact(p.targetReais)}")
            }
            sectionTitle(context.getString(R.string.pdf_analytics_section_trends))
            state.trends.forEach { t ->
                val arrow = if (t.up) "↑" else "↓"
                bulletRow(
                    "${context.getString(t.labelRes)}  $arrow  ${t.changeLabel}  (${t.percent}%)",
                )
            }
            if (state.emerging.isNotEmpty()) {
                sectionTitle(context.getString(R.string.pdf_analytics_section_emerging))
                state.emerging.forEach { e ->
                    paragraph("${e.title}\n${e.body}")
                }
            }
            sectionTitle(context.getString(R.string.pdf_analytics_section_ai_predictions))
            paragraph(state.aiPredictionTitle)
            state.aiPredictions.forEach { paragraph("• ${it.title}") }
            sectionTitle(context.getString(R.string.pdf_analytics_section_ai_recommendations))
            state.aiRecommendations.forEach { paragraph("• ${it.text}") }
            y += 8f
            paragraph(context.getString(R.string.pdf_analytics_footer_tagline), smallPaint)
            finishPage()
        }

        private fun startFirstPageAfterHeader() {
            finishPage()
            pageIndex++
            val info = PdfDocument.PageInfo.Builder(PAGE_W, PAGE_H, pageIndex).create()
            val p = document.startPage(info)
            page = p
            canvas = p.canvas
            canvas!!.drawColor(Palette.pageBg)
            drawHeader(canvas!!)
            y = 118f
        }

        private fun metaBlock() {
            val stamp = ZonedDateTime.now().format(
                DateTimeFormatter.ofPattern("dd MMM yyyy · HH:mm", Locale.getDefault()),
            )
            paragraph(context.getString(R.string.pdf_analytics_generated, stamp), smallPaint)
            paragraph(
                "${context.getString(R.string.pdf_analytics_focus_section)}: ${tabLabel()}",
                smallPaint,
            )
            y += 8f
            horizontalRule()
            y += 16f
        }

        private fun tabLabel(): String = when (state.selectedTab) {
            0 -> context.getString(R.string.analyses_tab_roi)
            1 -> context.getString(R.string.analyses_tab_trends)
            else -> context.getString(R.string.analyses_tab_ai)
        }

        private fun roiHero() {
            ensureSpace(90f)
            val c = canvas!!
            val boxPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.WHITE
                style = Paint.Style.FILL
            }
            c.drawRoundRect(MARGIN, y, PAGE_W - MARGIN, y + 72f, 8f, 8f, boxPaint)
            val border = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Palette.rule
                style = Paint.Style.STROKE
                strokeWidth = 1f
            }
            c.drawRoundRect(MARGIN, y, PAGE_W - MARGIN, y + 72f, 8f, 8f, border)
            val labelPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Palette.textTertiary
                textSize = 9f
                typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
                letterSpacing = 0.08f
            }
            c.drawText(
                context.getString(R.string.pdf_analytics_roi_total).uppercase(Locale.getDefault()),
                MARGIN + 16f,
                y + 22f,
                labelPaint,
            )
            val roiText = state.roiTotalFormatted
            c.drawText(roiText, MARGIN + 16f, y + 52f, metricPaint)
            val delta = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
                textSize = 12f
                color = Palette.accent
                typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
            }
            val deltaText = "+${state.roiDeltaPercent}% · ${context.getString(R.string.pdf_analytics_roi_delta)}"
            c.drawText(deltaText, MARGIN + 200f, y + 48f, delta)
            y += 88f
        }

        private fun sectionTitle(title: String) {
            y += 8f
            ensureSpace(36f)
            val c = canvas!!
            c.drawText(title.uppercase(Locale.getDefault()), MARGIN, y + 14f, sectionPaint)
            y += 22f
            c.drawLine(MARGIN, y, PAGE_W - MARGIN, y, Paint().apply {
                color = Palette.accent
                strokeWidth = 2f
                isAntiAlias = true
            })
            y += 14f
        }

        private fun bulletRow(text: String) {
            paragraph("· $text", bodyPaint)
        }

        private fun paragraph(text: String, paint: TextPaint = bodyPaint) {
            val layout = StaticLayout.Builder.obtain(text, 0, text.length, paint, textColumnWidth)
                .setAlignment(Layout.Alignment.ALIGN_NORMAL)
                .setLineSpacing(0f, 1.2f)
                .setIncludePad(false)
                .build()
            ensureSpace(layout.height.toFloat() + 12f)
            val c = canvas!!
            c.save()
            c.translate(MARGIN, y)
            layout.draw(c)
            c.restore()
            y += layout.height + 10f
        }

        private fun horizontalRule() {
            ensureSpace(4f)
            canvas!!.drawLine(MARGIN, y, PAGE_W - MARGIN, y, Paint().apply {
                color = Palette.rule
                strokeWidth = 0.75f
                isAntiAlias = true
            })
            y += 6f
        }

        private fun ensureSpace(needed: Float) {
            if (y + needed <= PAGE_H - MARGIN) return
            finishPage()
            startContinuationPage()
        }

        private fun startContinuationPage() {
            pageIndex++
            val info = PdfDocument.PageInfo.Builder(PAGE_W, PAGE_H, pageIndex).create()
            val p = document.startPage(info)
            page = p
            canvas = p.canvas
            canvas!!.drawColor(Palette.pageBg)
            y = MARGIN
            canvas!!.drawText(
                context.getString(R.string.pdf_analytics_continuation),
                MARGIN,
                y + 10f,
                smallPaint,
            )
            y += 28f
        }

        private fun drawHeader(c: Canvas) {
            val accentBar = Paint().apply { color = Palette.accent; style = Paint.Style.FILL }
            c.drawRect(0f, 0f, PAGE_W.toFloat(), 12f, accentBar)
            val nav = Paint().apply { color = Palette.primary; style = Paint.Style.FILL }
            c.drawRect(0f, 12f, PAGE_W.toFloat(), 100f, nav)
            val logo = loadLogoBitmap(context, 56)
            val textStart = if (logo != null) {
                c.drawBitmap(logo, MARGIN, 28f, null)
                MARGIN + 68f
            } else {
                MARGIN
            }
            val brand = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.WHITE
                textSize = 28f
                typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
            }
            c.drawText("ARIA", textStart, 60f, brand)
            val sub = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.argb(230, 255, 255, 255)
                textSize = 11f
                typeface = Typeface.SANS_SERIF
            }
            c.drawText(context.getString(R.string.pdf_analytics_subtitle), textStart, 82f, sub)
        }

        private fun finishPage() {
            page?.let { document.finishPage(it) }
            page = null
            canvas = null
        }
    }

    private fun loadLogoBitmap(context: Context, sizePx: Int): Bitmap? {
        val d = ResourcesCompat.getDrawable(context.resources, R.mipmap.ic_launcher, context.theme)
            ?: return null
        val w = if (d.intrinsicWidth > 0) d.intrinsicWidth else sizePx
        val h = if (d.intrinsicHeight > 0) d.intrinsicHeight else sizePx
        val bmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        val cvs = Canvas(bmp)
        d.setBounds(0, 0, w, h)
        d.draw(cvs)
        return Bitmap.createScaledBitmap(bmp, sizePx, sizePx, true)
    }
}

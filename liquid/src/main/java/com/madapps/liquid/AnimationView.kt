package com.madapps.liquid

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PointF
import android.graphics.RectF
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View

class AnimationView @JvmOverloads constructor(
  context: Context,
  attrs: AttributeSet? = null,
  defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

  private var pullHeight: Int = 0
  private var pullDelta: Int = 0
  private var widthOffset: Float = 0.toFloat()

  private var aniStatus = AnimatorStatus.PULL_DOWN

  private var backPaint: Paint? = null
  private var ballPaint: Paint? = null
  private var outPaint: Paint? = null
  private var path: Path? = null

  private var radius: Int = 0
  private var localWidth: Int = 0
  private var localHeight: Int = 0

  private var refreshStart = 90
  private var refreshStop = 90
  private var targetDegree = 270
  private var isStart = true
  private var isRefreshing = true

  private var lastHeight: Int = 0

  private val relHeight: Int get() = (spriDeta * (1 - relRatio)).toInt()

  private val springDelta: Int get() = (pullDelta * sprRatio).toInt()

  private var start1: Long = 0
  private var stop: Long = 0
  private var spriDeta: Int = 0

  private val relRatio: Float
    get() {
      if (System.currentTimeMillis() >= stop) {
        springUp()
        return 1f
      }
      val ratio = (System.currentTimeMillis() - start1) / REL_DRAG_DUR.toFloat()
      return Math.min(ratio, 1f)
    }
  private var sprStart: Long = 0
  private var sprStop: Long = 0

  private val sprRatio: Float
    get() {
      if (System.currentTimeMillis() >= sprStop) {
        popBall()
        return 1f
      }
      val ratio = (System.currentTimeMillis() - sprStart) / SPRING_DUR.toFloat()
      return Math.min(1f, ratio)
    }
  private var popStart: Long = 0
  private var popStop: Long = 0

  private val popRatio: Float
    get() {
      if (System.currentTimeMillis() >= popStop) {
        startOutCir()
        return 1f
      }

      val ratio = (System.currentTimeMillis() - popStart) / POP_BALL_DUR.toFloat()
      return Math.min(ratio, 1f)
    }
  private var outStart: Long = 0
  private var outStop: Long = 0

  private val outRatio: Float
    get() {
      if (System.currentTimeMillis() >= outStop) {
        aniStatus = AnimatorStatus.REFRESHING
        isRefreshing = true
        return 1f
      }
      val ratio = (System.currentTimeMillis() - outStart) / OUTER_DUR.toFloat()
      return Math.min(ratio, 1f)
    }
  private var doneStart: Long = 0
  private var doneStop: Long = 0

  private val doneRatio: Float
    get() {
      if (System.currentTimeMillis() >= doneStop) {
        aniStatus = AnimatorStatus.STOP
        if (onViewAniDone != null) {
          onViewAniDone!!.viewAniDone()
        }
        return 1f
      }

      val ratio = (System.currentTimeMillis() - doneStart) / DONE_DUR.toFloat()
      return Math.min(ratio, 1f)
    }

  private var onViewAniDone: OnViewAniDone? = null

  internal enum class AnimatorStatus {
    PULL_DOWN,
    DRAG_DOWN,
    REL_DRAG,
    SPRING_UP,
    POP_BALL,
    OUTER_CIR,
    REFRESHING,
    DONE,
    STOP;

    override fun toString(): String = when (this) {
      PULL_DOWN -> "pull down"
      DRAG_DOWN -> "drag down"
      REL_DRAG -> "release drag"
      SPRING_UP -> "spring up"
      POP_BALL -> "pop ball"
      OUTER_CIR -> "outer circle"
      REFRESHING -> "refreshing..."
      DONE -> "done!"
      STOP -> "stop"
    }
  }

  init {
    initView(context)
  }

  private fun initView(context: Context) {

    pullHeight = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP, 100f,
        context.resources.displayMetrics
    )
        .toInt()
    pullDelta = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP, 50f,
        context.resources.displayMetrics
    )
        .toInt()
    widthOffset = 0.5f
    backPaint = Paint()
    backPaint!!.isAntiAlias = true
    backPaint!!.style = Paint.Style.FILL
    backPaint!!.color = -0x746f51

    ballPaint = Paint()
    ballPaint!!.isAntiAlias = true
    ballPaint!!.color = -0x1
    ballPaint!!.style = Paint.Style.FILL

    outPaint = Paint()
    outPaint!!.isAntiAlias = true
    outPaint!!.color = -0x1
    outPaint!!.style = Paint.Style.STROKE
    outPaint!!.strokeWidth = 5f


    path = Path()

  }

  override fun onMeasure(
    widthMeasureSpec: Int,
    heightMeasureSpec: Int
  ) {
    var tempHeightMeasureSpec = heightMeasureSpec
    val height = View.MeasureSpec.getSize(tempHeightMeasureSpec)
    if (height > pullDelta + pullHeight) {
      tempHeightMeasureSpec = View.MeasureSpec.makeMeasureSpec(
          pullDelta + pullHeight,
          View.MeasureSpec.getMode(tempHeightMeasureSpec)
      )
    }
    super.onMeasure(widthMeasureSpec, tempHeightMeasureSpec)
  }

  override fun onLayout(
    changed: Boolean,
    left: Int,
    top: Int,
    right: Int,
    bottom: Int
  ) {
    super.onLayout(changed, left, top, right, bottom)
    if (changed) {
      radius = height / 6
      localWidth = width
      localHeight = height
      when {
        localHeight < pullHeight -> aniStatus = AnimatorStatus.PULL_DOWN
      }
      when {
        aniStatus == AnimationView.AnimatorStatus.PULL_DOWN && localHeight >= pullHeight -> aniStatus =
            AnimatorStatus.DRAG_DOWN
      }

    }
  }

  override fun onDraw(canvas: Canvas) {
    super.onDraw(canvas)
    when (aniStatus) {
      AnimationView.AnimatorStatus.PULL_DOWN -> canvas.drawRect(
          0f, 0f, localWidth.toFloat(),
          localHeight.toFloat(), backPaint!!
      )
      AnimationView.AnimatorStatus.REL_DRAG, AnimationView.AnimatorStatus.DRAG_DOWN -> drawDrag(
          canvas
      )
      AnimationView.AnimatorStatus.SPRING_UP -> {
        drawSpring(canvas, springDelta)
        invalidate()
      }
      AnimationView.AnimatorStatus.POP_BALL -> {
        drawPopBall(canvas)
        invalidate()
      }
      AnimationView.AnimatorStatus.OUTER_CIR -> {
        drawOutCir(canvas)
        invalidate()
      }
      AnimationView.AnimatorStatus.REFRESHING -> {
        drawRefreshing(canvas)
        invalidate()
      }
      AnimationView.AnimatorStatus.DONE -> {
        drawDone(canvas)
        invalidate()
      }
      AnimationView.AnimatorStatus.STOP -> drawDone(canvas)
    }

    if (aniStatus == AnimatorStatus.REL_DRAG) {
      val params = layoutParams
      var height: Int
      do {
        height = relHeight
      } while (height == lastHeight && relRatio != 1f)
      lastHeight = height
      params.height = pullHeight + height
      requestLayout()
    }

  }

  private fun drawDrag(canvas: Canvas) {
    canvas.drawRect(0f, 0f, localWidth.toFloat(), pullHeight.toFloat(), backPaint!!)
    path!!.reset()
    path!!.moveTo(0f, pullHeight.toFloat())
    path!!.quadTo(
        widthOffset * localWidth, (pullHeight + (localHeight - pullHeight) * 2).toFloat(),
        localWidth.toFloat(), pullHeight.toFloat()
    )
    canvas.drawPath(path!!, backPaint!!)
  }

  private fun drawSpring(
    canvas: Canvas,
    springDelta: Int
  ) {
    path!!.reset()
    path!!.moveTo(0f, 0f)
    path!!.lineTo(0f, pullHeight.toFloat())
    path!!.quadTo(
        (localWidth / 2).toFloat(), (pullHeight - springDelta).toFloat(),
        localWidth.toFloat(), pullHeight.toFloat()
    )
    path!!.lineTo(localWidth.toFloat(), 0f)
    canvas.drawPath(path!!, backPaint!!)
    val curH = pullHeight - springDelta / 2
    if (curH > pullHeight - pullDelta / 2) {
      val leftX = (localWidth / 2 - 2 * radius + sprRatio * radius).toInt()
      path!!.reset()
      path!!.moveTo(leftX.toFloat(), curH.toFloat())
      path!!.quadTo(
          (localWidth / 2).toFloat(), curH - radius.toFloat() * sprRatio * 2f,
          (localWidth - leftX).toFloat(), curH.toFloat()
      )
      canvas.drawPath(path!!, ballPaint!!)
    } else {
      canvas.drawArc(
          RectF(
              (localWidth / 2 - radius).toFloat(), (curH - radius).toFloat(),
              (localWidth / 2 + radius).toFloat(), (curH + radius).toFloat()
          ),
          180f, 180f, true, ballPaint!!
      )
    }

  }

  private fun drawPopBall(canvas: Canvas) {
    path!!.reset()
    path!!.moveTo(0f, 0f)
    path!!.lineTo(0f, pullHeight.toFloat())
    path!!.quadTo(
        (localWidth / 2).toFloat(), (pullHeight - pullDelta).toFloat(),
        localWidth.toFloat(), pullHeight.toFloat()
    )
    path!!.lineTo(localWidth.toFloat(), 0f)
    canvas.drawPath(path!!, backPaint!!)

    val cirCentStart = pullHeight - pullDelta / 2
    val cirCenY = (cirCentStart - radius.toFloat() * 2f * popRatio).toInt()

    canvas.drawArc(
        RectF(
            (localWidth / 2 - radius).toFloat(), (cirCenY - radius).toFloat(),
            (localWidth / 2 + radius).toFloat(), (cirCenY + radius).toFloat()
        ),
        180f, 360f, true, ballPaint!!
    )

    if (popRatio < 1) {
      drawTail(canvas, cirCenY, cirCentStart + 1, popRatio)
    } else {
      canvas.drawCircle(
          (localWidth / 2).toFloat(),
          cirCenY.toFloat(),
          radius.toFloat(),
          ballPaint!!
      )
    }

  }

  private fun drawTail(
    canvas: Canvas,
    centerY: Int,
    bottom: Int,
    fraction: Float
  ) {
    val bezier1w = (localWidth / 2 + radius * 3 / 4 * (1 - fraction)).toInt()
    val start = PointF((localWidth / 2 + radius).toFloat(), centerY.toFloat())
    val bezier1 = PointF(bezier1w.toFloat(), bottom.toFloat())
    val bezier2 = PointF((bezier1w + radius / 2).toFloat(), bottom.toFloat())

    path!!.reset()
    path!!.moveTo(start.x, start.y)
    path!!.quadTo(
        bezier1.x, bezier1.y,
        bezier2.x, bezier2.y
    )
    path!!.lineTo(localWidth - bezier2.x, bezier2.y)
    path!!.quadTo(
        localWidth - bezier1.x, bezier1.y,
        localWidth - start.x, start.y
    )
    canvas.drawPath(path!!, ballPaint!!)
  }

  private fun drawOutCir(canvas: Canvas) {
    path!!.reset()
    path!!.moveTo(0f, 0f)
    path!!.lineTo(0f, pullHeight.toFloat())
    path!!.quadTo(
        (localWidth / 2).toFloat(), pullHeight - (1 - outRatio) * pullDelta,
        localWidth.toFloat(), pullHeight.toFloat()
    )
    path!!.lineTo(localWidth.toFloat(), 0f)
    canvas.drawPath(path!!, backPaint!!)
    val innerY = pullHeight - pullDelta / 2 - radius * 2
    canvas.drawCircle((localWidth / 2).toFloat(), innerY.toFloat(), radius.toFloat(), ballPaint!!)
  }

  private fun drawRefreshing(canvas: Canvas) {
    canvas.drawRect(0f, 0f, localWidth.toFloat(), localHeight.toFloat(), backPaint!!)
    val innerY = pullHeight - pullDelta / 2 - radius * 2
    canvas.drawCircle((localWidth / 2).toFloat(), innerY.toFloat(), radius.toFloat(), ballPaint!!)
    val outerR = radius + 10
    refreshStart += if (isStart) 3 else 10
    refreshStop += if (isStart) 10 else 3
    refreshStart %= 360
    refreshStop %= 360
    var swipe = refreshStop - refreshStart
    swipe = if (swipe < 0) swipe + 360 else swipe
    canvas.drawArc(
        RectF(
            (localWidth / 2 - outerR).toFloat(), (innerY - outerR).toFloat(),
            (localWidth / 2 + outerR).toFloat(), (innerY + outerR).toFloat()
        ),
        refreshStart.toFloat(), swipe.toFloat(), false, outPaint!!
    )
    if (swipe >= targetDegree) {
      isStart = false
    } else if (swipe <= 10) {
      isStart = true
    }
    if (!isRefreshing) {
      applyDone()

    }
  }

  fun setRefreshing(isFresh: Boolean) {
    isRefreshing = isFresh
  }

  private fun drawDone(canvas: Canvas) {
    val beforeColor = outPaint!!.color
    if (doneRatio < 0.3) {
      canvas.drawRect(0f, 0f, localWidth.toFloat(), localHeight.toFloat(), backPaint!!)
      val innerY = pullHeight - pullDelta / 2 - radius * 2
      canvas.drawCircle((localWidth / 2).toFloat(), innerY.toFloat(), radius.toFloat(), ballPaint!!)
      val outerR = (radius.toFloat() + 10f + 10 * doneRatio / 0.3f).toInt()
      val afterColor = Color.argb(
          (0xff * (1 - doneRatio / 0.3f)).toInt(), Color.red(beforeColor),
          Color.green(beforeColor), Color.blue(beforeColor)
      )
      outPaint!!.color = afterColor
      canvas.drawArc(
          RectF(
              (localWidth / 2 - outerR).toFloat(), (innerY - outerR).toFloat(),
              (localWidth / 2 + outerR).toFloat(), (innerY + outerR).toFloat()
          ),
          0f, 360f, false, outPaint!!
      )
    }
    outPaint!!.color = beforeColor
    if (doneRatio >= 0.3 && doneRatio < 0.7) {
      canvas.drawRect(0f, 0f, localWidth.toFloat(), localHeight.toFloat(), backPaint!!)
      val fraction = (doneRatio - 0.3f) / 0.4f
      val startCentY = pullHeight - pullDelta / 2 - radius * 2
      val curCentY = (startCentY + (pullDelta / 2 + radius * 2) * fraction).toInt()
      canvas.drawCircle(
          (localWidth / 2).toFloat(),
          curCentY.toFloat(),
          radius.toFloat(),
          ballPaint!!
      )
      if (curCentY >= pullHeight - radius * 2) {
        drawTail(canvas, curCentY, pullHeight, 1 - fraction)
      }
    }
    if (!(doneRatio < 0.7 || doneRatio > 1)) {
      val fraction = (doneRatio - 0.7f) / 0.3f
      canvas.drawRect(0f, 0f, localWidth.toFloat(), localHeight.toFloat(), backPaint!!)
      val leftX =
        ((localWidth / 2).toFloat() - radius.toFloat() - 2f * radius.toFloat() * fraction).toInt()
      path!!.reset()
      path!!.moveTo(leftX.toFloat(), pullHeight.toFloat())
      path!!.quadTo(
          (localWidth / 2).toFloat(), pullHeight - radius * (1 - fraction),
          (localWidth - leftX).toFloat(), pullHeight.toFloat()
      )
      canvas.drawPath(path!!, ballPaint!!)
    }
  }

  fun releaseDrag() {
    start1 = System.currentTimeMillis()
    stop = start1 + REL_DRAG_DUR
    aniStatus = AnimatorStatus.REL_DRAG
    spriDeta = localHeight - pullHeight
    requestLayout()
  }

  private fun springUp() {
    sprStart = System.currentTimeMillis()
    sprStop = sprStart + SPRING_DUR
    aniStatus = AnimatorStatus.SPRING_UP
    invalidate()
  }

  private fun popBall() {
    popStart = System.currentTimeMillis()
    popStop = popStart + POP_BALL_DUR
    aniStatus = AnimatorStatus.POP_BALL
    invalidate()
  }

  private fun startOutCir() {
    outStart = System.currentTimeMillis()
    outStop = outStart + OUTER_DUR
    aniStatus = AnimatorStatus.OUTER_CIR
    refreshStart = 90
    refreshStop = 90
    targetDegree = 270
    isStart = true
    isRefreshing = true
    invalidate()
  }

  private fun applyDone() {
    doneStart = System.currentTimeMillis()
    doneStop = doneStart + DONE_DUR
    aniStatus = AnimatorStatus.DONE
  }

  fun setOnViewAniDone(onViewAniDone: OnViewAniDone) {
    this.onViewAniDone = onViewAniDone
  }

  interface OnViewAniDone {
    fun viewAniDone()
  }

  fun setAniBackColor(color: Int) {
    backPaint!!.color = color
  }

  fun setAniForeColor(color: Int) {
    ballPaint!!.color = color
    outPaint!!.color = color
    setBackgroundColor(color)
  }

  fun setRadius(smallTimes: Int) {
    radius = localHeight / smallTimes
  }

  companion object {
    private const val REL_DRAG_DUR: Long = 200

    private const val SPRING_DUR: Long = 200

    private const val POP_BALL_DUR: Long = 300

    private const val OUTER_DUR: Long = 200

    private const val DONE_DUR: Long = 1000
  }
}
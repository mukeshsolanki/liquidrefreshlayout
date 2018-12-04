package com.madapps.liquid

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import android.widget.FrameLayout

class LiquidRefreshLayout @JvmOverloads constructor(
  context: Context,
  attrs: AttributeSet? = null,
  defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

  private var headerBackColor = -0x746f51
  private var headerForeColor = -0x1
  private var headerCircleSmaller = 6

  private var pullHeight: Float = 0.toFloat()
  private var headerHeight: Float = 0.toFloat()
  private var childView: View? = null
  private var header: AnimationView? = null

  private var isRefreshing: Boolean = false

  private var touchStartY: Float = 0.toFloat()

  private var touchCurY: Float = 0.toFloat()

  private var upBackAnimator: ValueAnimator? = null
  private var upTopAnimator: ValueAnimator? = null

  private val decelerateInterpolator = DecelerateInterpolator(10f)

  private var onRefreshListener: OnRefreshListener? = null

  init {
    init(context, attrs)
  }

  private fun init(
    context: Context,
    attrs: AttributeSet?
  ) {

    if (childCount > 1) {
      throw RuntimeException("you can only attach one child")
    }
    setAttrs(attrs)
    pullHeight = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP, 150f,
        context.resources.displayMetrics
    )
    headerHeight = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP, 100f,
        context.resources.displayMetrics
    )

    this.post {
      childView = getChildAt(0)
      addHeaderView()
    }
  }

  private fun setAttrs(attrs: AttributeSet?) {
    val a = context.obtainStyledAttributes(attrs, R.styleable.LiquidRefreshLayout)

    headerBackColor = a.getColor(R.styleable.LiquidRefreshLayout_AniBackColor, headerBackColor)
    headerForeColor = a.getColor(R.styleable.LiquidRefreshLayout_AniForeColor, headerForeColor)
    headerCircleSmaller = a.getInt(
        R.styleable.LiquidRefreshLayout_CircleSmaller,
        headerCircleSmaller
    )

    a.recycle()
  }

  private fun addHeaderView() {
    header = AnimationView(context)
    val params = FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0)
    params.gravity = Gravity.TOP
    header!!.layoutParams = params

    addViewInternal(header!!)
    header!!.setAniBackColor(headerBackColor)
    header!!.setAniForeColor(headerForeColor)
    header!!.setRadius(headerCircleSmaller)

    setUpChildAnimation()
  }

  private fun setUpChildAnimation() {
    if (childView == null) {
      return
    }
    upBackAnimator = ValueAnimator.ofFloat(pullHeight, headerHeight)
    upBackAnimator!!.addUpdateListener { animation ->
      val `val` = animation.animatedValue as Float
      if (childView != null) {
        childView!!.translationY = `val`
      }
    }
    upBackAnimator!!.duration = REL_DRAG_DUR
    upTopAnimator = ValueAnimator.ofFloat(headerHeight, 0f)
    upTopAnimator!!.addUpdateListener { animation ->
      var value = animation.animatedValue as Float
      value *= decelerateInterpolator.getInterpolation(value / headerHeight)
      if (childView != null) {
        childView!!.translationY = value
      }
      header!!.layoutParams.height = value.toInt()
      header!!.requestLayout()
    }
    upTopAnimator!!.duration = BACK_TOP_DUR

    header!!.setOnViewAniDone(object : AnimationView.OnViewAniDone {
      override fun viewAniDone() {
        upTopAnimator!!.start()
      }
    })
  }

  private fun addViewInternal(child: View) {
    super.addView(child)
  }

  override fun addView(child: View) {
    if (childCount >= 1) {
      throw RuntimeException("you can only attach one child")
    }

    childView = child
    super.addView(child)
    setUpChildAnimation()
  }

  private fun canChildScrollUp(): Boolean {
    return if (childView == null) {
      false
    } else childView!!.canScrollVertically(-1)
  }

  override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
    if (isRefreshing) {
      return true
    }
    when (ev.action) {
      MotionEvent.ACTION_DOWN -> {
        touchStartY = ev.y
        touchCurY = touchStartY
      }
      MotionEvent.ACTION_MOVE -> {
        val curY = ev.y
        val dy = curY - touchStartY
        if (dy > 0 && !canChildScrollUp()) {
          return true
        }
      }
    }
    return super.onInterceptTouchEvent(ev)
  }

  @SuppressLint("ClickableViewAccessibility")
  override fun onTouchEvent(event: MotionEvent): Boolean {
    if (isRefreshing) {
      return super.onTouchEvent(event)
    }

    when (event.action) {
      MotionEvent.ACTION_MOVE -> {
        touchCurY = event.y
        var dy = touchCurY - touchStartY
        dy = Math.min(pullHeight * 2, dy)
        dy = Math.max(0f, dy)


        if (childView != null) {
          val offsetY = decelerateInterpolator.getInterpolation(dy / 2f / pullHeight) * dy / 2
          childView!!.translationY = offsetY

          header!!.layoutParams.height = offsetY.toInt()
          header!!.requestLayout()
        }


        return true
      }

      MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
        if (childView != null) {
          if (childView!!.translationY >= headerHeight) {
            upBackAnimator!!.start()
            header!!.releaseDrag()
            isRefreshing = true
            if (onRefreshListener != null) {
              onRefreshListener!!.refreshing()
            }
          } else {
            val height = childView!!.translationY
            val backTopAni = ValueAnimator.ofFloat(height, 0f)
            backTopAni.addUpdateListener { animation ->
              var value = animation.animatedValue as Float
              value *= decelerateInterpolator.getInterpolation(value / headerHeight)
              if (childView != null) {
                childView!!.translationY = value
              }
              header!!.layoutParams.height = value.toInt()
              header!!.requestLayout()
            }
            backTopAni.duration = (height * BACK_TOP_DUR / headerHeight).toLong()
            backTopAni.start()
          }
        }
        return true
      }
      else -> return super.onTouchEvent(event)
    }
  }

  fun finishRefreshing() {
    if (onRefreshListener != null) {
      onRefreshListener!!.completeRefresh()
    }
    isRefreshing = false
    header!!.setRefreshing(false)
    finishLoading()
  }

  private fun finishLoading() {
    val height = childView!!.translationY
    val backTopAni = ValueAnimator.ofFloat(height, 0f)
    backTopAni.addUpdateListener { animation ->
      var variable = animation.animatedValue as Float
      variable *= decelerateInterpolator.getInterpolation(variable / headerHeight)
      if (childView != null) {
        childView!!.translationY = variable
      }
      header!!.layoutParams.height = variable.toInt()
      header!!.requestLayout()
    }
    backTopAni.duration = (height * BACK_TOP_DUR / headerHeight).toLong()
    backTopAni.start()
  }

  fun setOnRefreshListener(onRefreshListener: OnRefreshListener) {
    this.onRefreshListener = onRefreshListener
  }

  interface OnRefreshListener {
    fun completeRefresh()

    fun refreshing()
  }

  companion object {
    private const val BACK_TOP_DUR: Long = 600
    private const val REL_DRAG_DUR: Long = 200
  }
}
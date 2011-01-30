package the.trav.zomdroid

import android.graphics.Paint

object Color {
  def color(a:Int, r:Int, g:Int, b:Int) = {
    val paint = new Paint()
    paint.setARGB(a,r,g,b)
    paint
  }

  def white = color(255, 255,255,255)
  def green = color(255, 0  ,255,0  )
  def red   = color(255, 255,0  ,0  )
  def darkRed=color(255, 100,0  ,0  )
  def orange =color(255, 250,150,0  )
  def brown = color(255, 110,80 ,30 )
}

case class Color(a:Int, r:Int, g:Int, b:Int)

case object Black extends Color(255, 0,0,0)
case object White extends Color(255, 255,255,255)
case object Orange extends Color(255, 250,150,0)
case object Red extends Color(255, 255,0,0)
case object Green extends Color(255, 0,255,0)
case object Blue extends Color(255, 0,0,255)
case object Gray extends Color(255, 200,200,200)
case object DarkGray extends Color(255, 150,150,150)
case object Yellow extends Color(255, 255,255,0  )


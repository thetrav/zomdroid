package the.trav.zomdroid

import the.trav.zomdroid._
import Constants._
import android.graphics._

case class Hex(c:Coord) {
  def i = c.x
  def j = c.y
  def size = canvasSize.y / playerViewCircumference
  def r = size / 2
  def w = size
  def height = 3 * r / twoTimesSinSixtyDeg
  def h = height.asInstanceOf[Int]
  def yOff = if(j % 2 == 0) 1 else 0
  def xCoord = (2*i- yOff + 1) * w/2
  def yCoord = (j + 2/3) * height
  def x = xCoord.asInstanceOf[Int] + xOffset
  def y = yCoord.asInstanceOf[Int] + yOffset
  val paint = new Paint() 

  def setPaint(c:Color) {
    paint.setARGB(c.a, c.r, c.g, c.b)
  }

  def rect = new RectF(x, y, x+w, y+h)

  def drawCoords(c:Canvas) {
    if(showCoords) {
      setPaint(Orange)
      c.drawText((i+","+j).asInstanceOf[String], x, y, paint)
    }
  }

  def fillCircle(c:Canvas, color:Color) {
    setPaint(color)
    c.drawOval(rect, paint)
  }

  def fillHalfCircle(c:Canvas, color:Color) {
    setPaint(color)
    c.drawOval(new RectF(x+w/4, y+h/4, x+w/2+w/4, y+h/2+h/4), paint)
  }
}

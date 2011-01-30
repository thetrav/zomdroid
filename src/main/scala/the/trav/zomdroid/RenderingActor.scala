package the.trav.zomdroid

import actors.Actor
import actors.Actor._
import Constants._
import android.graphics.{Paint, Canvas}
import RenderingOperations.DrawFunction

object GlobalDrawingFunctions {
  var drawFunction = (canvas:Canvas) => {}
  var refreshFunction = () => {}

  def resize(w:Int, h:Int) {
    val frame = Coord(w, h)
    if(w > h) {
      dimensions = LandscapeDimensions(frame)
    } else {
      dimensions = PortraitDimensions(frame)
    }
  }
}

case class DisplayScene(function:DrawFunction)
case class DisplayTemporaryScene(function:DrawFunction, t:Long)

class RenderingActor() extends Actor {
  def draw(function:(Canvas) => Unit) {
    GlobalDrawingFunctions.drawFunction = function
    GlobalDrawingFunctions.refreshFunction()
  }

  def act() {
    loop {
      receive {
        case DisplayScene(f) => {
          log("received DisplayScene command")
          draw(f)
          Actors.inputActor ! AcceptInput
        }
        case DisplayTemporaryScene(f, t) => {
          log("received temporary scene to display for" + t + " milliseconds")
          draw(f)
          Yield.doYield()
          Thread.sleep(t)
          log("finished displaying, checking for next to display")
        }
        case _ => exit()
      }
    }
  }
}

object RenderingOperations {

  type DrawFunction = Canvas => Unit

  def drawStarved(canvas:Canvas) {

  }

  def drawAttack(canvas:Canvas, gameState:Game, coord:Coord) {
    log("drawing attack at:"+coord)
    drawScene(canvas, gameState)
    Hex(coord).fillCircle(canvas, Yellow)
  }

  def drawScene(canvas:Canvas, gameState:Game) {
    BoardAndroidRenderer(gameState.board).draw(canvas)
    drawStatus(canvas, gameState)
    if(showControls) drawControls(canvas, gameState)
  }

  def drawStatus(canvas:Canvas, gameState:Game) {
    val paint = new Paint()
    paint.setARGB(255,255,255,255)
    val offset = dimensions match {
      case LandscapeDimensions(_) => Coord(canvasSize.x, 10)
      case PortraitDimensions(_) => Coord(10, canvasSize.y)
      case _ => ORIGIN
    }
    canvas.translate(offset.x, offset.y)
    canvas.drawText(gameState.message, 10, 10, paint)
    BarWidget(Color.red, Color.red, Color.darkRed, "HP", gameState.board.player.health, playerStartHealth, Coord(10,30)).draw(canvas)
    BarWidget(Color.orange, Color.orange, Color.brown, "Food", gameState.board.player.food, playerStartFood, Coord(10,50)).draw(canvas)
    canvas.translate(-offset.x, -offset.y)
  }

  def drawControls(canvas:Canvas, gameState:Game) {
    val paint = Color.green
    def drawLine(x1:Double, y1:Double, x2:Double, y2:Double) {
      canvas.drawLine(x1.asInstanceOf[Float], y1.asInstanceOf[Float], x2.asInstanceOf[Float], y2.asInstanceOf[Float], paint)
    }
    drawLine(0, northInputBoundary, canvasSize.x, northInputBoundary)
    drawLine(0, southInputBoundary, canvasSize.x, southInputBoundary)
    drawLine(0, statusNorthBoundary, canvasSize.x, statusNorthBoundary)
    drawLine(westInputBoundary, 0, westInputBoundary, canvasSize.y)
  }
}

case class BarWidget(labelColor:Paint, fillColor:Paint, borderColor:Paint, label:String, value:Int, max:Int, pos:Coord) {
  def draw(canvas:Canvas) {
    canvas.translate(pos.x, pos.y)
    canvas.drawText(label+value, 10, 25, labelColor)
    val percentage = value.toDouble / max.toDouble
    val x = 82
    val y = 12
    val width = 100
    val height = 15
    canvas.drawRect(x, y, x+width, y+height, borderColor)
    canvas.drawRect(x+1, y+1, x-1+(width*percentage).asInstanceOf[Int], y-1+height, fillColor)
    canvas.translate(-pos.x, -pos.y)
  }
}
package the.trav.zomdroid

import _root_.android.app.Activity
import _root_.android.os.Bundle
import _root_.android.widget.TextView
import _root_.android.view._
import _root_.android.graphics._
import _root_.android.content._
import the.trav.zomdroid.Constants._
import the.trav.zomdroid._

case class DrawView(context: Context, parent:MainActivity) extends View(context) {
  override def onDraw(canvas:Canvas) {
    parent.board.draw(canvas)
  }

  override def onTouchEvent(event:MotionEvent) = {
    val action = event.getAction()
    val x = event.getX()
    val y = event.getY()
    action match {
      case MotionEvent.ACTION_UP => {
        //work out direction and trigger action
      }
      case _ => {
        //do nothing
      }
    }
    invalidate()
    true
  }
}

class MainActivity extends Activity {

  var numZombies = initialZombies

  var board = newBoard(numZombies)
  
  override def onCreate(savedInstanceState: Bundle) {
    super.onCreate(savedInstanceState)
    setContentView(DrawView(getApplicationContext(), this)) 
    
  }

  def showMessage(msg:String) {
    println("message="+msg)
  }

  def handleCommand(direction:Option[Direction]) {
    direction match {
      case Some(d) => {
        board.movePlayer(d) match {
          case Moved(b:Board) => {
            board = b
          }
          case Eaten => {
            showMessage("You were eaten by a zombie")
            numZombies += difficultyDecrease
            board = newBoard(numZombies)
          }
          case Starved => {
            showMessage("Starved to death")
            numZombies += difficultyDecrease
            board = newBoard(numZombies)
          }
          case Escaped => {
            showMessage("You Escaped")
            numZombies += difficultyIncrease
            board = newBoard(numZombies, board.player)
          }
          case Blocked => {
            showMessage("Cannot Move There")
          }
        }
      }
      case None => {

      }
    }
  }

  def addZombie(b:Board, ignore:Int) = {
    findFreeCoord(b, gridSize, gridSize, 10) match {
      case Some(c) => {
        b.addZombie(c)
      }
      case None => {
        b
      }
    }
  }

  def newBoard(numZombies:Int) = {
    addZombies(numZombies, Board.newBoard(gridSize, gridSize))
  }

  def addZombies(n:Int, b:Board) = {
    (0 until n).foldLeft[Board](b)(addZombie)
  }

  def newBoard(numZombies:Int, p:Player) = {
    addZombies(numZombies, Board.newBoard(gridSize, gridSize, p))
  }

  def findFreeCoord(b:Board, xMax:Int, yMax:Int, retry:Int):Option[Coord] = {
    val coord = Coord(random.nextInt(xMax), random.nextInt(yMax))

    if (b.player == coord || b.exit == coord || b.hasZombie(coord)) {
      if(retry > 0) findFreeCoord(b, xMax, yMax, retry-1) else None
    } else {
      Some(coord)
    }
  }
}

trait MoveResult
case class Moved(b:Board) extends MoveResult
case object Eaten extends MoveResult
case object Escaped extends MoveResult
case object Blocked extends MoveResult
case object Starved extends MoveResult

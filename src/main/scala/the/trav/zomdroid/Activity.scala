package the.trav.zomdroid

import _root_.android.app.Activity
import _root_.android.os.Bundle
import _root_.android.view._
import _root_.android.graphics._
import _root_.android.content._
import the.trav.zomdroid.Constants._
import the.trav.zomdroid._

case class DrawView(context: Context, parent:MainActivity) extends View(context) {

  var mousePos = Coord(0,0)
  override def onDraw(canvas:Canvas) {
    parent.board.draw(canvas)
    val paint = new Paint()
    paint.setARGB(255,255,255,255)
    canvas.drawText("mousePos:"+mousePos, 10, 310, paint)
//Hex(Coord(0,0)).fillCircle(canvas, Red)
 //Hex(Coord(1,0)).fillCircle(canvas, White)
  //Hex(Coord(-1,0)).fillCircle(canvas, Blue)
   //Hex(Coord(0,1)).fillCircle(canvas, Green)
  //Hex(Coord(0,-1)).fillCircle(canvas, Orange)
  
    }

  override def onTouchEvent(event:MotionEvent) = {
    val action = event.getAction()
    val x = event.getX()
    val y = event.getY()
    action match {
      case MotionEvent.ACTION_UP => {
        //work out direction and trigger action
        println("action_up at:"+x+","+y)
        mousePos = Coord(x.asInstanceOf[Int], y.asInstanceOf[Int])
        parent.handleAction(x, y)
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

  def handleCommand(d:Direction) {
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

  def handleAction(x:Float, y:Float) {
    findDir(x, y) match {
      case Some(dir) => {
        handleCommand(dir)
      }
      case None => {
        //do nothing
      }
    }
  }

  def findDir(x:Float, y:Float) = {
    val westInputBoundary = 145
    val northInputBoundary = 100
    val southInputBoundary = 200
    val statusNorthBoundary = 300
    if (y < statusNorthBoundary) {
      if (x < westInputBoundary) {
        if ( y < northInputBoundary) {
          Some(NW)
        } else if (y > southInputBoundary) {
          Some(SW)
        } else {
          Some(W)
        }
      } else {
        if (y < northInputBoundary) {
          Some(NE)
        } else if (y > southInputBoundary) {
          Some(SE)
        } else {
          Some(E)
        }
      }
    } else {
      None
    }
  }
}

trait MoveResult
case class Moved(b:Board) extends MoveResult
case object Eaten extends MoveResult
case object Escaped extends MoveResult
case object Blocked extends MoveResult
case object Starved extends MoveResult

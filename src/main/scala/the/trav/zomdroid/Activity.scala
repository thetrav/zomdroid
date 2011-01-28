package the.trav.zomdroid

import _root_.android.app.Activity
import _root_.android.os.Bundle
import _root_.android.view._
import _root_.android.graphics._
import _root_.android.content._
import the.trav.zomdroid.Constants._
import the.trav.zomdroid._

class DrawView(context: Context, parent:MainActivity) extends View(context) {

  override def onDraw(canvas:Canvas) {
    val frame = Coord(canvas.getWidth, canvas.getHeight)
    if(frame.x > frame.y) {
      dimensions = LandscapeDimensions(frame)
    } else {
      dimensions = PortraitDimensions(frame)
    }
    parent.draw(canvas)
  }

  override def onTouchEvent(event:MotionEvent) = {
    val action = event.getAction()
    val x = event.getX()
    val y = event.getY()
    action match {
      case MotionEvent.ACTION_UP => {
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

  var consoleText = ">"

  var refreshView = () => {}
  
  override def onCreate(savedInstanceState: Bundle) {
    super.onCreate(savedInstanceState)
    val view = new DrawView(getApplicationContext(), this)
    setContentView(view)
    refreshView = () => {view.invalidate()}
  }

  override def onCreateOptionsMenu(menu:Menu) = {
    val inflater = getMenuInflater()
    inflater.inflate(R.menu.game_menu, menu)
    true
  }

  override def onOptionsItemSelected(item:MenuItem) = {
    item.getItemId() match {
      case R.id.toggle_coords => {
        showCoords = !showCoords
      }
      case R.id.toggle_controls => {
        showControls = !showControls
      }
      case _ => {

      }
    }
    refreshView()
    true
  }

  def showMessage(msg:String) {
    consoleText = ">" + msg
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
  
  def draw(canvas:Canvas) {
    board.draw(canvas)
    drawStatus(canvas)
    if(showControls) drawControls(canvas)
  }

  def drawStatus(canvas:Canvas) {
    val paint = new Paint()
    paint.setARGB(255,255,255,255)
    val offset = dimensions match {
      case LandscapeDimensions(_) => Coord(canvasSize.x, 10)
      case PortraitDimensions(_) => Coord(10, canvasSize.y)
      case _ => ORIGIN
    }
    canvas.translate(offset.x, offset.y)
    canvas.drawText(consoleText, 10, 10, paint)
    BarWidget(Color.red, Color.red, Color.darkRed, "HP", board.player.health, playerStartHealth, Coord(10,30)).draw(canvas)
    BarWidget(Color.orange, Color.orange, Color.brown, "Food", board.player.food, playerStartFood, Coord(10,50)).draw(canvas)
    canvas.translate(-offset.x, -offset.y)
  }
  
  def drawControls(canvas:Canvas) {
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

trait MoveResult
case class Moved(b:Board) extends MoveResult
case object Eaten extends MoveResult
case object Escaped extends MoveResult
case object Blocked extends MoveResult
case object Starved extends MoveResult

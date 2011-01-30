package the.trav.zomdroid

import _root_.android.app.Activity
import _root_.android.os.Bundle
import _root_.android.view._
import _root_.android.graphics._
import _root_.android.content._
import the.trav.zomdroid.Constants._
import the.trav.zomdroid._

class DrawView(context: Context, parent:MainActivity) extends View(context) {

  override def onSizeChanged(w:Int, h:Int, oldW:Int, oldH:Int) {
    GlobalDrawingFunctions.resize(w, h)
  }

  override def onDraw(canvas:Canvas) {
    GlobalDrawingFunctions.drawFunction(canvas)
  }

  override def onTouchEvent(event:MotionEvent) = {
    val action = event.getAction()
    val x = event.getX()
    val y = event.getY()
    action match {
      case MotionEvent.ACTION_UP => {
        TouchInput.handleTouch(x, y)
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

  override def onCreate(savedInstanceState: Bundle) {
    super.onCreate(savedInstanceState)
    Actors.simulationActor ! SetGameStateCommand(Game.newBoard(initialZombies))
    val view = new DrawView(getApplicationContext(), this)
    setContentView(view)
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
    GlobalDrawingFunctions.refreshFunction()
    true
  }
}





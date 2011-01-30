package the.trav.zomdroid

import Constants._

object TouchInput {
  def handleTouch(x:Float, y:Float) {
    findDir(x, y) match {
      case Some(dir) => {
        Actors.simulationActor ! MoveCommand(dir)
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
}
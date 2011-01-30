package the.trav.zomdroid

import actors.Actor
import actors.Actor._
import Constants._
import android.util.Log

trait Command
case class MoveCommand(d:Direction) extends Command
case class SetGameStateCommand(game:Game) extends Command



class SimulationActor() extends Actor {
  var gameState:Option[Game] = None

  //Dodgey, will blow up if the game is not initialized, probably a symptom of relying on var
  def game = gameState match {
    case Some(g) => g
  }

  def act() {
    loop {
      receive {
        case SetGameStateCommand(game) => {
          gameState = Some(game)
          Actors.renderingActor ! DisplayScene(RenderingOperations.drawScene(_, game))
        }
        case MoveCommand(d) => {
          Log.i("ZOMDROID", "received player move command")
          gameState = Some(game.handleMoveCommand(d))
          Log.i("ZOMDROID", "sending current state to render")
          Actors.renderingActor ! DisplayScene(RenderingOperations.drawScene(_, gameState.get))
        }
        case _ => exit()
      }
    }
  }
}

//all state should be kept in this class
case class Game(board:Board, message:String) {


  def apply(b:Board) = Game(b, message)
  def apply(msg:String) = Game(board, msg)

  def showZombieMove(move:MoveResult) {
    move match {
      case Moved(b) => {
        Log.i("ZOMDROID", "showing zombie move")
        Actors.renderingActor ! DisplayTemporaryScene(RenderingOperations.drawScene(_, this(b)), zombieMoveTime)
      }
      case Attacked(c, _) => {
        Log.i("ZOMDROID", "showing zombie attack")
        Actors.renderingActor ! DisplayTemporaryScene(RenderingOperations.drawAttack(_, c), zombieMoveTime)
      }
    }

  }

  def simulateZombies():Game = {
    val steps = board.simulateZombies()
    steps.foreach(showZombieMove)
    if(board.player.food == 0) {
      Actors.renderingActor ! DisplayTemporaryScene(RenderingOperations.drawStarved, gameOverDisplayTime)
    }
    steps.head match {
      case Moved(b) => this(b)
      case Attacked(_, b) => {
        //Todo: check for player death here
        this(b)
      }
    }
  }

  def handleMoveCommand(d:Direction):Game = {
    val result = board.movePlayer(d)
    result match {
      case Moved(b:Board) => {
        val newState = this(b)
        Actors.renderingActor ! DisplayTemporaryScene(RenderingOperations.drawScene(_, newState), playerMoveTime)
        newState.simulateZombies()
      }
      case Attacked(coord:Coord, b:Board) => {
        val newState = this(b)
        Log.i("ZOMDROID", "sending attack animation frame")
        Actors.renderingActor ! DisplayTemporaryScene(RenderingOperations.drawAttack(_, coord-b.player.c), attackTime)
        Log.i("ZOMDROID", "simulating zombies")
        newState.simulateZombies()
      }
      case Blocked => {
        this("You cannot move there")
      }
      case Escaped => {
        this("you have escaped!  PLEASE REGISTER!")
      }
    }
  }
}

object Game {
  def newBoard(numZombies:Int) = {
    addZombies(numZombies, Board.newBoard(gridSize, gridSize))
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

  def addZombies(n:Int, b:Board) = {
    val board = (0 until n).foldLeft[Board](b)(addZombie)
    Game(board, "")
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


}

trait MoveResult
case class Moved(b:Board) extends MoveResult
case class Attacked(p:Coord, b:Board) extends MoveResult
case object Eaten extends MoveResult
case object Escaped extends MoveResult
case object Blocked extends MoveResult
case object Starved extends MoveResult

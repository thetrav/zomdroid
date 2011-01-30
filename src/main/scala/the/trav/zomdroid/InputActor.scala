package the.trav.zomdroid

import actors.Actor
import actors.Actor._

case object IgnoreInput
case object AcceptInput

class InputActor() extends Actor {
  def act() {
    loop {
      receive {
        case Some(d:Direction) => {
          this ! IgnoreInput
          Actors.simulationActor ! MoveCommand(d)
          waitForCompletion()
        }
        case None => {}
        case _ => exit()
      }
    }
  }

  def waitForCompletion() {
    var complete = false
    while (!complete) {
      receive {
        case AcceptInput => complete = true
        case EXIT_ACTORS => exit()
        case _ => {}
      }
    }
  }
}
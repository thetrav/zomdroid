package the.trav.zomdroid

import actors.Actor

case object EXIT_ACTORS

object Actors {
   def start(actors:Actor*) {
    actors.foreach((actor:Actor) => {actor.start})
  }

  val renderingActor = new RenderingActor
  val inputActor = new InputActor
  val simulationActor = new SimulationActor

  start(renderingActor, inputActor, simulationActor)
}
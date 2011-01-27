package the.trav.zomdroid

import java.util.Random

object Constants {

  val random = new Random(System.currentTimeMillis)

  val gridSize = 20
  val initialZombies = 20

  val difficultyIncrease = 2
  val difficultyDecrease = -3

  val foodUsedPerMove = 1

  val playerStartPos = Coord(0, gridSize/2)
  val playerStartFood = 500
  val playerStartHealth = 100

  val playerViewDistance = 4
  val playerViewCircumference = playerViewDistance * 2 - 1
  val zombieViewDistance = 3

  val zombieDamage = 5

  val showAllZombies = false
  val showCoords = false

  val twoTimesSinSixtyDeg = 2 * Math.sin(Math.toRadians(60))
  val frameSize = Coord(200, 300)
  val statusSize = Coord(200, frameSize.y)
  val canvasSize = Coord(frameSize.x, frameSize.y)

  val xOffset = canvasSize.x/2+25
  val yOffset = canvasSize.y/2

  val title = "zombocalypse"

}

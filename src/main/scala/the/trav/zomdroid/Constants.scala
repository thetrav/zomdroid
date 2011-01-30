package the.trav.zomdroid

import java.util.Random
import android.util.Log

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
  var showCoords = false
  var showControls = false

  //animation controls
  val zombieMoveTime = 100
  val playerMoveTime = zombieMoveTime
  val attackTime = 10
  val gameOverDisplayTime = 1000

  //sizes

  val twoTimesSinSixtyDeg = 2 * Math.sin(Math.toRadians(60))
  var dimensions:Dimensions = PortraitDimensions(Coord(200, 300))
  def frameSize = dimensions.frameSize
  def statusSize = dimensions.statusSize
  def canvasSize = dimensions.canvasSize


  def hexSize = dimensions.hexSize
  def hexRadius = dimensions.hexSize
  def hexWidth = dimensions.hexWidth
  def hexHeight = dimensions.hexHeight

  def xOffset = dimensions.xOffset
  def yOffset = dimensions.yOffset

  def westInputBoundary = canvasSize.x / 2
  def northInputBoundary = yOffset
  def southInputBoundary = yOffset + hexHeight
  def statusNorthBoundary = canvasSize.y

  def log(s:String) {
    Log.i("ZOMDROID", s)
  }
}

package the.trav.zomdroid

import Constants._

trait Dimensions {
  def frameSize:Coord
  def hexSize:Float
  def hexRadius:Float
  def hexWidth:Float
  def hexHeight:Float

  def canvasSize:Coord
  def statusSize:Coord

  def xOffset:Float
  def yOffset:Float
}

case class PortraitDimensions(frame:Coord) extends Dimensions {

  def frameSize = frame
  def hexSize = frameSize.x / playerViewCircumference
  def hexRadius = hexSize / 2
  def hexWidth = hexSize
  def hexHeight = (3 * hexRadius / twoTimesSinSixtyDeg).asInstanceOf[Float]

  def canvasSize = Coord(frameSize.x, (hexHeight * playerViewCircumference).asInstanceOf[Int])
  def statusSize = Coord(frameSize.x, frameSize.y - canvasSize.y)

  def xOffset = canvasSize.x/2-hexWidth
  def yOffset = canvasSize.y/2-hexHeight/2
}

case class LandscapeDimensions(frame:Coord) extends Dimensions {
  def frameSize = frame
  def hexSize = frameSize.y / playerViewCircumference
  def hexRadius = hexSize / 2
  def hexWidth = hexSize
  def hexHeight = (3 * hexRadius / twoTimesSinSixtyDeg).asInstanceOf[Float]

  def canvasSize = Coord((hexWidth * playerViewCircumference).asInstanceOf[Int], frameSize.y)
  def statusSize = Coord(frameSize.x, frameSize.y - canvasSize.y)

  def xOffset = canvasSize.x/2-hexWidth
  def yOffset = canvasSize.y/2-hexHeight
}
package the.trav.zomdroid

import android.graphics.Canvas
import Constants._

case class BoardAndroidRenderer(board:Board) {
  def draw(canvas:Canvas) {
    drawPlayerView(canvas)
    drawPlayer(canvas)
    if(showAllZombies) drawZombies(canvas)
  }

  def drawZombies(canvas:Canvas) {
    board.zombies.foreach((t:(Coord, Zombie)) => {
      val hex = Hex(t._1)
      hex.fillHalfCircle(canvas, Red)
      if(showCoords) hex.drawCoords(canvas)
    })
  }

  def drawPlayerView(canvas:Canvas) {
    def drawViewedTile(c:Coord) {
      val offsetCoord = c - board.player.c
      val hex = Hex(offsetCoord)
      hex.fillCircle(canvas, Gray)
      if(board.hasWall(c)) hex.fillCircle(canvas, board.walls(c).color)
      if(board.hasZombie(c)) hex.fillHalfCircle(canvas, Red)
      if(c == board.exit) hex.fillHalfCircle(canvas, Green)
      if(showCoords) hex.drawCoords(canvas)
    }
    board.player.getCircle(playerViewDistance).foreach(drawViewedTile)
  }

  def drawPlayer(canvas:Canvas) {
    val hex = Hex(Coord(0,0))
    hex.fillHalfCircle(canvas, Black)
    if(showCoords) hex.drawCoords(canvas)
  }
}
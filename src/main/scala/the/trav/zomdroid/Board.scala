package the.trav.zomdroid

import the.trav.zomdroid._
import Constants._
import android.graphics._

case class Player(c:Coord, food:Int, health:Int) {
  def apply(newPos:Coord) = Player(newPos, food, health)
  def apply(newPos:Coord, foodChange:Int, healthChange:Int) = Player(newPos, food+foodChange, health+healthChange)

  def getCircle(r:Int) = c.getCircle(r)
  def draw(canvas:Canvas) {
    val hex = Hex(c)
    hex.fillHalfCircle(canvas, Black)
    if(showCoords) hex.drawCoords(canvas)
  }

  def takeHit(damage:Int) = Player(c, food, health - damage)

  def go(d:Direction, n:Int) = c.go(d, n)
}
case class Zombie
case class Wall(color:Color)

object Board {
  def addColumnWalls(b:Board, row:Int) = {
    b.addWall(Coord(-1, row), DarkGray).addWall(Coord(gridSize, row), DarkGray)
  }

  def addRowWalls(b:Board, column:Int) = {
    b.addWall(Coord(column, -1), DarkGray).addWall(Coord(column, gridSize), DarkGray)
  }

  def newBoard(width:Int, height:Int):Board = {
    val player = Player(playerStartPos, playerStartFood, playerStartHealth)
    newBoard(width, height, player)
  }

  def newBoard(width:Int, height:Int, player:Player):Board = {
    val withoutWalls = Board(0, player(playerStartPos), Map[Coord, Zombie](), Coord(width-1, random.nextInt(height-1)), Map[Coord, Wall]())
    val withColumnWalls = (0 until width).foldLeft[Board](withoutWalls)(addColumnWalls)
    (0 until height).foldLeft[Board](withColumnWalls)(addRowWalls).addWall(Coord(-1,-1), DarkGray).addWall(Coord(gridSize, gridSize), DarkGray)
  }
}

case class Board(moves:Int, player:Player, zombies:Map[Coord, Zombie], exit:Coord, walls:Map[Coord, Wall]) {

  def apply(p:Player) = Board(moves+1, p, zombies, exit, walls)
  def apply(z:Map[Coord, Zombie]) = Board(moves, player, z, exit, walls)

  def direction(from:Coord, to:Coord) = {
    if(from.y == to.y && from.x < to.x) E else
    if(from.y == to.y && from.x > to.x) W else
    if(from.y > to.y && from.x < to.x) NE else
    if(from.y > to.y && from.x >= to.x) NW else
    if(from.y < to.y && from.x < to.x) SE else
    if(from.y < to.y && from.x >= to.x) SW else
    NO_DIRECTION
  }

  def draw(canvas:Canvas) {
    drawPlayerView(canvas)
    drawPlayer(canvas)
    if(showAllZombies) drawZombies(canvas)
  }

  def hasZombie(c:Coord) = zombies.contains(c)
  def hasWall(c:Coord) = walls.contains(c)
  def hasPlayer(c:Coord) = player.c == c

  def addZombie(c:Coord) = this(zombies + (c -> Zombie()))
  def addWall(c:Coord, color:Color) = Board(moves, player, zombies, exit, walls + (c->Wall(color)))

  def drawZombies(canvas:Canvas) {
    zombies.foreach((t:(Coord, Zombie)) => {
      val hex = Hex(t._1)
      hex.fillHalfCircle(canvas, Red)
      if(showCoords) hex.drawCoords(canvas)
    })
  }

  def drawPlayerView(canvas:Canvas) {
    def drawViewedTile(c:Coord) {
      val hex = Hex(c)
      hex.fillCircle(canvas, Gray)
      if(hasWall(c)) hex.fillCircle(canvas, walls(c).color)
      if(hasZombie(c)) hex.fillHalfCircle(canvas, Red)
      if(c == exit) hex.fillHalfCircle(canvas, Green)
      if(showCoords) hex.drawCoords(canvas)
    }
    player.getCircle(playerViewDistance).foreach(drawViewedTile)
  }

  def drawPlayer(canvas:Canvas) {
    player.draw(canvas)
  }

  def movePlayer(d:Direction):MoveResult = {
    val newPos = player.go(d, 1)
    if(hasZombie(newPos)) {
      attackZombie(newPos).simulateZombies()
    } else if (newPos == exit) {
      Escaped
    } else if (hasWall(newPos)) {
      Blocked
    } else if (player.food == 1){
      Starved
    } else {
      this(player(newPos, -foodUsedPerMove, 0)).simulateZombies()
    }
  }

  def attackZombie(newPos:Coord) = this(zombies - newPos)

  def attackPlayer() = Board(moves, player.takeHit(zombieDamage), zombies, exit, walls)

  def moveZombie(c:Coord, d:Direction) = {
    c.getCircle(zombieViewDistance).find((pos:Coord)=> hasPlayer(pos)) match {
      case Some(_) => {
        val newPos = c.go(d, 1)
        if(hasWall(newPos)) {
          this
        } else if(hasPlayer(newPos)) {
          attackPlayer()
        } else if(hasZombie(newPos)) {
          this
        } else {
          this(zombies - c + (newPos-> Zombie()))
        }
      }
      case None => this
    }
  }

  def simulateZombies() = {
    def moveZombie(b:Board, z:Coord) = {
      b.moveZombie(z, direction(z, b.player.c))
    }

    val b = zombies.keySet.foldLeft[Board](this)(moveZombie)
    if (player.health <= 0) Eaten else Moved(b)
  }
}

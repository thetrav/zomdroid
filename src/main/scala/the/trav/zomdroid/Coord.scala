package the.trav.zomdroid

import the.trav.zomdroid._

trait Direction
case object NE extends Direction
case object E extends Direction
case object SE extends Direction
case object SW extends Direction
case object W extends Direction
case object NW extends Direction
case object NO_DIRECTION extends Direction

object Coord {
  def getCircle(center:Coord, radius:Int): List[Coord] = {
    if(radius == 1) List(center) else {
      val many = for(pos <- adjacentPositions(center)) yield {
        center :: getCircle(pos, radius-1)
      }
      for(branch <- many; pos <- branch) yield pos
    }
  }

  def adjacentPositions(pos:Coord) = {
    if (pos.isEvenRow) {
      pos(-1,-1) :: pos(0,-1) :: pos(1,0) :: pos(0,1) :: pos(-1,1) :: pos(-1,0) :: List[Coord]()
    } else {
      pos(0,-1) :: pos(1,-1) :: pos(1,0) :: pos(1,1) :: pos(0,1) :: pos(-1,0) :: List[Coord]()
    }
  }
}

case class Coord(x:Int, y:Int) {

  def apply(xMod:Int, yMod:Int) = this + Coord(xMod, yMod)

  def +(c:Coord)  = Coord(x + c.x, y + c.y)

  def isEvenRow = y % 2 == 0


  def go(d:Direction, n:Int):Coord = {
    val neighbor = this + direction(d)
    if(n == 1) neighbor else neighbor + neighbor.go(d, n-1)
  }

  def getCircle(r:Int) = Coord.getCircle(this, r)

  def direction(d:Direction) = d match {
      case NE => new Coord(if(isEvenRow) 0 else 1, -1)
      case E => new Coord(1, 0)
      case SE => new Coord(if(isEvenRow) 0 else 1, 1)
      case SW => new Coord(if(isEvenRow) -1 else 0, 1)
      case W => new Coord(-1, 0)
      case NW => new Coord(if(isEvenRow) -1 else 0, -1)
      case NO_DIRECTION => ORIGIN
  }
}

case object ORIGIN extends Coord(0,0)

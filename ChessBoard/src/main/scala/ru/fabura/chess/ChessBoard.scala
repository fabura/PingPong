package ru.fabura.chess

/** Created by bulat.fattahov 2013 */
case class ChessBoard(col: Int, row: Int, figures: Set[(Cell, Figure)]) {
  /**
   * The Set of Cells, which are unreachable by figures
   */
  lazy val freeCells = figures.foldLeft(ChessBoard.entireBoard(col, row)) {
    case (cells, (cell, figure)) => cells.filterNot(figure.canReach(cell))
  }.view

  /**
   * Will this `figure` threaten other figures from the `cell`?
   */
  private def isAbleToPlace(figure: Figure)(cell: Cell): Boolean = !figures.exists(a => figure.canReach(cell)(a._1))

  protected def getLastCellWithTheSameFigure(figure: Figure): Option[Cell] = {
    val l = figures.filter(_._2 == figure)
    if (l.isEmpty) {
      None
    } else {
      Option(l.maxBy(_._1)._1)
    }
  }

  /**
   * Places figure after all same figures and returns the Seq of possible ChessBoard.
   */
  def placeFigureAtTheEnd(figure: Figure): Seq[ChessBoard] = {
    val lastCellWithTheSameFigure = getLastCellWithTheSameFigure(figure).getOrElse(Cell(0, 0))
    val freeCellsLocal = this.freeCells
    freeCellsLocal
      .filter(_ > lastCellWithTheSameFigure)
      .filter(isAbleToPlace(figure))
      .map(cell =>
      new ChessBoard(col, row, figures + (cell -> figure)) {
        override lazy val freeCells = freeCellsLocal.filterNot(figure.canReach(cell))

        override protected def getLastCellWithTheSameFigure(fig: Figure): Option[Cell] = {
          fig match {
            case f if f == figure => Option(cell)
          }
        } orElse super.getLastCellWithTheSameFigure(figure)

      }
      )
  }
}

object ChessBoard {
  def empty(col: Int, row: Int) = ChessBoard(col, row, figures = Set.empty)

  import scala.collection.mutable.{Map => MMap}

  private val allBoards: MMap[(Int, Int), Seq[Cell]] = MMap.empty

  def entireBoard(col: Int, row: Int): Seq[Cell] = allBoards.getOrElseUpdate(col -> row,
    for (x <- 1 to col; y <- 1 to row) yield Cell(x, y))
}

case class Cell(x: Int, y: Int) extends Ordered[Cell] {
  def compare(that: Cell): Int = that match {
    case Cell(tx, ty) if tx > this.x || (tx == this.x && ty > this.y) => -1
    case Cell(tx, ty) if tx == this.x && ty == this.y => 0
    case _ => 1
  }
}

sealed abstract class Figure {
  def canReach(from: Cell)(to: Cell): Boolean = from == to
}

object Figure {
  // It is used only for performance needs.
  def weight(f: Figure) = f match {
    case King => 5
    case Knight => 4
    case Bishop => 3
    case Rook => 2
    case Queen => 1
  }

  def getByName(str: String) = str match {
    case "K" => King
    case "Q" => Queen
    case "B" => Bishop
    case "N" => Knight
    case "R" => Rook
  }
}

case object Queen extends Figure {
  override def canReach(from: Cell)(to: Cell) = super.canReach(from)(to) || from.x == to.x || from.y == to.y || math.abs(from.x - to.x) == math.abs(from.y - to.y)
}

case object Rook extends Figure {
  override def canReach(from: Cell)(to: Cell) = super.canReach(from)(to) || from.x == to.x || from.y == to.y
}

case object Knight extends Figure {
  override def canReach(from: Cell)(to: Cell) = {
    val deltaX = math.abs(from.x - to.x)
    val deltaY = math.abs(from.y - to.y)
    super.canReach(from)(to) || (deltaX == 2 && deltaY == 1) || (deltaX == 1 && deltaY == 2)
  }
}

case object Bishop extends Figure {
  override def canReach(from: Cell)(to: Cell) = super.canReach(from)(to) || math.abs(from.x - to.x) == math.abs(from.y - to.y)
}

case object King extends Figure {
  override def canReach(from: Cell)(to: Cell) = super.canReach(from)(to) || math.abs(from.x - to.x) <= 1 && math.abs(from.y - to.y) <= 1
}
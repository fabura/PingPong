package ru.fabura.chess

/** Created by bulat.fattahov 2013 */
case class ChessBoard(col: Int, row: Int, figures: Set[(Cell, Figure)]) {
  /**
   * The Set of Cells, which are unreachable by figures
   */
  def freeCells = figures.foldLeft(ChessBoard.getAllBoard(col, row)) {
    case (cells, (cell, figure)) => cells.filterNot(figure.canReach(cell))
  }

  /**
   * Will this `figure` threaten other figures from the `cell`?
   */
  private def isAbleToPlace(figure: Figure)(cell: Cell): Boolean = !figures.exists(a=> figure.canReach(cell)(a._1))

  /**
   * Returns the Set of possible ChessBoards.
   */
  def placeFigure(figure: Figure): Set[ChessBoard] =
    freeCells
      .filter(isAbleToPlace(figure))
      .map(cell => ChessBoard(col, row, figures + ChessBoard.getTuple(cell, figure))
      ).toSet
}

object ChessBoard {
  def empty(col: Int, row: Int) = ChessBoard(col, row, figures = Set.empty)

  import scala.collection.mutable.{Map => MMap}

  private val cellFigureMap: MMap[(Cell, Figure), (Cell, Figure)] = MMap.empty

  def getTuple(cell: Cell, figure: Figure) = cellFigureMap.getOrElseUpdate(cell -> figure, cell -> figure)

  private val allBoards: MMap[(Int, Int), Seq[Cell]] = MMap.empty

  def getAllBoard(col: Int, row: Int): Seq[Cell] = allBoards.getOrElseUpdate(col -> row,
    for (x <- 1 to col; y <- 1 to row) yield Cell(x, y))
}

case class Cell(x: Int, y: Int)

sealed abstract class Figure {
  def canReach(cell1: Cell)(cell2: Cell): Boolean = cell1 == cell2
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
    case "Kn" => Knight
    case "R" => Rook
  }
}

case object Queen extends Figure {
  override def canReach(cell1: Cell)(cell2: Cell) = super.canReach(cell1)(cell2) || cell1.x == cell2.x || cell1.y == cell2.y || math.abs(cell1.x - cell2.x) == math.abs(cell1.y - cell2.y)
}

case object Rook extends Figure {
  override def canReach(cell1: Cell)(cell2: Cell) = super.canReach(cell1)(cell2) || cell1.x == cell2.x || cell1.y == cell2.y
}

case object Knight extends Figure {
  override def canReach(cell1: Cell)(cell2: Cell) = {
    val deltaX = math.abs(cell1.x - cell2.x)
    val deltaY = math.abs(cell1.y - cell2.y)
    super.canReach(cell1)(cell2) || (deltaX == 2 && deltaY == 1) || (deltaX == 1 && deltaY == 2)
  }
}

case object Bishop extends Figure {
  override def canReach(cell1: Cell)(cell2: Cell) = super.canReach(cell1)(cell2) || math.abs(cell1.x - cell2.x) == math.abs(cell1.y - cell2.y)
}

case object King extends Figure {
  override def canReach(cell1: Cell)(cell2: Cell) = super.canReach(cell1)(cell2) || math.abs(cell1.x - cell2.x) <= 1 && math.abs(cell1.y - cell2.y) <= 1
}
package ru.fabura.chess

import java.util.concurrent.atomic.AtomicInteger

/** Created by bulat.fattahov 2013 */
object ChessBoardMain extends App {
  if (args.isEmpty) {
    println("Usage: ChessBoardMain 6 9 K[ing] 2 Q[ueen] 1 B[ishop] 1 Kn[ight] 1 R[ook] 1")
    sys.exit()
  }

  val rows = args(0).toInt
//  val rows = 3
  val cols = args(1).toInt
//  val cols = 3

  val figures: Seq[Figure] = getFiguresFromArgs

//    val figures = (King :: King :: Queen :: Bishop :: Rook :: Knight :: Nil) sortBy Figure.weight
//    val figures = (Queen :: Knight:: Nil) sortBy Figure.weight

  val board = ChessBoard.empty(rows, cols)

  println(placementsOf(figures, board).size)


  def placementsOf(figures: Seq[Figure], onBoard: ChessBoard): Set[ChessBoard] = {
    figures match {
      case Nil => Set(onBoard)
      case last :: Nil =>
        onBoard.placeFigure(last)
      case head :: tail => onBoard.placeFigure(head).foldLeft(Set.empty[ChessBoard]) {
        case (set, b) => set ++ placementsOf(tail, b)
      }
    }
  }

  def getFiguresFromArgs = Seq(args.drop(2)
    .grouped(2)
    .map(arr => Figure.getByName(arr(0)) -> arr(1).toInt)
    .flatMap {
    case (figure, count) => Seq.fill(count)(figure)
  }.toSeq.sortBy(Figure.weight): _*)
}

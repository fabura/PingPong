package ru.fabura.chess

/** Created by bulat.fattahov 2013 */
object ChessBoardMain extends App {
  if (args.isEmpty) {
    println("Usage: ChessBoardMain 6 9 K[ing] 2 Q[ueen] 1 B[ishop] 1 [k]N[ight] 1 R[ook] 1")
    sys.exit()
  }

  val rows = args(0).toInt
  val cols = args(1).toInt

  val figures: Seq[Figure] = getFiguresFromArgs

  val board = ChessBoard.empty(rows, cols)

  println(countOfPlacements(figures, board))


  def countOfPlacements(figures: Seq[Figure], onBoard: ChessBoard): Int = {
    figures match {
      case Nil => 1
      case last :: Nil =>
        onBoard.placeFigureAtTheEnd(last).size
      case head :: tail => onBoard.placeFigureAtTheEnd(head).par.map(countOfPlacements(tail, _)).fold(0)(_ + _)
    }
  }

  def getFiguresFromArgs = Seq(args.drop(2)
    .grouped(2)
    .map(arr => Figure.getByName(arr(0)) -> arr(1).toInt)
    .flatMap {
    case (figure, count) => Seq.fill(count)(figure)
  }.toSeq.sortBy(Figure.weight): _*)
}

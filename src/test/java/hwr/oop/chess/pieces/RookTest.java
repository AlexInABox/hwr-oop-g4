package hwr.oop.chess.pieces;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import hwr.oop.chess.Color;
import hwr.oop.chess.Position;
import hwr.oop.chess.board.ChessBoard;
import java.util.Arrays;
import java.util.List;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RookTest {

  private ChessBoard board;

  @BeforeEach
  void setup() {
    board = new ChessBoard();
  }

  @Test
  void testWhiteRookConstructor() {
    Position position = new Position(0, 0);
    Piece whiteRook = new Rook(Color.WHITE, position, board);
    SoftAssertions.assertSoftly(
        softly -> {
          softly.assertThat(whiteRook.getColor()).isEqualTo(Color.WHITE);
          softly.assertThat(whiteRook.getPosition()).isEqualTo(position);
          softly.assertThat(whiteRook.getSymbol()).isEqualTo('R');
        });
  }

  @Test
  void testBlackRookConstructor() {
    Position position = new Position(0, 0);
    Piece blackRook = new Rook(Color.BLACK, position, board);
    SoftAssertions.assertSoftly(
        softly -> {
          softly.assertThat(blackRook.getColor()).isEqualTo(Color.BLACK);
          softly.assertThat(blackRook.getPosition()).isEqualTo(position);
          softly.assertThat(blackRook.getSymbol()).isEqualTo('r');
        });
  }

  @Test
  void testRookMove_successful() throws IllegalMoveException {
    Position position = new Position(2, 2);
    Position targetPosition = new Position(2, 5);
    Piece rook = new Rook(Color.WHITE, position, board);
    board.setPieceAtPosition(rook.getPosition(), rook);

    rook.moveTo(targetPosition);
    assertThat(rook.getPosition()).isEqualTo(targetPosition);
    assertThat(board.getPieceAtPosition(position)).isNull();
    assertThat(board.getPieceAtPosition(new Position(2, 3))).isNull();
  }

  @Test
  void testRookMove_fail() {
    Position position = new Position(2, 2);
    Position targetPosition = new Position(3, 4);
    Piece rook = new Rook(Color.WHITE, position, board);
    board.setPieceAtPosition(rook.getPosition(), rook);

    IllegalMoveException exception =
        assertThrows(IllegalMoveException.class, () -> rook.moveTo(targetPosition));
    String expectedMessage = "Illegal move";
    assertThat(exception.getMessage()).contains(expectedMessage);
    assertThat(rook.getPosition()).isEqualTo(position);

    assertThat(board.getPieceAtPosition(targetPosition)).isNull();
    assertThat(board.getPieceAtPosition(position)).isEqualTo(rook);
  }

  @Test
  void testRookMove_failObstacle() {
    Position position = new Position(2, 2);
    Position obstaclePosition = new Position(3, 2); // Position occupied by another piece
    Position targetPosition = new Position(5, 2);
    Piece rook = new Rook(Color.WHITE, position, board);
    Piece obstacle = new Pawn(Color.WHITE, obstaclePosition, board);
    board.setPieceAtPosition(rook.getPosition(), rook);
    board.setPieceAtPosition(obstacle.getPosition(), obstacle);

    IllegalMoveException exception =
        assertThrows(IllegalMoveException.class, () -> rook.moveTo(targetPosition));
    String expectedMessage = "Illegal move";
    assertThat(exception.getMessage()).contains(expectedMessage);
    assertThat(rook.getPosition()).isEqualTo(position);
  }

  @Test
  void equals_IdenticalPieces() {
    Piece piece1 = new Rook(Color.WHITE, new Position(0, 0), board);
    Piece piece2 = new Rook(Color.WHITE, new Position(0, 0), board);
    assertThat(piece1.equals(piece2)).isTrue();
  }

  @Test
  @SuppressWarnings("EqualsWithItself")
  void equals_sameInstance() {
    Piece piece = new Rook(Color.WHITE, new Position(0, 0), board);
    assertThat(piece.equals(piece)).isTrue();
  }

  @Test
  @SuppressWarnings("ConstantConditions")
  void equals_InstanceNull() {
    Piece piece = new Rook(Color.WHITE, new Position(0, 0), board);
    assertThat(piece.equals(null)).isFalse();
  }

  @Test
  @SuppressWarnings("EqualsBetweenInconvertibleTypes")
  void equals_DifferentClass() {
    Piece piece = new Rook(Color.WHITE, new Position(0, 0), board);
    assertThat(piece.equals("String")).isFalse();
  }

  @Test
  @SuppressWarnings("EqualsBetweenInconvertibleTypes")
  void equals_DifferentPieces() {
    Piece piece1 = new Rook(Color.WHITE, new Position(0, 0), board);
    Piece piece2 = new King(Color.WHITE, new Position(0, 0), board);
    assertThat(piece1.equals(piece2)).isFalse();
  }

  @Test
  void hashCode_IdenticalPieces() {
    Piece piece1 = new Rook(Color.WHITE, new Position(0, 0), board);
    Piece piece2 = new Rook(Color.WHITE, new Position(0, 0), board);
    assertThat(piece1.hashCode()).isEqualTo(piece2.hashCode());
  }

  @Test
  void hashCode_DifferentPieces() {
    Piece piece1 = new Rook(Color.WHITE, new Position(0, 0), board);
    Piece piece2 = new King(Color.WHITE, new Position(0, 0), board);
    assertThat(piece1.hashCode()).isNotEqualTo(piece2.hashCode());
  }

  @Test
  void testPieceSetPosition() {
    Position oldPosition = new Position(0, 0);
    Position newPosition = new Position(5, 0);
    Piece rook = new Rook(Color.WHITE, oldPosition, board);
    rook.setPosition(newPosition);
    assertThat(board.getPieceAtPosition(oldPosition)).isNull();
    assertThat(rook.getPosition()).isEqualTo(newPosition);
    assertThat(board.getPieceAtPosition(newPosition)).isEqualTo(rook);
  }

  @Test
  void testToString() {
    Position position = new Position(0, 0);
    Piece piece = new Rook(Color.WHITE, position, board);
    String expectedString = "Rook{color=WHITE, position=Position[row=0, column=0], symbol=R}";
    assertThat(piece.toString()).isEqualTo(expectedString);
  }

  @Test
  void testToStringWithDifferentPieceType() {
    ChessBoard chessBoard = new ChessBoard();
    Piece piece = new Pawn(Color.BLACK, new Position(6, 1), chessBoard);
    String expectedString = "Rook{color=BLACK, position=Position[row=6, column=1], symbol=p}";
    assertThat(piece.toString()).isNotEqualTo(expectedString);
  }

  @Test
  void testToStringWithDifferentPosition() {
    ChessBoard chessBoard = new ChessBoard();
    Piece piece = new Pawn(Color.BLACK, new Position(0, 0), chessBoard);
    String expectedString = "Pawn{color=BLACK, position=Position[row=4, column=3], symbol=p}";
    assertThat(piece.toString()).isNotEqualTo(expectedString);
  }

  @Test
  void getType() {
    Position position = new Position(0, 0);
    Rook rook = new Rook(Color.WHITE, position, board);
    assertThat(rook.getType()).isEqualTo(PieceType.ROOK);
  }

  @Test
  void hashCode_IdenticalHashCode() {
    ChessBoard board2 = new ChessBoard();
    Rook rook1 = new Rook(Color.WHITE, new Position(4, 4), board);
    Rook rook2 = new Rook(Color.WHITE, new Position(4, 4), board2);
    assertThat(rook1.hashCode()).isEqualTo(rook2.hashCode());
  }

  @Test
  void hashCode_DifferentHashCode() {
    Rook rook1 = new Rook(Color.BLACK, new Position(4, 4), board);
    Rook rook2 = new Rook(Color.WHITE, new Position(4, 4), board);
    assertThat(rook1.hashCode()).isNotEqualTo(rook2.hashCode());
  }

  @Test
  void equals_DifferentRooks() {
    Position position1 = new Position(4, 4);
    Position position2 = new Position(5, 5);
    Rook rook1 = new Rook(Color.WHITE, position1, board);
    Rook rook2 = new Rook(Color.BLACK, position2, board);
    assertThat(rook1.equals(rook2)).isFalse();
  }

  @Test
  void testRookPossibleMovesMutationInList_successful() {
    board.clearChessboard();
    Position kingPosition = new Position(2, 2);
    Piece king = new King(Color.WHITE, kingPosition, board);
    board.setPieceAtPosition(king.getPosition(), king);

    Position rookPosition = new Position(4, 4);

    Piece rook = new Rook(Color.WHITE, rookPosition, board);
    board.setPieceAtPosition(rook.getPosition(), rook);

    List<Position> possibleMoves = rook.possibleMoves();

    List<Position> expectedMoves =
        Arrays.asList(
            new Position(5, 4),
            new Position(6, 4),
            new Position(7, 4),
            new Position(3, 4),
            new Position(2, 4),
            new Position(1, 4),
            new Position(0, 4),
            new Position(4, 5),
            new Position(4, 6),
            new Position(4, 7),
            new Position(4, 3),
            new Position(4, 2),
            new Position(4, 1),
            new Position(4, 0));

    assertEquals(expectedMoves, possibleMoves);
  }

  @Test
  void testRookBlockCheck_successful() throws IllegalMoveException {
    board.clearChessboard();
    Position kingPosition = new Position(0, 0);
    Piece king = new King(Color.WHITE, kingPosition, board);
    board.setPieceAtPosition(king.getPosition(), king);

    Position rookPosition = new Position(1, 1);
    Position rookTarget = new Position(1, 0);
    Position enemyRookPosition = new Position(2, 0);

    Piece rook = new Rook(Color.WHITE, rookPosition, board);
    Piece enemyRook = new Rook(Color.BLACK, enemyRookPosition, board);

    board.setPieceAtPosition(rook.getPosition(), rook);
    board.setPieceAtPosition(enemyRook.getPosition(), enemyRook);

    rook.moveTo(rookTarget);
    assertThat(rook.getPosition()).isEqualTo(rookTarget);
  }

  @Test
  void testRookBlockCheck_fail() {
    board.clearChessboard();
    Position kingPosition = new Position(0, 0);
    Piece king = new King(Color.WHITE, kingPosition, board);
    board.setPieceAtPosition(king.getPosition(), king);

    Position rookPosition = new Position(1, 1);
    Position rookTarget = new Position(1, 3);
    Position enemyRookPosition = new Position(2, 0);

    Piece rook = new Rook(Color.WHITE, rookPosition, board);
    Piece enemyRook = new Rook(Color.BLACK, enemyRookPosition, board);

    board.setPieceAtPosition(rook.getPosition(), rook);
    board.setPieceAtPosition(enemyRook.getPosition(), enemyRook);

    IllegalMoveException exception =
        assertThrows(IllegalMoveException.class, () -> rook.moveTo(rookTarget));
    String expectedMessage = "Illegal move";
    assertThat(exception.getMessage()).contains(expectedMessage);
    assertThat(rook.getPosition()).isEqualTo(rookPosition);
  }

  @Test
  void testRookUnblockCheck_fail() {
    board.clearChessboard();
    Position kingPosition = new Position(0, 0);
    Piece king = new King(Color.WHITE, kingPosition, board);
    board.setPieceAtPosition(king.getPosition(), king);

    Position rookPosition = new Position(1, 0);
    Position rookTarget = new Position(1, 1);
    Position enemyRookPosition = new Position(2, 0);

    Piece rook = new Rook(Color.WHITE, rookPosition, board);
    Piece enemyRook = new Rook(Color.BLACK, enemyRookPosition, board);

    board.setPieceAtPosition(rook.getPosition(), rook);
    board.setPieceAtPosition(enemyRook.getPosition(), enemyRook);

    IllegalMoveException exception =
        assertThrows(IllegalMoveException.class, () -> rook.moveTo(rookTarget));
    String expectedMessage = "Illegal move";
    assertThat(exception.getMessage()).contains(expectedMessage);
    assertThat(rook.getPosition()).isEqualTo(rookPosition);
  }

  @Test
  void testRookTakeFriendlyKing_fail() {
    board.clearChessboard();
    Position kingPosition = new Position(0, 0);
    Piece king = new King(Color.WHITE, kingPosition, board);
    board.setPieceAtPosition(king.getPosition(), king);

    Position rookPosition = new Position(0, 1);
    Position rookTarget = new Position(0, 0);

    Piece rook = new Rook(Color.WHITE, rookPosition, board);

    board.setPieceAtPosition(rook.getPosition(), rook);

    IllegalMoveException exception =
        assertThrows(IllegalMoveException.class, () -> rook.moveTo(rookTarget));
    String expectedMessage = "Illegal move";
    assertThat(exception.getMessage()).contains(expectedMessage);
    assertThat(rook.getPosition()).isEqualTo(rookPosition);
  }

  @Test
  void testRookTakeEnemyKing_fail() {
    board.clearChessboard();
    Position kingPosition = new Position(7, 7);
    Piece king = new King(Color.WHITE, kingPosition, board);
    board.setPieceAtPosition(king.getPosition(), king);

    Position enemyKingPosition = new Position(0, 0);
    Piece enemyKing = new King(Color.BLACK, enemyKingPosition, board);
    board.setPieceAtPosition(enemyKing.getPosition(), enemyKing);

    Position rookPosition = new Position(0, 1);
    Position rookTarget = new Position(0, 0);

    Piece rook = new Rook(Color.WHITE, rookPosition, board);

    board.setPieceAtPosition(rook.getPosition(), rook);

    IllegalMoveException exception =
        assertThrows(IllegalMoveException.class, () -> rook.moveTo(rookTarget));
    String expectedMessage = "Illegal move";
    assertThat(exception.getMessage()).contains(expectedMessage);
    assertThat(rook.getPosition()).isEqualTo(rookPosition);
  }
}

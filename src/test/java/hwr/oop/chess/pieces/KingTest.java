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

// Tests for King
class KingTest {

  private ChessBoard board;

  @BeforeEach
  void setup() {
    board = new ChessBoard();
  }

  @Test
  void testWhiteKingConstructor() {
    Position position = new Position(0, 0);
    Piece whiteKing = new King(Color.WHITE, position, board);
    SoftAssertions.assertSoftly(
        softly -> {
          softly.assertThat(whiteKing.getColor()).isEqualTo(Color.WHITE);
          softly.assertThat(whiteKing.getPosition()).isEqualTo(position);
          softly.assertThat(whiteKing.getSymbol()).isEqualTo('K');
        });
  }

  @Test
  void testBlackKingConstructor() {
    Position position = new Position(0, 0);
    Piece blackKing = new King(Color.BLACK, position, board);
    SoftAssertions.assertSoftly(
        softly -> {
          softly.assertThat(blackKing.getColor()).isEqualTo(Color.BLACK);
          softly.assertThat(blackKing.getPosition()).isEqualTo(position);
          softly.assertThat(blackKing.getSymbol()).isEqualTo('k');
        });
  }

  @Test
  void testKingMove_successful() throws IllegalMoveException {
    board.clearChessboard();

    Position position = new Position(0, 0);
    Position targetPosition = new Position(1, 0);
    Piece king = new King(Color.WHITE, position, board);
    board.setPieceAtPosition(king.getPosition(), king);

    king.moveTo(targetPosition);
    assertThat(king.getPosition()).isEqualTo(targetPosition);
  }

  @Test
  void testKingMove_fail() {
    Position position = new Position(2, 2);
    Position targetPosition = new Position(1, 2);
    Piece king = new King(Color.WHITE, position, board);
    board.setPieceAtPosition(king.getPosition(), king);

    IllegalMoveException exception =
        assertThrows(IllegalMoveException.class, () -> king.moveTo(targetPosition));
    String expectedMessage = "Illegal move";
    assertThat(exception.getMessage()).contains(expectedMessage);
    assertThat(king.getPosition()).isEqualTo(position);
  }

  @Test
  void equals_IdenticalPieces() {
    Piece piece1 = new King(Color.WHITE, new Position(0, 0), board);
    Piece piece2 = new King(Color.WHITE, new Position(0, 0), board);
    assertThat(piece1.equals(piece2)).isTrue();
  }

  @Test
  @SuppressWarnings("EqualsWithItself")
  void equals_sameInstance() {
    Piece piece = new King(Color.WHITE, new Position(0, 0), board);
    assertThat(piece.equals(piece)).isTrue();
  }

  @Test
  @SuppressWarnings("ConstantConditions")
  void equals_InstanceNull() {
    Piece piece = new King(Color.WHITE, new Position(0, 0), board);
    assertThat(piece.equals(null)).isFalse();
  }

  @Test
  @SuppressWarnings("EqualsBetweenInconvertibleTypes")
  void equals_DifferentClass() {
    Piece piece = new King(Color.WHITE, new Position(0, 0), board);
    assertThat(piece.equals("String")).isFalse();
  }

  @Test
  @SuppressWarnings("EqualsBetweenInconvertibleTypes")
  void equals_DifferentPieces() {
    Piece piece1 = new King(Color.WHITE, new Position(0, 0), board);
    Piece piece2 = new Queen(Color.WHITE, new Position(0, 0), board);
    assertThat(piece1.equals(piece2)).isFalse();
  }

  @Test
  void hashCode_IdenticalPieces() {
    Piece piece1 = new King(Color.WHITE, new Position(0, 0), board);
    Piece piece2 = new King(Color.WHITE, new Position(0, 0), board);
    assertThat(piece1.hashCode()).isEqualTo(piece2.hashCode());
  }

  @Test
  void hashCode_DifferentPieces() {
    Piece piece1 = new King(Color.WHITE, new Position(0, 0), board);
    Piece piece2 = new Queen(Color.WHITE, new Position(0, 0), board);
    assertThat(piece1.hashCode()).isNotEqualTo(piece2.hashCode());
  }

  @Test
  void testPieceSetPosition() {
    Position oldPosition = new Position(0, 0);
    Position newPosition = new Position(5, 0);
    Piece king = new King(Color.WHITE, oldPosition, board);
    king.setPosition(newPosition);
    assertThat(board.getPieceAtPosition(oldPosition)).isNull();
    assertThat(king.getPosition()).isEqualTo(newPosition);
    assertThat(board.getPieceAtPosition(newPosition)).isEqualTo(king);
  }

  @Test
  void testToString() {
    Position position = new Position(0, 0);
    Piece piece = new King(Color.WHITE, position, board);
    String expectedString = "King{color=WHITE, symbol=K, position=Position[row=0, column=0]}";
    assertThat(piece.toString()).isEqualTo(expectedString);
  }

  @Test
  void testToStringWithDifferentPieceType() {
    ChessBoard chessBoard = new ChessBoard();
    Piece piece = new Pawn(Color.BLACK, new Position(6, 1), chessBoard);
    String expectedString = "King{color=BLACK, position=Position[row=6, column=1], symbol=p}";
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
    King king = new King(Color.WHITE, position, board);
    assertThat(king.getType()).isEqualTo(PieceType.KING);
  }

  @Test
  void hashCode_IdenticalHashCode() {
    ChessBoard board2 = new ChessBoard();
    King king1 = new King(Color.WHITE, new Position(4, 4), board);
    King king2 = new King(Color.WHITE, new Position(4, 4), board2);
    assertThat(king1.hashCode()).isEqualTo(king2.hashCode());
  }

  @Test
  void hashCode_DifferentHashCode() {
    King king1 = new King(Color.BLACK, new Position(4, 4), board);
    King king2 = new King(Color.WHITE, new Position(4, 4), board);
    assertThat(king1.hashCode()).isNotEqualTo(king2.hashCode());
  }

  @Test
  void equals_DifferentKings() {
    Position position1 = new Position(4, 4);
    Position position2 = new Position(5, 5);
    King king1 = new King(Color.WHITE, position1, board);
    King king2 = new King(Color.BLACK, position2, board);
    assertThat(king1.equals(king2)).isFalse();
  }

  // Test contested moves
  @Test
  void testKingMove_contestedByKnight() {
    board.clearChessboard();

    Position kingPosition = new Position(0, 0);
    Position kingTargetPosition = new Position(1, 0);

    Position knightPosition = new Position(0, 2);

    Piece king = new King(Color.WHITE, kingPosition, board);
    board.setPieceAtPosition(king.getPosition(), king);

    Piece knight = new Knight(Color.BLACK, knightPosition, board);
    board.setPieceAtPosition(knight.getPosition(), knight);

    IllegalMoveException exception =
        assertThrows(IllegalMoveException.class, () -> king.moveTo(kingTargetPosition));
    String expectedMessage = "Illegal move";
    assertThat(exception.getMessage()).contains(expectedMessage);
    assertThat(king.getPosition()).isEqualTo(kingPosition);
  }

  @Test
  void testKingMove_contestedByPawn() {
    board.clearChessboard();

    Position kingPosition = new Position(4, 3);
    Position kingTargetPositionLeft = new Position(4, 2);
    Position kingTargetPositionRight = new Position(4, 4);

    Position pawnPosition = new Position(5, 3);

    Piece king = new King(Color.WHITE, kingPosition, board);
    board.setPieceAtPosition(king.getPosition(), king);

    Piece pawn = new Pawn(Color.BLACK, pawnPosition, board);
    board.setPieceAtPosition(pawn.getPosition(), pawn);

    IllegalMoveException exceptionLeft =
        assertThrows(IllegalMoveException.class, () -> king.moveTo(kingTargetPositionLeft));
    String expectedMessageLeft = "Illegal move";
    assertThat(exceptionLeft.getMessage()).contains(expectedMessageLeft);
    assertThat(king.getPosition()).isEqualTo(kingPosition);

    IllegalMoveException exception =
        assertThrows(IllegalMoveException.class, () -> king.moveTo(kingTargetPositionRight));
    String expectedMessageRight = "Illegal move";
    assertThat(exception.getMessage()).contains(expectedMessageRight);
    assertThat(king.getPosition()).isEqualTo(kingPosition);
  }

  @Test
  void testKingMove_contestedByRook() {
    board.clearChessboard();

    Position kingPosition = new Position(0, 0);
    Position kingTargetPosition = new Position(1, 0);

    Position rookPosition = new Position(1, 1);

    Piece king = new King(Color.WHITE, kingPosition, board);
    board.setPieceAtPosition(king.getPosition(), king);

    Piece rook = new Rook(Color.BLACK, rookPosition, board);
    board.setPieceAtPosition(rook.getPosition(), rook);

    IllegalMoveException exception =
        assertThrows(IllegalMoveException.class, () -> king.moveTo(kingTargetPosition));
    String expectedMessage = "Illegal move";
    assertThat(exception.getMessage()).contains(expectedMessage);
    assertThat(king.getPosition()).isEqualTo(kingPosition);
  }

  @Test
  void testKingMove_contestedByFriendlyRook() throws IllegalMoveException {
    board.clearChessboard();

    Position kingPosition = new Position(0, 0);
    Position kingTargetPosition = new Position(1, 0);

    Position rookPosition = new Position(1, 1);

    Piece king = new King(Color.WHITE, kingPosition, board);
    board.setPieceAtPosition(king.getPosition(), king);

    Piece rook = new Rook(Color.WHITE, rookPosition, board);
    board.setPieceAtPosition(rook.getPosition(), rook);

    king.moveTo(kingTargetPosition);
    assertThat(king.getPosition()).isEqualTo(kingTargetPosition);
  }

  @Test
  void testKingMove_contestedByBishop() {
    board.clearChessboard();

    Position kingPosition = new Position(0, 0);
    Position kingTargetPosition = new Position(1, 0);

    Position bishopPosition = new Position(0, 1);

    Piece king = new King(Color.WHITE, kingPosition, board);
    board.setPieceAtPosition(king.getPosition(), king);

    Piece bishop = new Bishop(Color.BLACK, bishopPosition, board);
    board.setPieceAtPosition(bishop.getPosition(), bishop);

    IllegalMoveException exception =
        assertThrows(IllegalMoveException.class, () -> king.moveTo(kingTargetPosition));
    String expectedMessage = "Illegal move";
    assertThat(exception.getMessage()).contains(expectedMessage);
    assertThat(king.getPosition()).isEqualTo(kingPosition);
  }

  @Test
  void testKingMove_contestedByFriendlyBishop() throws IllegalMoveException {
    board.clearChessboard();

    Position kingPosition = new Position(0, 0);
    Position kingTargetPosition = new Position(1, 0);

    Position bishopPosition = new Position(0, 1);

    Piece king = new King(Color.WHITE, kingPosition, board);
    board.setPieceAtPosition(king.getPosition(), king);

    Piece bishop = new Bishop(Color.WHITE, bishopPosition, board);
    board.setPieceAtPosition(bishop.getPosition(), bishop);

    king.moveTo(kingTargetPosition);
    assertThat(king.getPosition()).isEqualTo(kingTargetPosition);
  }

  @Test
  void testKingMove_contestedByKing() {
    board.clearChessboard();

    Position kingPosition = new Position(0, 0);
    Position kingTargetPosition = new Position(1, 0);

    Position bishopPosition = new Position(2, 0);

    Piece king = new King(Color.WHITE, kingPosition, board);
    board.setPieceAtPosition(king.getPosition(), king);

    Piece enemyKing = new King(Color.BLACK, bishopPosition, board);
    board.setPieceAtPosition(enemyKing.getPosition(), enemyKing);

    IllegalMoveException exception =
        assertThrows(IllegalMoveException.class, () -> king.moveTo(kingTargetPosition));
    String expectedMessage = "Illegal move";
    assertThat(exception.getMessage()).contains(expectedMessage);
    assertThat(king.getPosition()).isEqualTo(kingPosition);
  }

  @Test
  void testKingMove_contestedByFriendlyKing() throws IllegalMoveException {
    board.clearChessboard();

    Position kingPosition = new Position(0, 0);
    Position kingTargetPosition = new Position(1, 0);

    Position friendlyKingPosition = new Position(2, 0);

    Piece king = new King(Color.WHITE, kingPosition, board);
    board.setPieceAtPosition(king.getPosition(), king);

    Piece friendlyKing = new King(Color.WHITE, friendlyKingPosition, board);
    board.setPieceAtPosition(friendlyKing.getPosition(), friendlyKing);

    king.moveTo(kingTargetPosition);
    assertThat(king.getPosition()).isEqualTo(kingTargetPosition);
  }

  @Test
  void testKingMove_contestedByQueen() {
    board.clearChessboard();

    Position kingPosition = new Position(0, 0);
    Position kingTargetPositionTop = new Position(1, 0);
    Position kingTargetPositionRight = new Position(0, 1);

    Position queenPosition = new Position(1, 2);

    Piece king = new King(Color.WHITE, kingPosition, board);
    board.setPieceAtPosition(king.getPosition(), king);

    Piece queen = new Queen(Color.BLACK, queenPosition, board);
    board.setPieceAtPosition(queen.getPosition(), queen);

    IllegalMoveException exceptionLeft =
        assertThrows(IllegalMoveException.class, () -> king.moveTo(kingTargetPositionTop));
    String expectedMessageLeft = "Illegal move";
    assertThat(exceptionLeft.getMessage()).contains(expectedMessageLeft);
    assertThat(king.getPosition()).isEqualTo(kingPosition);

    IllegalMoveException exception =
        assertThrows(IllegalMoveException.class, () -> king.moveTo(kingTargetPositionRight));
    String expectedMessageRight = "Illegal move";
    assertThat(exception.getMessage()).contains(expectedMessageRight);
    assertThat(king.getPosition()).isEqualTo(kingPosition);
  }

  @Test
  void testKingPossibleMovesMutationInList_successful() {
    board.clearChessboard();
    Position kingPosition = new Position(4, 4);

    Piece king = new King(Color.WHITE, kingPosition, board);
    board.setPieceAtPosition(king.getPosition(), king);

    List<Position> possibleMoves = king.possibleMoves();

    List<Position> expectedMoves =
        Arrays.asList(
            new Position(3, 3),
            new Position(3, 4),
            new Position(3, 5),
            new Position(4, 3),
            new Position(4, 5),
            new Position(5, 3),
            new Position(5, 4),
            new Position(5, 5));

    assertEquals(expectedMoves, possibleMoves);
  }

  @Test
  void testKingMoveToContestedPositionWhileInLineOfSight_fail() {
    board.clearChessboard();

    Position kingPosition = new Position(1, 1);
    Position kingTargetPositionLeft = new Position(1, 0);
    Position kingTargetPositionDown = new Position(0, 1);

    Position rightRookPosition = new Position(1, 2);
    Position topRookPosition = new Position(2, 1);

    Piece king = new King(Color.WHITE, kingPosition, board);
    board.setPieceAtPosition(king.getPosition(), king);

    Piece rightRook = new Rook(Color.BLACK, rightRookPosition, board);
    board.setPieceAtPosition(rightRook.getPosition(), rightRook);

    Piece topRook = new Rook(Color.BLACK, topRookPosition, board);
    board.setPieceAtPosition(topRook.getPosition(), topRook);

    IllegalMoveException exceptionLeft =
        assertThrows(IllegalMoveException.class, () -> king.moveTo(kingTargetPositionLeft));
    String expectedMessageLeft = "Illegal move";
    assertThat(exceptionLeft.getMessage()).contains(expectedMessageLeft);
    assertThat(board.getPieceAtPosition(kingTargetPositionLeft)).isNull();
    assertThat(board.getPieceAtPosition(kingPosition)).isEqualTo(king);

    IllegalMoveException exceptionDown =
        assertThrows(IllegalMoveException.class, () -> king.moveTo(kingTargetPositionDown));
    String expectedMessageDown = "Illegal move";
    assertThat(exceptionDown.getMessage()).contains(expectedMessageDown);
    assertThat(board.getPieceAtPosition(kingTargetPositionDown)).isNull();
    assertThat(board.getPieceAtPosition(kingPosition)).isEqualTo(king);
  }
}

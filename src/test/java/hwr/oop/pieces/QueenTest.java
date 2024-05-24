package hwr.oop.pieces;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import hwr.oop.Color;
import hwr.oop.Position;
import hwr.oop.board.ChessBoard;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class QueenTest {

  private ChessBoard board;

  @BeforeEach
  void setup() {
    board = new ChessBoard();
  }

  @Test
  void testWhiteQueenConstructor() {
    Position position = new Position(0, 0);
    Piece whiteQueen = new Queen(Color.WHITE, position, board);
    SoftAssertions.assertSoftly(
        softly -> {
          softly.assertThat(whiteQueen.getColor()).isEqualTo(Color.WHITE);
          softly.assertThat(whiteQueen.getPosition()).isEqualTo(position);
          softly.assertThat(whiteQueen.getSymbol()).isEqualTo('Q');
        });
  }

  @Test
  void testBlackQueenConstructor() {
    Position position = new Position(0, 0);
    Piece blackQueen = new Queen(Color.BLACK, position, board);
    SoftAssertions.assertSoftly(
        softly -> {
          softly.assertThat(blackQueen.getColor()).isEqualTo(Color.BLACK);
          softly.assertThat(blackQueen.getPosition()).isEqualTo(position);
          softly.assertThat(blackQueen.getSymbol()).isEqualTo('q');
        });
  }

  @Test
  void testQueenMove_successful() throws IllegalMoveException {
    Position position = new Position(2, 2);
    Position targetPosition = new Position(5, 5);
    Piece queen = new Queen(Color.WHITE, position, board);
    board.setPieceAtPosition(queen.getPosition(), queen);

    queen.moveTo(targetPosition);
    assertThat(queen.getPosition()).isEqualTo(targetPosition);
  }

  @Test
  void testQueenMove_fail() {
    Position position = new Position(2, 2);
    Position targetPosition = new Position(4, 5);
    Piece queen = new Queen(Color.WHITE, position, board);
    board.setPieceAtPosition(queen.getPosition(), queen);

    IllegalMoveException exception =
        assertThrows(IllegalMoveException.class, () -> queen.moveTo(targetPosition));
    String expectedMessage = "Illegal move";
    assertThat(exception.getMessage()).contains(expectedMessage);
    assertThat(queen.getPosition()).isEqualTo(position);
  }

  @Test
  void equals_IdenticalPieces() {
    Piece piece1 = new Queen(Color.WHITE, new Position(0, 0), board);
    Piece piece2 = new Queen(Color.WHITE, new Position(0, 0), board);
    assertThat(piece1.equals(piece2)).isTrue();
  }

  @Test
  @SuppressWarnings("EqualsWithItself")
  void equals_sameInstance() {
    Piece piece = new Queen(Color.WHITE, new Position(0, 0), board);
    assertThat(piece.equals(piece)).isTrue();
  }

  @Test
  @SuppressWarnings("ConstantConditions")
  void equals_InstanceNull() {
    Piece piece = new Queen(Color.WHITE, new Position(0, 0), board);
    assertThat(piece.equals(null)).isFalse();
  }

  @Test
  @SuppressWarnings("EqualsBetweenInconvertibleTypes")
  void equals_DifferentClass() {
    Piece piece = new Queen(Color.WHITE, new Position(0, 0), board);
    assertThat(piece.equals("String")).isFalse();
  }

  @Test
  @SuppressWarnings("EqualsBetweenInconvertibleTypes")
  void equals_DifferentPieces() {
    Piece piece1 = new Queen(Color.WHITE, new Position(0, 0), board);
    Piece piece2 = new King(Color.WHITE, new Position(0, 0), board);
    assertThat(piece1.equals(piece2)).isFalse();
  }

  @Test
  void hashCode_IdenticalPieces() {
    Piece piece1 = new Queen(Color.WHITE, new Position(0, 0), board);
    Piece piece2 = new Queen(Color.WHITE, new Position(0, 0), board);
    assertThat(piece1.hashCode()).isEqualTo(piece2.hashCode());
  }

  @Test
  void hashCode_DifferentPieces() {
    Piece piece1 = new Queen(Color.WHITE, new Position(0, 0), board);
    Piece piece2 = new King(Color.WHITE, new Position(0, 0), board);
    assertThat(piece1.hashCode()).isNotEqualTo(piece2.hashCode());
  }

  @Test
  void testPieceSetPosition() {
    Position oldPosition = new Position(0, 0);
    Position newPosition = new Position(5, 0);
    Piece queen = new Queen(Color.WHITE, oldPosition, board);
    queen.setPosition(newPosition);
    assertThat(board.getPieceAtPosition(oldPosition)).isNull();
    assertThat(queen.getPosition()).isEqualTo(newPosition);
    assertThat(board.getPieceAtPosition(newPosition)).isEqualTo(queen);
  }

  @Test
  void testToString() {
    Position position = new Position(0, 0);
    Piece piece = new Queen(Color.WHITE, position, board);
    String expectedString = "Queen{color=WHITE, position=Position[row=0, column=0], symbol=Q}";
    assertThat(piece.toString()).isEqualTo(expectedString);
  }

  @Test
  void testToStringWithDifferentPieceType() {
    ChessBoard chessBoard = new ChessBoard();
    Piece piece = new Pawn(Color.BLACK, new Position(6, 1), chessBoard);
    String expectedString = "Queen{color=BLACK, position=Position[row=6, column=1], symbol=p}";
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
    Queen queen = new Queen(Color.WHITE, position, board);
    assertThat(queen.getType()).isEqualTo(PieceType.QUEEN);
  }

  @Test
  void hashCode_IdenticalHashCode() {
    ChessBoard board2 = new ChessBoard();
    Queen queen1 = new Queen(Color.WHITE, new Position(4, 4), board);
    Queen queen2 = new Queen(Color.WHITE, new Position(4, 4), board2);
    assertThat(queen1.hashCode()).isEqualTo(queen2.hashCode());
  }

  @Test
  void hashCode_DifferentHashCode() {
    Queen queen1 = new Queen(Color.BLACK, new Position(4, 4), board);
    Queen queen2 = new Queen(Color.WHITE, new Position(4, 4), board);
    assertThat(queen1.hashCode()).isNotEqualTo(queen2.hashCode());
  }
  @Test
  void equals_DifferentQueens() {
    Position position1 = new Position(4, 4);
    Position position2 = new Position(5, 5);
    Queen queen1 = new Queen(Color.WHITE, position1, board);
    Queen queen2 = new Queen(Color.BLACK, position2, board);
    assertThat(queen1.equals(queen2)).isFalse();
  }

}
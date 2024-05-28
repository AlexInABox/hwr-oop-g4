package hwr.oop;

import hwr.oop.match.Match;
import hwr.oop.persistence.Persistence;
import hwr.oop.pieces.*;
import hwr.oop.player.Player;
import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

public class GameLogic implements Domain {

  Persistence persistence;
  private final Path pathMatches;
  private final Path pathPlayers;

  public GameLogic(Persistence persistence, String allMatchesPath, String allPlayersPath) {
    this.persistence = persistence;
    final File fileMatches = new File(allMatchesPath);
    pathMatches = fileMatches.toPath();
    final File filePlayers = new File(allPlayersPath);
    pathPlayers = filePlayers.toPath();
  }

  @Override
  public Match loadMatch(String matchId) {

    List<Match> matches = persistence.loadMatches(pathMatches);

    for (Match match : matches) {
      if (match.getId().equals(matchId)) {
        Player playerWhite = loadPlayer(match.getPlayerWhite().getName());
        Player playerBlack = loadPlayer(match.getPlayerBlack().getName());
        match.updatePlayers(playerWhite, playerBlack);

        return match;
      }
    }
    throw new MatchNotFoundException(matchId);
  }

  @Override
  public void saveMatch(Match newMatch) {
    List<Match> matches = persistence.loadMatches(pathMatches);
    boolean matchExists = false;
    for (int i = 0; i < matches.size(); i++) {
      if (matches.get(i).getId().equals(newMatch.getId())) {
        matches.set(i, newMatch);
        matchExists = true;
        break;
      }
    }
    if (!matchExists) {
      matches.add(newMatch);
    }
    persistence.saveMatches(matches, pathMatches);
  }

  @Override
  public void createMatch(Player playerWhite, Player playerBlack, String id) {
    Player loadedPlayerWhite = loadPlayer(playerWhite.getName());
    Player loadedPlayerBlack = loadPlayer(playerBlack.getName());

    if (matchExists(id)) {
      throw new MatchAlreadyExistsException(id);
    }

    Match newMatch = new Match(loadedPlayerWhite, loadedPlayerBlack, id);
    saveMatch(newMatch);
  }

  @Override
  public Player loadPlayer(String playerName) {
    return persistence.loadPlayers(pathPlayers).stream()
        .filter(player -> player.getName().equals(playerName))
        .findFirst()
        .orElseGet(
            () -> {
              Player newPlayer = new Player(playerName);
              savePlayer(newPlayer);
              return newPlayer;
            });
  }

  @Override
  public void savePlayer(Player newPlayer) {
    List<Player> players = persistence.loadPlayers(pathPlayers);
    for (int i = 0; i < players.size(); i++) {
      if (players.get(i).getName().equals(newPlayer.getName())) {
        players.set(i, newPlayer);
        persistence.savePlayers(players, pathPlayers);
        return;
      }
    }
    players.add(newPlayer);
    persistence.savePlayers(players, pathPlayers);
  }

  @Override
  public void moveTo(String oldPositionString, String newPositionString, Match match)
      throws IllegalMoveException, ConvertInputToPositionException {

    Position oldPosition = convertInputToPosition(oldPositionString);
    Position newPosition = convertInputToPosition(newPositionString);

    if (oldPosition.equals(newPosition)) {
      throw new IllegalMoveException(
          "Illegal move to position: "
              + newPosition
              + ". The start and end positions are the same.");
    }

    Piece currentPiece = match.getBoard().getPieceAtPosition(oldPosition);

    if (currentPiece == null) {
      throw new IllegalMoveException("No piece at the specified position: " + oldPosition);
    }

    if (currentPiece.getColor() != match.getNextToMove()) {
      throw new IllegalMoveException(
          "It's not your turn. Expected: "
              + match.getNextToMove()
              + ", but got: "
              + currentPiece.getColor());
    }

    List<Position> possibleMoves = currentPiece.possibleMoves();
    if (!possibleMoves.contains(newPosition)) {
      String firstTwoPossibleMoves =
          possibleMoves.stream().limit(2).map(Position::toString).collect(Collectors.joining(", "));
      throw new IllegalMoveException(
          "Illegal move to position: "
              + newPosition
              + ". Possible possible moves are for example: "
              + firstTwoPossibleMoves);
    }

    currentPiece.moveTo(newPosition);
    match.toggleNextToMove();
  }

  @Override
  public void acceptRemi(Match match) {
    match.declareWinner(MatchOutcome.REMI);
  }

  @Override
  public void resign(Match match) {
    Color currentPlayer = match.getNextToMove();
    if (currentPlayer == Color.WHITE) {
      match.declareWinner(MatchOutcome.BLACK);
    } else {
      match.declareWinner(MatchOutcome.WHITE);
    }
  }

  public String endGame(Match match) {
    Player playerWhite = loadPlayer(match.getPlayerWhite().getName());
    Player playerBlack = loadPlayer(match.getPlayerBlack().getName());
    // TODO: Adjust ELO for playerWhite and playerBlack
    String victoryMessage = "";
    switch (match.getWinner()) {
      case MatchOutcome.REMI -> victoryMessage = "The game ended in Remi.";
      case MatchOutcome.WHITE ->
          victoryMessage =
              "WHITE won this game. Congrats "
                  + playerWhite.getName()
                  + " (new ELO: "
                  + playerWhite.getElo()
                  + ")";
      case MatchOutcome.BLACK ->
          victoryMessage =
              "BLACK won this game. Congrats "
                  + playerBlack.getName()
                  + " (new ELO: "
                  + playerBlack.getElo()
                  + ")";
      case MatchOutcome.NOT_FINISHED_YET ->
          throw new TheMatchHasNotEndedException("The game has not ended yet");
    }
    deleteMatch(match.getId());
    return victoryMessage;
  }

  private void deleteMatch(String matchId) {
    List<Match> matches = persistence.loadMatches(pathMatches);
    matches.removeIf(match -> match.getId().equals(matchId));
    persistence.saveMatches(matches, pathMatches);
  }

  public static Position convertInputToPosition(String input)
      throws ConvertInputToPositionException {
    if (input.length() != 2
        || !Character.isLetter(input.charAt(0))
        || !Character.isDigit(input.charAt(1))) {
      throw new ConvertInputToPositionException(
          "Invalid input format. Please provide a valid position (e.g., 'a1').");
    }

    int column = input.charAt(0) - 'a';
    int row = Character.getNumericValue(input.charAt(1)) - 1;

    if (column < 0 || column >= 8 || row < 0 || row >= 8) {
      throw new ConvertInputToPositionException(
          "Invalid position. Position must be within the chessboard.");
    }

    return new Position(row, column);
  }

  private boolean matchExists(String matchId) {
    List<Match> matches = persistence.loadMatches(pathMatches);
    for (Match match : matches) {
      if (match.getId().equals(matchId)) {
        return true;
      }
    }
    return false;
  }
}

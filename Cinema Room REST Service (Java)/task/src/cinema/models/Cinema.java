package cinema.models;

import cinema.dto.Stats;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.*;
import java.util.stream.Stream;
import lombok.Getter;

@Getter
public class Cinema {

  public Cinema(int rows, int columns) {
    this.totalRows = rows;
    this.totalColumns = columns;
    this.availableSeats = new Seat[rows][columns];
    for (int i = 0; i < rows; i++) {
      for (int j = 0; j < columns; j++) {
        availableSeats[i][j] = new Seat(i + 1, j + 1);
      }
    }
  }

  public Seat getSeat(int row, int column) {
    if (row > totalRows || column > totalColumns || row < 1 || column < 1)
      throw new IllegalArgumentException("The number of a row or a column is out of bounds!");
    return availableSeats[row - 1][column - 1];
  }

  private Optional<Seat> getSeatByToken(String token) {
    for (Seat[] seats : availableSeats) {
      for (Seat seat : seats) {
        if (seat.getToken().toString().equals(token)) return Optional.of(seat);
      }
    }
    return Optional.empty();
  }

  public Seat refund(String token) throws IllegalArgumentException {
    Seat seat =
        getSeatByToken(token).orElseThrow(() -> new IllegalArgumentException("Wrong token!"));
    synchronized (seat) {
      seat.setPurchased(false);
      seat.setToken(UUID.randomUUID());
    }
    return seat;
  }

  public Seat purchase(int row, int column) throws IllegalArgumentException {
    Seat seat = getSeat(row, column);
    synchronized (seat) {
      if (seat.isPurchased())
        throw new IllegalArgumentException("The ticket has been already purchased!");
      seat.setPurchased(true);
    }
    return seat;
  }

  @JsonProperty("available_seats")
  public List<Seat> getAllSeats() {
    List<Seat> response = new ArrayList<>();
    for (int i = 1; i < totalRows + 1; i++) {
      for (int j = 1; j < totalColumns + 1; j++) {
        response.add(getSeat(i, j));
      }
    }
    return response;
  }

  @JsonIgnore
  private Stream<Seat> getPurchasedSeats() {
    return getAllSeats().stream().filter(Seat::isPurchased);
  }

  @JsonIgnore
  private long income() {
    return getPurchasedSeats().mapToLong(Seat::getPrice).sum();
  }

  @JsonIgnore
  public Stats getStats() {
    int capacity = totalColumns * totalRows;
    int purchased = (int) getPurchasedSeats().count();
    int available = capacity - purchased;
    return Stats.builder().available(available).income(income()).purchased(purchased).build();
  }

  @JsonIgnore private final Seat[][] availableSeats;

  @JsonProperty("total_rows")
  private int totalRows;

  @JsonProperty("total_columns")
  private int totalColumns;
}

package cinema.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.UUID;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Seat {
  public Seat(int row, int column) {
    this.row = row;
    this.column = column;
  }

  private int row;
  private int column;
  private int price;
  @JsonIgnore private boolean purchased;

  @JsonIgnore private UUID token = UUID.randomUUID();

  public int getPrice() {
    if (row <= 4) return 10;
    return 8;
  }
}

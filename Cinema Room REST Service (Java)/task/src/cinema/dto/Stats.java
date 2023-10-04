package cinema.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class Stats {
  @JsonProperty("current_income")
  private long income;

  @JsonProperty("number_of_available_seats")
  private int available;

  @JsonProperty("number_of_purchased_tickets")
  private int purchased;
}

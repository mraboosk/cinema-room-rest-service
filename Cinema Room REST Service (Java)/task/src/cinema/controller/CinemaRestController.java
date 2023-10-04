package cinema.controller;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

import cinema.models.Cinema;
import cinema.models.Seat;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class CinemaRestController {
  private static final String PASSWORD = "super_secret";
  static final String UNAUTHORIZED_ERROR_MESSAGE = "The password is wrong!";
  Cinema cinema = new Cinema(9, 9);

  @GetMapping("/seats")
  public Cinema seats() {
    return cinema;
  }

  @PostMapping("/return")
  public ResponseEntity<?> refund(@RequestBody Map<String, String> tokenJson) {
    try {
      Seat seat = cinema.refund(tokenJson.get("token"));
      Map<String, Object> response = new HashMap<>();
      response.put("returned_ticket", seat);
      return ResponseEntity.ok(response);
    } catch (IllegalArgumentException e) {
      return ResponseEntity.status(BAD_REQUEST)
          .body(Collections.singletonMap("error", e.getMessage()));
    }
  }

  @PostMapping("/purchase")
  public ResponseEntity<?> purchase(@RequestBody Map<String, Integer> purchaseRequest) {
    try {
      Map<String, Object> response = new HashMap<>();
      Seat seat = cinema.purchase(purchaseRequest.get("row"), purchaseRequest.get("column"));

      response.put("token", seat.getToken());
      response.put("ticket", seat);

      return ResponseEntity.ok(response);
    } catch (IllegalArgumentException e) {
      return ResponseEntity.status(BAD_REQUEST)
          .body(Collections.singletonMap("error", e.getMessage()));
    }
  }

  @GetMapping("/stats")
  public ResponseEntity<?> stats(@RequestParam Optional<String> password) {
    if (password.isEmpty() || !password.get().equals(this.PASSWORD)) {
      return ResponseEntity.status(UNAUTHORIZED)
          .body(Collections.singletonMap("error", UNAUTHORIZED_ERROR_MESSAGE));
    }
    return ResponseEntity.ok(cinema.getStats());
  }
}

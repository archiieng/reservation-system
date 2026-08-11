package school.sorokin.reservationsystem;

import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/reservation")
public class ReservationContoller { // process HTTP request

    private static final Logger log = LoggerFactory.getLogger(ReservationContoller.class);

    private final ReservationService reservationService;

    public ReservationContoller(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Reservation> getReservationByID(
            @PathVariable("id") Long id
    ) {
        log.info("Log called method by id: id="+id );
        try{
            return ResponseEntity.status(HttpStatus.OK)
                    .body(reservationService.getReservationByID(id));
        } catch(NoSuchElementException e){
            return ResponseEntity.status(404).build();
        }

        //return reservationService.getReservationByID(id);
    }

    @GetMapping(    )   // if to search by ("/{id}")
    public ResponseEntity<List<Reservation>> getAllReservation() {
        log.info("Log called method by id: id=");
        return ResponseEntity.ok(reservationService.findAllReservation());
        //return reservationService.findAllReservation();
    }

    @PostMapping
    public ResponseEntity<Reservation> createReservation(@RequestBody Reservation reservationToCreate) {
        log.info("Called createReservation");
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(reservationService.createReservation(reservationToCreate));
        //return reservationService.createReservation(reservationToCreate);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Reservation> updateReservation(
            @PathVariable("id") Long id,
            @RequestBody Reservation reservationToUpdate) {
        log.info("Log called updateReservation id={}, reservationToUpdate={}", id, reservationToUpdate);
        var updated = reservationService.updateReservation(id, reservationToUpdate);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}/cancel")
    public ResponseEntity<Reservation> deleteReservation(
            @PathVariable("id") Long id
    ){
        log.info("Log called deleteReservation id={}", id);
        try{
            reservationService.cancelReservation(id);
            return ResponseEntity.ok().build();
        } catch(NoSuchElementException e){
            return ResponseEntity.status(404).build();
        }

    }

    @PostMapping("/{id}/approve")
    public ResponseEntity<Reservation> approveReservation(
            @PathVariable("id") Long id
    ) {
        log.info("Log called approveReservation id={}", id);
        var reservation = reservationService.approveReservation(id);
        return ResponseEntity.ok(reservation);
    }
}

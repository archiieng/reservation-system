package school.sorokin.reservationsystem;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class ReservationService {

    private static final Logger log = LoggerFactory.getLogger(ReservationService.class);

    private final ReservationRepository repository;

    public ReservationService(ReservationRepository repository) {
        this.repository = repository;
    }

    public Reservation getReservationByID(
            Long id
    ) {

        ReservationEntity reservationEntity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Not found reservation id = " + id
                ));
        return toDomainRservation(reservationEntity);
    }

    public List<Reservation> findAllReservation() {

        List<ReservationEntity> allEntities = repository.findAll();
        return allEntities.stream()
                .map(this::toDomainRservation)
                .toList();
    }

    public Reservation createReservation(Reservation reservationToCreate) {
        if(reservationToCreate.id() != null){
            throw new IllegalArgumentException("ID should be empty");
        }
        var entityToSave = new ReservationEntity(
                null,
                reservationToCreate.userId(),
                reservationToCreate.roomId(),
                reservationToCreate.startDate(),
                reservationToCreate.endDate(),
                ReservationStatus.PENDING
        );
        var savedEntity = repository.save(entityToSave);
        return toDomainRservation(savedEntity);
    }

    public Reservation updateReservation(
            Long id,
            Reservation reservationToUpdate
    ) {
        var reservationEntity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Reservation with id " + id + " does not exist"));

        if(reservationEntity.getStatus() != ReservationStatus.PENDING) {
            throw new IllegalStateException("Cannot modify reservation with status " + reservationEntity.getStatus());
        }

        var reservationToSave = new  ReservationEntity(
                reservationEntity.getId(),
                reservationToUpdate.userId(),
                reservationToUpdate.roomId(),
                reservationToUpdate.startDate(),
                reservationToUpdate.endDate(),
                ReservationStatus.PENDING
        );

        var updatedReservation =  repository.save(reservationToSave);
        return toDomainRservation(updatedReservation);
    }

    @Transactional
    public void cancelReservation(Long id) {
        if(!repository.existsById(id)) {
            throw new NoSuchElementException("Reservation with id " + id + " does not exist");
        }
        repository.setStatus(id, ReservationStatus.CANCELLED);
        log.info("Reservation with id " + id + " has been cancelled");
    }


    public Reservation approveReservation(Long id) {
        var reservationEntity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Reservation with id " + id + " does not exist"));

        if(reservationEntity.getStatus() != ReservationStatus.PENDING) {
            throw new IllegalStateException("Cannot approve reservation with status " + reservationEntity.getStatus());
        }
        var isConflict = isReservationConflict(reservationEntity);
        if(isConflict) {
            throw new IllegalStateException("Cannot approve reservation with status " + reservationEntity.getStatus());
        }
        reservationEntity.setStatus(ReservationStatus.APPROVED);
        repository.save(reservationEntity);
        return toDomainRservation(reservationEntity);
    }

    private boolean isReservationConflict(ReservationEntity reservation) {
        var allReservations = repository.findAll();

        for(ReservationEntity existingReservation : allReservations) {
            if(reservation.getId().equals(existingReservation.getId())){
                continue;
            }
            if(!reservation.getRoomId().equals(existingReservation.getRoomId())) {
                continue;
            }
            if(existingReservation.getStatus().equals(ReservationStatus.APPROVED)) {
                continue;
            }
            if(reservation.getStartDate().isBefore(existingReservation.getEndDate()) &&
                    reservation.getStartDate().isBefore(reservation.getEndDate())) {
                return true;
            }

        }
        return false;
    }

    private Reservation toDomainRservation(
            ReservationEntity reservation
    ) {
        return new Reservation(
                reservation.getId(),
                reservation.getUserId(),
                reservation.getRoomId(),
                reservation.getStartDate(),
                reservation.getEndDate(),
                reservation.getStatus()
        );
    }
}

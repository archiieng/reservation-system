package school.sorokin.reservationsystem;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReservationRepository extends JpaRepository<ReservationEntity, Long> {

    //List<ReservationEntity> findAllByStatusIs(ReservationStatus status);

    /*@Query(value = "select * from reservations r where r.status = :status", nativeQuery = true)
    List<ReservationEntity> findAllByStatusIs(ReservationStatus status);

    @Query("select * from reservations r where r.status = :status")
    List<ReservationEntity> findAllByRoomId(@Param("roomId") Long roomId);

    @Modifying
    @Query("""
            update ReservationEntity r 
            set r.userId = :userid, r.roomId = :roomId, r.startDate = :startDate, r.endDate = :endDate, r.status = :status where r.id = :id""" )
    int updateAllFields(
            @Param("id") Long id,
            @Param("userId") Long userId,
            @Param("roomId") Long roomId,
            @Param("startDate") Long startDate,
            @Param("endDate") Long endDate,
            @Param("status") ReservationStatus status
    );*/

    @Modifying
    @Query("update ReservationEntity r set r.status = :status where r.id = :id")


    void setStatus(
            @Param("id") Long id,
            @Param("status") ReservationStatus reservationStatus
    );

}

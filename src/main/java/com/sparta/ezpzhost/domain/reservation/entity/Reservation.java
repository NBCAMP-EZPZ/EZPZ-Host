package com.sparta.ezpzhost.domain.reservation.entity;

import com.sparta.ezpzhost.domain.reservation.enums.ReservationStatus;
import com.sparta.ezpzhost.domain.review.entity.Review;
import com.sparta.ezpzhost.domain.slot.entity.Slot;
import com.sparta.ezpzhost.domain.user.entity.User;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import java.util.List;

import jakarta.persistence.Table;
import lombok.Getter;

@Entity
@Getter
@Table(name = "reservation", indexes = {
    @Index(name = "idx_reservation_user_status", columnList = "user_id, reservation_status, slot_id")
})
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reservation_id")
    private Long id;

    @Column(nullable = false)
    private int numberOfPersons;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ReservationStatus reservationStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "slot_id", nullable = false)
    private Slot slot;

    @OneToMany(mappedBy = "reservation", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Review> reviews;
}

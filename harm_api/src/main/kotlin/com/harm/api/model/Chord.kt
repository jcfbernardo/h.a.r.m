package com.harm.api.model

import jakarta.persistence.*

@Entity
@Table(name = "chords")
data class Chord(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false)
    val title: String,

    @Column(columnDefinition = "TEXT")
    val rawProgressionText: String,

    @Column(columnDefinition = "json")
    val processedProgressionJson: String,

    @Column(nullable = false)
    val styleName: String,

    @Column(nullable = false)
    val key: String,

    val bpm: Int,
    val timeSignature: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    val user: User
)
package com.harm.api.repository

import com.harm.api.model.Chord
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ChordRepository : JpaRepository<Chord, Long>
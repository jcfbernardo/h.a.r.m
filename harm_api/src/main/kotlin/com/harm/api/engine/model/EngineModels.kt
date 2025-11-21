package com.harm.api.engine.model

data class Chord (
    val rawChord: String
)

data class Measure (
    var section: String?,
    var chords: List<Chord>
)

data class ParsedProgression (
    var measures: List<Measure>
)

data class AnalyzedChord (
    var rawChord: String,
    var degrees : String
)

data class AnalyzedProgression (
    var measures: List<List<AnalyzedChord>>
)

data class ProcessedChord (
    var original: String,
    var sugestions: List<String>,
    var voicings: List<String>,
)

data class ProcessedProgression (
    var key: String,
    var styleName: String,
    var sections: List<Map<String, List<ProcessedChord>>>
)
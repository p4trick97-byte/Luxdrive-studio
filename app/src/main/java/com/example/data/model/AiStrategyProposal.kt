package com.example.data.model

data class AiStrategyProposal(
    val id: String,
    val category: AutomotiveCategory,
    val provisionalTitle: String,
    val coreIdea: String,
    val potentialAudience: String,
    val initialHook: String,
    val recommendedFormat: ProjectFormat,
    val suggestedDuration: String,
    val sourcesToVerify: List<String>,
    val videoGoal: String,
    val longVideoVersionSummary: String,
    val derivedShortsIdeas: List<String>,
    val tiktokAdaptationNotes: String,
    val instagramReelsAdaptationNotes: String,
    val verifiedFacts: List<String>,
    val estimatedData: List<String>,
    val editorialHypotheses: List<String>,
    val difficultyScore: Int = 3, // 1 to 5
    val viralityPotential: String = "Alto"
)

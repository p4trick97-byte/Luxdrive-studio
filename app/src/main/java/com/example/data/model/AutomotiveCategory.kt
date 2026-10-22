package com.example.data.model

enum class AutomotiveCategory(val displayName: String, val badgeColorHex: Long) {
    SUPERCAR_MAINTENANCE_REAL_COST("Quanto costa davvero mantenere una supercar", 0xFFE5C158),
    SUPERCAR_AND_HYPERCAR("Supercar e hypercar", 0xFFD4AF37),
    LEGENDARY_AND_RARE_CARS("Auto leggendarie e rare", 0xFF38BDF8),
    AUTOMOTIVE_STORIES_AND_BACKSTAGE("Storie e retroscena automobilistici", 0xFFA78BFA),
    ENGINES_AND_TECHNOLOGY("Motori e tecnologia", 0xFF34D399),
    PERFORMANCE_AND_COMPARISONS("Prestazioni e confronti", 0xFFF97316),
    HISTORIC_SPORTS_CARS("Auto sportive storiche", 0xFFF472B6),
    MAINTENANCE_AND_OWNERSHIP_COSTS("Costi di manutenzione e possesso", 0xFFFBBF24),
    RECORDS_AND_INNOVATIONS("Record e innovazioni automobilistiche", 0xFF60A5FA);

    companion object {
        fun fromDisplayName(name: String?): AutomotiveCategory {
            return entries.firstOrNull { it.displayName.equals(name, ignoreCase = true) }
                ?: SUPERCAR_AND_HYPERCAR
        }
    }
}

enum class ProjectFormat(val displayName: String, val iconLabel: String) {
    LONG_VIDEO("Video Lungo (16:9)", "16:9 Long"),
    SHORTS("YouTube Shorts (9:16)", "9:16 Short")
}

enum class ProjectStatus(val displayName: String, val stepIndex: Int) {
    IDEA("Idea", 0),
    DRAFT("Bozza Script", 1),
    IN_PRODUCTION("In Produzione", 2),
    READY("Pronto al Montaggio", 3),
    SCHEDULED("Programmato", 4),
    PUBLISHED("Pubblicato", 5)
}

data class StoryboardScene(
    val sceneNumber: Int,
    val durationSeconds: Int,
    val visualNotes: String,
    val onScreenText: String,
    val cameraMovementAndFx: String,
    val musicAndSoundFx: String
)

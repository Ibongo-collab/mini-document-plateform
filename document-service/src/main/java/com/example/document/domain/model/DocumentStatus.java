package com.example.document.domain.model;

/**
 * Cycle de vie d'un Document.
 * <p>
 * TODO — business implementation : les transitions autorisees entre statuts
 * (ex : UPLOADING -> AVAILABLE, AVAILABLE -> DELETED) doivent etre
 * implementees dans l'Aggregate Root {@link Document}.
 */
public enum DocumentStatus {

    /** Le fichier est en cours d'upload vers le storage. */
    UPLOADING,

    /** Le fichier est disponible et peut etre telecharge. */
    AVAILABLE,

    /** Le document a ete supprime (suppression logique ou physique). */
    DELETED
}

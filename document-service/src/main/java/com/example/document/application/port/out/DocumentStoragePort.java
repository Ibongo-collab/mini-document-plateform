package com.example.document.application.port.out;

import java.io.InputStream;

/**
 * Port sortant : stockage du contenu binaire d'un document.
 * <p>
 * Abstraction vers le futur adapter AWS S3 (S3DocumentStorageAdapter).
 * Represente conceptuellement trois operations : stocker, recuperer,
 * supprimer un fichier.
 * TODO — business implementation : l'implementation concrete (AWS SDK,
 * credentials, bucket, region...) est a ecrire dans adapter/out/storage.
 */
public interface DocumentStoragePort {

    /**
     * Stocke le contenu d'un fichier et retourne la cle de stockage (storageKey).
     */
    String store(String filename, InputStream content, long sizeInBytes);

    /**
     * Recupere le contenu d'un fichier a partir de sa cle de stockage.
     */
    InputStream retrieve(String storageKey);

    /**
     * Supprime un fichier a partir de sa cle de stockage.
     */
    void delete(String storageKey);
}

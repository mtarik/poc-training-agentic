package fr.formation.shop.domain.exception;

/**
 * Ressource métier introuvable.
 */
public class NotFoundException extends RuntimeException {

    public NotFoundException(String resource, Object id) {
        super("%s %s introuvable".formatted(resource, id));
    }
}

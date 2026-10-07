package fr.formation.shop.domain.exception;

/**
 * Violation d'une règle métier.
 */
public class BusinessException extends RuntimeException {

    public BusinessException(String message) {
        super(message);
    }
}

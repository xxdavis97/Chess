package chess.api.exception;

import chess.api.dto.response.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(GameNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleGameNotFound(GameNotFoundException ex) {
        ErrorResponse error = new ErrorResponse("GAME_NOT_FOUND", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(IllegalMoveException.class)
    public ResponseEntity<ErrorResponse> handleIllegalMove(IllegalMoveException ex) {
        ErrorResponse error = new ErrorResponse("ILLEGAL_MOVE", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(NotYourTurnException.class)
    public ResponseEntity<ErrorResponse> handleNotYourTurn(NotYourTurnException ex) {
        ErrorResponse error = new ErrorResponse("NOT_YOUR_TURN", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(PawnPromotionRequiredException.class)
    public ResponseEntity<ErrorResponse> handlePawnPromotionRequired(PawnPromotionRequiredException ex) {
        ErrorResponse error = new ErrorResponse("PAWN_PROMOTION_REQUIRED", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(GameFullException.class)
    public ResponseEntity<ErrorResponse> handleGameFull(GameFullException ex) {
        ErrorResponse error = new ErrorResponse("GAME_FULL", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(UnauthorizedMoveException.class)
    public ResponseEntity<ErrorResponse> handleUnauthorizedMove(UnauthorizedMoveException ex) {
        ErrorResponse error = new ErrorResponse("UNAUTHORIZED_MOVE", ex.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }

    @ExceptionHandler(ColorAlreadyTakenException.class)
    public ResponseEntity<ErrorResponse> handleColorAlreadyTaken(ColorAlreadyTakenException ex) {
        ErrorResponse error = new ErrorResponse("COLOR_TAKEN", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationErrors(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .reduce((a, b) -> a + "; " + b)
                .orElse("Validation failed");
        ErrorResponse error = new ErrorResponse("VALIDATION_ERROR", message);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        ErrorResponse error = new ErrorResponse("INTERNAL_ERROR", ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}

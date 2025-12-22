package chess.api.dto.websocket;

public enum GameUpdateType {
    MOVE,
    GAME_START,
    GAME_END,
    PLAYER_JOINED,
    PLAYER_LEFT,
    PAWN_PROMOTION_REQUIRED,
    DRAW_OFFER,
    DRAW_ACCEPTED,
    DRAW_DECLINED,
    OPPONENT_CONNECTED,
    OPPONENT_DISCONNECTED
}

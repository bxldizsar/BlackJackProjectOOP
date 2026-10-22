package blackjack.net;

import blackjack.model.GameState;

/**
 *
 * @author boldi
 */
// The screen that wants to know about changes in the room implements this.
// GameSync calls these methods on the Swing thread, so they can update the window.
public interface GameSyncListener
{
    // the room has a new version, show it
    void stateChanged(GameState state);

    // reading the room failed, show the message (it is tried again in a second)
    void syncFailed(String message);
}

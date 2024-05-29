package pcd.part2A.messages;
/**
 * Context about Player actor,
 * with list of message that receive
 */
public abstract class PlayerActorContext {
    public static final class CellSelected extends PlayerActorContext{}
    public static final class CellUpdated extends PlayerActorContext{}
    public static final class NotifyPlayerJoined extends PlayerActorContext{}
    public static final class NotifyPlayerLefted extends PlayerActorContext{}
}

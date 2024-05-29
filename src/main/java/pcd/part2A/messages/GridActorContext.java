package pcd.part2A.messages;
/**
 * Context about GridActor actor,
 * with list of message that receive
 */
public abstract class GridActorContext {
    public static final class PlayerJoined extends GridActorContext{}
    public static final class PlayerLefted extends GridActorContext{}
    public static final class SelectCell extends GridActorContext{}
    public static final class UpdateCell extends GridActorContext{}
}

package pcd.part2A.messages;
/**
 * Context about Coordinator actor,
 * with list of message that receive
 */
public abstract class CoordinatorActorContext {
    public static final class CreateGame extends CoordinatorActorContext{}
    public static final class JoinGame extends CoordinatorActorContext{}
    public static final class LeaveGame extends CoordinatorActorContext{}
}

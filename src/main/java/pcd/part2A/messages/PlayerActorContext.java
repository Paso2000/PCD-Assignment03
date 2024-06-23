package pcd.part2A.messages;

import akka.actor.typed.ActorRef;

import java.util.List;
import java.util.Optional;


/**
 * Context about Player actor,
 * with list of message that receive
 */
public abstract class PlayerActorContext {
    public static final class SolveSudoku extends PlayerActorContext {}
    public static final class LeaderSolve extends PlayerActorContext{
        public int[][] solvedGrid;
        public LeaderSolve(int[][] solvedGrid){
            this.solvedGrid = solvedGrid;
        }
    }
    public static final class SolveOfEveryone extends PlayerActorContext{
        public int[][] solvedGrid;
        public SolveOfEveryone(int[][] solvedGrid){
            this.solvedGrid = solvedGrid;
        }
    }
    public static final class SelectCell extends PlayerActorContext{
        public int row;
        public int col;
        public SelectCell(int row, int col) {
            super();
            this.row = row;
            this.col = col;
        }
    }
    public static final class LeaderSelect extends PlayerActorContext{
        public int row;
        public int col;
        public LeaderSelect(int row, int col) {
            super();
            this.row = row;
            this.col = col;
        }
    }
    public static final class SelectCellOfEveryone extends PlayerActorContext{
        public int row;
        public int col;
        public SelectCellOfEveryone(int row, int col) {
            super();
            this.row = row;
            this.col = col;
        }
    }
    public static final class ChangeCell extends PlayerActorContext{
        public int row;
        public int col;
        public int value;
        public ChangeCell(int row, int col, int value) {
            super();
            this.row = row;
            this.col = col;
            this.value = value;
        }
}
    public static final class LeaderChange extends PlayerActorContext{
        public int row;
        public int col;
        public int value;
        public LeaderChange(int row, int col, int value) {
            super();
            this.row = row;
            this.col = col;
            this.value = value;
        }
    }
    public static final class ChangeCellOfEveryone extends PlayerActorContext{
        public int row;
        public int col;
        public int value;
        public ChangeCellOfEveryone(int row, int col, int value) {
            super();
            this.row = row;
            this.col = col;
            this.value = value;
        }
    }
    public static final class NotifyNewPlayer extends PlayerActorContext{
        public ActorRef<PlayerActorContext> newPlayer;
        public int nGames;

        public NotifyNewPlayer(ActorRef<PlayerActorContext> newPlayer, int nGames){
            this.newPlayer = newPlayer;
            this.nGames=nGames;
        }
    }
    public static final class SendData extends PlayerActorContext{
        public ActorRef<PlayerActorContext> leader;
        public Optional<List<ActorRef<PlayerActorContext>>> otherPlayers;
        public int[][] grid;
        public SendData(ActorRef<PlayerActorContext> leader, Optional<List<ActorRef<PlayerActorContext>>> otherPlayers, int[][] grid) {
            super();
            this.leader = leader;
            this.otherPlayers = otherPlayers;
            this.grid = grid;
        }
    }
    public static final class LeaveGame extends PlayerActorContext{
        public ActorRef<PlayerActorContext> player;
        public LeaveGame(ActorRef<PlayerActorContext> player){
            this.player = player;
        }
    }
    public static final class ChangeLeader extends PlayerActorContext{
        public ActorRef<PlayerActorContext> player;
        public ChangeLeader(ActorRef<PlayerActorContext> player){
            this.player = player;
        }
    }
    public static final class DeletePlayer extends PlayerActorContext{
        public ActorRef<PlayerActorContext> player;
        public DeletePlayer(ActorRef<PlayerActorContext> player){
            this.player = player;
        }
    }
}

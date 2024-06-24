package pcd.part2A.Actors;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import akka.actor.typed.receptionist.Receptionist;
import akka.actor.typed.receptionist.ServiceKey;
import akka.japi.Pair;
import jnr.ffi.annotations.In;
import pcd.part2A.messages.GamesActorContext;
import pcd.part2A.messages.PlayerActorContext;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.*;
import java.util.List;

import static pcd.part2A.Utils.startup;

public class GamesActor extends AbstractBehavior<GamesActorContext> {


    public static Map<Integer, Pair<ActorRef<PlayerActorContext>, List<ActorRef<PlayerActorContext>>>> games = new HashMap<>();
    private int gamesNumber=0;
    public static final ServiceKey<GamesActorContext> SERVICE_KEY = ServiceKey.create(GamesActorContext.class, "ServiceKey");


    public GamesActor(ActorContext<GamesActorContext> context) {
        super(context);
        //registra se stesso al receptionist attraversio la servikey
        context.getSystem().receptionist().tell(Receptionist.register(SERVICE_KEY, context.getSelf()));
    }

    public static Behavior<GamesActorContext> create() {
        return Behaviors.setup(GamesActor::new);
    }

    @Override
    public Receive<GamesActorContext> createReceive() {
        return newReceiveBuilder()
                .onMessage(GamesActorContext.StartNewSudoku.class, this::onStartNewGame)
                .onMessage(GamesActorContext.JoinInGrid.class, this::onJoinInGrid)
                .onMessage(GamesActorContext.DeletePlayer.class, this::onDeletePlayer)
                .onMessage(GamesActorContext.ChangeLeader.class, this::onChangeLeader)
                .onMessage(GamesActorContext.DeleteMatch.class, this::onDeleteMatch)
                .build();
    }

    private Behavior<GamesActorContext> onDeleteMatch(GamesActorContext.DeleteMatch deleteMatch) {
        games.remove(deleteMatch.nGames);
        return Behaviors.same();
    }

    private Behavior<GamesActorContext> onChangeLeader(GamesActorContext.ChangeLeader changeLeader) {
        games.get(changeLeader.nGames).second().remove(changeLeader.player);
        games.put(changeLeader.nGames, new Pair<>(changeLeader.leader,games.get(changeLeader.nGames).second()));
        return Behaviors.same();
    }

    private Behavior<GamesActorContext> onDeletePlayer(GamesActorContext.DeletePlayer deletePlayer) {
        games.get(deletePlayer.nGames).second().remove(deletePlayer.playerToDelete);
        return Behaviors.same();
    }

    private Behavior<GamesActorContext> onJoinInGrid(GamesActorContext.JoinInGrid joinInGrid) {
        //aggiugne il player corrente nella lista di quella partita
        games.get(joinInGrid.nGame.get()).second().add(joinInGrid.player);
        //manda un messaggio al leader, con il riferimento del player
        games.get(joinInGrid.nGame.get()).first().tell(new PlayerActorContext.NotifyNewPlayer(joinInGrid.player,gamesNumber));
        return Behaviors.same();
    }

    private Behavior<GamesActorContext> onStartNewGame(GamesActorContext.StartNewSudoku startNewSudoku) {
        gamesNumber++;
        Pair<ActorRef<PlayerActorContext>, List<ActorRef<PlayerActorContext>>> allPlayers  = new Pair<>(startNewSudoku.leader, new ArrayList<ActorRef<PlayerActorContext>>());
        this.games.put(gamesNumber, allPlayers);
        //System.out.println(games);
        System.out.println("start new game");
        return Behaviors.same();
    }

    public static class GamesActorGui extends JFrame {
        private JButton newGameButton;
        private JButton joinGameButton;
        private JLabel labelJoinID;
        private static final JComboBox<Integer> fieldJoinID= new JComboBox();
        private static int nGame  = 0;


        public GamesActorGui() {
            setTitle("Cooperative Sudoku");
            setSize(400, 150);
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            JPanel panel = new JPanel();
            panel.setLayout(null);


            //Usa layout null per posizionare manualmente i componenti
            newGameButton = new JButton("New Game");
            joinGameButton = new JButton("Enter in a game");
            labelJoinID = new JLabel("Game ID:");

            // Imposta le dimensioni e le posizioni dei componenti
            newGameButton.setBounds(10, 60, 150, 25);
            joinGameButton.setBounds(200, 60, 150, 25);
            labelJoinID.setBounds(10, 20, 80, 25);
            fieldJoinID.setBounds(100, 20, 165, 25);

            // Aggiungi ActionListener ai pulsanti
            newGameButton.addActionListener(new ButtonClickListener());
            joinGameButton.addActionListener(new ButtonClickListener());

            panel.add(newGameButton);
            panel.add(joinGameButton);
            panel.add(labelJoinID);
            panel.add(fieldJoinID);
            add(panel, BorderLayout.CENTER);
            fieldJoinID.addFocusListener(new FocusAdapter() {
                @Override
                public void focusGained(FocusEvent e) {
                    fieldJoinID.removeAllItems();
                    games.keySet().forEach(fieldJoinID::addItem);
                }
            });
        }

        private static class ButtonClickListener implements ActionListener {
            @Override
            public void actionPerformed(ActionEvent e) {
                String command = e.getActionCommand();
                if (command.equals("New Game")) {
                    nGame++;
                    startup("playerNewGame", 0 , Optional.of(nGame));

                } else if (command.equals("Enter in a game")) {
                    if(!games.isEmpty()) {
                        //passo il valore della comboBox con la partita a cui voglio partecipare
                        startup("playerJoinGame", 0, Optional.ofNullable((Integer) fieldJoinID.getSelectedItem()));
                    }
                }
            }
        }

    }
    
}
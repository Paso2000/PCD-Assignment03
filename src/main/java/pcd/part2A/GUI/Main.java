package pcd.part2A.GUI;

import pcd.part2A.Actors.GamesActor;

import java.util.Optional;

import static pcd.part2A.Utils.startup;

public class Main {

    public static void main(String[] args) {
        startup("backend", 0, Optional.empty());
        GamesActor.GamesActorGui frame = new GamesActor.GamesActorGui();
        frame.setVisible(true);
    }
}

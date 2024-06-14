package pcd.part2A;

import akka.actor.typed.ActorSystem;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.Behaviors;
import akka.cluster.typed.Cluster;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import pcd.part2A.GUI.StartGUI;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
public class App{

    public static void main(String[] args) {
        StartGUI frame = new StartGUI();
        frame.setVisible(true);
    }
}

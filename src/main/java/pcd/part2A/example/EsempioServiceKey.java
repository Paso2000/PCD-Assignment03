package pcd.part2A.example;

import akka.actor.typed.Behavior;
import akka.actor.typed.ActorSystem;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.receptionist.Receptionist;
import akka.actor.typed.receptionist.ServiceKey;

/*
ESEMPIO DI COME SI USA UN SERVICEKEY CON IL SUO RECEPRIONIST
FORSE NON VA MA SERVE SOLO PER CAPIRE
 */

// Messaggi
public class EsempioServiceKey {
    public class MyMessage {
        public final String content;

        public MyMessage(String content) {
            this.content = content;
        }
    }

    // Attore che si registra presso il Receptionist
    public class MyServiceActor {
        public static final ServiceKey<MyMessage> SERVICE_KEY =
                ServiceKey.create(MyMessage.class, "MyService");

        public static Behavior<MyMessage> create() {
            return Behaviors.setup(context -> {
                // Registrazione del servizio presso il Receptionist
                context.getSystem().receptionist().tell(Receptionist.register(SERVICE_KEY, context.getSelf()));

                return Behaviors.receiveMessage(message -> {
                    // Gestione dei messaggi
                    System.out.println("Received message: " + message.content);
                    return Behaviors.same();
                });
            });
        }
    }

    // Attore che cerca il servizio presso il Receptionist
    public class MyClientActor {
        public static Behavior<Void> create() {
            return Behaviors.setup(context -> {
                // Sottoscrizione al servizio
                context.getSystem().receptionist().tell(Receptionist.subscribe(MyServiceActor.SERVICE_KEY, context.getSelf()));

                return Behaviors.receive(Void.class)
                        .onMessage(Receptionist.Listing.class, listing -> {
                            // Controllo se il servizio è disponibile
                            listing.getServiceInstances(MyServiceActor.SERVICE_KEY).forEach(service -> {
                                // Invio di un messaggio al servizio
                                service.tell(new MyMessage("Hello, Service!"));
                            });
                            return Behaviors.same();
                        })
                        .build();
            });
        }
    }

    // Main per avviare l'applicazione
    public class Main {
        public static void main(String[] args) {
            ActorSystem<Void> system = ActorSystem.create(Behaviors.setup(context -> {
                context.spawn(MyServiceActor.create(), "myService");
                context.spawn(MyClientActor.create(), "myClient");
                return Behaviors.empty();
            }), "MySystem");
        }
    }
}


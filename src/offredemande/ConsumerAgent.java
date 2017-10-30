package offredemande;

import jade.core.AID;
import jade.core.Agent;
import jade.core.Location;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;

import java.util.HashMap;
import java.util.Map;

import static java.lang.Thread.sleep;

/**
 * Created by SALIM on 25/12/2016.
 */
public class ConsumerAgent extends Agent {
    private String productName;
    private Float productPrice;
    private Location firstLocation;

    public void setup(){
        System.out.println("Agent Consumer " + this.getAID().getName() + " crée");
        firstLocation = here();

            // Un comportement qui permet de récupérer le produit
            // de migrer vers un autre Container pour formuler une demande,
            // d'envoyer la quantité au producer et de migrer vers le producer Container
            addBehaviour(new CyclicBehaviour() {
                @Override
                public void action() {
                    MessageTemplate msgtemplate = MessageTemplate.and(MessageTemplate.MatchPerformative(ACLMessage.INFORM),
                            MessageTemplate.MatchProtocol("information"));
                    ACLMessage message = receive(msgtemplate);
                    if(message != null){
                        Map product = new HashMap();
                        try {
                            product = (HashMap) message.getContentObject();
                        } catch (UnreadableException e) {
                            e.printStackTrace();
                        }
                        Location itineraire =(Location) product.get("location");
                        System.out.println(getAID().getName() + " Migre de la location " + firstLocation +
                                " vers la location : " + itineraire.getName());
                        myAgent.doMove(itineraire);
                        System.out.println(getAID().getName() + " est arrivé à la location : " + itineraire.getName());
                        productName = (String) product.get("productname");
                        productPrice = (Float)product.get("productprice");
                        System.out.println(getAID().getName() + " est entrain de formuler une demande pour le produit " + productName);
                        float demande = demande(productPrice);
                        System.out.println(getAID().getName() + " veut une quantité de " + (int)(demande));
                        ACLMessage request = new ACLMessage(ACLMessage.REQUEST);
                        request.addReceiver(new AID("producer",AID.ISLOCALNAME));
                        request.setContent(("" + demande));
                        send(request);
                        try {
                            sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        System.out.println(getAID().getName() + " Migre de la location " + itineraire.getName() +
                                " vers la location : " + firstLocation);
                        doMove(firstLocation);
                        System.out.println(getAID().getName() + " est arrivé à la location : " + firstLocation);
                        doDelete();
                    }
                    else{
                        block();
                    }
                }
            });


        }

    // Une fonction de la demande
    // Générée aléatoirement en fonction du prix unitaire fixé par le producer
    public float demande(float unitPrice){
        return (float)(unitPrice * Math.random() * 100 + 10);
    }

    protected void takeDown() {
        System.out.println("Consumeragent "+getAID().getName()+" terminating.");
    }


}

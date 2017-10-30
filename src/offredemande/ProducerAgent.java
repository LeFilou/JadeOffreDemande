package offredemande;

import jade.content.lang.Codec;
import jade.content.lang.sl.SLCodec;
import jade.content.onto.OntologyException;
import jade.content.onto.basic.Action;
import jade.content.onto.basic.Result;
import jade.core.AID;
import jade.core.Location;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.FIPANames;
import jade.domain.JADEAgentManagement.QueryPlatformLocationsAction;
import jade.domain.mobility.MobilityOntology;
import jade.gui.GuiAgent;
import jade.gui.GuiEvent;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;
import container.JadeContainer;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;


public class ProducerAgent extends GuiAgent {
    public static final int CREATE_EVENT = 1;
    public static final int ADVERTISE_EVENT = 2;

    private ProducerGUI producergui;

    private float unitPrice;
    private float unitCost;
    private int totalQuantitySold = 0;
    private Double amountProfit;
    private String nameProduct;

    private int repliesCounter = 0;
    private int numberConsumers;
    private int consumersCreated = 0;
    private int containerCreated = 0;

    private LinkedList<Location> locations;

    private ACLMessage request =  new ACLMessage(ACLMessage.REQUEST);


    // Getters et Setters
    public void setAmountProfit(Double amountProfit) {
        this.amountProfit = amountProfit;
    }

    public Double getAmountProfit() {
        return amountProfit;
    }

    public void settotalQuantitySold(int totalQuantitySold) {
        this.totalQuantitySold = totalQuantitySold;
    }

    public int gettotalQuantitySold() {
        return totalQuantitySold;
    }

    public void setUnitPrice(float unitPrice) {
        this.unitPrice = unitPrice;
    }

    public float getUnitPrice() {
        return unitPrice;
    }

    public void setNameProduct(String nameProduct) {
        this.nameProduct = nameProduct;
    }

    public void setNumberConsumers(int numberConsumers) {
        this.numberConsumers = numberConsumers;
    }

    public int getNumberConsumers() {
        return numberConsumers;
    }

    public String getNameProduct() {
        return nameProduct;
    }

    public float getUnitCost() {
        return unitCost;
    }

    public void setUnitCost(float unitCost) {
        this.unitCost = unitCost;
    }

    public void setup() {

        producergui = new ProducerGUI(this);
        producergui.showProducerGUI();

        getContentManager().registerLanguage(new SLCodec(), FIPANames.ContentLanguage.FIPA_SL0);
        getContentManager().registerOntology(MobilityOntology.getInstance());


        // Un comportement cyclique pour récupérer les demandes de chez Consumers
        // Prends fin quand le producer récupère toutes les réponses de chez les consumers crées
        addBehaviour(new CyclicBehaviour() {
            @Override
            public void action() {
                MessageTemplate template = MessageTemplate.MatchPerformative(ACLMessage.REQUEST);
                ACLMessage request = receive(template);
                if (request != null) {
                    // On récupère une demande de chez un consumer
                    String quantiteDemande = request.getContent();
                    //On l'ajoute à la quantité totale
                    totalQuantitySold += (int)Float.parseFloat(quantiteDemande);
                    repliesCounter++;

                    // Si tous les consumers ont effectué leur demande
                    if (repliesCounter >= getNumberConsumers()) {
                        // On calcule le profit total
                        amountProfit = ((double) ((getUnitPrice() - getUnitCost()) * totalQuantitySold));
                        System.out.println("Le profit total est " + getAmountProfit());
                        // On met à jour les JTextField
                        producergui.updatetotalQuantitySoldTextField(totalQuantitySold);
                        producergui.updateAmountProfitTextField(getAmountProfit());
                        // On réinitialise
                        totalQuantitySold = 0;
                        amountProfit = 0.0;
                    }
                } else {
                    block();
                }
            }
        });

    }

    // GUI EVENT
    @Override
    protected void onGuiEvent(GuiEvent guiEvent) {
        switch (guiEvent.getType()) {

            case CREATE_EVENT:
                // Un Comportement qui permet de créer des Agents Consumers
                addBehaviour(new OneShotBehaviour() {
                    @Override
                    public void action() {
                        int i;
                        for (i = consumersCreated; i < numberConsumers + consumersCreated; i++) {

                            // Nom de l'agent Consumer
                            String agentName = "consumerAgent" + i;
                            // Récupérer le Container ou se touvent le producer
                            ContainerController container = getContainerController();
                            AgentController consumer = null;
                            // Création de l'agent Consumer
                            try {
                                consumer = container.createNewAgent(agentName, "offredemande.ConsumerAgent", null);
                            } catch (StaleProxyException e) {
                                e.printStackTrace();
                            }
                            try {
                                consumer.start();
                            } catch (StaleProxyException e) {
                                e.printStackTrace();
                            }
                        }
                        // réinitialisation de l'indice des consumers
                        consumersCreated = i;
                    }
                });
                break;
            case ADVERTISE_EVENT:
                // Un comportement qui permet de :
                //      Migrer les agents consumers crées d'autre container
                //      Leur envoyer un ACLMESSAGE de type INFORM pour leur envoyer le prux et le nom du produit
                addBehaviour(new OneShotBehaviour() {
                    @Override
                    public void action() {
                        // Créer autant de JadeContainer que de Consumers
                        for (int i = 0; i < numberConsumers; i++) {
                            new JadeContainer("ConsumerContainer" + (i + 1)).getContainer();
                        }
                        // Récupérer les containers créer
                        locations = null;
                        getAllSites();

                        ACLMessage inform = new ACLMessage(ACLMessage.INFORM);
                        Map product = new HashMap();
                        product.put("productname", nameProduct);
                        product.put("productprice", unitPrice);

                        // Envoie du produit
                        int i = 0;
                        for (i = containerCreated; i < getNumberConsumers() + containerCreated; i++) {
                            product.put("location", locations.get(i));
                            try {
                                inform.setContentObject((Serializable) product);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            inform.setProtocol("information");
                            inform.addReceiver(new AID("consumerAgent" + i, AID.ISLOCALNAME));
                            send(inform);
                        }
                        containerCreated = i;
                    }
                });
                break;

        }
    }

    protected void takeDown() {
        System.out.println("Producer-agent " + getAID().getName() + " terminating.");
    }

    // Une fonction qui permet de récuperer les container
    public void getAllSites(){
        request.clearAllReceiver();
        request.addReceiver(getAMS());
        request.setLanguage(FIPANames.ContentLanguage.FIPA_SL0);
        request.setOntology(MobilityOntology.NAME);
        request.setProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST);
        try {
            Action action = new Action();
            action.setActor(getAMS());
            action.setAction(new QueryPlatformLocationsAction());
            getContentManager().fillContent(request, action);
        } catch (Exception e) {
            e.printStackTrace();
        }
        send(request);

        ACLMessage receivedMessage = blockingReceive(MessageTemplate.MatchSender(getAMS()));
        Result result = null;
        try {
            result = (Result) getContentManager().extractContent(receivedMessage);
        } catch (Codec.CodecException e) {
            e.printStackTrace();
        } catch (OntologyException e) {
            e.printStackTrace();
        }
        Iterator<Location> iterator = result.getItems().iterator();
        locations = new LinkedList<Location>();
        System.out.println("Récupération de tous les Jade containers");
        while(iterator.hasNext()){
            Location lct = iterator.next();
            if (!lct.getName().equals("ProducerContainer") && !lct.getName().equals("Main-Container")) {
                System.out.println(lct);
                locations.add(lct);
            }
        }
    }

}





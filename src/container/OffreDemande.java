package container;

import jade.core.Agent;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;

/**
 * Point d'entrée de l'application
 */
public class OffreDemande {
    private ContainerController mainContainer, container;
    private AgentController producer;

    public OffreDemande() throws StaleProxyException {

        // Création du Main Container et du Jade Container pour le Producer
        mainContainer = new MainContainer().getContainer();
        container = new JadeContainer("ProducerContainer").getContainer();
        // Création de l'agent Producer
        producer = container.createNewAgent("producer","offredemande.ProducerAgent",null);
        producer.start();
    }


    public static void main(String[] args) throws StaleProxyException {
        new OffreDemande();
    }
}

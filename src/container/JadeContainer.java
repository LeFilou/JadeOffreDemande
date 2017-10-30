package container;

import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.util.ExtendedProperties;
import jade.wrapper.ContainerController;

public class JadeContainer {
    private ContainerController container;


    /**
     * Jade Container Constructeur
     * Prend en paramètre le nom du container
     * @param containerName
     */
    public JadeContainer(String containerName){
        super();
        Runtime rt = Runtime.instance();
        ProfileImpl profile = new ProfileImpl(false);

        profile.setParameter(ProfileImpl.MAIN_HOST,"localhost");
        profile.setParameter(ProfileImpl.CONTAINER_NAME, containerName);
        container = rt.createAgentContainer(profile);
    }

    public ContainerController getContainer(){
        return container;
    }
}

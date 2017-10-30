package container;

import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.util.ExtendedProperties;
import jade.util.leap.Properties;
import jade.wrapper.ContainerController;

public class MainContainer {

    private ContainerController mainContainer;

    /**
     * Constructeur du Main Container
     */
    public MainContainer(){
        super();
        Runtime rt = Runtime.instance();
        Properties p = new ExtendedProperties();
        p.setProperty("gui","true");
        ProfileImpl profile = new ProfileImpl(p);
        mainContainer = rt.createMainContainer(profile);
    }

    public ContainerController getContainer(){
        return mainContainer;
    }
}

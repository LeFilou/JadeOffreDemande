package offredemande;

import jade.gui.GuiEvent;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


/**
 *  Une classe Swing pour l'interface graphique
 */
public class ProducerGUI extends JFrame implements ActionListener {

    private JTextField nameTextField,unitPriceTextField,unitCostTextField,numberConsumersTextField,
                       totalQuantitySoldTextField,amountProfitTextField;

    private JButton advertiseJButton,consumersJButton;
    // Events
    public static final String ADVERTISELABEL = "ADVERTISE";
    public static final String CREATELABEL = "CREATE NEW CONSUMERS";

    // Producer Agent
    private ProducerAgent produceragent;



    public ProducerGUI(ProducerAgent produceragent) {
        this.produceragent = produceragent;
        this.setTitle("Producer GUI");
        this.setSize(700, 400);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        initGUI();
    }

    public void initGUI() {

        /**
         * Product name
         */
        JPanel namePanel = new JPanel();
        nameTextField = new JTextField();
        JLabel nameLabel = new JLabel("Enter a name");
        namePanel.setPreferredSize(new Dimension(220, 60));
        nameTextField.setPreferredSize(new Dimension(100, 25));
        namePanel.setBorder(BorderFactory.createTitledBorder("Product name"));
        namePanel.add(nameLabel);
        namePanel.add(nameTextField);

        /**
         * Unit Price
         */
        JPanel unitPricePanel = new JPanel();
        unitPriceTextField = new JTextField();
        JLabel unitPriceLabel = new JLabel("Enter a Unit Price");
        unitPricePanel.setPreferredSize(new Dimension(220, 60));
        unitPriceTextField.setPreferredSize(new Dimension(100, 25));
        unitPricePanel.setBorder(BorderFactory.createTitledBorder("Unit Price"));
        unitPricePanel.add(unitPriceLabel);
        unitPricePanel.add(unitPriceTextField);

        /**
         * Unit Cost
         */
        JPanel unitCostPanel = new JPanel();
        unitCostTextField = new JTextField();
        JLabel unitCostLabel = new JLabel("Enter a Unit Cost");
        unitCostPanel.setPreferredSize(new Dimension(220, 60));
        unitCostTextField.setPreferredSize(new Dimension(100, 25));
        unitCostPanel.setBorder(BorderFactory.createTitledBorder("Unit Cost"));
        unitCostPanel.add(unitCostLabel);
        unitCostPanel.add(unitCostTextField);

        /**
         * Number of consumers
         */
        JPanel numberConsumersPanel = new JPanel();
        numberConsumersTextField = new JTextField();
        JLabel numberConsumersLabel = new JLabel("Enter number of Consumers");
        numberConsumersPanel.setPreferredSize(new Dimension(320, 60));
        numberConsumersTextField.setPreferredSize(new Dimension(100, 25));
        numberConsumersPanel.setBorder(BorderFactory.createTitledBorder("Number of consumers"));
        numberConsumersPanel.add(numberConsumersLabel);
        numberConsumersPanel.add(numberConsumersTextField);


        // Consumers creator JButton
        consumersJButton = new JButton(CREATELABEL);


        /**
         * Total quantity sold
         */
        JPanel totalQuantitySoldPanel = new JPanel();
        totalQuantitySoldTextField = new JTextField();
        JLabel totalQuantitySoldLabel = new JLabel(" Total quantity sold");
        totalQuantitySoldPanel.setPreferredSize(new Dimension(320, 60));
        totalQuantitySoldTextField.setPreferredSize(new Dimension(100, 25));
        totalQuantitySoldPanel.setBorder(BorderFactory.createTitledBorder("Total quantity sold"));
        totalQuantitySoldPanel.add(totalQuantitySoldLabel);
        totalQuantitySoldPanel.add(totalQuantitySoldTextField);

        /**
         * Amount of profit
         */
        JPanel amountProfitPanel = new JPanel();
        amountProfitTextField = new JTextField();
        JLabel amountProfiteLabel = new JLabel("Amount of profit");
        amountProfitPanel.setPreferredSize(new Dimension(320, 60));
        amountProfitTextField.setPreferredSize(new Dimension(100, 25));
        amountProfitPanel.setBorder(BorderFactory.createTitledBorder("Amount of profit"));
        amountProfitPanel.add(amountProfiteLabel);
        amountProfitPanel.add(amountProfitTextField);

        // Advertise button
        advertiseJButton = new JButton(ADVERTISELABEL);


        // Consumer button action
        consumersJButton.addActionListener(this);
        advertiseJButton.addActionListener(this);

        nameTextField.setEnabled(false);
        unitPriceTextField.setEnabled(false);
        unitCostTextField.setEnabled(false);
        totalQuantitySoldTextField.setEditable(false);
        amountProfitTextField.setEditable(false);
        advertiseJButton.setEnabled(false);

        JPanel westContent = new JPanel();
        westContent.setLayout(new BoxLayout(westContent, BoxLayout.PAGE_AXIS));
        westContent.add(namePanel);
        westContent.add(unitPricePanel);
        westContent.add(unitCostPanel);
        westContent.add(numberConsumersPanel);
        westContent.add(consumersJButton);

        JPanel eastContent = new JPanel();
        eastContent.setLayout(new BoxLayout(eastContent, BoxLayout.PAGE_AXIS));
        eastContent.add(totalQuantitySoldPanel);
        eastContent.add(amountProfitPanel);
        eastContent.add(advertiseJButton);


        JLabel pgui = new JLabel("Producer GUI");
        JPanel titlePanel = new JPanel();
        titlePanel.add(pgui, BorderLayout.CENTER);
        this.getContentPane().add(titlePanel, BorderLayout.NORTH);
        this.getContentPane().add(westContent, BorderLayout.WEST);
        this.getContentPane().add(eastContent, BorderLayout.EAST);
    }

    public void showProducerGUI(){
        this.setVisible(true);
    }

    /**
     *  Une fonction qui met à jour le JTextField de la quantité totale vendue
     * @param totalQuantity
     */
    public void updatetotalQuantitySoldTextField(int totalQuantity){
        totalQuantitySoldTextField.setText("" + totalQuantity);
    }

    /**
     * Une fonction qui met à jour le JTextField du Amount Profit
     * @param amountProfit
     */
    public void updateAmountProfitTextField(Double amountProfit){
        amountProfitTextField.setText("" + amountProfit);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String action = e.getActionCommand();

        // Action d'ajout de nouveaux consumers
        if(action.equalsIgnoreCase(CREATELABEL)){
            int numberConsumer = Integer.parseInt((String) numberConsumersTextField.getText());
            produceragent.setNumberConsumers(numberConsumer);
            nameTextField.setEnabled(true);
            unitPriceTextField.setEnabled(true);
            unitCostTextField.setEnabled(true);
            advertiseJButton.setEnabled(true);
            GuiEvent guievent = new GuiEvent((Object)this,produceragent.CREATE_EVENT);
            produceragent.postGuiEvent(guievent);
        }
        // Action d'advertise
        else if (action.equalsIgnoreCase(ADVERTISELABEL)){
            float unitPrice = Float.parseFloat((String) unitPriceTextField.getText());
            String nameProduct = (String) nameTextField.getText();
            produceragent.setUnitPrice(unitPrice);
            produceragent.setNameProduct(nameProduct);
            produceragent.setUnitCost(Float.parseFloat((String)unitCostTextField.getText()));
            GuiEvent guievent = new GuiEvent((Object)this,produceragent.ADVERTISE_EVENT);
            produceragent.postGuiEvent(guievent);
        }
    }
}

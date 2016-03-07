
package com.chrinor.jmeter.plugins.json;

import java.awt.BorderLayout;
import javax.swing.BorderFactory;
import org.apache.jmeter.assertions.gui.AbstractAssertionGui;
import org.apache.jmeter.gui.util.VerticalPanel;
import org.apache.jmeter.testelement.TestElement;
import org.apache.jorphan.gui.JLabeledTextField;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.apache.jorphan.gui.JLabeledChoice;
import org.apache.jorphan.logging.LoggingManager;
import org.apache.log.Logger;

/**
 *
 * @author chrisr
 */
public class JSONPathLengthAssertionGui extends AbstractAssertionGui implements ChangeListener
{
    private static final Logger log = LoggingManager.getLoggerForClass();

    //
    private JLabeledTextField jsonPath = null;
    private JLabeledTextField length = null;
    private JLabeledChoice operator = null;

    public JSONPathLengthAssertionGui() {
        init();
    }

    public void init() {
        setLayout(new BorderLayout());
        setBorder(makeBorder());
        add(makeTitlePanel(), BorderLayout.NORTH);

        VerticalPanel panel = new VerticalPanel();
        panel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));

        jsonPath = new JLabeledTextField("JSON Path: ");
        operator = new JLabeledChoice("Operator: ", new String[]{ "==", "!=", ">", ">=", "<", "<=" });
        length = new JLabeledTextField("Length: ");

        panel.add(jsonPath);
        panel.add(operator);
        panel.add(length);

        add(panel, BorderLayout.CENTER);
    }

    @Override
    public void clearGui() {
        super.clearGui();
        jsonPath.setText("$.");
        length.setText("");
    }

    @Override
    public TestElement createTestElement() {
        JSONPathLengthAssertion jpAssertion = new JSONPathLengthAssertion();
        modifyTestElement(jpAssertion);
        return jpAssertion;
    }

    @Override
    public String getLabelResource() {
        return this.getClass().getSimpleName();
    }

    @Override
    public String getStaticLabel() {
        return "JSON Path Length Assertion";
    }

    @Override
    public void modifyTestElement(TestElement element) {
        super.configureTestElement(element);
        if (element instanceof JSONPathLengthAssertion) {
            JSONPathLengthAssertion jpAssertion = (JSONPathLengthAssertion) element;
            jpAssertion.setJsonPath(jsonPath.getText());
            jpAssertion.setOperator(operator.getText());
            jpAssertion.setLength(length.getText());
        }
    }

    @Override
    public void configure(TestElement element) {
        super.configure(element);
        JSONPathLengthAssertion jpAssertion = (JSONPathLengthAssertion) element;
        jsonPath.setText(jpAssertion.getJsonPath());
        operator.setText(jpAssertion.getOperator());
        length.setText(jpAssertion.getLength());
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        // do nothing...
    }
}
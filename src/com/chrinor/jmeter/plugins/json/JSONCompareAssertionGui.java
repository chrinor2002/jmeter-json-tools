
package com.chrinor.jmeter.plugins.json;

import java.awt.BorderLayout;
import javax.swing.BorderFactory;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.jmeter.assertions.gui.AbstractAssertionGui;
import org.apache.jmeter.gui.util.VerticalPanel;
import org.apache.jmeter.testelement.TestElement;
import org.apache.jorphan.gui.JLabeledChoice;
import org.apache.jorphan.gui.JLabeledTextArea;
import org.apache.jorphan.gui.JLabeledTextField;
import org.apache.jorphan.logging.LoggingManager;
import org.apache.log.Logger;

/**
 *
 * @author chrisr
 */
public class JSONCompareAssertionGui extends AbstractAssertionGui implements ChangeListener
{
    private static final Logger log = LoggingManager.getLoggerForClass();

    private JLabeledChoice compareTo = null;
    private JLabeledTextArea inputValue = null;
    private JLabeledTextField inputVariableName = null;
    private JLabeledChoice compareMode = null;


    public JSONCompareAssertionGui() {
        init();
    }

    public void init() {
        setLayout(new BorderLayout());
        setBorder(makeBorder());
        add(makeTitlePanel(), BorderLayout.NORTH);

        VerticalPanel panel = new VerticalPanel();
        panel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));

        compareTo = new JLabeledChoice("Compare To: ", new String[]{ JSONCompareAssertion.COMPARE_SCOPE_INPUT, JSONCompareAssertion.COMPARE_SCOPE_VARIABLE });
        inputValue = new JLabeledTextArea("JSON Input: ");
        inputVariableName = new JLabeledTextField("Variable Name: ");
        compareMode = new JLabeledChoice("Compare Mode: ", new String[]{
            JSONCompareAssertion.COMPARE_MODE_LENIENT,
            JSONCompareAssertion.COMPARE_MODE_STRICT,
            JSONCompareAssertion.COMPARE_MODE_NON_EXTENSIBLE,
            JSONCompareAssertion.COMPARE_MODE_STRICT_ORDER
        });

        panel.add(compareTo);
        panel.add(inputValue);
        panel.add(inputVariableName);
        panel.add(compareMode);

        add(panel, BorderLayout.CENTER);
    }

    @Override
    public void clearGui() {
        super.clearGui();
        compareTo.setText(JSONCompareAssertion.COMPARE_SCOPE_INPUT);
        inputValue.setText("");
        inputVariableName.setText("");
        compareMode.setText(JSONCompareAssertion.COMPARE_MODE_LENIENT);
    }

    @Override
    public TestElement createTestElement() {
        JSONCompareAssertion jpAssertion = new JSONCompareAssertion();
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
        if (element instanceof JSONCompareAssertion) {
            JSONCompareAssertion jpAssertion = (JSONCompareAssertion) element;
            jpAssertion.setCompareScope(compareTo.getText());
            jpAssertion.setCompareValue(inputValue.getText());
            jpAssertion.setCompareVariableName(inputVariableName.getText());
            jpAssertion.setCompareModeValue(compareMode.getText());
        }
    }

    @Override
    public void configure(TestElement element) {
        super.configure(element);
        JSONCompareAssertion jpAssertion = (JSONCompareAssertion) element;
        compareTo.setText(jpAssertion.getCompareScope());
        inputValue.setText(jpAssertion.getCompareValue());
        inputVariableName.setText(jpAssertion.getCompareVariableName());
        compareMode.setText(jpAssertion.getCompareModeValue());
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        // do nothing...
    }
}
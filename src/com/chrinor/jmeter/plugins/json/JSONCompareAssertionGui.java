
package com.chrinor.jmeter.plugins.json;

import java.awt.BorderLayout;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.jmeter.assertions.gui.AbstractAssertionGui;
import org.apache.jmeter.gui.util.VerticalPanel;
import org.apache.jmeter.testelement.TestElement;
import org.apache.jorphan.gui.JLabeledChoice;
import org.apache.jorphan.gui.JLabeledTextArea;
import org.apache.jorphan.gui.JLabeledTextField;
import org.apache.jorphan.gui.layout.VerticalLayout;
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
    private JLabeledTextField applyPath = null;
    private JLabeledTextArea inputValue = null;
    private JLabeledTextField inputPath = null;
    private JLabeledTextField inputVariableName = null;
    private JLabeledChoice compareMode = null;


    public JSONCompareAssertionGui() {
        init();
    }

    public void init() {
        setLayout(new VerticalLayout(5, VerticalLayout.BOTH, VerticalLayout.TOP));
        setBorder(makeBorder());

        add(makeTitlePanel());
        Box box = Box.createVerticalBox();
        box.add(createScopePanel(true));
        add(box);

        VerticalPanel panel = new VerticalPanel();
        panel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));

        applyPath = new JLabeledTextField("Apply JSON path: ");
        compareTo = new JLabeledChoice("Compare To: ", new String[]{ JSONCompareAssertion.COMPARE_SCOPE_INPUT, JSONCompareAssertion.COMPARE_SCOPE_VARIABLE });
        inputPath = new JLabeledTextField("Input JSON path: ");
        inputValue = new JLabeledTextArea("JSON Input: ");
        inputVariableName = new JLabeledTextField("Variable Name: ");
        compareMode = new JLabeledChoice("Compare Mode: ", new String[]{
            JSONCompareAssertion.COMPARE_MODE_LENIENT,
            JSONCompareAssertion.COMPARE_MODE_STRICT,
            JSONCompareAssertion.COMPARE_MODE_NON_EXTENSIBLE,
            JSONCompareAssertion.COMPARE_MODE_STRICT_ORDER
        });

        panel.add(applyPath);
        panel.add(compareTo);
        panel.add(inputPath);
        panel.add(inputValue);
        panel.add(inputVariableName);
        panel.add(compareMode);

        add(panel);
    }

    @Override
    public void clearGui() {
        super.clearGui();
        applyPath.setText("$.");
        compareTo.setText(JSONCompareAssertion.COMPARE_SCOPE_INPUT);
        inputPath.setText("$.");
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
        return "JSON Comparison Assertion";
    }

    @Override
    public void modifyTestElement(TestElement element) {
        super.configureTestElement(element);
        if (element instanceof JSONCompareAssertion) {
            JSONCompareAssertion jsonAssertion = (JSONCompareAssertion) element;
            saveScopeSettings(jsonAssertion);
            jsonAssertion.setCompareJsonPath(applyPath.getText());
            jsonAssertion.setInputJsonPath(inputPath.getText());
            jsonAssertion.setCompareScope(compareTo.getText());
            jsonAssertion.setCompareValue(inputValue.getText());
            jsonAssertion.setCompareVariableName(inputVariableName.getText());
            jsonAssertion.setCompareModeValue(compareMode.getText());
        }
    }

    @Override
    public void configure(TestElement element) {
        super.configure(element);
        JSONCompareAssertion jsonAssertion = (JSONCompareAssertion) element;
        showScopeSettings(jsonAssertion, true);
        applyPath.setText(jsonAssertion.getCompareJsonPath());
        compareTo.setText(jsonAssertion.getCompareScope());
        inputPath.setText(jsonAssertion.getInputJsonPath());
        inputValue.setText(jsonAssertion.getCompareValue());
        inputVariableName.setText(jsonAssertion.getCompareVariableName());
        compareMode.setText(jsonAssertion.getCompareModeValue());
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        // do nothing...
    }
}

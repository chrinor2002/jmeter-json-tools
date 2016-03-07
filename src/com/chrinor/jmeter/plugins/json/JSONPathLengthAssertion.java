
package com.chrinor.jmeter.plugins.json;

import com.jayway.jsonpath.JsonPath;
import net.minidev.json.JSONArray;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.jmeter.testelement.AbstractTestElement;
import org.apache.jmeter.assertions.Assertion;
import org.apache.jmeter.assertions.AssertionResult;
import org.apache.jorphan.logging.LoggingManager;
import org.apache.log.Logger;
import java.io.Serializable;

/**
 *
 * @author chrisr
 */
public class JSONPathLengthAssertion extends AbstractTestElement implements Serializable, Assertion
{
    private static final Logger log = LoggingManager.getLoggerForClass();
    public static final String JSONPATH = "JSON_PATH";
    public static final String LENGTH = "LENGTH";
    public static final String OPERATOR = "OPERATOR";
    //public static final String INVERT = "INVERT";

    public String getJsonPath() {
        return getPropertyAsString(JSONPATH);
    }
    public void setJsonPath(String jsonPath) {
        setProperty(JSONPATH, jsonPath);
    }

    public String getLength() {
        return getPropertyAsString(LENGTH);
    }
    public int getLengthAsInt() {
        String length = getLength();
        return Integer.getInteger(length);
    }
    public void setLength(String length) {
        setProperty(LENGTH, length);
    }

    public String getOperator() {
        return getPropertyAsString(OPERATOR);
    }
    public void setOperator(String op) {
        setProperty(OPERATOR, op);
    }

    /*public void setInvert(boolean invert) {
        setProperty(INVERT, invert);
    }
    public boolean isInvert() {
        return getPropertyAsBoolean(INVERT);
    }*/

    private void doAssert(String jsonString) {
        Object value = JsonPath.read(jsonString, getJsonPath());

        if (value instanceof JSONArray) {
            JSONArray arr = (JSONArray) value;

            switch(getOperator()){
                case "==":
                    if(arr.size() == getLengthAsInt()) return;
                    break;

                case "!=":
                    if(arr.size() == getLengthAsInt()) return;
                    break;

                case ">":
                    if(arr.size() > getLengthAsInt()) return;
                    break;

                case ">=":
                    if(arr.size() >= getLengthAsInt()) return;
                    break;

                case "<":
                    if(arr.size() < getLengthAsInt()) return;
                    break;

                case "<=":
                    if(arr.size() <= getLengthAsInt()) return;
                    break;

                default:
                    throw new RuntimeException(String.format("'%s' is not a known operator", getOperator()));
            }

            throw new RuntimeException(String.format("Length of '%s' was not '%s' '%s'", arr.size(), getOperator(), getLength()));
        }

        //if (isExpectNull())
            //throw new RuntimeException(String.format("Value expected to be null, but found '%s'", value));
        //else
            throw new RuntimeException(String.format("Value at path '%s' was not a JSONArray", getJsonPath()));
    }

    @Override
    public AssertionResult getResult(SampleResult samplerResult)
    {
        AssertionResult result = new AssertionResult(getName());
        byte[] responseData = samplerResult.getResponseData();
        if (responseData.length == 0) {
            return result.setResultForNull();
        }

        result.setFailure(false);
        result.setFailureMessage("");

        //if (!isInvert()) {
            try {
                doAssert(new String(responseData));
            } catch (Exception e) {
                if (log.isDebugEnabled()) {
                    log.debug("Assertion failed", e);
                }
                result.setFailure(true);
                result.setFailureMessage(e.getMessage());
            }
        /*} else {
            try {
                doAssert(new String(responseData));
                result.setFailure(true);
                result.setFailureMessage("Failed that JSONPath " + getJsonPath() + " length not matches " + getLength());

            } catch (Exception e) {
                if (log.isDebugEnabled()) {
                    log.debug("Assertion failed", e);
                }
            }
        }*/
        return result;
    }
}
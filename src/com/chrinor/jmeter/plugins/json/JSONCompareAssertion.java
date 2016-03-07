/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chrinor.jmeter.plugins.json;

import com.jayway.jsonpath.JsonPath;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Field;
import org.apache.commons.lang3.StringUtils;
import org.apache.jmeter.assertions.Assertion;
import org.apache.jmeter.assertions.AssertionResult;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.jmeter.testelement.AbstractScopedAssertion;
import org.apache.jorphan.logging.LoggingManager;
import org.apache.log.Logger;
import org.json.JSONException;
import org.skyscreamer.jsonassert.JSONCompare;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.skyscreamer.jsonassert.JSONCompareResult;

/**
 *
 * @author chrisr
 */
public class JSONCompareAssertion extends AbstractScopedAssertion implements Serializable, Assertion
{
    private static final Logger log = LoggingManager.getLoggerForClass();

    public static final String COMPARE_SCROPE = "Compare.scrope";
    public static final String COMPARE_JSON_PATH = "Compare.json_path";
    public static final String COMPARE_TO_JSON_PATH = "Compare.value.json_path";
    public static final String COMPARE_VARIABLE = "Compare.variable";
    public static final String COMPARE_VALUE = "Compare.value";
    public static final String COMPARE_MODE = "Compare.mode";
    public static final String COMPARE_MODE_LENIENT = "LENIENT";
    public static final String COMPARE_MODE_STRICT = "STRICT";
    public static final String COMPARE_MODE_NON_EXTENSIBLE = "NON_EXTENSIBLE";
    public static final String COMPARE_MODE_STRICT_ORDER = "STRICT_ORDER";

    public static final String COMPARE_SCOPE_INPUT = "Input";
    public static final String COMPARE_SCOPE_VARIABLE = "JMeter Variable";

    @Override
    public AssertionResult getResult(SampleResult response)
    {
        AssertionResult result = new AssertionResult(getName());
        byte[] responseString;
        byte[] compareString;

        result.setFailure(false);
        result.setFailureMessage("");

        try{
            if(isScopeVariable()) {
                if (log.isDebugEnabled()) {
                    log.debug(new StringBuilder("variable is set to ").append(getVariableName()).toString());
                }
                responseString = getThreadContext().getVariables().get(getVariableName()).getBytes("UTF-8");
            } else {
                responseString = response.getResponseData();
            }

            if (responseString == null || responseString.length == 0) {
                return result.setResultForNull();
            }

            if (log.isDebugEnabled()) {
                log.debug(new StringBuilder("compare variable is set to ").append(getCompareVariableName()).toString());
            }
            String inputCompareString = getThreadContext().getVariables().get(getCompareVariableName());
            if(getCompareScope().equals(COMPARE_SCOPE_VARIABLE) && !StringUtils.isEmpty(inputCompareString)) {
                compareString = inputCompareString.getBytes("UTF-8");
            } else if(getCompareScope().equals(COMPARE_SCOPE_INPUT)) {
                compareString = getCompareValue().getBytes("UTF-8");
            } else {
                compareString = null;
            }

            if (getCompareScope().equals(COMPARE_SCOPE_INPUT) && (compareString == null || compareString.length == 0)) {
                throw new IllegalArgumentException("Input json string cannot be empty when comparing to direct input.");
            }

            if (log.isDebugEnabled()) {
                log.debug(new StringBuilder("Input is set to ").append(compareString).toString());
            }

            String responseJson = new String(responseString, "UTF-8");
            if (log.isDebugEnabled()) {
                log.debug(new StringBuilder("responseJson is set to ").append(responseJson).toString());
            }
            responseJson = JsonPath.read(responseJson, getCompareJsonPath()).toString();
            if (log.isDebugEnabled()) {
                log.debug(new StringBuilder("responseJson is now set to ").append(responseJson).toString());
            }
            String compareJson = new String(compareString, "UTF-8");
            if (log.isDebugEnabled()) {
                log.debug(new StringBuilder("compareJson is set to ").append(compareJson).toString());
            }
            compareJson = JsonPath.read(compareJson, getInputJsonPath()).toString();
            if (log.isDebugEnabled()) {
                log.debug(new StringBuilder("compareJson is now set to ").append(compareJson).toString());
            }

            JSONCompareResult jsonResult = JSONCompare.compareJSON(compareJson, responseJson, getCompareMode());

            if (jsonResult.failed()) {
                if (log.isDebugEnabled()) {
                    log.debug(jsonResult.getMessage());
                }
                result.setFailure(true);
                result.setFailureMessage(new StringBuilder("JSONCompareResult: ").append(jsonResult.getMessage()).toString());
            }

        }catch(IllegalArgumentException e){
            log.warn("Something bad happened with JSON: ", e);
            result.setError(true);
            result.setFailureMessage(new StringBuilder("IllegalArgumentException: ").append(e.getMessage()).toString());
        }catch(JSONException e){
            log.warn("Cannot parse result content or input", e);
            result.setError(true);
            result.setFailureMessage(new StringBuilder("JSONException: ").append(e.getMessage()).toString());
        }catch(IOException e){
            log.warn("Cannot parse result content", e);
            result.setError(true);
            result.setFailureMessage(new StringBuilder("IOException: ").append(e.getMessage()).toString());
        }finally{
            return result;
        }
    }

    /*public static boolean compareObjects(Object a, Object b){
        // first check class
        boolean sameClass = a.getClass().equals(b.getClass());
        if(sameClass){
            // try
            if(a instanceof JSONObject){
                // compare objects, recursive?
            }else if(a instanceof JSONAssert){
            }
        }else{
            return false;
        }
    }*/

    public String getCompareScope(){
        return getPropertyAsString(COMPARE_SCROPE);
    }
    public void setCompareScope(String scope){
        setProperty(COMPARE_SCROPE, scope);
    }

    public String getCompareJsonPath(){
        return getPropertyAsString(COMPARE_JSON_PATH);
    }
    public void setCompareJsonPath(String scope){
        setProperty(COMPARE_JSON_PATH, scope);
    }

    public String getInputJsonPath(){
        return getPropertyAsString(COMPARE_TO_JSON_PATH);
    }
    public void setInputJsonPath(String scope){
        setProperty(COMPARE_TO_JSON_PATH, scope);
    }

    public String getCompareValue(){
        return getPropertyAsString(COMPARE_VALUE);
    }
    public void setCompareValue(String value){
        setProperty(COMPARE_VALUE, value);
    }

    public String getCompareVariableName(){
        return getPropertyAsString(COMPARE_VARIABLE);
    }
    public void setCompareVariableName(String value){
        setProperty(COMPARE_VARIABLE, value);
    }

    public JSONCompareMode getCompareMode(){
        String mode = getCompareModeValue();
        JSONCompareMode compareMode = null;
        try{
            Field f = JSONCompareMode.class.getField(mode);
            compareMode = (JSONCompareMode)f.get(null);
            return compareMode;
        }catch(Exception e){
            log.debug(new StringBuilder("Unable to get a compare mode for field name of ").append(mode).toString(), e);
        }finally{
            return compareMode;
        }
    }
    public String getCompareModeValue(){
        return getPropertyAsString(COMPARE_MODE);
    }
    public void setCompareModeValue(String value){
        try{
            // ignore the result, this is aquired later when actually called by another getter
            JSONCompareMode.class.getField(value);
        }catch(NoSuchFieldException e){
            log.debug(new StringBuilder("Unable to get a compare mode for field name of ").append(value).toString(), e);
            throw new IllegalArgumentException(value + " is not a valid compare mode.");
        }
        setProperty(COMPARE_MODE, value);
    }
}

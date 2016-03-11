/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chrinor.jmeter.plugins.json;

import com.atlantbh.jmeter.plugins.jsonutils.jsonpathextractor.JSONPathExtractor;
import org.apache.jmeter.processor.PreProcessor;
import org.apache.jorphan.logging.LoggingManager;
import org.apache.log.Logger;

/**
 *
 * @author chreid
 */
public class JSONPathPreExtract extends JSONPathExtractor implements PreProcessor
{
    private static final Logger log = LoggingManager.getLoggerForClass();

    public JSONPathPreExtract(){
        super();
    }
}

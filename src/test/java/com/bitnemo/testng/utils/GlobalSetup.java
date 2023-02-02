package com.bitnemo.testng.utils;

import com.bitnemo.testng.base.CoreFunction;
import org.testng.annotations.BeforeSuite;

public class GlobalSetup {
    CoreFunction coreFunction = CoreFunction.getInstance();
    @BeforeSuite
    public void globalSetup(){
        coreFunction.globalSetup();
    }
}

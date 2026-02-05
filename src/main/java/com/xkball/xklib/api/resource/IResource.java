package com.xkball.xklib.api.resource;

import java.io.BufferedReader;
import java.io.InputStream;

public interface IResource {
    
    InputStream open();
    
    BufferedReader openAsReader();
    
}

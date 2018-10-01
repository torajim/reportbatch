package com.skplanet.dpa.reportbatch.common.util;

import org.jasypt.util.text.BasicTextEncryptor;

/**
 * Created by 1003724 on 2017-11-21.
 */
public class TextEncryptor {
    private BasicTextEncryptor textEncryptor = new BasicTextEncryptor();

    public TextEncryptor(){
        textEncryptor.setPassword("asdfwscv09824l=!@#$%sedns!@#%!dfu342sdlkvw432edfsd");
    }

    public String encrypt(String text){
        return this.textEncryptor.encrypt(text);
    }

    public String decrypt(String text){
        return this.textEncryptor.decrypt(text);
    }
}